package com.example.umma_university

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SubmitComplaintActivity : AppCompatActivity() {

    private lateinit var complaintRepository: ComplaintRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit_complaint)

        complaintRepository = ComplaintRepository()

        val etMessage = findViewById<EditText>(R.id.etComplaintMessage)
        val spinnerDept = findViewById<Spinner>(R.id.spinnerDepartment)
        val btnSubmit = findViewById<Button>(R.id.btnSubmitComplaint)

        // Departments must match what admins can assign to support staff
        val departments = listOf("Welfare", "ICT", "Registry", "Finance", "Library")
        spinnerDept.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            departments
        )

        btnSubmit.setOnClickListener {
            val message = etMessage.text.toString().trim()
            val department = spinnerDept.selectedItem.toString()

            if (message.isEmpty()) {
                Toast.makeText(this, "Please describe your issue", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            complaintRepository.submitComplaint(message, department) { success, errorMsg ->
                runOnUiThread {
                    if (success) {
                        Toast.makeText(this, "Complaint submitted to $department", Toast.LENGTH_LONG).show()
                        etMessage.text.clear()
                        finish()
                    } else {
                        Toast.makeText(this, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}