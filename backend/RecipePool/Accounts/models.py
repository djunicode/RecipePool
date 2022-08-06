from django.db import models

from django.contrib.auth.models import (AbstractBaseUser, BaseUserManager)
from rest_framework_simplejwt.tokens import RefreshToken

from django.conf import settings
from Recipe.models import Ingredient

# Create your models here.

def upload_path_handler(instance, filename):
    return "images/products/{title}/{file}".format(
        title=instance.user, file=filename
    )

class UserManager(BaseUserManager):

    def create_superuser(self, email, firstname, lastname, password=None, is_admin=True, is_staff=True):
        if not email:
            raise ValueError('Users must have an email address')

        user = self.model(
            email=self.normalize_email(email),
        )

        user.set_password(password)
        user.firstname = firstname
        user.lastname = lastname
        user.admin = is_admin
        user.staff = is_staff
        user.is_active = True
        user.save(using=self._db)
        return user
        
    def create_staffuser(self, email, firstname, lastname, password):
        user = self.create_superuser(
            email,
            firstname,
            lastname,
            password=password
        )
        user.admin = False
        user.save(using=self._db)
        return user

    def create_user(self, email, firstname, lastname, password,phone_number,gender,DOB, image, role):
        user = self.create_superuser(
            email,
            firstname,
            lastname,
            password=password
        )
        user.phone_number=phone_number
        user.gender=gender
        user.DOB=DOB
        user.staff = False
        user.admin = False
        user.is_active = False
        user.role = role
        user.image = image
        user.save(using=self._db)
        return user

AUTH_PROVIDERS = {'facebook':'facebook', 'google':'google', 'twitter':'twitter', 'email':'email'}

GENDER = (
    ('--', '--'),
    ('Male', 'Male'),
    ('Female', 'Female'),
)

def upload_path_handler(instance, filename):
    return "images/profile/{label}/{file}".format(
        label=instance.firstname + '_' + instance.lastname, file=filename
    )

class User(AbstractBaseUser):
    email = models.EmailField(
        verbose_name='email address',
        max_length=255,
        unique=True,
    )
    firstname         = models.CharField(max_length=60)
    lastname          = models.CharField(max_length=60)
    phone_number      = models.DecimalField(max_digits = 10, decimal_places = 0,null = True,blank=True)
    role              = models.CharField(max_length=200, null = True, blank = True)
    image             = models.ImageField(upload_to = upload_path_handler,null = True, blank = True)
    gender            = models.CharField(max_length = 100,choices = GENDER, default = '--')
    DOB               = models.DateField(null=True,blank=True)
    is_active         = models.BooleanField(default=False)
    staff             = models.BooleanField(default=False)
    admin             = models.BooleanField(default=False)
    auth_provider     = models.CharField(max_length = 255, blank = False, null = False, default=AUTH_PROVIDERS.get('email'))
    

    USERNAME_FIELD = 'email'

    REQUIRED_FIELDS = ['firstname', 'lastname']

    objects = UserManager()

    def __str__(self):
        return self.email

    def has_perm(self, perm, obj=None):
        return True

    def has_module_perms(self, app_label):
        return True

    @property
    def is_staff(self):
        return self.staff

    @property
    def is_admin(self):
        return self.admin

    def tokens(self):
        refresh = RefreshToken.for_user(self)
        return {
            'refresh': str(refresh),
            'access': str(refresh.access_token)
        }

class Inventory(models.Model):
    user            = models.ForeignKey(settings.AUTH_USER_MODEL,on_delete = models.CASCADE, related_name='user_inventory')
    ingredient      = models.ForeignKey(Ingredient,on_delete=models.CASCADE,related_name='inventory_ingredient')
    ingredient_name = models.CharField(max_length=255, blank=True, null=True)
    quantity        = models.FloatField(default = 1.0)

    class Meta:
        verbose_name_plural = 'Inventories'
    
    def __str__(self):
        return f"{self.ingredient} - {self.quantity}"


class ShoppingList(models.Model):
    user            = models.ForeignKey(settings.AUTH_USER_MODEL,on_delete = models.CASCADE, related_name='user_shopping_list')
    ingredient      = models.ForeignKey(Ingredient,on_delete=models.CASCADE,related_name='list_ingredient')
    quantity        = models.FloatField(default = 1.0)
    
    def __str__(self):
        return f"{self.ingredient} - {self.quantity}"