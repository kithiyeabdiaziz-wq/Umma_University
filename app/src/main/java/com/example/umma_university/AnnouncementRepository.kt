package com.example.umma_university

import com.google.firebase.firestore.FirebaseFirestore

class AnnouncementRepository {
    private val db = FirebaseFirestore.getInstance()

    // Fetch only the latest (Used on Dashboard)
    fun getLatestAnnouncement(onResult: (Announcement?, String?) -> Unit) {
        db.collection("announcements").orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING).limit(1).get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val doc = result.documents[0]
                    val announcement = doc.toObject(Announcement::class.java)
                    onResult(announcement, null)
                } else {
                    onResult(null, "No announcements yet")
                }
            }
            .addOnFailureListener { e -> onResult(null, e.message) }
    }

    // NEW: Fetch ALL announcements (Used in News Hub)
    fun getAllAnnouncements(onResult: (List<Announcement>?, String?) -> Unit) {
        db.collection("announcements").orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING).get()
            .addOnSuccessListener { result ->
                val list = mutableListOf<Announcement>()
                for (doc in result) {
                    val announcement = doc.toObject(Announcement::class.java)
                    if (announcement != null) list.add(announcement)
                }
                onResult(list, null)
            }
            .addOnFailureListener { e -> onResult(null, e.message) }
    }

    // NEW: Submit an inquiry
    fun submitInquiry(message: String, onResult: (Boolean, String?) -> Unit) {
        val inquiry = hashMapOf(
            "message" to message,
            "timestamp" to System.currentTimeMillis()
        )
        db.collection("inquiries").add(inquiry)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }
}