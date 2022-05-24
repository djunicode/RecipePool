from django.contrib import admin
from .models import Ingredient,IngredientList,Recipe,Likes,Favourite,Cuisine,RecipeSteps
# Register your models here.

class RecipeStepsInlineAdmin(admin.StackedInline):
    model = RecipeSteps
    extra = 1

class IngredientListInlineAdmin(admin.StackedInline):
    model = IngredientList
    extra = 1


admin.site.register(Ingredient)
@admin.register(Recipe)
class RecipeAdmin(admin.ModelAdmin):
    inlines = [IngredientListInlineAdmin, RecipeStepsInlineAdmin]
    
admin.site.register(IngredientList)
admin.site.register(RecipeSteps)
admin.site.register(Likes)
admin.site.register(Favourite)
admin.site.register(Cuisine)