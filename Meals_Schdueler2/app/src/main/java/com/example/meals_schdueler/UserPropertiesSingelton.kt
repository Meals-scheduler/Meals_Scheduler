package com.example.meals_schdueler

import android.util.Log
import java.util.*
import kotlin.collections.ArrayList

class UserPropertiesSingelton {



    companion object {
        private var userproperties: UserPropertiesSingelton? =
            null

        fun getInstance(): UserPropertiesSingelton? {
            if (userproperties == null) {
                userproperties =
                    UserPropertiesSingelton()

            }
            return userproperties
        }


    }

    var userIngredients : ArrayList<Ingredient>? = null
    var userRecipes : ArrayList<Recipe>? = null


    fun logout_setNULL() {
        userproperties = null
    }

    fun getUserIngredientss(): ArrayList<Ingredient>? {
        if (userIngredients == null) userIngredients =  ArrayList<Ingredient>()
        return userIngredients
    }

    fun setUserIngredientss(userIngredients:  ArrayList<Ingredient>?){
        this.userIngredients = userIngredients
    }

    fun getUserRecipess(): ArrayList<Recipe>? {
        if (userRecipes == null) userRecipes =  ArrayList<Recipe>()
        return userRecipes
    }

    fun setUserRecipess(userIngredients:  ArrayList<Recipe>?){
        this.userRecipes = userIngredients
    }
}