package com.example.umma_university

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FeeRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun getMyFeeRecord(onResult: (FeeRecord?, String?) -> Unit) {
        val uid = auth.currentUser?.uid
        if (uid == null) return onResult(null, "Not logged in")

        db.collection("users").document(uid).get()
            .addOnSuccessListener { userDoc ->
                val regNumber = userDoc.getString("regNumber") ?: ""
                if (regNumber.isEmpty()) return@addOnSuccessListener onResult(null, "No reg number found")

                db.collection("fees").document(regNumber).get()
                    .addOnSuccessListener { feeDoc ->
                        if (feeDoc.exists()) {
                            val feeRecord = feeDoc.toObject(FeeRecord::class.java)
                            onResult(feeRecord, null)
                        } else {
                            onResult(null, "No fee record found for your account.")
                        }
                    }
                    .addOnFailureListener { e -> onResult(null, e.message) }
            }
            .addOnFailureListener { e -> onResult(null, e.message) }
    }
}