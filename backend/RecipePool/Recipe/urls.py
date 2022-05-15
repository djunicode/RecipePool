from django.urls import path
from . import views

urlpatterns = [
    path('test/',views.StoreRecipes,name = "Store Recipes"),
    #path('search-or/',views.RecipeSearchOR.as_view(),name = "search-or"),
    path('filter-ingredient/',views.filterIngredients,name = "filter-ingredient"),
    path('filter-fridge/',views.filterFridge,name = "filter-fridge"),
    path('filter-cuisine/',views.filterCuisneType,name = "filter-cuisine"),
    path('recipe/<str:pk>/',views.RecipeView.as_view(), name = "Recipe"),
    path('cuisine/<str:pk>/',views.CuisineView.as_view(), name = "Cuisine"),
    path('ingredient-list/<int:pk>/', views.IngredientListView.as_view(),name = "Ingredient-List"),
    path('filter-meal/',views.filterMealType,name = "filter-meal"),
    #path('search-and/',views.RecipeSearchAND.as_view(),name = "search-and"),
    path('trending/',views.TrendingRecipes.as_view(),name = "Trending Recipes"),
    path('trending-cuisine/',views.TrendingCuisines.as_view(),name = "Trending Cuisines"),
    path('favourite/',views.FavouriteRecipes.as_view(),name = "Favourite"),
]