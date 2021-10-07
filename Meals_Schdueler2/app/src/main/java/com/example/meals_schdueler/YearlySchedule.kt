package com.example.meals_schdueler

import android.util.Log
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class YearlySchedule(
    yearlyId: Int,
    ownerId: Int,
    numOfMonth: String,
    monthlyIds: String,
    totalCost: Double,
    isUpdate: Boolean
) : GetAndPost {

    var builder: java.lang.StringBuilder? = null
    var yearlyId = yearlyId
    var ownerId = ownerId
    var numOfMonth = numOfMonth
    var monthlyIds = monthlyIds
    var totalCost = totalCost
    var isUpdate = isUpdate






    override fun DoNetWorkOpreation(): String {
        var input = ""
        //if we insert a new daily and not updating
        if (!isUpdate) {
            yearlyId = getYearly().toInt() + 1 // getting current RecipeID first

        }
     //  yearlyId = 1

        if (yearlyId != -1)
            input = postData() // now we upload the current ingredient details.

        return input
    }


    private fun postData(): String {


        return try {

            // values go to - Ingredient Table
            var link =
                "https://elad1.000webhostapp.com/postYearlySchedule.php"

            if (isUpdate) {

                link = "https://elad1.000webhostapp.com/updateYearly.php"

            }
            var data = URLEncoder.encode("YearlyID", "UTF-8") + "=" +
                    URLEncoder.encode(yearlyId.toString(), "UTF-8")
            data += "&" + URLEncoder.encode("numOfMonth", "UTF-8") + "=" +
                    URLEncoder.encode(numOfMonth, "UTF-8")
            data += "&" + URLEncoder.encode("monthlyIds", "UTF-8") + "=" +
                    URLEncoder.encode(monthlyIds, "UTF-8")

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


    private fun getYearly(): String {
        val link = "https://elad1.000webhostapp.com/getYearlyID.php"


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