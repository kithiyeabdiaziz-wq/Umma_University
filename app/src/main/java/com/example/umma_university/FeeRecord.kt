package com.example.umma_university

data class FeeRecord(
    val studentRegNumber: String = "",
    val totalFees: Double = 0.0,
    val amountPaid: Double = 0.0,
    val balance: Double = 0.0,
    val semester: String = ""
)