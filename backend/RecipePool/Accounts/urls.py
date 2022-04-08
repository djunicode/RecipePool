from django.urls import path
from .views import SignUp, Login, VerifyEmail, RequestPasswordResetEmail, SetNewPasswordAPIView, PasswordTokenCheckAPI, LogoutAPIView
from rest_framework_simplejwt.views import TokenRefreshView

urlpatterns = [
    path('signup/', SignUp.as_view(), name='Signup'),
    path('email-verify/', VerifyEmail.as_view(), name="EmailVerification"),
    path('login/', Login.as_view(), name="Login"),
    path('logout/', LogoutAPIView.as_view(), name="logout"),
    path('token-refresh/',TokenRefreshView.as_view(),name="RefreshToken"),
    path('request-reset-email/', RequestPasswordResetEmail.as_view(),
         name="request-reset-email"),
    path('password-reset/<str:uidb64>/<str:token>/',
         PasswordTokenCheckAPI.as_view(), name='password-reset-confirm'),
    path('password-reset-complete', SetNewPasswordAPIView.as_view(),
         name='password-reset-complete')
]