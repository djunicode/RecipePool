from rest_framework import serializers
from django.contrib.auth import get_user_model
from django.contrib import auth
from rest_framework.exceptions import AuthenticationFailed
from rest_framework_simplejwt.tokens import RefreshToken, TokenError
# for reseting password
from django.contrib.auth.tokens import PasswordResetTokenGenerator
from django.utils.encoding import force_str, smart_bytes
from django.utils.http import urlsafe_base64_decode, urlsafe_base64_encode
from django.urls import reverse

from .models import ShoppingList, Inventory
from .utils import Util
from django.conf import settings




User = get_user_model()


class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ['email', 'password', 'firstname', 'lastname','phone_number','gender','DOB']
       
    def save_user(self, validated_data):
        user = User.objects.create_user( 
                                password=validated_data.get('password'), 
                                email=validated_data.get('email'),
                                firstname=validated_data.get('firstname'),
                                lastname=validated_data.get('lastname'),
                                phone_number=validated_data.get('phone_number'),
                                gender=validated_data.get('gender'),
                                DOB=validated_data.get('DOB')
                                )
        user.save()
        return user


class EmailVerificationSerializer(serializers.ModelSerializer):
    token = serializers.CharField(max_length=666)

    class Meta:
        model = User
        fields = ['token']


class LoginSerializer(serializers.ModelSerializer):
    email = serializers.EmailField(max_length=255, min_length=3)
    password = serializers.CharField(max_length=68, min_length=3)
    tokens = serializers.CharField(max_length=68, min_length=8, read_only=True)

    class Meta:
        model=User
        fields = ['email', 'password', 'tokens']

    def validate(self, attrs):

        email = attrs.get('email','')
        password = attrs.get('password', '')

        filtered_user_by_email = User.objects.filter(email=email)
        auth_user = auth.authenticate(email=email, password=password)

        if not auth_user:
            raise AuthenticationFailed("Invalid credentials, try again")
        if not auth_user.is_active:
            raise AuthenticationFailed("Email not verified yet!!")

        tokens = RefreshToken.for_user(user=auth_user)
        return {
            'email': auth_user.email,
            'name': (auth_user.firstname +" "+ auth_user.lastname),
            'refresh': str(tokens),
            'access': str(tokens.access_token)
        }


class ResetPasswordEmailRequestSerializer(serializers.Serializer):
    email = serializers.EmailField(min_length=2)

    redirect_url = serializers.CharField(max_length=500, required=False)

    class Meta:
        fields = ['email']


class SetNewPasswordSerializer(serializers.Serializer):
    password = serializers.CharField(
        min_length=6, max_length=68, write_only=True)
    token = serializers.CharField(
        min_length=1, write_only=True)
    uidb64 = serializers.CharField(      #uidb64 is the user’s id encoded in base 64.
        min_length=1, write_only=True)   #Encoding is done so that the data remains intact without modification.

    class Meta:
        fields = ['password', 'token', 'uidb64']

    def validate(self, attrs):
        try:
            password = attrs.get('password')
            token = attrs.get('token')
            uidb64 = attrs.get('uidb64')

            id = force_str(urlsafe_base64_decode(uidb64)) #Returns a str object representing decoded uidb64.
            user = User.objects.get(id=id)
            if not PasswordResetTokenGenerator().check_token(user, token):
                raise AuthenticationFailed('The reset link is invalid', 401)

            user.set_password(password)
            user.save()

            return (user)
        except Exception as e:
            raise AuthenticationFailed('The reset link is invalid', 401)
        return super().validate(attrs)


class LogoutSerializer(serializers.Serializer):
    refresh = serializers.CharField()

    default_error_message = {
        'bad_token': ('Token is expired or invalid')
    }

    def validate(self, attrs):
        self.token = attrs['refresh']
        return attrs

    def save(self, **kwargs):

        try:
            RefreshToken(self.token).blacklist()

        except TokenError:
            self.fail('bad_token')


from . import google
from .register import register_social_user
import os
from rest_framework.exceptions import AuthenticationFailed
from decouple import config

class GoogleSocialAuthSerializer(serializers.Serializer):
    auth_token = serializers.CharField()

    def validate_auth_token(self, auth_token):
        user_data = google.Google.validate(auth_token)
        try:
            user_data['sub']
        except:
            raise serializers.ValidationError('The token is invalid or expired. Please login again')
        
        if user_data['aud'] != config('GOOGLE_CLIENT_ID'):
            raise AuthenticationFailed('oops, who are you?')
        
        user_id = user_data['sub']
        email = user_data['email']
        first_name = user_data['given_name']
        last_name = user_data['family_name']
        provider = 'google'

        return register_social_user(provider = provider, user_id = user_id, email = email, first_name=first_name,last_name=last_name)



class InventorySerializer(serializers.ModelSerializer):

    class Meta:
        model = Inventory
        fields = '__all__'


class ShoppingListSerializer(serializers.ModelSerializer):

    class Meta:
        model = ShoppingList
        fields = '__all__'