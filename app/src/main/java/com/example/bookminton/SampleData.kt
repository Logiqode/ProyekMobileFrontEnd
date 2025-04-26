package com.example.bookminton

import java.time.LocalDate
import java.time.LocalTime

object SampleData {
    // Define our 3 locations
    private val eaglesCourt = "Eagles Badminton Court"
    private val falconsArena = "Falcons Badminton Arena"
    private val hawksCenter = "Hawks Sports Center"

    val sampleBookings = listOf(
        Booking(
            courtName = eaglesCourt,
            courtNumber = "1",
            date = LocalDate.now().plusDays(1),
            startTime = LocalTime.of(11, 0),
            endTime = LocalTime.of(13, 0),
            price = 30.0
        ),
        Booking(
            courtName = falconsArena,
            courtNumber = "VIP 3",
            date = LocalDate.now().plusDays(3),
            startTime = LocalTime.of(15, 0),
            endTime = LocalTime.of(17, 0),
            price = 40.0
        ),
        Booking(
            courtName = hawksCenter,
            courtNumber = "2",
            date = LocalDate.now().plusDays(2),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(11, 0),
            price = 35.0
        ),
        Booking(
            courtName = eaglesCourt,
            courtNumber = "2",
            date = LocalDate.now().plusDays(5),
            startTime = LocalTime.of(13, 0),
            endTime = LocalTime.of(15, 0),
            price = 45.0
        )
    )

    val sampleTransactions = listOf(
        Transaction(booking = sampleBookings[0]),
        Transaction(booking = sampleBookings[1]),
        Transaction(booking = sampleBookings[2]),
        Transaction(booking = sampleBookings[3]),
        Transaction(
            booking = Booking(
                courtName = hawksCenter,
                courtNumber = "VIP 1",
                date = LocalDate.now().minusDays(2),
                startTime = LocalTime.of(18, 0),
                endTime = LocalTime.of(20, 0),
                price = 60.0,
                bookingDate = LocalDate.now().minusDays(4)
            )
        ),
        Transaction(
            booking = Booking(
                courtName = falconsArena,
                courtNumber = "4",
                date = LocalDate.now().minusDays(5),
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(12, 0),
                price = 35.0,
                bookingDate = LocalDate.now().minusDays(7)
            )
        ),
        Transaction(
            booking = Booking(
                courtName = eaglesCourt,
                courtNumber = "VIP 1",
                date = LocalDate.now().minusDays(8),
                startTime = LocalTime.of(16, 0),
                endTime = LocalTime.of(18, 0),
                price = 65.0,
                bookingDate = LocalDate.now().minusDays(10)
            )
        ),
        Transaction(
            booking = Booking(
                courtName = hawksCenter,
                courtNumber = "3",
                date = LocalDate.now().minusDays(12),
                startTime = LocalTime.of(14, 0),
                endTime = LocalTime.of(16, 0),
                price = 40.0,
                bookingDate = LocalDate.now().minusDays(14)
            )
        )
    )
}