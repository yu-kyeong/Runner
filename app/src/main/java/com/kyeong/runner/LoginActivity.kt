package com.kyeong.runner

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {

    //firebase
    private lateinit var auth: FirebaseAuth
    val RC_SIGN_IN = 1000
    val SECOND_ACTIVITY : Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var email : EditText = findViewById(R.id.idName)
        var password : EditText = findViewById(R.id.pwd)
        val loginBtn : Button = findViewById(R.id.log_in_btn)


        //auth.signInWithEmailAndPassword(email.toString(),password.toString())
          //  .addOnCompleteListener(LoginActivity, new OnCompleteListener<AuthResult>()

/*
        //구글 연동 로그인
        auth = Firebase.auth
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build() ,
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        //Create and launch sign-in intent
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN)*/



/*        loginBtn.setOnClickListener {
            Log.d("loginBtn", "loginBtn 클릭")
            val intent = Intent(this@LoginActivity , MainActivity::class.java)
            intent.putExtra("id", email.text.toString())
            intent.putExtra("pwd", password.text.toString())
            Log.d("id : ",  email.text.toString())
            Log.d("pwd : ",  password.text.toString())
            //startActivityForResult(intent , SECOND_ACTIVITY)
            startActivity(intent)
        }*/

        loginBtn.setOnClickListener {
        auth = Firebase.auth
        auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)

                    val intent = Intent(this@LoginActivity , HomeActivity::class.java)
                    intent.putExtra("id", email.text.toString())
                    intent.putExtra("pwd", password.text.toString())
                    Log.d("id : ",  email.text.toString())
                    Log.d("pwd : ",  password.text.toString())
                    startActivity(intent)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
        }



    }

    private fun updateUI(user: FirebaseUser?) {

    }





}