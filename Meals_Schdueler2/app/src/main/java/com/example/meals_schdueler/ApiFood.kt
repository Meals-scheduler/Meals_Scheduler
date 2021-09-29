package com.example.meals_schdueler

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.lang.Error
import java.net.HttpURLConnection
import java.net.URL


class ApiFood(id: Int, childFragmentManager: FragmentManager, context: Context) : Fragment(),
    GetAndPost {
    // private var fcid = 55005
    private var isRecipe = true
    private var isIngredient = false
    private var j = 0
    private var k = 0
    private var ingredientsIds: ArrayList<String> = ArrayList()
    private var ingredientList: ArrayList<Ingredient> = ArrayList()
    private var ingredientsQuantity: ArrayList<Float> = ArrayList()
    private var wantToUpload = false
    private var fcid = id
    private var childFragmentManager2 = childFragmentManager
    private var context2 = context
    private var reallyWantToUpload = false
    var totalcost = 0F
    var instructionsValue: HowToStroreValue? = null
    var portions = ""
    var typeOfMeal = ""
    var name = ""
    var image = ""
    //  private var instructiosBool = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  setContentView(R.layout.api_test)


        // for (i in 1..2) {
        startTask()


        // }


    }


    private fun almostUploadRecipe() {
        wantToUpload = true
        isRecipe = true
        startTask()

    }

    private fun uploadRecipe() {
        reallyWantToUpload = true
        isRecipe = true
        startTask()
    }

    fun startTask() {
        var s = AsynTaskNew(this, childFragmentManager2)
        s.execute()
    }


    override fun DoNetWorkOpreation(): String {
        var link = ""
        //  Log.v("Elad1", "FCID is " + fcid)
        if (isRecipe) {
            Log.v("Sivan", "here is recipe")
            link =
                " https://api.spoonacular.com/recipes/" + fcid + "/information?apiKey=bd6b560e6d934e2ebe9cd08f20c7ee42&includeNutrition=true"
            // fcid += 1
        } else if (isIngredient) {
            Log.v("Sivan", "here is ingredient")
            link =

                "https://api.spoonacular.com/food/ingredients/" + ingredientsIds.get(j++) + "/information?amount=1&apiKey=bd6b560e6d934e2ebe9cd08f20c7ee42"
        } else {
            link =
                "https://api.spoonacular.com/recipes/" + fcid + "/analyzedInstructions?apiKey=bd6b560e6d934e2ebe9cd08f20c7ee42"

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
            Log.v("Sivan", "first here talking ing ids")
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
                if (!ingredientsIds.contains(id) && !(id.equals("null")))
                    ingredientsIds.add(id)
                k = ingredientsIds.size
//            description = jsonSecondary.getString("description")
                Log.v("Elad1", "id " + id)

                //var myObj = JSONObject(num)
                //var name2 = myObj.getString("name")
                //  Log.v("Elad1","NAME " + name2)

            }
            isIngredient = true
            for (i in ingredientsIds) {
                Log.v("Sivan", "second taking ingredients info")
                startTask()

            }


            // isRecipe = true

        } else if (isRecipe && wantToUpload) {

            val jsonObject = JSONObject(str)
            name = jsonObject.getString("title")
            portions = jsonObject.getString("servings")
            // val totalcost = jsonObject.getString("pricePerServing")
             image = jsonObject.getString("image")
            val vegetarian = jsonObject.getString("vegetarian")
            val vegan = jsonObject.getString("vegan")
            val dairyFree = jsonObject.getString("dairyFree")
            typeOfMeal = ""
            if (dairyFree.equals("true") && vegetarian.equals("false")) {
                typeOfMeal = "Meat"
            } else if (dairyFree.equals("true") && vegan.equals("true")) {
                typeOfMeal = "Parve"
            } else if (dairyFree.equals("false")) {
                typeOfMeal = "Dairy"
            }
            name = name.replace('\'', ' ')


            val ingredients = jsonObject.getString("extendedIngredients")
            var instructions = jsonObject.get("summary").toString()

          //  instructions = instructions.replace('\'', ' ')

            val arr = JSONArray(ingredients)

            for (i in 0 until arr.length()) {
                val jsonSecondary = arr.getJSONObject(i)
                var amount = jsonSecondary.getString("amount")
                var unit = ""


                try {
                    unit = jsonSecondary.getString("unit")
                } catch (e: Error) {
                    try {
                        unit = jsonSecondary.getString("unitShort")
                    } catch (e: Error) {
                        unit = ""
                    }

                }


                var floatGrams = 1F

                when (unit.toString()) {

                    "cups", "cup" -> floatGrams = (120.0F) * amount.toFloat()
                    "tsps", "tsp" -> floatGrams = (4.2F) * amount.toFloat()
                    "ml" -> floatGrams = amount.toFloat()
                    "Tbsps", "Tbsp", "Tb" -> floatGrams = (14.3F) * amount.toFloat()
                    "meduim" -> floatGrams = (20.0F) * amount.toFloat()
                    "small" -> floatGrams = (10.0F) * amount.toFloat()
                    "large", "serving" , "servings" -> floatGrams = (30.0F) * amount.toFloat()
                    "", "stick", "Slice","slices","slice" -> floatGrams = (20.0F) * amount.toFloat()


                }


                ingredientsQuantity.add(floatGrams)


            }

            totalcost = calculateCost()


            isIngredient = false
            isRecipe = false
            startTask()


        } else if (!isRecipe && isIngredient) {

            var proteinAmount = 0F
            var carbsAmount = 0F
            var fatAmount = 0F


            val jsonObject = JSONObject(str)
            val id = jsonObject.getString("id")
            var name = jsonObject.getString("original")
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
            name = name.replace('\'', ' ')


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
                Log.v("Sivan", "Ingredients:::")
                Log.v("Sivan", id.toString())
                Log.v("Sivan", name)
                Log.v("Sivan", proteinAmount.toString())
                Log.v("Sivan", carbsAmount.toString())
                Log.v("Sivan", fatAmount.toString())
                Log.v("Sivan", value.toString())

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
                var s = AsynTaskNew(ing, childFragmentManager2)
                s.execute()

                Log.v("Sivan", "K Size " + k)
                if (k == 0) {
                    Log.v("Sivan", "third trying to upload recipe")
                    isIngredient = false
                    almostUploadRecipe()
                }
            }
//
//        Log.v("Elad1","Name of the recipe " + name)
//        Log.v("Elad1","Ingredients " + ingredient)

        } else {
            var instructions = ""
            val arr = JSONArray(str)


            for (i in 0 until arr.length()) {
                val jsonSecondary = arr.getJSONObject(i)
                var nameOfStep = jsonSecondary.getString("name")
                if (nameOfStep.equals("")) {
                    nameOfStep = name
                }
                Log.v("Sivan", "Step name " + name)
                instructions += name + ":\n"

                val arr2 = JSONArray(jsonSecondary.getString("steps"))
                var stepNum = 1
                for (j in 0 until arr2.length()) {
                    val jsonSecondary2 = arr2.getJSONObject(j)
                    var step = jsonSecondary2.getString("step")
                    Log.v("Sivan", "Step is " + step)
                    instructions+= "" + stepNum +") " +step
                    stepNum++
                }
            }

            instructionsValue = HowToStroreValue(instructions)
            var bitmap: Bitmap? = null
            lifecycleScope.launch {
                var bitmap = getBitMap(image)

                Log.v("Sivan", "Recipes:::")
                Log.v("Sivan", fcid.toString())
                Log.v("Sivan", name)
                Log.v("Sivan", typeOfMeal.toString())
                Log.v("Sivan", portions.toString())
                Log.v("Sivan", totalcost.toString())
                Log.v("Sivan", ingredientList.toString())
                Log.v("Sivan", ingredientsQuantity.toString())


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
                    ingredientsQuantity,
                    instructionsValue!!
                )
                //  fcid++
                var s = AsynTaskNew(recipe, childFragmentManager2)
                s.execute()
            }
        }

    }


    private suspend fun getBitMap(imageToUpload: String): Bitmap {

        var loading: ImageLoader = ImageLoader(context2)
        var request = ImageRequest.Builder(context2)
            .data(imageToUpload).build()

        var result = (loading.execute(request) as SuccessResult).drawable
        return (result as BitmapDrawable).bitmap


    }

    fun calculateCost(): Float {
        var calc: Float = 0f
        var j = 0

        for (i in ingredientList!!) {


            var cur = ingredientsQuantity!!.get(j) * i.costPerGram.toFloat() / 100

            calc += cur
            j++
        }

        return calc
    }

}