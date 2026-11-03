package com.example.umma_university

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegistrationRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Save a registration to Firestore
    fun registerUnit(unit: CourseUnit, onResult: (Boolean, String?) -> Unit) {
        val uid = auth.currentUser?.uid
        if (uid == null) return onResult(false, "Not logged in")

        db.collection("users").document(uid).get()
            .addOnSuccessListener { userDoc ->
                val regNumber = userDoc.getString("regNumber")?.trim() ?: ""
                if (regNumber.isEmpty()) return@addOnSuccessListener onResult(false, "No reg number found")

                // Create the registration object using the exact reg number
                val registration = Registration(
                    studentRegNumber = regNumber,
                    unitCode = unit.unitCode,
                    unitName = unit.unitName,
                    courseCode = unit.courseCode
                )

                // FIX: Replace slashes with underscores so Firestore doesn't create sub-collections
                val safeRegNumber = regNumber.replace("/", "-")
                val docId = "${safeRegNumber}_${unit.unitCode}"

                db.collection("registrations").document(docId).set(registration)
                    .addOnSuccessListener { onResult(true, null) }
                    .addOnFailureListener { e -> onResult(false, e.message) }
            }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    // Fetch the units the student has registered for
    fun getMyRegisteredUnits(onResult: (List<Registration>?, String?) -> Unit) {
        val uid = auth.currentUser?.uid
        if (uid == null) return onResult(null, "Not logged in")

        db.collection("users").document(uid).get()
            .addOnSuccessListener { userDoc ->
                val regNumber = userDoc.getString("regNumber")?.trim() ?: ""
                if (regNumber.isEmpty()) return@addOnSuccessListener onResult(null, "No reg number found")

                // This queries the database for the exact string "BSCS/2025/52691"
                db.collection("registrations").whereEqualTo("studentRegNumber", regNumber).get()
                    .addOnSuccessListener { result ->
                        val list = mutableListOf<Registration>()
                        for (doc in result) {
                            val reg = doc.toObject(Registration::class.java)
                            if (reg != null) list.add(reg)
                        }
                        onResult(list, null)
                    }
                    .addOnFailureListener { e -> onResult(null, e.message) }
            }
            .addOnFailureListener { e -> onResult(null, e.message) }
    }
}