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

    var userIngredients: TreeMap<String,Ingredient>? = null
    var userRecipes: HashMap<String, Recipe>? = null
    var dailySchedule: TreeMap<String, DailySchedule>? = null
    var weeklyScheudle: TreeMap<String, WeeklySchedule>? = null
    var monthlyScheudle: TreeMap<String, MonthlySchedule>? = null
    var yearlyScheudle: TreeMap<String, YearlySchedule>? = null
    var userMapRecipes: HashMap<Int, Recipe>? = null
    var userUpcomingSchedule: ArrayList<UpComingScheudule>? = null


    fun logout_setNULL() {
        userproperties = null
    }

    fun getUserUpcomingSchedulee(): ArrayList<UpComingScheudule>? {
        if (userUpcomingSchedule == null) userUpcomingSchedule = ArrayList()
        return userUpcomingSchedule
    }

    fun setUserUpcomingSchedulee(upComingScheudule: ArrayList<UpComingScheudule>?) {
        this.userUpcomingSchedule = upComingScheudule

    }

    fun getUserMapRecipe(): HashMap<Int, Recipe>? {
        if (userMapRecipes == null) userMapRecipes = HashMap()
        return userMapRecipes
    }

    fun setUserMapRecipe(userMapRecipe: HashMap<Int, Recipe>?) {
        this.userMapRecipes = userMapRecipe
    }

    fun getUserIngredientss(): TreeMap<String,Ingredient>? {
        if (userIngredients == null) userIngredients = TreeMap<String,Ingredient>()
        return userIngredients
    }

    fun setUserIngredientss(userIngredients: TreeMap<String,Ingredient>?) {
        this.userIngredients = userIngredients
    }

    fun getUserRecipess(): HashMap<String, Recipe>? {
        if (userRecipes == null) userRecipes = HashMap<String, Recipe>()
        return userRecipes
    }

    fun setUserRecipess(userIngredients: HashMap<String, Recipe>?) {
        this.userRecipes = userIngredients
    }


    fun getUserDaily(): TreeMap<String, DailySchedule>? {
        if (dailySchedule == null) dailySchedule = TreeMap<String, DailySchedule>()
        return dailySchedule
    }


    fun setUserDaily(dailySchedule: TreeMap<String, DailySchedule>?) {
        this.dailySchedule = dailySchedule
    }


    fun getUserWeekly(): TreeMap<String, WeeklySchedule>? {
        if (weeklyScheudle == null) weeklyScheudle = TreeMap<String, WeeklySchedule>()
        return weeklyScheudle
    }

    fun setUserWeekly(weeklyScheudle: TreeMap<String, WeeklySchedule>?) {
        this.weeklyScheudle = weeklyScheudle
    }

    fun getUserMonthly(): TreeMap<String, MonthlySchedule>? {
        if (monthlyScheudle == null) monthlyScheudle = TreeMap<String, MonthlySchedule>()
        return monthlyScheudle
    }

    fun setUserMonthly(monthlyScheudle: TreeMap<String, MonthlySchedule>?) {
        this.monthlyScheudle = monthlyScheudle
    }


    fun getUserYearly(): TreeMap<String, YearlySchedule>? {
        if (yearlyScheudle == null) yearlyScheudle = TreeMap<String, YearlySchedule>()
        return yearlyScheudle
    }

    fun setUserYearly(yearlyScheudle: TreeMap<String, YearlySchedule>?) {
        this.yearlyScheudle = yearlyScheudle
    }
}