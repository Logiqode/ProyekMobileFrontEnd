package com.example.bookminton.data

import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class Booking(
    val bookingId: String = UUID.randomUUID().toString(),
    val venueId: String,
    val venueName: String,
    val courtId: String,
    val courtNumber: String,
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val status: BookingStatus = BookingStatus.UPCOMING,
    val bookingDate: LocalDate = LocalDate.now()
)

enum class BookingStatus {
    UPCOMING, COMPLETED
}

data class Transaction(
    val transactionId: String = UUID.randomUUID().toString(),
    val bookingId: String,
    val amount: Double,
    val status: String = "Successful",
    val courtId: String? = null
)

data class Sport(
    val sportId: String = UUID.randomUUID().toString(),
    val name: String
)

data class SportPricing(
    val sport: Sport,
    val pricePerHour: Double
)

data class Court(
    val courtId: String = UUID.randomUUID().toString(),
    val courtNumber: String,
    val sports: List<SportPricing>,
    val status: CourtStatus = CourtStatus.AVAILABLE
)

enum class CourtStatus {
    AVAILABLE, RESERVED, UNAVAILABLE
}

data class Venue(
    val venueId: String = UUID.randomUUID().toString(),
    val name: String,
    val address: String,
    val facilities: List<String>,
    val courts: List<Court>,
    val openHours: Pair<LocalTime, LocalTime>
)