package com.example.meals_schdueler

import android.util.Log
import com.example.meals_schdueler.dummy.DailySchedule
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

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
    var dailySchedule : ArrayList<DailySchedule>? = null
    var userMapRecipes : HashMap<Int,Recipe>? = null


    fun logout_setNULL() {
        userproperties = null
    }

    fun getUserMapRecipe(): HashMap<Int,Recipe>?{
        if(userMapRecipes == null) userMapRecipes = HashMap()
        return userMapRecipes
    }

    fun setUserMapRecipe(userMapRecipe : HashMap<Int,Recipe>?){
        this.userMapRecipes = userMapRecipe
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


    fun getUserDaily(): ArrayList<DailySchedule>? {
        if (dailySchedule == null) dailySchedule =  ArrayList<DailySchedule>()
        return dailySchedule
    }

    fun setUserDaily(dailySchedule:  ArrayList<DailySchedule>?){
        this.dailySchedule = dailySchedule
    }
}