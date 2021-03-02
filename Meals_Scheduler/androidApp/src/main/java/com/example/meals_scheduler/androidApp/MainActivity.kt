package com.example.meals_scheduler.androidApp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.meals_scheduler.shared.Stam

fun greet(): String {
    return Stam().Hello()
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tv: TextView = findViewById(R.id.text_view)
        tv.text = greet()

        val btn: Button = findViewById(R.id.buttonStam)

        btn.setOnClickListener(View.OnClickListener {
            //user not in the first time.
            val intent = Intent(this, UserInterface::class.java)
            startActivity(intent)

        })
    }
}


