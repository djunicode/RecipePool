import json
from urllib.error import HTTPError
import django
from django.http import JsonResponse
import requests
from rest_framework import generics
from rest_framework.decorators import api_view, permission_classes
from .serializers import CuisineSerializer, FavouriteSerializer, IngredientListSerializer, RecipeSerializer,IngredientSerializer,RecipeStepsSerializer
from rest_framework.views import APIView
from django.http import JsonResponse
from rest_framework import status
from django.contrib.auth import get_user_model
from rest_framework.permissions import IsAuthenticated
from .models import Cuisine, Favourite, Ingredient, IngredientList, Likes, Recipe, RecipeSteps
from .serializers import CuisineSerializer, FavouriteSerializer, RecipeSerializer
from Accounts.models import Inventory
from rest_framework.response import Response

import datetime
import urllib
from urllib.parse import urlparse
import urllib.request
from bs4 import BeautifulSoup
from django.core.files import File
from django.core.files.temp import NamedTemporaryFile
from decouple import config

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
    permission_classes = [IsAuthenticated,]

    def get(self, request, pk):
        try:
            user = User.objects.get(email = request.user)
        except User.DoesNotExist:
            content = {'detail': 'No such user exists'}
            return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)
        if pk == '0':
            try:
                recipe = Recipe.objects.filter(createdBy = user)
            except Recipe.DoesNotExist:
                content = {'detail': 'No recipes created by this user'}
                return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)
        else:
            try:
                recipe = Recipe.objects.get(id=pk)
            except Recipe.DoesNotExist:
                content = {'detail': 'No such Recipe'}
                return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)
            recipeDetails = RecipeSerializer(recipe, many=False, context={'request': request})
            return JsonResponse(recipeDetails.data,status = status.HTTP_200_OK)
        recipeDetails = RecipeSerializer(recipe, many=True, context={'request': request})
        return JsonResponse(recipeDetails.data, safe=False,status = status.HTTP_200_OK)


    def post(self, request, pk):
        try:
            user = User.objects.get(email = request.user)
        except User.DoesNotExist:
            content = {'detail': 'No such user exists'}
            return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)
        serializer = RecipeSerializer(data=request.data)
        if serializer.is_valid():
            serializer = serializer.create(user)
            recipeDetails = RecipeSerializer(serializer, many=False)
            return JsonResponse(recipeDetails.data, status = status.HTTP_202_ACCEPTED)
        return JsonResponse(serializer.errors, status = status.HTTP_400_BAD_REQUEST)

    def patch(self,request,pk):
        try:
            user = User.objects.get(email = request.user)
        except User.DoesNotExist:
            content = {'detail': 'No such user exists'}
            return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)
        try:
            recipe = Recipe.objects.get(id = pk)
            if recipe.createdBy != self.request.user:
                obj, created = Likes.objects.get_or_create(user=self.request.user,recipe=recipe)
                if created == True:
                    recipe.likes += 1

                    cuisine_data_dict = {}
                    recipe.cuisine.likes += 1
                    cuisine_data_dict['likes'] = recipe.cuisine.likes
                    cuisine = Cuisine.objects.get(pk = recipe.cuisine.pk)
                    serializer = CuisineSerializer(instance = cuisine, data=cuisine_data_dict, partial = True)
                    if serializer.is_valid():
                        serializer.save()
                else:
                    obj.delete()
                    recipe.likes -= 1
                    cuisine_data_dict = {}
                    recipe.cuisine.likes -= 1
                    cuisine_data_dict['likes'] = recipe.cuisine.likes
                    cuisine = Cuisine.objects.get(pk = recipe.cuisine.pk)
                    serializer = CuisineSerializer(instance = cuisine, data=cuisine_data_dict, partial = True)
                    if serializer.is_valid():
                        serializer.save()
            
            else:
                try:
                    recipe = Recipe.objects.get(id = pk, createdBy = user)
                except Recipe.DoesNotExist:
                    content = {'detail': 'No such Recipe created by this user'}
                    return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)
                    
        except Recipe.DoesNotExist:
            content = {'detail': 'No such Recipe available'}
            return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)
        
        serializer = RecipeSerializer(instance = recipe, data=request.data, partial = True)
        if serializer.is_valid():
            user_product = serializer.update(recipe,request.data)
            # user_product.save()
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
        return JsonResponse({'Response': 'Recipe successfully deleted!'},status = status.HTTP_200_OK)


class IngredientListView(APIView):
    serializer_class = IngredientListSerializer
    permission_classes = [IsAuthenticated,]
    def post(self, request, pk):
        try:
            user = User.objects.get(email = request.user)
        except User.DoesNotExist:
            content = {'detail': 'No such user exists'}
            return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)
        try:
            recipe = Recipe.objects.get(id = pk)
        except Recipe.DoesNotExist:
            content = {'detail': 'No such Recipe exists'}
            return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)
        try:
            recipe = Recipe.objects.get(id = pk, createdBy = user)
        except Recipe.DoesNotExist:
            content = {'detail': 'No such Recipe created by this user'}

        try:
            ingredient = Ingredient.objects.get(name__contains = request.data['name'])
        except Ingredient.DoesNotExist:
            ingredient = Ingredient.objects.create(name = request.data['name'])
        try:
            ing_list = IngredientList.objects.get(recipe = recipe, ingredient=ingredient)
        except IngredientList.DoesNotExist:
            IngredientList.objects.create(recipe=recipe,ingredient=ingredient, **request.data)
            ing_list = IngredientList.objects.filter(recipe = recipe)
            recipeDetails = IngredientListSerializer(ing_list, many=True)
            return JsonResponse(recipeDetails.data,safe = False, status = status.HTTP_202_ACCEPTED)
        content = {'detail': 'Ingredient already for this recipe, edit it in put method'}
        return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)
        # if request.data.get('ingredient',False) != False:
        #     try:
        #         ing_list = IngredientList.objects.get(recipe = recipe, ingredient=request.data['ingredient'])
        #     except IngredientList.DoesNotExist:
        #         list = IngredientList(recipe=recipe)
        #         serializer = IngredientListSerializer(list,data = request.data)
        #         if serializer.is_valid():
        #             serializer.save()
        #             ing_list = IngredientList.objects.filter(recipe = recipe)
        #             examDetails = IngredientListSerializer(ing_list, many=True)
        #             return JsonResponse(examDetails.data,safe = False, status = status.HTTP_202_ACCEPTED)
        #         return JsonResponse(serializer.errors, status = status.HTTP_400_BAD_REQUEST)
        #     content = {'detail': 'Ingredient already for this recipe, edit it in put method'}
        #     return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)
        # else:
        #     content = {'ingredient' : '[This is a required field]'}
        #     return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)


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
        replace_name = ing_list.name
        serializer = IngredientListSerializer(instance = ing_list, data=request.data, partial = True)
        if serializer.is_valid():
            serializer = serializer.save()
            serializer.ingredient = replace
            serializer.name = replace_name
            serializer.save()
            ing_list = IngredientList.objects.filter(recipe = ing_list.recipe)
            recipeDetails = IngredientListSerializer(ing_list, many=True)
            return JsonResponse(recipeDetails.data,safe = False, status = status.HTTP_202_ACCEPTED)
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
        recipeDetails = IngredientListSerializer(ing_list, many=True)
        return JsonResponse(recipeDetails.data,safe = False, status = status.HTTP_202_ACCEPTED)



class RecipeStepsView(APIView):

    serializer_class = RecipeStepsSerializer
    permission_classes = [IsAuthenticated,]
    def post(self, request, pk):
        try:
            user = User.objects.get(email = request.user)
        except User.DoesNotExist:
            content = {'detail': 'No such user exists'}
            return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)
        try:
            recipe = Recipe.objects.get(id = pk)
        except Recipe.DoesNotExist:
            content = {'detail': 'No such Recipe exists'}
            return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)
        try:
            recipe = Recipe.objects.get(id = pk, createdBy = user)
        except Recipe.DoesNotExist:
            content = {'detail': 'No such Recipe created by this user'}
        recipe_step = RecipeSteps(recipe = recipe)
        serializer = RecipeStepsSerializer(recipe_step,data=request.data)
        if serializer.is_valid():
            serializer.save()
            step_list = RecipeSteps.objects.filter(recipe = recipe)
            RecipeStepsDetails = RecipeStepsSerializer(step_list, many=True)
            return JsonResponse(RecipeStepsDetails.data,safe=False, status = status.HTTP_202_ACCEPTED)
        return JsonResponse(serializer.errors, status = status.HTTP_400_BAD_REQUEST) 


    def put(self,request,pk):
        try:
            user = User.objects.get(email = request.user)
        except User.DoesNotExist:
            content = {'detail': 'No such user exists'}
            return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)
        try:
            step_list = RecipeSteps.objects.get(id=pk)
        except RecipeSteps.DoesNotExist:
            content = {'detail': 'No such RecipeSteps available for this recipe'}
            return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)
        serializer =RecipeStepsSerializer(instance = step_list, data=request.data, partial = True)
        if serializer.is_valid():
            serializer = serializer.save()
            step_list = RecipeSteps.objects.filter(recipe = step_list.recipe)
            RecipeStepsDetails = RecipeStepsSerializer(step_list, many=True)
            return JsonResponse(RecipeStepsDetails.data,safe = False, status = status.HTTP_202_ACCEPTED)
        return JsonResponse(serializer.errors, status = status.HTTP_400_BAD_REQUEST)


    def delete(self,request,pk):
        try:
            user = User.objects.get(email = request.user)
        except User.DoesNotExist:
            content = {'detail': 'No such user exists'}
            return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)
        try:
            step_list = RecipeSteps.objects.get(id=pk)
        except RecipeSteps.DoesNotExist:
            content = {'detail': 'No such RecipeSteps available for this recipe'}
            return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)
        step_list.delete()
        step_list = RecipeSteps.objects.filter(recipe = step_list.recipe)
        RecipeStepsDetails = RecipeStepsSerializer(step_list, many=True)
        return JsonResponse(RecipeStepsDetails.data,safe = False, status = status.HTTP_202_ACCEPTED)



class CuisineView(APIView):
    serializer_class = CuisineSerializer
    permission_classes = [IsAuthenticated,]
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
        if request.data.get('cuisine_name',False) != False:
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
        else:
            content = {'cuisine_name' : '[This is a required field]'}
            return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)



class IngredientView(APIView):
    serializer_class = IngredientSerializer
    permission_classes = [IsAuthenticated,]
    def get(self, request, pk):
        #to show list of cuisine in dropdown list
        if pk == '0':
            ingredient = Ingredient.objects.all()
        #get a particular cuisine
        else:
            try:
                ingredient = Ingredient.objects.get(id=pk)
            except Ingredient.DoesNotExist:
                content = {'detail': 'No such Ingredient exists'}
                return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)
            ingredient = IngredientSerializer(ingredient, many=False)
            return JsonResponse(ingredient.data,status = status.HTTP_200_OK)
        ingredient = IngredientSerializer(ingredient, many=True)
        return JsonResponse(ingredient.data, safe=False,status = status.HTTP_200_OK)


    def post(self, request, pk):
        #if user wants to create a new cuisine
        if request.data.get('name',False) != False:
            try:
                ingredient = Ingredient.objects.get(name=request.data['name'])
            except Ingredient.DoesNotExist:   
                serializer = IngredientSerializer(data=request.data)
                if serializer.is_valid():
                    serializer.save()
                    return JsonResponse(serializer.data, status = status.HTTP_202_ACCEPTED)
                return JsonResponse(serializer.errors, status = status.HTTP_400_BAD_REQUEST) 
            ingredientreturn = IngredientSerializer(ingredient, many=False)
            return JsonResponse(ingredientreturn.data,status = status.HTTP_200_OK)
        else:
            content = {'name' : '[This is a required field]','image' : '[This is a required field]'}
            return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)


class SearchView(APIView):
    serializer_class = RecipeSerializer

    def post(self,request):
        recipe_query = request.data['recipe']
        filters = request.data['filters']
        keywords = recipe_query.split()
        edamam = StoreRecipes(" or ".join(map(str.lower,keywords)))
        recipes = []
        for word in keywords:
            recipe = Recipe.objects.filter(label__icontains = word)
            if "Under 30 mins" in filters:
                recipe = recipe.filter(totalTime__lte = datetime.time(minute = 30))
            if "Under 20 mins" in filters:
                recipe = recipe.filter(totalTime__lte = datetime.time(minute = 20))
            if "Veg" in filters:
                recipe = recipe.filter(healthLabels__icontains = "Vegetarian")
            if "Sweet" in filters:
                recipe = recipe.filter(totalNutrients__icontains = "sugar")
            recipes += recipe
        serializer = self.serializer_class(recipes, many = True)
        return JsonResponse(serializer.data, status = status.HTTP_200_OK, safe = False)


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
def filterCuisineType(request):
    cuisine_query = request.data['cuisine']
    cuisine_list = Cuisine.objects.filter(cuisine_name__in = cuisine_query) #Filter ingredients present in query list
    recipe = Recipe.objects.filter(cuisine__in = cuisine_list).distinct() #List the recipes related to the ingredients in the ingredient list
    serializer = RecipeSerializer(recipe, many=True)
    return JsonResponse(serializer.data, safe = False)


#Query the database for Recipe using given meal type
@api_view(['POST'])
def filterMealType(request):
    meal_query = request.data['meal']
    recipe_list = []
    if 'lunch' in meal_query or 'dinner' in meal_query:
        serializer = RecipeSerializer(Recipe.objects.filter(mealType = 'lunch/dinner').distinct(), many=True)
        recipe_list += serializer.data
    elif 'dessert' in meal_query:
        serializer = RecipeSerializer(Recipe.objects.filter(dishType = 'desserts').distinct(), many=True)
        recipe_list += serializer.data
    recipe = Recipe.objects.filter(mealType__in = meal_query).distinct() #List the recipes related to the ingredients in the ingredient list
    serializer = RecipeSerializer(recipe, many=True)
    recipe_list += serializer.data
    return JsonResponse(recipe_list, safe = False)
    
class TrendingRecipes(generics.ListAPIView):
    """
    Returns the first 12 recipes with highest rating
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

class FavouriteRecipes(generics.GenericAPIView):
    """
    To create and return the favourite recipes of current user
    """
    permission_classes = [IsAuthenticated,]
    serializer_class = FavouriteSerializer

    def get(self,request):
        favourites = Favourite.objects.filter(user=self.request.user).values('recipe')
        favourite_recipes =  Recipe.objects.filter(id__in = favourites)
        serializer = RecipeSerializer(favourite_recipes , many=True)
        return Response(serializer.data)
    
    def post(self,request):
        recipe_id = self.request.data['recipe']
        recipe = Recipe.objects.get(id=recipe_id)
        serializer = FavouriteSerializer(data = request.data)
        if serializer.is_valid(raise_exception=True):
            try:
                serializer.save(user = self.request.user,recipe = recipe)
            except django.db.utils.IntegrityError:
                return JsonResponse({'Response': "Recipe already marked favourite!"})

        return JsonResponse(serializer.data, status = status.HTTP_202_ACCEPTED)


    def delete(self,request):
        pk = self.request.data['pk']
        try:
            favourite_recipe = Recipe.objects.get(id=pk)
            favourite_obj = Favourite.objects.get(recipe = favourite_recipe)
            favourite_obj.delete()
        except Recipe.DoesNotExist:
            content = {'detail': 'No such Recipe marked as favourite by this user'}
            return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)
        return JsonResponse({'Response': 'Recipe is now removed from favourites!'},status = status.HTTP_200_OK)


def StoreRecipes(q):
    #q = "coffee or croissant"
    #health = "soy-free"
    print(q)
    url = f"https://api.edamam.com/api/recipes/v2"
    params = {"type": "public",
    "q":q,
    "app_id": config("APP_ID"),
    "app_key": config("APP_KEY"),
    #"health":health,
    "field":["cuisineType","label","totalTime","url", "image", "healthLabels","totalNutrients","calories","mealType","dishType","ingredients"]
    # cuisine is a list, instructions not there, manage steps from url, Time is a float, img is url, healthLabels is a list, totalNut is a dict,
    # calories is a float, meal is a list '/' separated, dish has dessert, ing is a list of dicts
    }
    payload={}
    headers = {}

    response = requests.request("GET", url,params=params, headers=headers, data=payload)
    for hit in response.json()['hits']:
        recipe = hit['recipe']
        try:
            rec = Recipe.objects.get(label = recipe['label'])
            continue
        except:
            cuisineType = recipe['cuisineType'][0]
            cuisine = Cuisine.objects.get_or_create(cuisine_name = cuisineType)[0]
            totalNutrient = ""
            ingredient_lists = []
            steps_list = []

            try:
                req = urllib.request.Request(url=recipe['url'], headers ={'User-Agent': 'Mozilla / 5.0 (X11 Linux x86_64) AppleWebKit / 537.36 (KHTML, like Gecko) Chrome / 52.0.2743.116 Safari / 537.36 PostmanRuntime/7.29.0'})
                response = urllib.request.urlopen(req)
                html_doc = response.read()
                soup = BeautifulSoup(html_doc, 'html.parser')
                json_object = soup.find(type="application/ld+json")
                instruction_dict = {}
                if json_object:
                    instruction_dict = json.loads(json_object.string)
                for instruction in instruction_dict:
                    ins = {}
                    find_key = instruction.get('recipeInstructions')
                    if find_key:
                        for item in find_key:
                            ins = {}
                            ins['steps'] = item['text']
                            steps_list.append(ins)
            except:
                pass

            for nutrient in recipe['totalNutrients']:
                string_nutrient = recipe['totalNutrients'][nutrient]['label'] + "-" + str(recipe['totalNutrients'][nutrient]['quantity']) + " " + recipe['totalNutrients'][nutrient]['unit'] + ", "
                totalNutrient += string_nutrient

            for ingredient in recipe['ingredients']:
                ing = {}
                ing['name'] = ingredient['food']
                ing['quantity'] = ingredient['quantity']
                ingredient_lists.append(ing)

            time = recipe['totalTime']
            minutes = int(time)
            hours = 0
            if minutes/60 >= 1:
                hours = int(minutes/60)
                minutes = minutes%60
            seconds = (time - int(time))*60

            try:
                name = urlparse(recipe['image']).path.split('/')[-1]
                img_temp = NamedTemporaryFile()
                req = urllib.request.Request(url = recipe['image'], headers= {'User-Agent': 'Mozilla / 5.0 (X11 Linux x86_64) AppleWebKit / 537.36 (KHTML, like Gecko) Chrome / 52.0.2743.116 Safari / 537.36 PostmanRuntime/7.29.0'})
                img_temp.write(urllib.request.urlopen(req).read())
                img_temp.flush()
            except:
                img_temp = None

            r_o  = {
            "cuisine" : cuisine,
            "label" : recipe['label'],
            "instructions" : "working around it",
            #"createdBy" : User.objects.get(email = "admin@gmail.com").id,
            "totalTime" : datetime.time(hour = hours, minute = minutes,second = int(seconds)),
            "url" : recipe['url'],
            "image" : None, #(File(open(content[0])), name),
            "healthLabels" : ", ".join(recipe['healthLabels']),
            "totalNutrients" : totalNutrient,
            "calories" : round(recipe['calories']),
            "cuisineType" : recipe['cuisineType'][0],
            "mealType" : recipe['mealType'][0],
            "dishType" : recipe['dishType'][0],
            "ingredient_list" : ingredient_lists,
            "steps_list" : steps_list
            }
            serializer = RecipeSerializer(data = r_o)
            if serializer.is_valid(raise_exception=True):
                serializer.create(User.objects.get(email = "admin@gmail.com"))
            recipe = Recipe.objects.get(label = recipe['label'])
            recipe.image.save(name, File(img_temp))
            recipe.save()
    #return JsonResponse(response.json(), safe = False)
    return 1
