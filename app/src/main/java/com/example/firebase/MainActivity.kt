package com.example.firebase

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.firebase.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    lateinit var sharedPreferences:SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        initview()
    }

    private fun initview() {
         sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)

        binding.btncreateaccount.setOnClickListener {
            var email = binding.edtemail.text.toString()
            var name = binding.edtname.text.toString()
            var password = binding.edtpassword.text.toString()
            var adress = binding.edtadress.text.toString()
            var mobilenumber = binding.edtMobileNumber.text.toString()

            Log.e("TAG", "initview: "+adress+""+mobilenumber )
            if (name.isEmpty()) {
                Toast.makeText(this, "please enter a email", Toast.LENGTH_SHORT).show()
            } else if (adress.isEmpty()) {
                Toast.makeText(this, "please enter a adress", Toast.LENGTH_SHORT).show()
            } else if (mobilenumber.isEmpty()) {
                Toast.makeText(this, "please enter a mobile no.", Toast.LENGTH_SHORT).show()
            } else if (email.isEmpty()) {
                Toast.makeText(this, "please enter a email", Toast.LENGTH_SHORT).show()
            } else if (password.isEmpty()) {
                Toast.makeText(this, "please enter a password", Toast.LENGTH_SHORT).show()
            } else {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {

                    if (it.isSuccessful) {
                        Toast.makeText(this, "account created successfully", Toast.LENGTH_SHORT).show()

                        val myEdit: SharedPreferences.Editor = sharedPreferences.edit()
                        myEdit.putBoolean("isLogin", true)
                        myEdit.putString("name",name)
                        myEdit.putString("email",email)
                        myEdit.putString("adress",adress)
                        myEdit.putString("mobileNumber",mobilenumber)
                        myEdit.commit()
                        var i = Intent(this, AccountDetailsActivity::class.java)
                        startActivity(i)
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.txtlogin.setOnClickListener {
            var i = Intent(this, LogInActivity::class.java)
            startActivity(i)
        }
    }
}