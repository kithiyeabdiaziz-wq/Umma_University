package com.example.umma_university

data class LibraryBook(
    val bookId: String = "",
    val title: String = "",
    val author: String = "",
    val location: String = "", // e.g., "Shelf 3, Section B"
    val status: String = "Available", // Available, Borrowed
    val borrowedBy: String = "", // Student Reg Number
    val dueDate: Long = 0L // Timestamp of when it should be returned
)