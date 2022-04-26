from rest_framework.exceptions import AuthenticationFailed
from .models import User
from django.contrib.auth import authenticate,get_user_model
import os, random
from decouple import config

from rest_framework_simplejwt.tokens import RefreshToken, TokenError
#makes sure all usernames are unique


User = get_user_model()


def generate_username(name):

    username = "".join(name.split(' ')).lower()
    if not User.objects.filter(username=username).exists():
        return username
    else:
        random_username = username + str(random.randint(0, 1000))
    return generate_username(random_username)



def register_social_user(provider, user_id, email, first_name,last_name):
    filtered_user_by_email = User.objects.filter(email = email)

    if filtered_user_by_email.exists():
        print(filtered_user_by_email[0].auth_provider)
        if provider == filtered_user_by_email[0].auth_provider:
            register_user = authenticate(email = email, password =config('SOCIAL_SECRET'))
            # tokens = RefreshToken.for_user(user=register_user)
            return {
                'firstname':first_name,
                'lastname':last_name,
                'email':register_user.email, 
                'tokens': register_user.tokens()
                }
        else:
            raise AuthenticationFailed(detail='Please continue your login using ' + filtered_user_by_email[0].auth_provider)  
    else:
        user = {
             'email':email,'password':config('SOCIAL_SECRET'),'firstname':first_name,"lastname":last_name
            }
        user = User.objects.create_user(**user) 
        user.is_active = True
        user.auth_provider = provider
        # user.set_password(config('SOCIAL_SECRET'))
        user.save()

        new_user = authenticate(email = email, password = config('SOCIAL_SECRET'))
        # tokens = RefreshToken.for_user(user=new_user)
        return {
            # 'username': new_user.username, 
            'firstname':first_name,
            'lastname':last_name,
            'email':new_user.email, 
            'tokens': new_user.tokens()
            }