package com.nguyen.instafire

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.nguyen.instafire.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    companion object {
        const val TAG = "LoginActivity"
    }

    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_login)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val auth = FirebaseAuth.getInstance()
        // navigate to PostsActivity instead of always showing the login screen
        if (auth.currentUser != null) {
            goPostsActivity()
        }

        binding.btLogin.setOnClickListener {
            // disable login
            binding.btLogin.isEnabled = false

            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Email/password cannot be empty", Toast.LENGTH_LONG).show()
            } else {
                // menu {Tools - Firebase} will help connect this app to Firebase in steps 1 and 2
                // check current auth state per step 3
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    // enable login
                    binding.btLogin.isEnabled = true

                    if (task.isSuccessful) {
                        Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
                        goPostsActivity()
                    } else {
                        Log.e(TAG, "signInWithEmailAndPassword failed", task.exception)
                        Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun goPostsActivity() {
        Log.i(TAG, "goPostsActivity")
        val intent = Intent(this, PostActivity::class.java)
        startActivity(intent)
        finish()
    }
}