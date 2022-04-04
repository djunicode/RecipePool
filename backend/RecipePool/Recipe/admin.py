from django.contrib import admin
from .models import Ingredient,IngredientList,Recipe,Likes,Favourite
# Register your models here.
admin.site.register(Ingredient)
admin.site.register(Recipe)
admin.site.register(IngredientList)
admin.site.register(Likes)
admin.site.register(Favourite)