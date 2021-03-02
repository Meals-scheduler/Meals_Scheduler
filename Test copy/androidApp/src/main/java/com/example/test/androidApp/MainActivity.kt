package com.example.test.androidApp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.test.shared.Greeting
import android.widget.TextView

fun greet(): String {
   // return Greeting().greeting()
    return "ELad"
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tv : TextView = findViewById(R.id.text_view)
        val bt : Button = findViewById(R.id.button)
        tv.text = greet()
    }
}
