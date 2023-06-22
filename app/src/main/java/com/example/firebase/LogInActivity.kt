package com.example.firebase

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.firebase.databinding.ActivityLogInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LogInActivity : AppCompatActivity() {
    lateinit var binding: ActivityLogInBinding
    private lateinit var auth: FirebaseAuth
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        initview()
    }

    private fun initview() {
        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)

        binding.btnlogin.setOnClickListener {

            val email = binding.edtemail.text.toString()
            val password = binding.edtpassword.text.toString()
            Log.e("TAG", "initview: " + email + "" + password)
            if (email.isEmpty()) {
                Toast.makeText(this, "please enter a email", Toast.LENGTH_SHORT).show()
            } else if (password.isEmpty()) {
                Toast.makeText(this, "please enter a password", Toast.LENGTH_SHORT).show()
            } else {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "succeessfully log in", Toast.LENGTH_SHORT).show()
                        val myEdit: SharedPreferences.Editor = sharedPreferences.edit()
                        myEdit.putBoolean("isLogin", true)
                        myEdit.putString("email", email)
                        myEdit.commit()
                        Log.e("TAG", "email: " + email)

                        val i = Intent(this, AccountDetailsActivity::class.java)
                        startActivity(i)
                    }
                }.addOnFailureListener {
                    Log.e("TAG", "initview:faikure " + it.message)
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}