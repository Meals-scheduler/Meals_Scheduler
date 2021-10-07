package com.example.meals_schdueler

import android.util.Log
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class Event(
    eventId: Int,
    ownerId: Int,
    eventName: String,
    quantities: String,
    recipeIds: String,
    totalCost: Double,
    isUpdate: Boolean


) : GetAndPost {

    var builder: java.lang.StringBuilder? = null
    var eventId = eventId
    var ownerId = ownerId
    var quantities = quantities
    var recipeIds = recipeIds
    var isUpdate = isUpdate
    var totalCost = totalCost
    var eventName = eventName


    override fun DoNetWorkOpreation(): String {
        var input = ""
        //if we insert a new daily and not updating
        if (!isUpdate) {
            eventId = getEvent().toInt() + 1 // getting current RecipeID first

        }



        if (eventId != -1)
            input = postData() // now we upload the current ingredient details.

        return input
    }

    private fun postData(): String {


        return try {

            // values go to - Ingredient Table
            var link =
                "https://elad1.000webhostapp.com/postEventSchedule.php"

            if (isUpdate) {

                link = "https://elad1.000webhostapp.com/updateEvent.php"

            }


            // print here ingredient elemtnes

            var data = URLEncoder.encode("EventID", "UTF-8") + "=" +
                    URLEncoder.encode(eventId.toString(), "UTF-8")
            data += "&" + URLEncoder.encode("recipeIds", "UTF-8") + "=" +
                    URLEncoder.encode(recipeIds, "UTF-8")
            data += "&" + URLEncoder.encode("eventName", "UTF-8") + "=" +
                    URLEncoder.encode(eventName, "UTF-8")


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

        }.toString()

    }


    private fun getEvent(): String {
        val link = "https://elad1.000webhostapp.com/getEventID.php"


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