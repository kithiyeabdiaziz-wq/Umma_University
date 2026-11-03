package com.example.umma_university

data class User(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val role: String = "", // "student", "lecturer", "admin"
    val regNumber: String = "",
    val department: String = "",
    val phoneNumber: String = "", // NEW FIELD
    val photoUrl: String = ""     // NEW FIELD
)