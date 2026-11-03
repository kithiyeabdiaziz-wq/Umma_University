package com.example.umma_university

import com.google.firebase.firestore.FirebaseFirestore

class StudentRepository {
    private val db = FirebaseFirestore.getInstance()

    // Fetch all units from the database
    fun getAllUnits(onResult: (List<CourseUnit>?, String?) -> Unit) {
        db.collection("units").get()
            .addOnSuccessListener { result ->
                val unitList = mutableListOf<CourseUnit>()
                for (document in result) {
                    val unit = document.toObject(CourseUnit::class.java)
                    if (unit != null) {
                        unitList.add(unit)
                    }
                }
                onResult(unitList, null)
            }
            .addOnFailureListener { e ->
                onResult(null, e.message)
            }
    }
}