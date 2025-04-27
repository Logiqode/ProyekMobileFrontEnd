package com.example.bookminton.data

import java.time.LocalDate
import java.time.LocalTime
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.bookminton.sampleData.*
import java.time.temporal.ChronoUnit

object DataSingleton {
    private val _bookings = mutableStateListOf<Booking>()
    private val _transactions = mutableStateListOf<Transaction>()

    val bookings: List<Booking> get() = _bookings.toList()
    val transactions: List<Transaction> get() = _transactions.toList()
    val venues: List<Venue> = SampleData.venues

    fun createBooking(
        venueId: String,
        courtId: String,
        sport: Sport,
        date: LocalDate,
        startTime: LocalTime,
        endTime: LocalTime
    ): Booking? {
        val venue = venues.firstOrNull { it.venueId == venueId } ?: return null
        val court = venue.courts.firstOrNull { it.courtId == courtId } ?: return null

        // 1. Check court status
        if (court.status != CourtStatus.AVAILABLE) return null

        // 2. Validate operating hours
        if (!isWithinOperatingHours(venue, startTime, endTime)) return null

        // 3. Validate sport availability
        val sportPricing = court.sports.firstOrNull { it.sport == sport } ?: return null

        // 4. Calculate amount
        val durationHours = ChronoUnit.HOURS.between(startTime, endTime).toDouble()
        val amount = durationHours * sportPricing.pricePerHour

        // 5. Create records
        val booking = Booking(
            venueId = venueId,
            venueName = venue.name,
            courtId = courtId, // Added courtId to Booking
            courtNumber = court.courtNumber,
            date = date,
            startTime = startTime,
            endTime = endTime
        )

        _bookings.add(booking)
        _transactions.add(Transaction(bookingId = booking.bookingId, amount = amount))

        return booking
    }

    private fun isWithinOperatingHours(
        venue: Venue,
        startTime: LocalTime,
        endTime: LocalTime
    ): Boolean {
        return startTime >= venue.openHours.first &&
                endTime <= venue.openHours.second &&
                startTime < endTime
    }

    fun getCourtAvailability(
        venueId: String,
        courtId: String,
        date: LocalDate,
        timeSlot: Pair<LocalTime, LocalTime>
    ): CourtStatus {
        val venue = venues.firstOrNull { it.venueId == venueId } ?: return CourtStatus.UNAVAILABLE
        val court = venue.courts.firstOrNull { it.courtId == courtId } ?: return CourtStatus.UNAVAILABLE

        return when {
            court.status == CourtStatus.UNAVAILABLE -> CourtStatus.UNAVAILABLE
            !isWithinOperatingHours(venue, timeSlot.first, timeSlot.second) -> CourtStatus.UNAVAILABLE
            hasBookingConflict(venueId, courtId, date, timeSlot) -> CourtStatus.RESERVED
            else -> CourtStatus.AVAILABLE
        }
    }

    // New helper function (used by both methods)
    private fun hasBookingConflict(
        venueId: String,
        courtId: String,
        date: LocalDate,
        timeSlot: Pair<LocalTime, LocalTime>
    ): Boolean {
        return _bookings.any { booking ->
            booking.venueId == venueId &&
                    booking.courtId == courtId &&
                    booking.date == date &&
                    !(timeSlot.second <= booking.startTime || timeSlot.first >= booking.endTime)
        }
    }

    // New function for batch availability checks
    fun getAvailableCourts(
        sport: Sport,
        date: LocalDate,
        timeSlot: Pair<LocalTime, LocalTime>
    ): List<Pair<Venue, Court>> {
        return venues.flatMap { venue ->
            venue.courts
                .filter { court ->
                    court.sports.any { it.sport == sport } &&
                            court.status == CourtStatus.AVAILABLE &&
                            isWithinOperatingHours(venue, timeSlot.first, timeSlot.second) &&
                            !hasBookingConflict(venue.venueId, court.courtId, date, timeSlot)
                }
                .map { court -> venue to court }
        }
    }
}