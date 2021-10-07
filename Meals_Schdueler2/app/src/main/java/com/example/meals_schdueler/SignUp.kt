package com.example.meals_schdueler

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class SignUp : AppCompatActivity() ,View.OnClickListener,GetAndPost {


    lateinit var userFirstName : EditText
    lateinit var userLastName : EditText
    lateinit var firstName : String
    lateinit var lastName : String
    lateinit var userEmail : EditText
    lateinit var email : String
    lateinit var userPassword : EditText
    lateinit var password : String
    lateinit var picture : String
    lateinit var btnSignUp : Button
    lateinit var loginText : TextView
    var userID = 0
    var builder: java.lang.StringBuilder? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up)
        setSupportActionBar(findViewById(R.id.toolbar))


        userFirstName = findViewById(R.id.firstName)
        userEmail = findViewById(R.id.email)
        userPassword = findViewById(R.id.password)
        userLastName = findViewById(R.id.lastName)
        btnSignUp = findViewById(R.id.buttonSignUp)
        loginText = findViewById(R.id.loginText)
        loginText.setOnClickListener(this)
        btnSignUp.setOnClickListener(this)
        firstName = ""
        lastName = ""
        email = ""
        password = ""
        picture = ""






    }

    override fun onClick(p0: View?) {
       if(p0 == btnSignUp){
            if(CheckValidity()){
                firstName = userFirstName.text.toString()
                lastName = userLastName.text.toString()
                email = userEmail.text.toString()
                password = userPassword.text.toString()
                picture = "Nothing ATM"
                var s = AsynTaskNew(this, supportFragmentManager,baseContext)
                s.execute()
            }
           else{
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Sorry,you must fill all details,try again!")
                builder.setPositiveButton(
                    "Got it!"
                ) { dialog, id -> dialog.cancel() }.show()
            }

       }
        else if(p0 == loginText){
           val i = Intent(applicationContext, MainActivity::class.java)
           startActivity(i)
       }
    }

    private fun CheckValidity(): Boolean {

        if(userFirstName.text.length == 0 || userEmail.text.length == 0  || userPassword.text.length == 0 || userLastName.text.length == 0 ){
            return false
        }
        return  true

    }

    override fun DoNetWorkOpreation(): String {
        var input = ""
        //encodePicture()
        // if we insert a new ingredient and not updating

         userID  = getUserID().toInt() + 1 // getting current IngredientID first


        // ingredientID = 1

        if (userID != 0)
            input = postData() // now we upload the current ingredient details.

        return input
    }

    private fun getUserID(): String {
        val link = "https://elad1.000webhostapp.com/getUserID.php"


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


    private fun postData(): String {
         try {

            // values go to - Ingredient Table
            var link = "https://elad1.000webhostapp.com/postUser.php?UserName="+firstName

            // print here ingredient elemtnes
            var data = URLEncoder.encode("UserID", "UTF-8") + "=" +
                    URLEncoder.encode(userID.toString(), "UTF-8")
            data += "&" + URLEncoder.encode("FirstName", "UTF-8") + "=" +
                    URLEncoder.encode(firstName, "UTF-8")
            data += "&" + URLEncoder.encode("LastName", "UTF-8") + "=" +
                    URLEncoder.encode(lastName, "UTF-8")
            data += "&" + URLEncoder.encode("Email", "UTF-8") + "=" +
                    URLEncoder.encode(email, "UTF-8")

            data += "&" + URLEncoder.encode("Password", "UTF-8") + "=" +
                    URLEncoder.encode(password, "UTF-8")
            data += "&" + URLEncoder.encode("Picture", "UTF-8") + "=" +
                    URLEncoder.encode(picture, "UTF-8")



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

        return builder.toString()
    }

    override fun getData(str: String) {
        // if user isnt exists yet
       if(str=="no"){
           Toast.makeText(this,"User registered succussfully",Toast.LENGTH_LONG).show()
           var i = Intent(applicationContext, UserInterFace::class.java)
           i.putExtra("UserID",userID)
           startActivity(i)

       }
        // if user already exists
        else{
           Toast.makeText(this,"User registered faiiled,user name already exists",Toast.LENGTH_LONG).show()
       }
    }




}