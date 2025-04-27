package com.example.bookminton.sampleData

import com.example.bookminton.data.Booking
import com.example.bookminton.data.Court
import com.example.bookminton.data.Sport
import com.example.bookminton.data.SportPricing
import com.example.bookminton.data.Venue
import java.time.LocalTime

object SampleData {
    // 1. Define Sports First
    val badminton = Sport(name = "Badminton")
    val basketball = Sport(name = "Basketball")
    val tennis = Sport(name = "Tennis")
    val futsal = Sport(name = "Futsal")

    // 2. Define Venues with their Courts and Sports
    val venues = listOf(
        Venue(
            name = "Merak Merah Badminton",
            address = "123 Sports Lane, Jakarta",
            facilities = listOf("Showers", "Lockers", "Cafe"),
            courts = listOf(
                Court(
                    courtNumber = "Badminton 1",
                    sports = listOf(SportPricing(badminton, 50000.0))
                ),
                Court(
                    courtNumber = "Badminton 2",
                    sports = listOf(SportPricing(badminton, 50000.0))
                ),
                Court(
                    courtNumber = "Badminton 3",
                    sports = listOf(SportPricing(badminton, 50000.0))
                ),
                Court(
                    courtNumber = "Badminton VIP 1",
                    sports = listOf(SportPricing(badminton, 60000.0))
                ),
                Court(
                    courtNumber = "Badminton VIP 2",
                    sports = listOf(SportPricing(badminton, 60000.0))
                ),
            ),
            openHours = LocalTime.of(8, 0) to LocalTime.of(22, 0)
        ),
        Venue(
            name = "Garuda Sports Arena",
            address = "456 Champion Road, Bandung",
            facilities = listOf("Parking", "Pro Shop"),
            courts = listOf(
                Court(
                    courtNumber = "Futsal 1",
                    sports = listOf(SportPricing(futsal, 70000.0))
                ),
                Court(
                    courtNumber = "Futsal 2",
                    sports = listOf(SportPricing(futsal, 70000.0))
                ),
                Court(
                    courtNumber = "Basketball 1",
                    sports = listOf(SportPricing(basketball, 70000.0))
                ),
                Court(
                    courtNumber = "Basketball 2",
                    sports = listOf(SportPricing(basketball, 70000.0))
                )
            ),
            openHours = LocalTime.of(7, 0) to LocalTime.of(21, 0)
        ),
        Venue(
            name = "Elang Geulis Fitness",
            address = "789 Fitness Street, Surabaya",
            facilities = listOf("GYM", "Sauna", "Showers", "Lockers"),
            courts = listOf(
                Court(
                    courtNumber = "Tennis 1",
                    sports = listOf(SportPricing(tennis, 80000.0))
                ),
                Court(
                    courtNumber = "Tennis 2",
                    sports = listOf(SportPricing(tennis, 80000.0))
                ),
                Court(
                    courtNumber = "Badminton 1",
                    sports = listOf(SportPricing(badminton, 55000.0))
                ),
                Court(
                    courtNumber = "Badminton 2",
                    sports = listOf(SportPricing(badminton, 55000.0))
                )
            ),
            openHours = LocalTime.of(6, 0) to LocalTime.of(22, 0)
        ),
        Venue(
            name = "New Parrots Basketball",
            address = "321 Bird Avenue, Bali",
            facilities = listOf("Cafeteria", "Lounge"),
            courts = listOf(
                Court(
                    courtNumber = "Badminton 1",
                    sports = listOf(SportPricing(badminton, 60000.0))
                ),
                Court(
                    courtNumber = "Basketball 1",
                    sports = listOf(SportPricing(basketball, 90000.0))
                )
            ),
            openHours = LocalTime.of(8, 0) to LocalTime.of(0, 0)
        )
    )
}
