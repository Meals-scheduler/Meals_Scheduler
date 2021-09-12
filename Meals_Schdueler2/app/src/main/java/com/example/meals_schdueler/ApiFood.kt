package com.example.meals_schdueler

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL


class ApiFood : AppCompatActivity(), GetAndPost {
    private var fcid = 49504
    private var isRecipe = true
    private var j = 0
    private var k = 0
    private var ingredientsIds: ArrayList<String> = ArrayList()
    private var ingredientList: ArrayList<Ingredient> = ArrayList()
    private var ingredientsQuantity: ArrayList<Float> = ArrayList()
    private var wantToUpload = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.api_test)


        //  for (i in 1..5) {
        startTask()


        //  }


    }

    private fun uploadRecipe() {
        wantToUpload = true
        isRecipe = true
        startTask()

    }

    fun startTask() {
        var s = AsynTaskNew(this, supportFragmentManager)
        s.execute()
    }


    override fun DoNetWorkOpreation(): String {
        var link = ""
        //  Log.v("Elad1", "FCID is " + fcid)
        if (isRecipe) {
            Log.v("Sivan" ,"here is recipe")
            link =
                " https://api.spoonacular.com/recipes/" + fcid + "/information?apiKey=b25a553bb12141e8840687eeae933fe1&includeNutrition=true"
           // fcid += 1
        }

        else {
            Log.v("Sivan" ,"here is ingredient")
            link =

                "https://api.spoonacular.com/food/ingredients/" + ingredientsIds.get(j++) + "/information?amount=1&apiKey=b25a553bb12141e8840687eeae933fe1"
        }


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



        return sb.toString()
    }

    // to avoid empty string cells .split function returns.
    fun CharSequence.splitIgnoreEmpty(vararg delimiters: String): List<String> {
        return this.split(*delimiters).filter {
            it.isNotEmpty()
        }
    }

    override fun getData(str: String) {
        Log.v("Elad1", str)

        if (isRecipe && !wantToUpload) {
            Log.v("Sivan" ,"first here talking ing ids")
            isRecipe = false
            val jsonObject = JSONObject(str)
            val ingredients = jsonObject.getString("extendedIngredients")
//        var ingredient = jsonObject.getString("ingredients")
//        var foodNutrients = jsonObject.getString("foodNutrients")
//
            val arr = JSONArray(ingredients)
//
//
//        // now we want to get the texts as they are in JSON.
            for (i in 0 until arr.length()) {
                val jsonSecondary = arr.getJSONObject(i)
                // var id = ""
                // name , picture , amount
                var description = ""
                var id = jsonSecondary.getString("id")
                ingredientsIds.add(id)
                k = ingredientsIds.size
//            description = jsonSecondary.getString("description")
                Log.v("Elad1", "id " + id)

                //var myObj = JSONObject(num)
                //var name2 = myObj.getString("name")
                //  Log.v("Elad1","NAME " + name2)

            }
            for (i in ingredientsIds) {
                Log.v("Sivan" ,"second taking ingredients info")
                startTask()

            }


            // isRecipe = true

        } else if (isRecipe && wantToUpload) {

            val jsonObject = JSONObject(str)
            val name = jsonObject.getString("title")
            val portions = jsonObject.getString("servings")
            val totalcost = jsonObject.getString("pricePerServing")
            val image = jsonObject.getString("image")
            val vegetarian = jsonObject.getString("vegetarian")
            val vegan = jsonObject.getString("vegan")
            val dairyFree = jsonObject.getString("dairyFree")
            var typeOfMeal = ""
            if (dairyFree.equals("true") && vegetarian.equals("false")) {
                typeOfMeal = "Meat"
            } else if (dairyFree.equals("true") && vegan.equals("true")) {
                typeOfMeal = "Parve"
            } else if (dairyFree.equals("false")) {
                typeOfMeal = "Dairy"
            }

            val ingredients = jsonObject.getString("extendedIngredients")


            val arr = JSONArray(ingredients)

            for (i in 0 until arr.length()) {
                val jsonSecondary = arr.getJSONObject(i)

                var amount = jsonSecondary.getString("amount")
                ingredientsQuantity.add(amount.toFloat())


            }
            var bitmap: Bitmap? = null
            lifecycleScope.launch {
                var bitmap = getBitMap(image)

                Log.v("Sivan" ,"Recipes:::" )
                Log.v("Sivan" ,fcid.toString())
                Log.v("Sivan" ,name)
                Log.v("Sivan" ,typeOfMeal.toString())
                Log.v("Sivan" ,portions.toString())
                Log.v("Sivan" ,totalcost.toString())
                Log.v("Sivan" ,ingredientList.toString())
                Log.v("Sivan" ,ingredientsQuantity.toString())

                var recipe = Recipe(
                    fcid,
                    -1,
                    name,
                    bitmap,
                    typeOfMeal,
                    portions,
                    totalcost.toDouble(),
                    true,
                    true,
                    ingredientList,
                    ingredientsQuantity
                )

                var s = AsynTaskNew(recipe, supportFragmentManager)
                s.execute()
            }

        } else if (!isRecipe) {

            var proteinAmount = 0F
            var carbsAmount = 0F
            var fatAmount = 0F


            val jsonObject = JSONObject(str)
            val id = jsonObject.getString("id")
            val name = jsonObject.getString("original")
            Log.v("Elad1", "Ingredient Name " + name)
            val image = jsonObject.getString("image")
            val imageToUpload = "https://spoonacular.com/cdn/ingredients_250x250/" + image
            val cost = jsonObject.getString("estimatedCost")
            val jsonObject2 = JSONObject(cost)
            val value = jsonObject2.get("value")
            val nutrition = jsonObject.getString("nutrition")
            val jsonObjectNut = JSONObject(nutrition)
            val nutrients = jsonObjectNut.getString("nutrients")
            val arr = JSONArray(nutrients)


            for (i in 0 until arr.length()) {
                val jsonSecondary = arr.getJSONObject(i)

                var title = jsonSecondary.getString("title")
                if (title.equals("Protein")) {
                    proteinAmount = jsonSecondary.getString("amount").toFloat() // 0.05
                    Log.v("Elad1", "amount " + proteinAmount)
                } else if (title.equals("Fat")) {
                    fatAmount = jsonSecondary.getString("amount").toFloat() // 0.01
                    Log.v("Elad1", "amount " + fatAmount)
                } else if (title.equals("Carbohydrates")) { // 0.07
                    carbsAmount = jsonSecondary.getString("amount").toFloat()
                    Log.v("Elad1", "amount " + carbsAmount)
                }

                // Log.v("Elad1", "title " + title)
                // var myObj = JSONObject(title)


            }


            proteinAmount *= 100
            carbsAmount *= 100
            fatAmount *= 100

            Log.v("elad1", proteinAmount.toString())
            Log.v("elad1", carbsAmount.toString())
            Log.v("elad1", fatAmount.toString())


            var bitmap: Bitmap? = null
            lifecycleScope.launch {
                var bitmap = getBitMap(imageToUpload)


                //var imageBitmap = getBitmapFromURL(imageToUpload)
                Log.v("Sivan" ,"Ingredients:::" )
                Log.v("Sivan" ,id.toString())
                Log.v("Sivan" ,name)
                Log.v("Sivan" ,proteinAmount.toString())
                Log.v("Sivan" ,carbsAmount.toString())
                Log.v("Sivan" ,fatAmount.toString())
                Log.v("Sivan" ,value.toString())

                var ing = Ingredient(
                    id.toInt(),
                    -1,
                    name,
                    bitmap!!,
                    "Type",
                    "Seasson",
                    "How to Store",
                    true,
                    true,
                    proteinAmount,
                    carbsAmount,
                    fatAmount,
                    "nutritiousDes",
                    value.toString(),
                    false

                )
                ingredientList.add(ing)
                k--
                var s = AsynTaskNew(ing, supportFragmentManager)
                s.execute()

                Log.v("Sivan" ,"K Size " + k)
                if(k == 0 ){
                    Log.v("Sivan" ,"third trying to upload recipe")
                    uploadRecipe()
                }
            }
//
//        Log.v("Elad1","Name of the recipe " + name)
//        Log.v("Elad1","Ingredients " + ingredient)

        }
    }


    private suspend fun getBitMap(imageToUpload: String): Bitmap {

        var loading: ImageLoader = ImageLoader(this)
        var request = ImageRequest.Builder(this)
            .data(imageToUpload).build()

        var result = (loading.execute(request) as SuccessResult).drawable
        return (result as BitmapDrawable).bitmap


    }

}