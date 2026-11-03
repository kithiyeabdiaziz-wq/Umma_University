package com.example.umma_university

data class ScheduleItem(
    val unitCode: String = "",
    val type: String = "", // "LECTURE" or "EXAM"
    val dayDate: String = "", // e.g., "Monday" or "12th Dec 2024"
    val time: String = "", // e.g., "08:00 AM"
    val venue: String = "" // e.g., "LT-1" or "Exam Hall A"
)