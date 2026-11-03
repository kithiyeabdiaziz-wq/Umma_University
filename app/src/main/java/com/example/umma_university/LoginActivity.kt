package com.example.umma_university

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        authRepository = AuthRepository()

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvGoToRegister = findViewById<TextView>(R.id.tvGoToRegister)
        val tvForgotPassword = findViewById<TextView>(R.id.tvForgotPassword)
        val radioGroupRole = findViewById<RadioGroup>(R.id.radioGroupRole)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim().lowercase()
            val password = etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 1. Find out which role the user selected on the screen
            val selectedRole = when (radioGroupRole.checkedRadioButtonId) {
                R.id.radioStudent -> "student"
                R.id.radioLecturer -> "lecturer"
                R.id.radioAdmin -> "admin"
                R.id.radioSupport -> "support"
                else -> "student"
            }

            // 2. Call Firebase Auth
            authRepository.login(email, password) { success, roleOrError ->
                runOnUiThread {
                    if (success) {
                        val actualRole = roleOrError // role from Firestore

                        // 3. Verify selected role matches the database role
                        if (actualRole == selectedRole) {
                            Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()

                            when (actualRole) {
                                "student" -> startActivity(Intent(this, StudentDashboardActivity::class.java))
                                "lecturer" -> startActivity(Intent(this, LecturerDashboardActivity::class.java))
                                "admin" -> startActivity(Intent(this, AdminDashboardActivity::class.java))
                                "support" -> startActivity(Intent(this, SupportDashboardActivity::class.java))
                            }
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "Access Denied! You are not registered as a $selectedRole.",
                                Toast.LENGTH_LONG
                            ).show()
                            authRepository.logout()
                        }
                    } else {
                        Toast.makeText(this, "Error: $roleOrError", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        // Forgot Password Logic
        tvForgotPassword.setOnClickListener {
            val email = etEmail.text.toString().trim().lowercase()
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email first", Toast.LENGTH_SHORT).show()
            } else {
                authRepository.resetPassword(email) { success, errorMsg ->
                    runOnUiThread {
                        if (success) {
                            Toast.makeText(this, "Password reset link sent to $email", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

        tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}