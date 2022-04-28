from google.auth.transport import requests
from google.oauth2 import id_token
from rest_framework.response import Response

class Google:
    """to get user info and return it"""
    def validate(auth_token):
        try:
            idinfo = id_token.verify_oauth2_token(auth_token, requests.Request())
            if 'accounts.google.com' in idinfo['iss']:
                return idinfo
        except:
            return Response("the token is either invalid or has expired")