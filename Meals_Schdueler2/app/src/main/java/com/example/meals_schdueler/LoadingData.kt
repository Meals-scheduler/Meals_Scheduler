package com.example.meals_schdueler

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.meals_schdueler.dummy.DailySchedule
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.HashMap


class LoadingData : AppCompatActivity(), GetAndPost {

    private var ingredientList: HashMap<String, Ingredient>? = null
    private var recipeList: HashMap<String, Recipe>? = null
    private var dailyList: HashMap<String, DailySchedule>? = null
    private var weeklyList: HashMap<String, WeeklySchedule>? = null
    private var monthlyList: HashMap<String, MonthlySchedule>? = null
    private var yearlyList: HashMap<String, YearlySchedule>? = null


    private var sortedIngredient: TreeMap<String, Ingredient>? = null
    private var sortedRecipe: TreeMap<String, Recipe>? = null
    private var sortedDaily: TreeMap<String, DailySchedule>? = null
    private var sortedWeekly: TreeMap<String, WeeklySchedule>? = null
    private var sortedMonthly: TreeMap<String, MonthlySchedule>? = null
    private var sortedYearly: TreeMap<String, YearlySchedule>? = null
    var j = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recipeList = HashMap()
        ingredientList = HashMap()
        dailyList = HashMap()
        weeklyList = HashMap()
        monthlyList = HashMap()
        yearlyList = HashMap()



        startTask()
    }

    fun startTask() {
//        for (i in 0..2) {
//            var s = AsynTaskNew(this, supportFragmentManager,)
//            s.execute()
//        }

    }

    override fun DoNetWorkOpreation(): String {
        var link = ""
        when (j) {
            0 -> link =
                "https://elad1.000webhostapp.com/getIngredient.php?ownerID=" + UserInterFace.userID
            1 -> link =
                "https://elad1.000webhostapp.com/getRecipe.php?ownerID=" + UserInterFace.userID;
            2 -> link =
                "https://elad1.000webhostapp.com/getDaily.php?ownerID=" + UserInterFace.userID;
            3 -> link =
                "https://elad1.000webhostapp.com/getWeekly.php?ownerID=" + UserInterFace.userID;
            4 -> link =
                "https://elad1.000webhostapp.com/getMonthly.php?ownerID=" + UserInterFace.userID;
            5 -> link =
                "https://elad1.000webhostapp.com/getYearly.php?ownerID=" + UserInterFace.userID;

        }
        j += 1

        val sb = StringBuilder()

        val url = URL(link)
        val urlConnection = url.openConnection() as HttpURLConnection
        try {
            val `in`: InputStream = BufferedInputStream(urlConnection.inputStream)
            val bin = BufferedReader(InputStreamReader(`in`))
            // temporary string to hold each line read from the reader.
            var inputLine: String?

            while (bin.readLine().also { inputLine = it } != null) {
                sb.append(inputLine)

            }
        } finally {
            // regardless of success or failure, we will disconnect from the URLConnection.
            urlConnection.disconnect()
        }


        //Log.v("Elad1", "Id came is" + sb.toString())
        return sb.toString()


    }

    override fun getData(str: String) {

        if (!str.equals("")) {

            if (j == 0) { // loading ingredients.
                ingredientList!!.clear()
                // fixed a default .split spaces , and fixed spaces in howToStore.
                // when we add an ingredient it doesnt update in real time. we have to re compile!!!

                val ingredients: Array<String> = str.splitIgnoreEmpty("***").toTypedArray()

                for (i in ingredients.indices) {

                    var ingredient2 = ingredients[i].splitIgnoreEmpty("*")



                    ingredientList?.put(
                        ingredient2[0],
                        Ingredient(
                            ingredient2[0].toInt(),
                            ingredient2[1].toInt(),
                            ingredient2[2],
                            ImageConvert.StringToBitMap(ingredient2[3].toString())!!,
                            ingredient2[4],
                            ingredient2[5],
                            ingredient2[6],
                            ingredient2[7].toBoolean(),
                            ingredient2[8].toBoolean(),
                            ingredient2[9].toFloat(),
                            ingredient2[10].toFloat(),
                            ingredient2[11].toFloat(),
                            ingredient2[12],
                            ingredient2[13],
                            false

                        )
                    )


                }
                sortedIngredient!!.clear()
                sortedIngredient!!.putAll(ingredientList!!)


                // initializing the singelton with the user's ingredients list to keep it here on code.
                // should do it on another place !!!
                UserPropertiesSingelton.getInstance()!!.setUserIngredientss(sortedIngredient)


            }


        } else if (j == 1) {

            recipeList!!.clear()
            val recipesAndIngredients: Array<String> = str.splitIgnoreEmpty("***").toTypedArray()
            var recipesAndIngredients2 = recipesAndIngredients[0].splitIgnoreEmpty("*")


            var currentID = recipesAndIngredients2[0].toInt()

            // saving all the ingredietns and quantities of each Recipe in map.
            // first we extract the ids and quantity into map.
            // then we use another map to convert all ids to real ingredients.

            var hashMap: HashMap<Int, Pair<ArrayList<String>, ArrayList<Float>>> =
                HashMap()

            var map: HashMap<Int, ArrayList<Ingredient>> = HashMap()


            var quantities: ArrayList<Float> = ArrayList()
            var ids: ArrayList<String> = ArrayList()
            var ingredientList2: ArrayList<Ingredient> = ArrayList()
            //  var quantities: ArrayList<String> = ArrayList()
            var ingID = "-1"
            for (i in recipesAndIngredients.indices) {
                var recipesAndIngredients2 = recipesAndIngredients[i].splitIgnoreEmpty("*")

                if (recipesAndIngredients2.size == 11) {

                    if (recipesAndIngredients2[0].toInt() != currentID) {

                        // copying the lists of the ids and aquantities
                        // var tmpIds = deepCopy(ids)
                        //var tmpQuantities = deepCopy(quantities)
                        // put it into the map with the Key of the RecipeID
                        hashMap.put(currentID, Pair(ids, quantities))
                        quantities = ArrayList()
                        ids = ArrayList()

                        // copy the ingredietns lists to map with key of RecipieID
                        // var tmplist = deepCopyIng(ingredientList2)
                        map.put(currentID, ingredientList2)
                        ingredientList2 = ArrayList()
                        // switching to next recipe id
                        currentID = recipesAndIngredients2[0].toInt()


                        // clear all lists for the next recipe lists
                        // ingredientList2.clear()
                        // ids.clear()
                        // quantities.clear()
                    }
                    if (ingID != recipesAndIngredients2[9]) {
                        ids.add(recipesAndIngredients2[9])
                        quantities.add(recipesAndIngredients2[10].toFloat())
                        // getting the specific ingredient by its ID
                        ingredientList2.add(
                            UserPropertiesSingelton.getInstance()!!.getUserIngredientss()
                                ?.get(recipesAndIngredients2[9])!!
                        )
                        ingID = recipesAndIngredients2[9]
                    }


                } else {
                    break
                }
            }
            hashMap.put(currentID, Pair(ids, quantities))
            map.put(currentID, ingredientList2)


            currentID = -1
//            recipesAndIngredients2 = recipesAndIngredients[0].splitIgnoreEmpty("*")
//            currentID = recipesAndIngredients2[0].toInt()
            var recipeMap = HashMap<Int, Recipe>()
            var j = 0
            for (i in recipesAndIngredients.indices) {

                var recipe2 = recipesAndIngredients[i].splitIgnoreEmpty("*")
                if (recipe2.size == 11) {
                    var s = recipe2[0].toInt()
                    if (s != currentID) {
                        var instructions = HowToStroreValue(recipe2[9])
                        recipeList?.put(
                            recipe2[0],
                            Recipe(
                                recipe2[0].toInt(),
                                recipe2[1].toInt(),
                                recipe2[2],
                                ImageConvert.StringToBitMap(recipe2[3].toString())!!,
                                recipe2[4],
                                recipe2[5],
                                recipe2[6].toDouble(),
                                recipe2[7].toBoolean(),
                                recipe2[8].toBoolean(),
                                map.get(recipe2[0].toInt())!!,
                                hashMap.get(recipe2[0].toFloat())!!.second,
                                instructions

                            )
                        )
                        recipeMap.put(recipe2[0].toInt(), recipeList!!.get(recipe2[0])!!)
                        currentID = recipe2[0].toInt()

                    }
                    // a map to save each recipe by its ID

                }
            }


            // initializing the singelton with the user's Recipes list to keep it here on code.
            // should do it on another place !!!
            UserPropertiesSingelton.getInstance()!!.setUserRecipess(recipeList)
            UserPropertiesSingelton.getInstance()!!.setUserMapRecipe(recipeMap)



        }


    }

    fun CharSequence.splitIgnoreEmpty(vararg delimiters: String): List<String> {
        return this.split(*delimiters).filter {
            it.isNotEmpty()
        }
    }
}