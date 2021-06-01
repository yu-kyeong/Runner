package com.kyeong.runner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import android.text.TextWatcher
import androidx.core.widget.addTextChangedListener

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val email: EditText = findViewById(R.id.new_id)
        val password: EditText = findViewById(R.id.new_pwd)
        val rePwd: EditText = findViewById(R.id.re_pwd)
        val nickName: EditText = findViewById(R.id.new_nick)
        val sex: RadioGroup = findViewById(R.id.radio_group)

        val signUp: Button = findViewById(R.id.newSign_up)

        auth = Firebase.auth

        email.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                TODO("Not yet implemented")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                TODO("Not yet implemented")
            }

            override fun afterTextChanged(p0: Editable?) {
                TODO("Not yet implemented")
            }
        })

        isValidMail(email)




        signUp.setOnClickListener {
            Log.d("signUpActivity", "onClick")
            //if (email.text.toString() != null && password.text.toString() != null && rePwd.text.toString() != null && nickName.text.toString() != null) {
                //password 6개 이상
                if (email.text.toString().length == 0 ){
                    Toast.makeText(baseContext , "이메일을 입력해주세요", Toast.LENGTH_SHORT ).show()
                    email.requestFocus()
                    return@setOnClickListener
                }
                if (password.text.toString().length == 0){
                    Toast.makeText(baseContext, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
                    password.requestFocus()
                    return@setOnClickListener
                }
                if (rePwd.text.toString().length == 0){
                    Toast.makeText(baseContext, "비밀번호 확인 입력해주세요", Toast.LENGTH_SHORT).show()
                    rePwd.requestFocus()
                    return@setOnClickListener
                }
                if (password.text.toString() != rePwd.text.toString()){
                    Toast.makeText(baseContext, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
                    rePwd.requestFocus()
                    return@setOnClickListener
                }



                auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            /////////
                            Log.d("signUpActivity", "첫 번째 if")
                            if (password.text.length >= 6) {
                                if (password.text.toString() == rePwd.text.toString()) {
                                    Log.d("signUpActivity", "두 번째 if")
                                    // Sign in success, update UI with the signed-in user's information
                                    val user = auth.currentUser
                                    Log.d("signUpActivity", "createUserWithEmail:success")
                                    val intent =
                                        Intent(this@SignUpActivity, LoginActivity::class.java)
                                    intent.putExtra("id", email.text.toString())
                                    intent.putExtra("pw", password.text.toString())
                                    intent.putExtra("nick", nickName.text.toString())
                                    when (sex.checkedRadioButtonId) {
                                        R.id.male -> intent.putExtra("sex", 1)

                                        R.id.female -> intent.putExtra("sex", 2)
                                    }

                                    startActivity(intent)
                                    updateUI(user)
                                }
                            }
                            /////////

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("signUpActivity", "createUserWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                            updateUI(null)
                        }


                    }
            /*} else{
                Toast.makeText(
                    baseContext, "", Toast.LENGTH_SHORT
                ).show()
            }*/
        }
    }



    fun isValidMail(email:EditText){
         android.util.Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()
    }


    private fun updateUI(user: FirebaseUser?) {

    }

}