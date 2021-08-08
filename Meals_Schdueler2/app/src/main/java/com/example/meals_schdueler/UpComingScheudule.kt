package com.example.meals_schdueler

import android.util.Log
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder


class UpComingScheudule (

    scheduleId: Int,
    date: String,
    dailyID : Int
  ) : GetAndPost {

    var builder: java.lang.StringBuilder? = null
    var scheduleId = scheduleId
    var date = date
    var dailyID = dailyID


  override fun DoNetWorkOpreation(): String {
    var input = ""


    scheduleId = getDailyScheduleID().toInt() + 1 // getting current IngredientID first


    // ingredientID = 1

    if (scheduleId != -1)
      input = postData() // now we upload the current ingredient details.

    return input
  }

  private fun postData(): String {


    return try {

      // values go to - Ingredient Table
      var link = "https://elad1.000webhostapp.com/postSchedule.php"

      // print here ingredient elemtnes
      var data = URLEncoder.encode("ScheduleID", "UTF-8") + "=" +
              URLEncoder.encode(scheduleId.toString(), "UTF-8")
      data += "&" + URLEncoder.encode("ownerID", "UTF-8") + "=" +
              URLEncoder.encode(UserInterFace.userID.toString(), "UTF-8")
      data += "&" + URLEncoder.encode("Date", "UTF-8") + "=" +
              URLEncoder.encode(date, "UTF-8")
      data += "&" + URLEncoder.encode("DailyID", "UTF-8") + "=" +
              URLEncoder.encode(dailyID.toString(), "UTF-8")



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

  private fun getDailyScheduleID(): String {

    val link = "https://elad1.000webhostapp.com/getScheduleID.php"


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
    Log.v("Elad1", "Delete successfully")
  }

}