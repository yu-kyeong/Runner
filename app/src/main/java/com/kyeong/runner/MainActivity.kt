package com.kyeong.runner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {


    //firebase
    val RC_SIGN_IN = 1000
    private lateinit var auth: FirebaseAuth
    //lateinit var signUp : Button
    lateinit var logIn : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //button
        val signUp : Button = findViewById(R.id.sign_up)
         logIn = findViewById(R.id.log_in)

        signUp.setOnClickListener {
            Log.d("signUp","signUp 클릭")
            val intent1 = Intent(this@MainActivity, SignUpActivity::class.java)
            startActivity(intent1)

        }


         logIn.setOnClickListener {
             Log.d("logIn", "logIn 클릭")
             val intent2 = Intent(this@MainActivity, LoginActivity::class.java)
             startActivity(intent2)

         }

        if (intent.hasExtra("id") && intent.hasExtra("pw"))
        {
        val id = intent.getStringExtra("id")
        val pwd = intent.getStringExtra("pwd")
        Log.d("id+pwd \t",id+pwd)

        }
    }

    class LogInListener : AppCompatActivity() , View.OnClickListener{
        override fun onClick(p0: View?) {

        }
    }

}

