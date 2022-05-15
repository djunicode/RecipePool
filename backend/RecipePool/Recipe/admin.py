from django.contrib import admin
from .models import Ingredient,IngredientList,Recipe,Likes,Favourite,Cuisine
# Register your models here.

class IngredientListInlineAdmin(admin.StackedInline):
    model = IngredientList
    extra = 1


admin.site.register(Ingredient)
admin.site.register(Recipe)
class RecipeAdmin(admin.ModelAdmin):
    inlines = [IngredientListInlineAdmin]
admin.site.register(IngredientList)
admin.site.register(Likes)
admin.site.register(Favourite)
admin.site.register(Cuisine)