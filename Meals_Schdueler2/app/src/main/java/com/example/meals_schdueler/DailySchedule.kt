package com.example.meals_schdueler.dummy

import android.util.Log
import com.example.meals_schdueler.GetAndPost
import com.example.meals_schdueler.UserInterFace
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class DailySchedule(
    dailyId: Int,
    ownerId: Int,
    numOfMeals: String,
    quantities: String,
    recipeIds: String,
    totalCost:Double,
    isUpdate: Boolean


) : GetAndPost {

    var builder: java.lang.StringBuilder? = null
    var dailyId = dailyId
    var ownerId = ownerId
    var numOfMeals = numOfMeals
    var quantities = quantities
    var recipeIds = recipeIds
    var isUpdate = isUpdate
    var totalCost = totalCost


    override fun DoNetWorkOpreation(): String {
        var input = ""
         //if we insert a new daily and not updating
        if (!isUpdate) {
            dailyId = getDaily().toInt() + 1 // getting current RecipeID first

        }


        if (dailyId != -1)
            input = postData() // now we upload the current ingredient details.

        return input
    }

    private fun postData(): String {


        return try {

            // values go to - Ingredient Table
            var link =
                "https://elad1.000webhostapp.com/postDailySchedule.php"

            if (isUpdate){

               link = "https://elad1.000webhostapp.com/updateDaily.php"

            }

            // print here ingredient elemtnes

            var data = URLEncoder.encode("DailyID", "UTF-8") + "=" +
                    URLEncoder.encode(dailyId.toString(), "UTF-8")
            data += "&" + URLEncoder.encode("numOfMeals", "UTF-8") + "=" +
                    URLEncoder.encode(numOfMeals, "UTF-8")
            data += "&" + URLEncoder.encode("recipeIds", "UTF-8") + "=" +
                    URLEncoder.encode(recipeIds, "UTF-8")

            data += "&" + URLEncoder.encode("quantities", "UTF-8") + "=" +
                    URLEncoder.encode(quantities, "UTF-8")
            data += "&" + URLEncoder.encode("ownerID", "UTF-8") + "=" +
                    URLEncoder.encode(ownerId.toString(), "UTF-8")
            data += "&" + URLEncoder.encode("totalCost", "UTF-8") + "=" +
                    URLEncoder.encode(totalCost.toString(), "UTF-8")





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
            Log.v("Elad1", "Failled")
        }.toString()

    }

    private fun getDaily(): String {
        val link = "https://elad1.000webhostapp.com/getDailyID.php"


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

    override fun getData(str: String) {
        print("DD")
    }
}