from django.db import models
from django.conf import settings


def upload_path_handler(instance, filename):
    return "images/ingredients/{name}/{file}".format(
        name=instance.name, file=filename
    )

class Ingredient(models.Model):
    name        = models.CharField(max_length = 50)
    image       = models.ImageField(upload_to = upload_path_handler)

    def __str__(self):
        return self.name

def upload_path_handler(instance, filename):
    return "images/recipes/{label}/{file}".format(
        label=instance.label, file=filename
    )


class Cuisine(models.Model):
    cuisine_name    = models.CharField(max_length=50,primary_key=True)
    image           = models.ImageField(upload_to = upload_path_handler,blank=True,null=True)
    likes           = models.PositiveIntegerField(default = 0)

    def __str__(self):
        return self.cuisine_name


class Recipe(models.Model):
    cuisine            = models.ForeignKey(Cuisine,on_delete=models.CASCADE,related_name='recipe_cuisine',null = True, blank = True)
    createdBy          = models.ForeignKey(settings.AUTH_USER_MODEL,on_delete = models.CASCADE, related_name='recipe_user',null = True, blank = True)
    label              = models.CharField(max_length=50)
    instructions       = models.TextField(max_length=255)
    totalTime          = models.TimeField()
    url                = models.URLField(null=True)
    image              = models.ImageField(upload_to = upload_path_handler,null = True, blank = True)
    healthLabels       = models.CharField(max_length=100)
    totalNutrients     = models.CharField(max_length=100)
    calories           = models.IntegerField()
    cuisineType        = models.CharField(max_length=50)
    mealType           = models.CharField(max_length=50)
    dishType           = models.CharField(max_length=50)
    likes              = models.PositiveIntegerField(default = 0)
    missingIngredients = models.CharField(max_length=100, null = True, blank = True)

    def __str__(self):
        return self.label

class IngredientList(models.Model):
    recipe          = models.ForeignKey(Recipe,on_delete=models.CASCADE,related_name='ingredient_list',null=True,blank=True)
    ingredient      = models.ForeignKey(Ingredient,on_delete=models.CASCADE,related_name='recipe_ingredient')
    quantity        = models.FloatField(default = 1.0)

    def __str__(self):
        return f"{self.ingredient} - {self.quantity}"

class Likes(models.Model):
    user            = models.ForeignKey(settings.AUTH_USER_MODEL,on_delete = models.CASCADE, related_name='user_likes')
    recipe          = models.ForeignKey(Recipe,on_delete=models.CASCADE,related_name='recipe_likes')

    class Meta:
        verbose_name_plural = 'Likes'

    def __str__(self):
        return f"{self.recipe} by {self.user}"

class Favourite(models.Model):
    user            = models.ForeignKey(settings.AUTH_USER_MODEL,on_delete = models.CASCADE, related_name='user_fav')
    recipe          = models.ForeignKey(Recipe,on_delete=models.CASCADE,related_name='recipe_fav')

    def __str__(self):
        return f"{self.recipe} for {self.user}"


def upload_path_handler(instance, filename):
    return "images/cuisines/{label}/{file}".format(
        label=instance.label, file=filename
    )

    

#To be done later
'''
class DietLog(models.Model):
    user            = models.ForeignKey(settings.AUTH_USER_MODEL,on_delete = models.CASCADE, related_name='user_log')
    recipe          = models.ForeignKey(Recipe,on_delete=models.CASCADE,related_name='recipe_log')
    date            = models.DateField(auto_now_add=True)
    calories        = models.FloatField(default = 0)
    cholestrol      = models.FloatField(default = 0)
    fat             = models.FloatField(default = 0)
    sugar           = models.FloatField(default = 0)
    proteins        = models.FloatField(default = 0)

    def __str__(self):
        return f"{self.user} on {self.date}"
'''