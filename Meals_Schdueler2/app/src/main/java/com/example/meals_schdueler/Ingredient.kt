package com.example.meals_schdueler

import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.URL
import java.net.URLEncoder

class Ingredient(
    ingridentName: String,
    costPerGram: String,
    typeOfMeal: String,
    TypeOfSeason: String,
    picture: String,
    howToStore: String,
    shareInfo: Boolean,
    shareIngredient: Boolean,
    ownerId : Int
) : GetAndPost {


    var builder: java.lang.StringBuilder? = null
    var ingridentName = ingridentName
    var costPerGram = costPerGram
    var typeOfMeal = typeOfMeal
    var typeofSeason = TypeOfSeason
    var picture = picture
    var howToStore = howToStore
    var shareInfo = shareInfo
    var shareIngredient = shareIngredient
    var ownerId = ownerId




    override fun DoNetWorkOpreation(): String {
        Log.v("Elad1", "started asyn")
        return try {
            val link = "https://elad1.000webhostapp.com/postIngredient.php"
            var data = URLEncoder.encode("HowToStore", "UTF-8") + "=" +
                    URLEncoder.encode(howToStore, "UTF-8")
            data += "&" + URLEncoder.encode("Season", "UTF-8") + "=" +
                    URLEncoder.encode(typeofSeason, "UTF-8")
            data += "&" + URLEncoder.encode("TypeOfMeal", "UTF-8") + "=" +
                    URLEncoder.encode(typeOfMeal ,"UTF-8")

            data += "&" + URLEncoder.encode("Picture", "UTF-8") + "=" +
                    URLEncoder.encode(picture ,"UTF-8")
            data += "&" + URLEncoder.encode("IngredientName", "UTF-8") + "=" +
                    URLEncoder.encode(ingridentName ,"UTF-8")
            data += "&" + URLEncoder.encode("OwnerID", "UTF-8") + "=" +
                    URLEncoder.encode(ownerId.toString() ,"UTF-8")


            Log.v("Elad1", "started asyn 1")
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
            Log.v("Elad1", "started asyn2")
            // Read Server Response
            while (reader.readLine().also { line = it } != null) {
                builder!!.append(line)
                break
            }
            builder.toString()
            Log.v("Elad1", builder.toString())
            Log.v("Elad1", "asyn worked")
        } catch (e: Exception) {
           Log.v("Elad","Failled")
        }.toString()
    }


}