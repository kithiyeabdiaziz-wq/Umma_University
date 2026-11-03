package com.example.umma_university

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun getUserProfile(onResult: (User?, String?) -> Unit) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            onResult(null, "Not logged in")
            return
        }
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val user = doc.toObject(User::class.java)
                onResult(user, null)
            }
            .addOnFailureListener { e -> onResult(null, e.message) }
    }

    // UPDATED: Now saves phone and photo URL
    fun updateProfile(name: String, department: String, phone: String, photoUrl: String, onResult: (Boolean, String?) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onResult(false, "Not logged in")

        val updates = mapOf(
            "name" to name,
            "department" to department,
            "phoneNumber" to phone,
            "photoUrl" to photoUrl
        )

        db.collection("users").document(uid)
            .update(updates)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }
}