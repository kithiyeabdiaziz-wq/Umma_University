package com.example.umma_university

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        authRepository = AuthRepository()

        val etName = findViewById<EditText>(R.id.etRegName)
        val etEmail = findViewById<EditText>(R.id.etRegEmail)
        val etPassword = findViewById<EditText>(R.id.etRegPassword)
        val etRegNum = findViewById<EditText>(R.id.etRegNumber)
        val etDept = findViewById<EditText>(R.id.etRegDept)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim().lowercase()
            val password = etPassword.text.toString()

            // Trim only. Keep it exactly as the user typed it.
            val regNum = etRegNum.text.toString().trim()
            val dept = etDept.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || regNum.isEmpty() || dept.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newUser = User(
                email = email,
                name = name,
                role = "student",
                regNumber = regNum,
                department = dept
            )

            authRepository.register(newUser, password) { success, errorMsg ->
                runOnUiThread {
                    if (success) {
                        Toast.makeText(this, "Registration Successful!", Toast.LENGTH_LONG).show()
                        finish() // Goes back to the Login screen
                    } else {
                        Toast.makeText(this, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}