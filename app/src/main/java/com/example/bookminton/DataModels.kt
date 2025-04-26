package com.example.bookminton

import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class Booking(
    val id: String = UUID.randomUUID().toString(),
    val courtName: String,
    val courtNumber: String,
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val price: Double,
    val bookingDate: LocalDate = LocalDate.now()
)

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val booking: Booking,
    val status: String = "Successful"
)