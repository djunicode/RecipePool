from rest_framework import serializers
from .models import Cuisine, Ingredient,IngredientList,Recipe,Likes,Favourite

class IngredientSerializer(serializers.ModelSerializer):

    class Meta:
        model = Ingredient
        fields = '__all__'


class IngredientListSerializer(serializers.ModelSerializer):

    class Meta:
        model = IngredientList
        fields = '__all__'

class CuisineSerializer(serializers.ModelSerializer):

    class Meta:
        model = Cuisine
        fields = '__all__'

class RecipeSerializer(serializers.ModelSerializer):
    ingredient_list = IngredientListSerializer(many=True)
    class Meta:
        model = Recipe
        fields = [
            'id',
            'cuisine',
            'createdBy',         
            'label',             
            'instructions',      
            'totalTime',         
            'url',               
            'image',             
            'healthLabels',      
            'totalNutrients',    
            'calories',          
            'cuisineType',       
            'mealType',          
            'dishType',          
            'likes',             
            'missingIngredients',
            'ingredient_list',
        ]
    def to_representation(self, instance):
        response = super().to_representation(instance)
        response['cuisine'] = CuisineSerializer(instance.cuisine).data
        return response

    def create(self,user):
        ingredient_list_data = self.validated_data.pop('ingredient_list')
        recipe = Recipe.objects.create(**self.validated_data, createdBy = user)
        for data in ingredient_list_data:
            IngredientList.objects.create(recipe=recipe, **data)
        return recipe

    def update(self, instance, validated_data):
        # ingredient_list_data = validated_data.pop('ingredient_list')
        # ingredients = (instance.ingredient_list).all()
        # ingredients = list(ingredients)
        try:
            user_cuisine = Cuisine.objects.get(cuisine_name=validated_data.get('cuisine',False))
        except Cuisine.DoesNotExist:
            user_cuisine = None
        try:
            cuisine = Cuisine.objects.get(cuisine_name=instance.cuisine)
        except Cuisine.DoesNotExist:
            cuisine = None
            # content = {'detail': 'No such Cuisine exists'}
            print(validated_data.get('createdBy'))
            # return JsonResponse(content, status = status.HTTP_404_NOT_FOUND)
        instance.cuisine            = validated_data.get(user_cuisine,cuisine)
        instance.createdBy          = validated_data.get('createdBy',instance.createdBy) 
        instance.label              = validated_data.get('label',instance.label) 
        instance.instructions       = validated_data.get('instructions',instance.instructions) 
        instance.totalTime          = validated_data.get('totalTime',instance.totalTime) 
        instance.url                = validated_data.get('url',instance.url) 
        instance.image              = validated_data.get('image',instance.image) 
        instance.healthLabels       = validated_data.get('healthLabels',instance.healthLabels) 
        instance.totalNutrients     = validated_data.get('totalNutrients',instance.totalNutrients) 
        instance.calories           = validated_data.get('calories',instance.calories) 
        instance.cuisineType        = validated_data.get('cuisineType',instance.cuisineType) 
        instance.mealType           = validated_data.get('mealType',instance.mealType) 
        instance.dishType           = validated_data.get('dishType',instance.dishType) 
        instance.likes              = validated_data.get('likes',instance.likes) 
        instance.missingIngredients = validated_data.get('missingIngredients',instance.missingIngredients) 
        instance.save()

        # for i in ingredient_list_data:
        #     try:
        #         user_ingredient =Ingredient.objects.get(id=i['ingredient'])
        #         print(user_ingredient)
        #     except Ingredient.DoesNotExist:
        #         user_ingredient = None
        #     ing = ingredients.pop(0)
        #     try:
        #         ingredient = Ingredient.objects.get(name=ing.ingredient)
        #     except Ingredient.DoesNotExist:
        #         ingredient = None
        #     ing.ingredient = i.get(user_ingredient, ingredient)
        #     ing.quantity = i.get('quantity', ing.quantity)
        #     ing.save()
        return instance


class LikesSerializer(serializers.ModelSerializer):

    class Meta:
        model = Likes
        fields = '__all__'


class FavouriteSerializer(serializers.ModelSerializer):

    class Meta:
        model = Favourite
        fields = '__all__'
