package com.example.umma_university

data class JobPost(
    val title: String = "",
    val company: String = "",
    val deadline: String = "",
    val type: String = "" // "Internship" or "Full-time"
)