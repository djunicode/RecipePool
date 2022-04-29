from rest_framework import generics
from rest_framework.response import Response
from rest_framework.permissions import IsAuthenticated
from .models import Cuisine, Favourite, Ingredient, IngredientList, Recipe
from .serializers import CuisineSerializer, FavouriteSerializer, RecipeSerializer

# Create your views here.

#Query the database for Recipe using given ingredients with OR condition 
class RecipeSearchOR(generics.ListAPIView):
    serializer_class = RecipeSerializer

    def get_queryset(self):
        ingredient_query = self.request.query_params.get('q',None).lower().split('|') #Extract the query string and insert the ingredients in a query list
        ingredient = Ingredient.objects.filter(name__in = ingredient_query)  #Filter ingredients present in query list
        ingredient_list = IngredientList.objects.filter(ingredient__in = ingredient) #Filter Ingredient Lists with the given query ingredients 
        recipe = Recipe.objects.filter(id__in = ingredient_list) #List the recipes related to the ingredients in the ingredient list
        return recipe

#Query the database for Recipe using given ingredients with AND condition
class RecipeSearchAND(generics.ListAPIView):
    serializer_class = RecipeSerializer

    def get_queryset(self):
        ingredient_query_list = self.request.query_params.get('q',None).lower().split('&') #Extract the query string and insert the ingredients in a query list
        query_ingredients = Ingredient.objects.filter(name__in = ingredient_query_list)  #Filter ingredients present in query list
        ingredient_list = IngredientList.objects.all()
        for query_ingredient in query_ingredients:
            ingredient_list = ingredient_list.objects.filter(ingredientÍ = query_ingredient) #Filter Ingredient Lists with the given query ingredients 
        recipe = Recipe.objects.filter(id__in = ingredient_list) #List the recipes related to the ingredients in the ingredient list
        return recipe
    
class TrendingRecipes(generics.ListAPIView):
    """
    Returns the first 5 recipes with highest rating
    """
    serializer_class = RecipeSerializer

    def get_queryset(self):
        trending_recipes = Recipe.objects.order_by('-likes')[:12]
        return trending_recipes

class TrendingCuisines(generics.ListAPIView):
    """
    Returns the cuisines in decreasing order of rating
    """
    serializer_class = CuisineSerializer

    def get_queryset(self):
        trending_cuisine = Cuisine.objects.order_by('-likes')
        return trending_cuisine

class FavouriteRecipes(generics.ListAPIView):
    """
    Returns the favourite recipes of current user
    """
    serializer_class = RecipeSerializer
    permission_classes = [IsAuthenticated,]

    def get_queryset(self):
        favourites = Favourite.objects.filter(user=self.request.user)
        list = []
        for item in favourites:
            list.append(Recipe.objects.get(id = item.recipe.id))
        return list