package com.example.umma_university

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class ComplaintRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Student: Submit complaint (now requires a department)
    fun submitComplaint(
        message: String,
        department: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: return onResult(false, "Not logged in")

        if (department.isEmpty()) {
            return onResult(false, "Please select a department")
        }

        db.collection("users").document(uid).get().addOnSuccessListener { doc ->
            val regNumber = doc.getString("regNumber") ?: "Unknown"
            val complaint = Complaint(
                studentRegNumber = regNumber,
                message = message,
                status = "Pending",
                department = department
            )

            db.collection("complaints").add(complaint)
                .addOnSuccessListener { onResult(true, null) }
                .addOnFailureListener { e -> onResult(false, e.message) }
        }.addOnFailureListener { e -> onResult(false, e.message) }
    }

    // Student: View own complaints
    fun getMyComplaints(onResult: (List<Complaint>?, String?) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onResult(null, "Not logged in")

        db.collection("users").document(uid).get().addOnSuccessListener { doc ->
            val regNumber = doc.getString("regNumber") ?: ""

            db.collection("complaints")
                .whereEqualTo("studentRegNumber", regNumber)
                .get()
                .addOnSuccessListener { result ->
                    val list = mutableListOf<Complaint>()
                    for (document in result) {
                        val complaint = document.toObject(Complaint::class.java)
                        if (complaint != null) list.add(complaint)
                    }
                    onResult(list, null)
                }.addOnFailureListener { e -> onResult(null, e.message) }
        }.addOnFailureListener { e -> onResult(null, e.message) }
    }

    // Admin: Fetch ALL complaints
    fun getAllComplaints(onResult: (List<Pair<String, Complaint>>?, String?) -> Unit) {
        db.collection("complaints").get()
            .addOnSuccessListener { result ->
                val list = mutableListOf<Pair<String, Complaint>>()
                for (doc in result) {
                    val complaint = doc.toObject(Complaint::class.java)
                    if (complaint != null) {
                        list.add(Pair(doc.id, complaint))
                    }
                }
                onResult(list, null)
            }
            .addOnFailureListener { e -> onResult(null, e.message) }
    }

    // Support: Fetch complaints for a specific department only
    fun getComplaintsByDepartment(
        department: String,
        onResult: (List<Pair<String, Complaint>>?, String?) -> Unit
    ) {
        db.collection("complaints")
            .whereEqualTo("department", department)
            .get()
            .addOnSuccessListener { result ->
                val list = mutableListOf<Pair<String, Complaint>>()
                for (doc in result) {
                    val complaint = doc.toObject(Complaint::class.java)
                    if (complaint != null) list.add(Pair(doc.id, complaint))
                }
                onResult(list, null)
            }
            .addOnFailureListener { e -> onResult(null, e.message) }
    }

    // Admin: Mark a complaint as "Resolved"
    fun resolveComplaint(documentId: String, onResult: (Boolean, String?) -> Unit) {
        db.collection("complaints").document(documentId)
            .update("status", "Resolved")
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    // Support: Approve / Reject / Resolve + audit trail
    fun updateComplaintStatus(
        documentId: String,
        newStatus: String,
        resolverDept: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: ""
        val patch = mapOf(
            "status" to newStatus,
            "resolvedByRole" to "support",
            "resolvedByUid" to uid,
            "resolvedByDept" to resolverDept,
            "resolvedAt" to FieldValue.serverTimestamp()
        )
        db.collection("complaints").document(documentId).update(patch)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    fun submitComplaint(message: String, onResult: (Boolean, String?) -> Unit) {


    }
}