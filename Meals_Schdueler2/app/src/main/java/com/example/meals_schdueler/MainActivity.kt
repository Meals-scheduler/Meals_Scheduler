package com.example.meals_schdueler


//import android.widget.Toolbar
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Paint
import android.graphics.Paint.UNDERLINE_TEXT_FLAG
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationView
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.sign


class MainActivity : AppCompatActivity(), View.OnClickListener, GetAndPost,CompoundButton.OnCheckedChangeListener {
//
//    lateinit var mDrawerLayout: DrawerLayout  // lateinit preventing the object from being null.
//    lateinit var nNavigationView: NavigationView
//    lateinit var mFragmentManager: FragmentManager
//    lateinit var mFragmentTransaction: FragmentTransaction
//

    lateinit var btnLogin: Button
    lateinit var signUpText: TextView
    lateinit var userName: EditText
    lateinit var passWord: EditText
    lateinit var rememeberMe: CheckBox
    var username = ""
    var password = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)


        userName = findViewById(R.id.username)
        passWord = findViewById(R.id.password)
        btnLogin = findViewById(R.id.buttonLogin)
        signUpText = findViewById(R.id.signUpText)
        rememeberMe = findViewById(R.id.checkBoxRemember)


        //signUpText.setOnHoverListener(this)
        rememeberMe.setOnCheckedChangeListener(this)
        btnLogin.setOnClickListener(this)
        signUpText.setOnClickListener(this)

        var preferences : SharedPreferences = getSharedPreferences("checkbox", MODE_PRIVATE)
        var s = preferences.getString("remember","")

        // means the user is written in the SharedPrefrences
        if(!s.equals("false") && !s.equals("")){
            val i = Intent(applicationContext, UserInterFace::class.java)
            i.putExtra("UserID", s!!.toInt())
            startActivity(i)
        }

    }

    override fun onClick(p0: View?) {

        if (p0 == btnLogin) {
            // check if username and password currect
            username = userName.text.toString()
            password = passWord.text.toString()
            var s = AsynTaskNew(this)
            s.execute()

        } else if (p0 == signUpText) {
            val i = Intent(applicationContext, SignUp::class.java)
            startActivity(i)

        }
    }

    //?ingredientID="+ingredientID

    override fun DoNetWorkOpreation(): String {
        val link =
            "https://elad1.000webhostapp.com/getUser.php?Username=" + username + "&Password=" + password
        Log.v("Elad1", "here")

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

        if (str.equals("")) {
            Toast.makeText(this, "Username or Password are wrong!!", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Login successfully!!", Toast.LENGTH_LONG).show()
            val i = Intent(applicationContext, UserInterFace::class.java)
            i.putExtra("UserID", str.toInt())
            startActivity(i)

            // here if the user checkd that he wants to remember him :
            if(rememeberMe!!.isChecked){
                // creating SharedPrefrences object - its a file in the storge of the phone that we can write to it.
                var preferences : SharedPreferences = getSharedPreferences("checkbox", MODE_PRIVATE) // private access to the file
                var editor : SharedPreferences.Editor = preferences.edit()
                editor.putString("remember",str)
                editor.apply()

            }
            else if (!rememeberMe.isChecked){
                var preferences : SharedPreferences = getSharedPreferences("checkbox", MODE_PRIVATE) // private access to the file, only our app can read it
                var editor : SharedPreferences.Editor = preferences.edit()
                editor.putString("remember","false")
                editor.apply()
            }
        }


    }

    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
//        if(p0!!.isChecked){
//            // creating SharedPrefrences object - its a file in the storge of the phone that we can write to it.
//          var preferences : SharedPreferences = getSharedPreferences("checkbox", MODE_PRIVATE) // private access to the file
//            var editor : SharedPreferences.Editor = preferences.edit()
//            editor.putString("remember","true")
//            editor.apply()
//
//        }
//        else if (!p0.isChecked){
//            var preferences : SharedPreferences = getSharedPreferences("checkbox", MODE_PRIVATE) // private access to the file, only our app can read it
//            var editor : SharedPreferences.Editor = preferences.edit()
//            editor.putString("remember","false")
//            editor.apply()
//        }
    }


}

