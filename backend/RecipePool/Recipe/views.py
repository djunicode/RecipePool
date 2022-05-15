from django.http import JsonResponse
import requests
from rest_framework import generics
from rest_framework.decorators import api_view, permission_classes
from .serializers import CuisineSerializer, FavouriteSerializer, IngredientListSerializer, RecipeSerializer
from rest_framework.views import APIView
from django.http import JsonResponse
from rest_framework import status
from django.contrib.auth import get_user_model
from rest_framework.permissions import IsAuthenticated
from .models import Cuisine, Favourite, Ingredient, IngredientList, Recipe
from .serializers import CuisineSerializer, FavouriteSerializer, RecipeSerializer
from Accounts.models import Inventory

User = get_user_model()
'''
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
        return recipe'''

class RecipeView(APIView):
    serializer_class = RecipeSerializer
    permission_classes = [IsAuthenticated]

    def get(self, request, pk):
        try:
            user = User.objects.get(email = request.user)
        except User.DoesNotExist:
            content = {'detail': 'No such user exists'}
            return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)
        if pk == '0':
            recipe = Recipe.objects.filter(createdBy = user)
        else:
            try:
                recipe = Recipe.objects.get(id=pk)
            except Recipe.DoesNotExist:
                content = {'detail': 'No such Recipe'}
                return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)
            try:
                recipe = Recipe.objects.get(id=int(pk),createdBy = user)
            except Recipe.DoesNotExist:
                content = {'detail': 'No such Recipe by this user'}
                return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)
            examDetails = RecipeSerializer(recipe, many=False)
            return JsonResponse(examDetails.data,status = status.HTTP_200_OK)
        examDetails = RecipeSerializer(recipe, many=True)
        return JsonResponse(examDetails.data, safe=False,status = status.HTTP_200_OK)


    def post(self, request, pk):
        try:
            user = User.objects.get(email = request.user)
        except User.DoesNotExist:
            content = {'detail': 'No such user exists'}
            return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)
        print(user)
        serializer = RecipeSerializer(data=request.data)
        if serializer.is_valid():
            serializer = serializer.create(user)
            examDetails = RecipeSerializer(serializer, many=False)
            return JsonResponse(examDetails.data, status = status.HTTP_202_ACCEPTED)
        return JsonResponse(serializer.errors, status = status.HTTP_400_BAD_REQUEST)

    def patch(self,request,pk):
        try:
            user = User.objects.get(email = request.user)
        except User.DoesNotExist:
            content = {'detail': 'No such user exists'}
            return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)
        try:
            recipe = Recipe.objects.get(id = pk)
        except Recipe.DoesNotExist:
            content = {'detail': 'No such Recipe available'}
            return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)
        try:
            recipe = Recipe.objects.get(id = pk, createdBy = user)
        except Recipe.DoesNotExist:
            content = {'detail': 'No such Recipe created by this user'}
            return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)
        serializer = RecipeSerializer(instance = recipe, data=request.data, partial = True)
        if serializer.is_valid():
            user_product = serializer.update(recipe,request.data)
            user_product.save()
            return JsonResponse(serializer.data, status = status.HTTP_202_ACCEPTED)
        return JsonResponse(serializer.errors, status = status.HTTP_400_BAD_REQUEST)

    def delete(self,request,pk):
        try:
            user = User.objects.get(email = request.user)
        except User.DoesNotExist:
            content = {'detail': 'No such user exists'}
            return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)
        try:
            recipe = Recipe.objects.get(id = pk)
        except Recipe.DoesNotExist:
            content = {'detail': 'No such Recipe available'}
            return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)
        try:
            recipe = Recipe.objects.get(id = pk, createdBy = user)
        except Recipe.DoesNotExist:
            content = {'detail': 'No such Recipe created by this user'}
            return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)
        recipe.delete()
        return JsonResponse({'Response': 'Recipe succsesfully delete!'},status = status.HTTP_200_OK)


class IngredientListView(APIView):
    def post(self, request, pk):
        try:
            user = User.objects.get(email = request.user)
        except User.DoesNotExist:
            content = {'detail': 'No such user exists'}
            return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)
        try:
            recipe = Recipe.objects.get(id = pk)
        except Recipe.DoesNotExist:
            content = {'detail': 'No such user exists'}
            return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)
        try:
            recipe = Recipe.objects.get(id = pk, createdBy = user)
        except Recipe.DoesNotExist:
            content = {'detail': 'No such Recipe created by this user'}
        if request.data.get('ingredient',False) != False:
            try:
                ing_list = IngredientList.objects.get(recipe = recipe, ingredient=request.data['ingredient'])
            except IngredientList.DoesNotExist:
                list = IngredientList(recipe=recipe)
                serializer = IngredientListSerializer(list,data = request.data)
                if serializer.is_valid():
                    serializer.save()
                    ing_list = IngredientList.objects.filter(recipe = recipe)
                    examDetails = IngredientListSerializer(ing_list, many=True)
                    return JsonResponse(examDetails.data,safe = False, status = status.HTTP_202_ACCEPTED)
                return JsonResponse(serializer.errors, status = status.HTTP_400_BAD_REQUEST)
            content = {'detail': 'Ingredient already for this recipe, edit it in put method'}
            return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)
        else:
            content = {'detail': 'ingredient : [This is a required field]'}
            return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)


    def put(self,request,pk):
        try:
            user = User.objects.get(email = request.user)
        except User.DoesNotExist:
            content = {'detail': 'No such user exists'}
            return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)
        try:
            ing_list = IngredientList.objects.get(id=pk)
        except IngredientList.DoesNotExist:
            content = {'detail': 'No such Ingredient available for this recipe'}
            return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)
        replace = ing_list.ingredient
        serializer = IngredientListSerializer(instance = ing_list, data=request.data, partial = True)
        if serializer.is_valid():
            serializer = serializer.save()
            serializer.ingredient = replace
            serializer.save()
            ing_list = IngredientList.objects.filter(recipe = ing_list.recipe)
            examDetails = IngredientListSerializer(ing_list, many=True)
            return JsonResponse(examDetails.data,safe = False, status = status.HTTP_202_ACCEPTED)
        return JsonResponse(serializer.errors, status = status.HTTP_400_BAD_REQUEST)


    def delete(self,request,pk):
        try:
            user = User.objects.get(email = request.user)
        except User.DoesNotExist:
            content = {'detail': 'No such user exists'}
            return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)
        try:
            ing_list = IngredientList.objects.get(id=pk)
        except IngredientList.DoesNotExist:
            content = {'detail': 'No such Ingredient available for this recipe'}
            return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)
        ing_list.delete()
        ing_list = IngredientList.objects.filter(recipe = ing_list.recipe)
        examDetails = IngredientListSerializer(ing_list, many=True)
        return JsonResponse(examDetails.data,safe = False, status = status.HTTP_202_ACCEPTED)


class CuisineView(APIView):
    serializer_class = CuisineSerializer

    def get(self, request, pk):
        #to show list of cuisine in dropdown list
        if pk == '0':
            cuisine = Cuisine.objects.all()
        #get a particular cuisine
        else:
            try:
                cuisine = Cuisine.objects.get(cuisine_name=pk)
            except Cuisine.DoesNotExist:
                content = {'detail': 'No such Cuisine exists'}
                return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)
            cuisine = CuisineSerializer(cuisine, many=False)
            return JsonResponse(cuisine.data,status = status.HTTP_200_OK)
        cuisine = CuisineSerializer(cuisine, many=True)
        return JsonResponse(cuisine.data, safe=False,status = status.HTTP_200_OK)


    def post(self, request, pk):
        #if user wants to create a new cuisine
        try:
            cuisine = Cuisine.objects.get(cuisine_name=request.data['cuisine_name'])
        except Cuisine.DoesNotExist:   
            serializer = CuisineSerializer(data=request.data)
            if serializer.is_valid():
                serializer.save()
                return JsonResponse(serializer.data, status = status.HTTP_202_ACCEPTED)
            return JsonResponse(serializer.errors, status = status.HTTP_400_BAD_REQUEST) 
        cuisinereturn = CuisineSerializer(cuisine, many=False)
        return JsonResponse(cuisinereturn.data,status = status.HTTP_200_OK)



#Query the database for Recipe using given ingredients
@api_view(['POST'])
def filterIngredients(request):
    ingredient_query = request.data['ingredient']
    ingredient = Ingredient.objects.filter(name__in = ingredient_query) #Filter ingredients present in query list
    ingredient_list = IngredientList.objects.filter(ingredient__in = ingredient) #Filter Ingredient Lists with the given query ingredients 
    recipe = Recipe.objects.filter(ingredient_list__in = ingredient_list).distinct() #List the recipes related to the ingredients in the ingredient list
    serializer = RecipeSerializer(recipe, many=True)
    return JsonResponse(serializer.data, safe = False)

#Query the database for Recipe using user's inventory
@api_view(['GET'])
@permission_classes([IsAuthenticated])
def filterFridge(request):
    ingredient_query = Inventory.objects.filter(user=request.user)
    ingredient = Ingredient.objects.filter(inventory_ingredient__in = ingredient_query) #Filter ingredients present in query list
    ingredient_list = IngredientList.objects.filter(ingredient__in = ingredient) #Filter Ingredient Lists with the given query ingredients 
    recipe = Recipe.objects.filter(ingredient_list__in = ingredient_list).distinct() #List the recipes related to the ingredients in the ingredient list
    serializer = RecipeSerializer(recipe, many=True)
    return JsonResponse(serializer.data, safe = False)

#Query the database for Recipe using given cuisine type
@api_view(['POST'])
def filterCuisneType(request):
    cuisine_query = request.data['cuisine']
    cuisine_list = Cuisine.objects.filter(cuisine_name__in = cuisine_query) #Filter ingredients present in query list
    recipe = Recipe.objects.filter(cuisine__in = cuisine_list).distinct() #List the recipes related to the ingredients in the ingredient list
    serializer = RecipeSerializer(recipe, many=True)
    return JsonResponse(serializer.data, safe = False)


#Query the database for Recipe using given meal type
@api_view(['POST'])
def filterMealType(request):
    meal_query = request.data['meal']
    recipe = Recipe.objects.filter(mealType__in = meal_query).distinct() #List the recipes related to the ingredients in the ingredient list
    serializer = RecipeSerializer(recipe, many=True)
    return JsonResponse(serializer.data, safe = False)
    
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

def StoreRecipes(request):
    q = "coffee or croissant"
    health = "soy-free"
    url = f"https://api.edamam.com/api/recipes/v2"
    params = {"type": "public",
    "q":q,
    "app_id":"6dc0ee0f",
    "app_key": "e93f8556a7a6558a9a6557cee409937c",
    #"health":health,
    "field":["label","healthLabels"]}
    payload={}
    headers = {}

    response = requests.request("GET", url,params=params, headers=headers, data=payload)

    return JsonResponse(response.json(), safe = False)