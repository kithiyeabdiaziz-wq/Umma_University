package com.example.umma_university

import com.google.firebase.firestore.FirebaseFirestore

class MarksRepository {
    private val db = FirebaseFirestore.getInstance()

    fun saveMark(studentRegNumber: String, unitCode: String, mark: String, onResult: (Boolean, String?) -> Unit) {
        val resultData = hashMapOf(
            "studentRegNumber" to studentRegNumber,
            "unitCode" to unitCode,
            "mark" to mark
        )

        // FIXED: Use a custom document ID (RegNum_UnitCode) and .set()
        // instead of .add() so it overwrites instead of duplicating.
        val docId = "${studentRegNumber}_${unitCode}"

        db.collection("results").document(docId).set(resultData)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    fun getAllStudents(onResult: (List<User>?, String?) -> Unit) {
        db.collection("users").whereEqualTo("role", "student").get()
            .addOnSuccessListener { result ->
                val students = mutableListOf<User>()
                for (doc in result) {
                    val user = doc.toObject(User::class.java)
                    if (user != null) students.add(user)
                }
                onResult(students, null)
            }
            .addOnFailureListener { e -> onResult(null, e.message) }
    }
}