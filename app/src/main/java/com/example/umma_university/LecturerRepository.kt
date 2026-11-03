package com.example.umma_university

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LecturerRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun getMyUnits(onResult: (List<CourseUnit>?, String?) -> Unit) {
        val currentEmail = auth.currentUser?.email ?: return onResult(null, "No user logged in")

        // Find units where lecturerEmail matches the current user
        db.collection("units").whereEqualTo("lecturerEmail", currentEmail).get()
            .addOnSuccessListener { result ->
                val unitList = mutableListOf<CourseUnit>()
                for (document in result) {
                    val unit = document.toObject(CourseUnit::class.java)
                    if (unit != null) unitList.add(unit)
                }
                onResult(unitList, null)
            }
            .addOnFailureListener { e -> onResult(null, e.message) }
    }
}