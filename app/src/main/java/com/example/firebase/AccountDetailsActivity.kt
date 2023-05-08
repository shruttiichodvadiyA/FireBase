package com.example.firebase

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.firebase.databinding.ActivityAccountDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class AccountDetailsActivity : AppCompatActivity() {
    lateinit var binding: ActivityAccountDetailsBinding
    lateinit var sharedPreferences:SharedPreferences
    lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initview()
    }

    private fun initview() {
         sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        binding.txtname.text = sharedPreferences.getString("name","")
        binding.txtadress.text = sharedPreferences.getString("adress","")
        binding.txtMobilNumber.text = sharedPreferences.getString("mobileNumber","")
        binding.txtemail.text = sharedPreferences.getString("email","")

        binding.btnlogout.setOnClickListener {
            var myEdit:SharedPreferences.Editor=sharedPreferences.edit()
            myEdit.remove("isLogin")
            myEdit.commit()
            firebaseAuth= Firebase.auth
            firebaseAuth=FirebaseAuth.getInstance()

            var i=Intent(this,MainActivity::class.java)
            startActivity(i)
        }


    }
}