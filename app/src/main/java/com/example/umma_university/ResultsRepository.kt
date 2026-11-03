package com.example.umma_university

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ResultsRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun getMyResults(onResult: (List<Result>?, String?) -> Unit) {
        val uid = auth.currentUser?.uid
        if (uid == null) return onResult(null, "Not logged in")

        // 1. Get the student's registration number
        db.collection("users").document(uid).get()
            .addOnSuccessListener { userDoc ->
                val rawReg = userDoc.getString("regNumber") ?: ""
                val myRegNumber = rawReg.trim().uppercase()

                // 2. Fetch ALL results from the collection
                db.collection("results").get()
                    .addOnSuccessListener { allDocs ->
                        val list = mutableListOf<Result>()

                        for (doc in allDocs) {
                            val result = doc.toObject(Result::class.java)

                            if (result != null && result.regNumber.isNotEmpty()) {
                                // Compare the registration numbers safely
                                if (result.regNumber.trim().uppercase() == myRegNumber) {
                                    list.add(result)
                                }
                            }
                        }
                        onResult(list, null)
                    }
                    .addOnFailureListener { e ->
                        onResult(null, e.message)
                    }
            }
            .addOnFailureListener { e -> onResult(null, e.message) }
    }
}