package com.example.umma_university

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AttendanceRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun getMyAttendance(onResult: (List<AttendanceRecord>?, String?) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onResult(null, "Not logged in")

        // 1. Get the student's Reg Number
        db.collection("users").document(uid).get()
            .addOnSuccessListener { userDoc ->
                val regNumber = userDoc.getString("regNumber")?.trim() ?: ""
                if (regNumber.isEmpty()) return@addOnSuccessListener onResult(null, "No reg number found")

                // 2. Fetch their attendance
                db.collection("attendance")
                    .whereEqualTo("studentRegNumber", regNumber)
                    .get()
                    .addOnSuccessListener { result ->
                        val list = mutableListOf<AttendanceRecord>()
                        for (doc in result) {
                            val record = doc.toObject(AttendanceRecord::class.java)
                            if (record != null) list.add(record)
                        }
                        // Sort by date (newest first)
                        list.sortByDescending { it.date }
                        onResult(list, null)
                    }
                    .addOnFailureListener { e -> onResult(null, e.message) }
            }
            .addOnFailureListener { e -> onResult(null, e.message) }
    }
}