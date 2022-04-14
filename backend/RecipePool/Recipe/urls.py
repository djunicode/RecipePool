from django.urls import path
from . import views

urlpatterns = [
    path('',views.RecipeSearchOR.as_view(),name = "search-or"),
]