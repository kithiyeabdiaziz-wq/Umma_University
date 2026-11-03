package com.example.umma_university

import com.google.firebase.firestore.FirebaseFirestore

class TimetableRepository {
    private val db = FirebaseFirestore.getInstance()

    // Fetch Lectures
    fun getAllTimetableEntries(onResult: (List<TimetableEntry>?, String?) -> Unit) {
        db.collection("timetable").get()
            .addOnSuccessListener { result ->
                val list = mutableListOf<TimetableEntry>()
                for (doc in result) {
                    val entry = doc.toObject(TimetableEntry::class.java)
                    if (entry != null) list.add(entry)
                }
                onResult(list, null)
            }
            .addOnFailureListener { e -> onResult(null, e.message) }
    }

    // NEW: Fetch Exams
    fun getAllExams(onResult: (List<ScheduleItem>?, String?) -> Unit) {
        db.collection("exams").get()
            .addOnSuccessListener { result ->
                val list = mutableListOf<ScheduleItem>()
                for (doc in result) {
                    val exam = doc.toObject(ScheduleItem::class.java)
                    if (exam != null) list.add(exam)
                }
                onResult(list, null)
            }
            .addOnFailureListener { e -> onResult(null, e.message) }
    }
}