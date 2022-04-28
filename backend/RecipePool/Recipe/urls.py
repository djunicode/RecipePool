from django.urls import path
from . import views

urlpatterns = [
    path('search-or/',views.RecipeSearchOR.as_view(),name = "search-or"),
    path('search-and/',views.RecipeSearchAND.as_view(),name = "search-or"),
    path('trending/',views.TrendingRecipes.as_view(),name = "Trending Recipes"),
]