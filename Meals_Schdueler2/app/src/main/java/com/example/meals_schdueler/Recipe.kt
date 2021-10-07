package com.example.meals_schdueler

import android.graphics.Bitmap
import android.util.Log
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class Recipe(

    recipeId: Int,
    ownerId: Int,
    recipeName: String,
    pictureBitMap: Bitmap,
    typeOfMeal: String,
    numOfPortions: String,
    // instructions: String,
    totalCost: Double,
    shareRecipe: Boolean,
    shareInfo: Boolean,
    // isUpdate: Boolean,
    listOfIngredients: ArrayList<Ingredient>,
    listOfQuantity: ArrayList<Float>,
    instructions : HowToStroreValue


    ) : GetAndPost {

    var builder: java.lang.StringBuilder? = null
    var recipeId = recipeId
    var typeOfMeal = typeOfMeal

    var pictureBitMap = pictureBitMap
    var picture: String = ""
    var recipeName = recipeName
    var numOfPortions = numOfPortions

    //var instructions = instructions
    var shareInfo = shareInfo
    var shareRecipe = shareRecipe
    var ownerId = ownerId
    var totalCost = totalCost
    var listOfIngredients = listOfIngredients
    var quantityList = listOfQuantity
    var ingredietnsID: String = ""
    var ingredietnsQuantity: String = ""
     var instructions = instructions


    fun encodePicture() {
        var image = ImageConvert(pictureBitMap)
        picture = image.BitMapToString().toString()


    }

    override fun DoNetWorkOpreation(): String {

        // making arr of ingreditns ID to send
        for (i in listOfIngredients) {
            ingredietnsID += "" + i.ingredientID + " "

        }
        for (i in quantityList) {

            ingredietnsQuantity += "" + i + " "
        }

        var input = ""
        encodePicture()
        // if we insert a new ingredient and not updating
        //if (!isUpdate) {
        recipeId = getRecipe().toInt() + 1 // getting current RecipeID first
        // }

        // ingredientID = 1

        if (recipeId != -1)
            input = postData() // now we upload the current ingredient details.

        return input

    }

    private fun postData(): String {

        return try {

            // values go to - Ingredient Table
            var link = "https://elad1.000webhostapp.com/postRecipe.php"
//            if (isUpdate){
//                link = "https://elad1.000webhostapp.com/updateIngredient.php?ingredientID="+ingredientID
//
//            }

            if(instructions.howToStore.equals("")){
                instructions.howToStore = "no data"
            }
            // print here ingredient elemtnes
            var data = URLEncoder.encode("RecipeID", "UTF-8") + "=" +
                    URLEncoder.encode(recipeId.toString(), "UTF-8")
            data += "&" + URLEncoder.encode("OwnerID", "UTF-8") + "=" +
                    URLEncoder.encode(ownerId.toString(), "UTF-8")
            data += "&" + URLEncoder.encode("RecipeName", "UTF-8") + "=" +
                    URLEncoder.encode(recipeName, "UTF-8")
            data += "&" + URLEncoder.encode("Picture", "UTF-8") + "=" +
                    URLEncoder.encode(picture, "UTF-8")

            data += "&" + URLEncoder.encode("TypeOfMeal", "UTF-8") + "=" +
                    URLEncoder.encode(typeOfMeal, "UTF-8")
            data += "&" + URLEncoder.encode("Portions", "UTF-8") + "=" +
                    URLEncoder.encode(numOfPortions.toString(), "UTF-8")
            data += "&" + URLEncoder.encode("TotalCost", "UTF-8") + "=" +
                    URLEncoder.encode(totalCost.toString(), "UTF-8")


            // now 2 values for another table -  Share_Ingredient_Table

            data += "&" + URLEncoder.encode("ShareInfo", "UTF-8") + "=" +
                    URLEncoder.encode(shareInfo.toString(), "UTF-8")
            data += "&" + URLEncoder.encode("ShareRecipe", "UTF-8") + "=" +
                    URLEncoder.encode(shareRecipe.toString(), "UTF-8")

            // 4 values for another table - Nutritious_Table


            data += "&" + URLEncoder.encode("ingredientsArrID", "UTF-8") + "=" +
                    URLEncoder.encode(ingredietnsID, "UTF-8")
            data += "&" + URLEncoder.encode("QuantityList", "UTF-8") + "=" +
                    URLEncoder.encode(ingredietnsQuantity, "UTF-8")
            data += "&" + URLEncoder.encode("instructions", "UTF-8") + "=" +
                    URLEncoder.encode(instructions.howToStore, "UTF-8")



            val url = URL(link)
            val conn = url.openConnection()
            conn.readTimeout = 10000
            conn.connectTimeout = 15000
            conn.doOutput = true
            val wr = OutputStreamWriter(conn.getOutputStream())
            wr.write(data)
            wr.flush()
            val reader = BufferedReader(InputStreamReader(conn.getInputStream()))
            builder = StringBuilder()
            var line: String? = null

            // Read Server Response
            while (reader.readLine().also { line = it } != null) {
                builder!!.append(line)
                break
            }

            builder.toString()

        } catch (e: Exception) {

        }.toString()

    }

    private fun getRecipe(): String {
        val link = "https://elad1.000webhostapp.com/getRecipeID.php"


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

    override fun getData(str: String) {
        print("DD")
    }


}