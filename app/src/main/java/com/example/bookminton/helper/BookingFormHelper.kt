package com.example.bookminton.helper

import com.example.bookminton.data.Booking
import com.example.bookminton.data.CourtStatus
import com.example.bookminton.data.Sport
import com.example.bookminton.data.SportPricing
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit

object BookingFormHelper {
    // Helper function to check if booking is for today
    fun isSameDayBooking(selectedDate: LocalDate?): Boolean {
        return selectedDate == LocalDate.now()
    }

    // Calculate the earliest available time for same-day bookings
    fun calculateEarliestAvailableTime(): LocalTime {
        val currentTime = LocalTime.now()
        // Round up to the next full hour
        return if (currentTime.minute > 0) {
            currentTime.plusHours(1).withMinute(0)
        } else {
            currentTime.withMinute(0)
        } // Add the 1 hour buffer
    }

    // Check if a time range is available
    fun isTimeRangeAvailable(
        start: LocalTime,
        end: LocalTime,
        courtStatus: CourtStatus,
        venueHours: Pair<LocalTime, LocalTime>,
        existingBookings: List<Booking>,
        selectedDate: LocalDate?
    ): Boolean {
        // Check court status
        if (courtStatus != CourtStatus.AVAILABLE) return false

        // Check operating hours
        if (start < venueHours.first || end > venueHours.second) return false

        // Check one-hour buffer for same-day bookings
        if (isSameDayBooking(selectedDate)) {
            val earliestTime = calculateEarliestAvailableTime()
            if (start < earliestTime) return false
        }

        // Check against existing bookings
        return existingBookings.none { booking ->
            (start >= booking.startTime && start < booking.endTime) ||
                    (end > booking.startTime && end <= booking.endTime) ||
                    (start <= booking.startTime && end >= booking.endTime)
        }
    }

    // Generate all possible time options based on venue hours
    fun generateTimeOptions(venueHours: Pair<LocalTime, LocalTime>): List<LocalTime> {
        val startHour = venueHours.first.hour
        val endHour = venueHours.second.hour
        return (startHour..endHour).map { hour -> LocalTime.of(hour, 0) }
    }

    // Calculate booking price
    fun calculatePrice(
        sport: Sport?,
        sportsPricing: List<SportPricing>,
        startTime: LocalTime,
        endTime: LocalTime
    ): Double {
        return sport?.let { selectedSport ->
            sportsPricing.firstOrNull { it.sport == selectedSport }?.let { pricing ->
                ChronoUnit.HOURS.between(startTime, endTime) * pricing.pricePerHour
            }
        } ?: 0.0
    }

}