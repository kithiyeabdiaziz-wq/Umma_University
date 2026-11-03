package com.example.umma_university

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore

class CareerAlumniActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_career_alumni)

        // Find Views
        val tabLayout = findViewById<TabLayout>(R.id.tabLayoutCareer)
        val layoutAdmissions = findViewById<LinearLayout>(R.id.layoutAdmissions)
        val layoutJobs = findViewById<LinearLayout>(R.id.layoutJobs)
        val layoutAlumni = findViewById<LinearLayout>(R.id.layoutAlumni)

        // Add Tabs
        tabLayout.addTab(tabLayout.newTab().setText("Admissions"))
        tabLayout.addTab(tabLayout.newTab().setText("Jobs & Careers"))
        tabLayout.addTab(tabLayout.newTab().setText("Alumni"))

        // Tab Switching Logic
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                layoutAdmissions.visibility = View.GONE
                layoutJobs.visibility = View.GONE
                layoutAlumni.visibility = View.GONE

                when (tab?.position) {
                    0 -> layoutAdmissions.visibility = View.VISIBLE
                    1 -> layoutJobs.visibility = View.VISIBLE
                    2 -> layoutAlumni.visibility = View.VISIBLE
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // --- ADMISSIONS LOGIC ---
        val etName = findViewById<EditText>(R.id.etAdmName)
        val etEmail = findViewById<EditText>(R.id.etAdmEmail)
        val etCourse = findViewById<EditText>(R.id.etAdmCourse)
        val btnSubmitApp = findViewById<Button>(R.id.btnSubmitApplication)

        val etCheckStatusEmail = findViewById<EditText>(R.id.etCheckStatusEmail)
        val btnCheckStatus = findViewById<Button>(R.id.btnCheckStatus)

        // Submit Application
        btnSubmitApp.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim().lowercase()
            val course = etCourse.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || course.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val application = AdmissionApplication(
                fullName = name,
                email = email,
                course = course,
                status = "Pending"
            )

            db.collection("admissions").add(application)
                .addOnSuccessListener {
                    Toast.makeText(this, "Application Submitted Successfully!", Toast.LENGTH_LONG).show()
                    etName.text.clear()
                    etEmail.text.clear()
                    etCourse.text.clear()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }

        // Check Status
        btnCheckStatus.setOnClickListener {
            val email = etCheckStatusEmail.text.toString().trim().lowercase()
            if (email.isEmpty()) {
                Toast.makeText(this, "Enter your email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            db.collection("admissions").whereEqualTo("email", email).get()
                .addOnSuccessListener { result ->
                    if (result.isEmpty) {
                        Toast.makeText(this, "No application found for this email.", Toast.LENGTH_LONG).show()
                    } else {
                        val status = result.documents[0].getString("status")
                        Toast.makeText(this, "Your Application Status: $status", Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}