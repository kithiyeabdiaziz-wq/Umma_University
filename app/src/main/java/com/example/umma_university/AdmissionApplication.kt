package com.example.umma_university

data class AdmissionApplication(
    val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val course: String = "",
    val status: String = "Pending"
)