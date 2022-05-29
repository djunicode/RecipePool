from multiprocessing import AuthenticationError
from django.urls import reverse
from django.conf import settings
from django.contrib.auth import get_user_model
from rest_framework import (mixins, generics, status, permissions)
from Recipe.models import Ingredient
from .models import Inventory
from rest_framework_simplejwt.tokens import RefreshToken
from django.http.response import HttpResponse, JsonResponse
import jwt
from rest_framework.views import APIView
from drf_spectacular.utils import extend_schema, OpenApiParameter
from drf_spectacular.types import OpenApiTypes
from rest_framework import (mixins, generics, status, permissions)
from rest_framework.response import Response

#reseting password
from django.contrib.auth.tokens import PasswordResetTokenGenerator
from django.utils.encoding import smart_str, force_str, smart_bytes, DjangoUnicodeDecodeError
from django.utils.http import urlsafe_base64_decode, urlsafe_base64_encode
from django.contrib.sites.shortcuts import get_current_site
from decouple import config
from django.shortcuts import redirect, render
import os
# Create your views here.

from .utils import Util
from .serializers import (UserSerializer, EmailVerificationSerializer, LoginSerializer, 
                          ResetPasswordEmailRequestSerializer, SetNewPasswordSerializer, 
                          LogoutSerializer,GoogleSocialAuthSerializer, InventorySerializer)

User = get_user_model()

class SignUp(mixins.ListModelMixin, mixins.CreateModelMixin, generics.GenericAPIView):
    
    serializer_class = UserSerializer

    def post(self, request, *args, **kwargs):
        serializer1 = UserSerializer(data=request.data)
        if serializer1.is_valid():
            user_data = serializer1.save_user(serializer1.data)
            token = RefreshToken.for_user(user_data).access_token
            relative_link = reverse('EmailVerification')
            abs_url = settings.FRONT_END_HOST + relative_link + "user-id=" + str(token)
            email_body = "Hiii" + "! Use link below to verify your email \n"+ abs_url
            data ={'email_body': email_body, 'email_subject': "Verify your Email",'to_email':user_data.email}
            Util.send_email(data)
            return JsonResponse({'status': 'created', 'token': str(token)}, status=status.HTTP_201_CREATED)
        return JsonResponse(serializer1.errors, status=status.HTTP_400_BAD_REQUEST)


class VerifyEmail(APIView):

    serializer_class = EmailVerificationSerializer

    token_param_config = OpenApiParameter('token', OpenApiTypes.STR, OpenApiParameter.QUERY, description="Enter token here")

    @extend_schema(parameters=[token_param_config],)
    def get(self, request, *args, **kwargs):
        token = request.GET.get('token')

        try:
            payload = jwt.decode(token,settings.SECRET_KEY, algorithms=['HS256'])
            user = User.objects.get(id=payload['user_id'])
            if not user.is_active:
                user.is_active = True
                user.save()
            return JsonResponse({'status': 'Email Successfully Verified'}, status=status.HTTP_200_OK)
        except jwt.ExpiredSignatureError as identifier:
            return JsonResponse({'error':"Activation Link has expired"}, status=status.HTTP_400_BAD_REQUEST)
        except jwt.exceptions.DecodeError as identifier:
            return JsonResponse({'error':"Invalid Token"}, status=status.HTTP_400_BAD_REQUEST)


class Login(generics.GenericAPIView):

    serializer_class = LoginSerializer

    def post(self, request, *args, **kwargs):
        serializer = self.serializer_class(data=request.data, context={'request':request})
        serializer.is_valid(raise_exception=True)
        return JsonResponse(serializer.validated_data, status=status.HTTP_200_OK)

class RequestPasswordResetEmail(generics.GenericAPIView):
    serializer_class = ResetPasswordEmailRequestSerializer

    def post(self, request):
        #serializer = self.serializer_class(data=request.data)

        email = request.data.get('email', '')

        if User.objects.filter(email=email).exists():
            user = User.objects.get(email=email)
            uidb64 = urlsafe_base64_encode(smart_bytes(user.id))
            token = PasswordResetTokenGenerator().make_token(user)
            current_site = get_current_site(
                request=request).domain
            relativeLink = reverse(
                'password-reset-confirm', kwargs={'uidb64': uidb64, 'token': token})

            redirect_url = request.data.get('redirect_url', '')
            absurl = 'http://'+current_site + relativeLink
            email_body = 'Hello, \n Use link below to reset your password  \n' + \
                absurl+"?redirect_url="+redirect_url
            data = {'email_body': email_body, 'to_email': user.email,
                    'email_subject': 'Reset your passsword'}
            Util.send_email(data)
        return Response({'success': 'We have sent you a link to reset your password'}, status=status.HTTP_200_OK)


class PasswordTokenCheckAPI(generics.GenericAPIView):
    serializer_class = SetNewPasswordSerializer

    def get(self, request, uidb64, token):

        redirect_url = request.GET.get('redirect_url')

        try:
            id = smart_str(urlsafe_base64_decode(uidb64)) #Returns a str object representing decoded uidb64. 
            user = User.objects.get(id=id)

            if not PasswordResetTokenGenerator().check_token(user, token):
                if len(redirect_url) > 3:
                    return redirect(redirect_url+'?token_valid=False')
                else:
                    return redirect(os.environ.get('FRONTEND_URL', '')+'?token_valid=False')

            if redirect_url and len(redirect_url) > 3:
                return redirect(redirect_url+'?token_valid=True&message=Credentials Valid&uidb64='+uidb64+'&token='+token)
            else:
                return redirect(os.environ.get('FRONTEND_URL', '')+'?token_valid=False')

        except DjangoUnicodeDecodeError as identifier:
            try:
                if not PasswordResetTokenGenerator().check_token(user):
                    return redirect(redirect_url+'?token_valid=False')
                    
            except UnboundLocalError as e:
                return Response({'error': 'Token is not valid, please request a new one'}, status=status.HTTP_400_BAD_REQUEST)



class SetNewPasswordAPIView(generics.GenericAPIView):
    serializer_class = SetNewPasswordSerializer

    def patch(self, request):
        serializer = self.serializer_class(data=request.data)
        serializer.is_valid(raise_exception=True)
        return Response({'success': True, 'message': 'Password reset success'}, status=status.HTTP_200_OK)


class LogoutAPIView(generics.GenericAPIView):
    serializer_class = LogoutSerializer
    permission_classes = (permissions.IsAuthenticated,)

    def post(self, request):
        serializer = self.serializer_class(data=request.data)
        serializer.is_valid(raise_exception=True)
        serializer.save()

        return Response({'message':'Logged out successfully'},status=status.HTTP_204_NO_CONTENT)



##############SOCIAL AUTH#######################

class GoogleAuth(generics.GenericAPIView):

    serializer_class = GoogleSocialAuthSerializer

    def post(self, request):
        serializer = self.serializer_class(data=request.data)
        serializer.is_valid(raise_exception=True)
        data = ((serializer.validated_data)['auth_token'])
        return Response(data, status=status.HTTP_200_OK)

def index(request):
    GOOGLE_CLIENT_ID = config('GOOGLE_CLIENT_ID')
    content = {'GOOGLE_CLIENT_ID': GOOGLE_CLIENT_ID}
    return render(request, 'index.html', content)

class InventoryView(APIView):
    serializer_class = InventorySerializer
    permission_classes = [IsAuthenticated,]
    
    def post(self, request):
        try:
            user = User.objects.get(email = request.user)
        except User.DoesNotExist:
            content = {'detail': 'No such user exists'}
            return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)

        try:
            ingredient = Ingredient.objects.get(name__contains = request.data['ingredient'])
        except Ingredient.DoesNotExist:
            ingredient = Ingredient.objects.create(name = request.data['ingredient'])
        try:
            inventory_list = Inventory.objects.get(ingredient=ingredient)
        except Inventory.DoesNotExist:
            Inventory.objects.create(user=user,ingredient = ingredient, quantity = request.data['quantity'])
            return JsonResponse(request.data,safe = False, status = status.HTTP_202_ACCEPTED)
        content = {'detail': 'Ingredient already for this recipe, edit it in put method'}
        return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)
    
    def put(self,request,pk):
        try:
            user = User.objects.get(email = request.user)
        except User.DoesNotExist:
            content = {'detail': 'No such user exists'}
            return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)
        try:
            inventory_list = Inventory.objects.get(id=pk)
        except Inventory.DoesNotExist:
            content = {'detail': 'No such Ingredient available for this recipe'}
            return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)
        replace = inventory_list.ingredient
        replace_name = inventory_list.name
        serializer = InventorySerializer(instance = inventory_list, data=request.data, partial = True)
        if serializer.is_valid():
            serializer = serializer.save()
            serializer.ingredient = replace
            serializer.name = replace_name
            serializer.save()
            return JsonResponse(request.data,safe = False, status = status.HTTP_202_ACCEPTED)
        return JsonResponse(serializer.errors, status = status.HTTP_400_BAD_REQUEST)


    def delete(self,request,pk):
        try:
            user = User.objects.get(email = request.user)
        except User.DoesNotExist:
            content = {'detail': 'No such user exists'}
            return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)
        try:
            inventory_list = Inventory.objects.get(id=pk)
        except Inventory.DoesNotExist:
            content = {'detail': 'No such Ingredient available for this recipe'}
            return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)
        inventory_list.delete()
        return JsonResponse("Successfully deleted!",safe = False, status = status.HTTP_202_ACCEPTED)
