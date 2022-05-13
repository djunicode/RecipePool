from django.urls import path
from . import views

urlpatterns = [
    path('search-or/',views.RecipeSearchOR.as_view(),name = "search-or"),
    path('search-and/',views.RecipeSearchAND.as_view(),name = "search-or"),
    path('trending/',views.TrendingRecipes.as_view(),name = "Trending Recipes"),
    path('trending-cuisine/',views.TrendingCuisines.as_view(),name = "Trending Cuisines"),
    path('favourite/',views.FavouriteRecipes.as_view(),name = "Favourite"),
    path('recipe/<str:pk>/',views.RecipeView.as_view(), name = "Recipe"),
    path('cuisine/<str:pk>/',views.CuisineView.as_view(), name = "Cuisine"),
    path('ingredient-list/<int:pk>/', views.IngredientListView.as_view(),name = "Ingredient-List")
]