package com.example.meals_schdueler

import java.util.*

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
}