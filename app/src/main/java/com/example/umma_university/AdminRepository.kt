package com.example.umma_university

import com.google.firebase.firestore.FirebaseFirestore

class AdminRepository {
    private val db = FirebaseFirestore.getInstance()

    fun addCourse(course: Course, onResult: (Boolean, String?) -> Unit) {
        db.collection("courses").document(course.courseCode).set(course)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    fun addUnit(courseUnit: CourseUnit, onResult: (Boolean, String?) -> Unit) {
        db.collection("units").document(courseUnit.unitCode).set(courseUnit)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    fun postAnnouncement(announcement: Announcement, onResult: (Boolean, String?) -> Unit) {
        db.collection("announcements").add(announcement)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    fun addTimetableEntry(entry: TimetableEntry, onResult: (Boolean, String?) -> Unit) {
        db.collection("timetable").add(entry)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    fun addFeeRecord(feeRecord: FeeRecord, onResult: (Boolean, String?) -> Unit) {
        db.collection("fees").document(feeRecord.studentRegNumber).set(feeRecord)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }
}