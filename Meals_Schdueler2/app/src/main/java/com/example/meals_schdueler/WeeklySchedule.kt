package com.example.meals_schdueler

import android.util.Log
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class WeeklySchedule(
    weeklyId: Int,
    ownerId: Int,
    numOfDay: String,
    dailyIds: String,
    totalCost:Double,
    isUpdate: Boolean

) : GetAndPost {



    var builder: java.lang.StringBuilder? = null
    var weeklyId = weeklyId
    var ownerId = ownerId
    var numOfDay = numOfDay
    var dailyIds = dailyIds
    var totalCost = totalCost
    var isUpdate = isUpdate

    override fun DoNetWorkOpreation(): String {
        var input = ""
        //if we insert a new daily and not updating
        if (!isUpdate) {
            weeklyId = getWeekly().toInt() + 1 // getting current RecipeID first

        }


        // ingredientID = 1
        //   Log.v("Elad1", "current ID " + ingredientID)
        if (weeklyId != -1)
            input = postData() // now we upload the current ingredient details.

        return input
    }


    private fun postData(): String {


        return try {

            // values go to - Ingredient Table
            var link =
                "https://elad1.000webhostapp.com/postWeeklySchedule.php"

            if (isUpdate){
                Log.v("Elad1","YESSSSSS enter")
                link = "https://elad1.000webhostapp.com/updateDaily.php"

            }
            var data = URLEncoder.encode("WeeklyID", "UTF-8") + "=" +
                    URLEncoder.encode(weeklyId.toString(), "UTF-8")
            data += "&" + URLEncoder.encode("numOfDay", "UTF-8") + "=" +
                    URLEncoder.encode(numOfDay, "UTF-8")
            data += "&" + URLEncoder.encode("dailyIds", "UTF-8") + "=" +
                    URLEncoder.encode(dailyIds, "UTF-8")

            data += "&" + URLEncoder.encode("ownerID", "UTF-8") + "=" +
                    URLEncoder.encode(ownerId.toString(), "UTF-8")
            data += "&" + URLEncoder.encode("totalCost", "UTF-8") + "=" +
                    URLEncoder.encode(totalCost.toString(), "UTF-8")




            Log.v("Elad1", data)
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
            Log.v("Elad1", "Failled")
        }.toString()

    }



    private fun getWeekly(): String {
        val link = "https://elad1.000webhostapp.com/getWeeklyID.php"
        Log.v("Elad1", "here222222222")

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


        Log.v("Elad1", "Id came is" + sb.toString())
        return sb.toString()
    }
    override fun getData(str: String) {
        print("DD")
    }
}