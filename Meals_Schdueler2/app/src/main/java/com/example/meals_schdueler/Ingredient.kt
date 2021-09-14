package com.example.meals_schdueler

import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class Ingredient(

    ingredientId: Int,
    ownerId: Int,
    ingridentName: String,
    pictureBitMap: Bitmap,
    typeOfMeal: String,
    TypeOfSeason: String,
    howToStore: String,
    shareIngredient: Boolean,
    shareInfo: Boolean,
    protein_: Float,
    carbs_: Float,
    fat_: Float,
    nutritiousDes: String,
    costPerGram: String,
    isUpdate: Boolean


) : GetAndPost {


    var builder: java.lang.StringBuilder? = null
    var ingridentName = ingridentName
    var costPerGram = costPerGram
    var typeOfMeal = typeOfMeal
    var typeofSeason = TypeOfSeason
    var pictureBitMap = pictureBitMap
    var picture: String = ""
    var howToStore = howToStore
    var shareInfo = shareInfo
    var shareIngredient = shareIngredient
    var ownerId = ownerId
    var ingredientID = ingredientId

    //var ingredientID = -1
    var fat = fat_
    var carbs_ = carbs_
    var protein_ = protein_
    var nutritiousDes = nutritiousDes
    var isUpdate = isUpdate
    //var flag = flag


    @RequiresApi(Build.VERSION_CODES.O)
    fun encodePicture() {
        var image = ImageConvert(pictureBitMap)
        picture = image.BitMapToString().toString()


    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun DoNetWorkOpreation(): String {


        var input = ""
        if (pictureBitMap != null)
            encodePicture()
        // if we insert a new ingredient and not updating
        if (!isUpdate) {
            ingredientID = getIngredientID().toInt() + 1 // getting current IngredientID first

        }

        // ingredientID = 1

        if (ingredientID != -1)
            input = postData() // now we upload the current ingredient details.

        return input

    }


    private fun getIngredientID(): String {
        val link = "https://elad1.000webhostapp.com/getIngredientID.php"


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


        // Log.v("Elad1", "Id came is" + sb.toString())
        return sb.toString()
    }

    private fun postData(): String {
        return try {

            // values go to - Ingredient Table
            var link = "https://elad1.000webhostapp.com/postIngredient.php"
            if (isUpdate) {
                link =
                    "https://elad1.000webhostapp.com/updateIngredient.php?ingredientID=" + ingredientID

            }

            Log.v("Omer","IngredientID " + ingredientID.toString())
            Log.v("Omer","OwnerID " + ownerId.toString())
            Log.v("Omer","IngredientName " + ingridentName.toString())
            Log.v("Omer","TypeOfMeal " + typeOfMeal.toString())
            Log.v("Omer","typeofSeason " + typeofSeason.toString())
            Log.v("Omer","howToStore " + howToStore.toString())
            Log.v("Omer","ShareInfo " + shareInfo.toString())
            Log.v("Omer","shareIngredient " + shareIngredient.toString())
            Log.v("Omer","costPerGram " + costPerGram.toString())
            Log.v("Omer","Fat " + fat.toString())
            Log.v("Omer","carbs_ " + carbs_.toString())
            Log.v("Omer","protein_ " + protein_.toString())
            Log.v("Omer","nutritiousDes " + nutritiousDes.toString())

            if(nutritiousDes.equals("")){
                nutritiousDes = "no data"
            }
            if(howToStore.equals("")){
                howToStore = "no data"
            }
            // print here ingredient elemtnes
            var data = URLEncoder.encode("IngredientID", "UTF-8") + "=" +
                    URLEncoder.encode(ingredientID.toString(), "UTF-8")
            data += "&" + URLEncoder.encode("OwnerID", "UTF-8") + "=" +
                    URLEncoder.encode(ownerId.toString(), "UTF-8")
            data += "&" + URLEncoder.encode("IngredientName", "UTF-8") + "=" +
                    URLEncoder.encode(ingridentName, "UTF-8")
            data += "&" + URLEncoder.encode("Picture", "UTF-8") + "=" +
                    URLEncoder.encode(picture, "UTF-8")

            data += "&" + URLEncoder.encode("TypeOfMeal", "UTF-8") + "=" +
                    URLEncoder.encode(typeOfMeal, "UTF-8")
            data += "&" + URLEncoder.encode("Season", "UTF-8") + "=" +
                    URLEncoder.encode(typeofSeason, "UTF-8")
            data += "&" + URLEncoder.encode("HowToStore", "UTF-8") + "=" +
                    URLEncoder.encode(howToStore, "UTF-8")


            // now 2 values for another table -  Share_Ingredient_Table

            data += "&" + URLEncoder.encode("ShareInfo", "UTF-8") + "=" +
                    URLEncoder.encode(shareInfo.toString(), "UTF-8")
            data += "&" + URLEncoder.encode("ShareIngredient", "UTF-8") + "=" +
                    URLEncoder.encode(shareIngredient.toString(), "UTF-8")

            // 1 value for another table - Ingredient_Cost_Table

            data += "&" + URLEncoder.encode("CostPerGram", "UTF-8") + "=" +
                    URLEncoder.encode(costPerGram, "UTF-8")

            // 4 values for another table - Nutritious_Table

            data += "&" + URLEncoder.encode("Fat", "UTF-8") + "=" +
                    URLEncoder.encode(fat.toString(), "UTF-8")
            data += "&" + URLEncoder.encode("Carbs", "UTF-8") + "=" +
                    URLEncoder.encode(carbs_.toString(), "UTF-8")
            data += "&" + URLEncoder.encode("Protein", "UTF-8") + "=" +
                    URLEncoder.encode(protein_.toString(), "UTF-8")
            data += "&" + URLEncoder.encode("NutritiousDes", "UTF-8") + "=" +
                    URLEncoder.encode(nutritiousDes, "UTF-8")


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
            Log.v("Sivan","Ingredient : " + builder.toString())
            builder.toString()
        } catch (e: Exception) {
            Log.v("Elad1", "Failled")
        }.toString()
    }

    override fun getData(str: String) {
        print("dd")


    }


}