package com.example.bookminton.data

import java.time.LocalDate
import java.time.LocalTime
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

object DataSingleton {
    private val _bookings = mutableStateListOf<Booking>()
    private val _transactions = mutableStateListOf<Transaction>()

    val bookings: List<Booking> get() = _bookings
    val transactions: List<Transaction> get() = _transactions

    fun addBooking(booking: Booking){
        _bookings.add(booking)
        _transactions.add(Transaction(booking = booking))
    }

    fun clearAll(){
        _bookings.clear()
        _transactions.clear()
    }
}