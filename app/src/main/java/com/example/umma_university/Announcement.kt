package com.example.umma_university

data class Announcement(
    val title: String = "",
    val message: String = "",
    val postedBy: String = "", // Admin's email or name
    val timestamp: Long = System.currentTimeMillis()
)