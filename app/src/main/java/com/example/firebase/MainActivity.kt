package com.example.firebase

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.ContactsContract.DisplayNameSources.EMAIL
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebase.databinding.ActivityMainBinding
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var googleSignInClient: GoogleSignInClient
    lateinit var callbackManager: CallbackManager
    lateinit var sharedPreferences: SharedPreferences
    var email="email"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        initview()
    }

    private fun initview() {
        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        if (sharedPreferences.getBoolean("isLogin", false) == true) {
            var i = Intent(this, AccountDetailsActivity::class.java)
            startActivity(i)
            finish()
        }

        binding.btncreateaccount.setOnClickListener {
            var email = binding.edtemail.text.toString()
            var name = binding.edtname.text.toString()
            var password = binding.edtpassword.text.toString()
            var adress = binding.edtadress.text.toString()
            var mobilenumber = binding.edtMobileNumber.text.toString()

            Log.e("TAG", "initview: " + adress + "" + mobilenumber)
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
                        Toast.makeText(this, "account created successfully", Toast.LENGTH_SHORT)
                            .show()

                        val myEdit: SharedPreferences.Editor = sharedPreferences.edit()
                        myEdit.putBoolean("isLogin", true)
                        myEdit.putString("name", name)
                        myEdit.putString("email", email)
                        myEdit.putString("adress", adress)
                        myEdit.putString("mobileNumber", mobilenumber)
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

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("635164525693-g0orn8gcjg3ebb03mm42fb6e2t8j20s0.apps.googleusercontent.com")
            .requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnSignInGoogle.setOnClickListener {
            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent, 100)
        }


        // facebook login.................................*****************************
        firebaseAuth = FirebaseAuth.getInstance()
        callbackManager = CallbackManager.Factory.create()
        binding.loginButton.setReadPermissions(Arrays.asList(email))
        binding.loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onCancel() {

                }

                override fun onError(error: FacebookException) {
                }

                @SuppressLint("CommitPrefEdits")
                override fun onSuccess(result: LoginResult) {


                    Log.e("TAG", "onSuccess:=== "+result.accessToken)
                    var request = GraphRequest.newMeRequest(result.accessToken, object : GraphRequest.GraphJSONObjectCallback {
                            override fun onCompleted(obj: JSONObject?, response: GraphResponse?) {

                                    var email = obj?.getString("email")

                                    Log.e("TAG", "onCompleted: " + email + "" + obj)
                            }
                        })
                    val parameters = Bundle()
                    parameters.putString("fields", "email")
                    request.parameters = parameters
                    request.executeAsync()


                    val credential: AuthCredential = FacebookAuthProvider.getCredential(result.accessToken.token)
                    firebaseAuth = FirebaseAuth.getInstance()
                    firebaseAuth.signInWithCredential(credential).addOnCompleteListener {

                        if (it.isSuccessful) {
                            var i=Intent(this@MainActivity,AccountDetailsActivity::class.java)
                            val myEdit: SharedPreferences.Editor = sharedPreferences.edit()
                            myEdit.putBoolean("isLogin",true)
                            myEdit.commit()
                            startActivity(i)
                            Toast.makeText(this@MainActivity, "firebase Authentecation successful", Toast.LENGTH_SHORT).show()

                        }
                    }.addOnFailureListener {
                        Log.e("TAG", "onSuccess: "+it.message)
                        Toast.makeText(this@MainActivity, "firebase Authentecation failed", Toast.LENGTH_SHORT).show()
                    }

                }

            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === 100) {
            // When request code is equal to 100 initialize task
            val signInAccountTask: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            // check condition
            if (signInAccountTask.isSuccessful()) {
                // When google sign in successful initialize string
                val s = "Google sign in successful"
                // Display Toast
                Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
                // Initialize sign in account
                try {
                    // Initialize sign in account
                    val googleSignInAccount: GoogleSignInAccount =
                        signInAccountTask.getResult(ApiException::class.java)
                    // Check condition
                    if (googleSignInAccount != null) {
                        // When sign in account is not equal to null initialize auth credential
                        val authCredential =
                            GoogleAuthProvider.getCredential(googleSignInAccount.idToken, null)
                        // Check credential
                        firebaseAuth.signInWithCredential(authCredential)
                            .addOnCompleteListener(this,
                                OnCompleteListener<AuthResult?> { task ->
                                    // Check condition
                                    if (task.isSuccessful) {
                                        // When task is successful redirect to profile activity display Toast
                                        startActivity(
                                            Intent(
                                                this@MainActivity,
                                                AccountDetailsActivity::class.java
                                            ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        )
                                        Toast.makeText(
                                            this,
                                            "Firebase authentication successful",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    } else {
                                        // When task is unsuccessful display Toast
                                        Toast.makeText(
                                            this,
                                            "Authentication Failed",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    }
                                })
                    }
                } catch (e: ApiException) {
                    e.printStackTrace()
                }
            }
        }
    }


}