package com.example.umma_university

import com.google.firebase.firestore.FirebaseFirestore

class AssignmentRepository {
    private val db = FirebaseFirestore.getInstance()

    fun addAssignment(assignment: Assignment, onResult: (Boolean, String?) -> Unit) {
        db.collection("assignments").add(assignment)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    fun getAssignmentsForUnit(unitCode: String, onResult: (List<Assignment>?, String?) -> Unit) {
        db.collection("assignments").whereEqualTo("unitCode", unitCode).get()
            .addOnSuccessListener { result ->
                val list = mutableListOf<Assignment>()
                for (doc in result) {
                    val a = doc.toObject(Assignment::class.java)
                    if (a != null) list.add(a)
                }
                onResult(list, null)
            }
            .addOnFailureListener { e -> onResult(null, e.message) }
    }

    // NEW: Fetch ALL assignments for students
    fun getAllAssignments(onResult: (List<Assignment>?, String?) -> Unit) {
        db.collection("assignments").get()
            .addOnSuccessListener { result ->
                val list = mutableListOf<Assignment>()
                for (doc in result) {
                    val a = doc.toObject(Assignment::class.java)
                    if (a != null) list.add(a)
                }
                onResult(list, null)
            }
            .addOnFailureListener { e -> onResult(null, e.message) }
    }
}