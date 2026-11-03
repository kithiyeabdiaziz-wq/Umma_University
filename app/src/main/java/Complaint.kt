package com.example.umma_university

data class Complaint(
    val studentRegNumber: String = "",
    val message: String = "",
    val status: String = "Pending",
    val department: String = ""
)