package com.example.umma_university

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CampusFacilitiesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_campus_facilities)

        val etRoomType = findViewById<EditText>(R.id.etHostelRoomType)
        val etSpecialNeeds = findViewById<EditText>(R.id.etHostelSpecialNeeds)
        val btnApplyHostel = findViewById<Button>(R.id.btnApplyHostel)

        btnApplyHostel.setOnClickListener {
            val roomType = etRoomType.text.toString().trim()
            val specialNeeds = etSpecialNeeds.text.toString().trim()

            if (roomType.isEmpty()) {
                Toast.makeText(this, "Please enter a room type", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val db = FirebaseFirestore.getInstance()

            if (uid != null) {
                // Fetch the student's reg number to link the application
                db.collection("users").document(uid).get().addOnSuccessListener { doc ->
                    val regNumber = doc.getString("regNumber") ?: "Unknown"

                    val application = hashMapOf(
                        "studentRegNumber" to regNumber,
                        "roomType" to roomType,
                        "specialNeeds" to specialNeeds,
                        "status" to "Pending",
                        "timestamp" to System.currentTimeMillis()
                    )

                    db.collection("hostelApplications").add(application)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Hostel Application Submitted Successfully!", Toast.LENGTH_LONG).show()
                            etRoomType.text.clear()
                            etSpecialNeeds.text.clear()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                }
            }
        }
    }
}