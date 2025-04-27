package com.example.bookminton.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.bookminton.ui.theme.*
import androidx.compose.ui.Alignment
import com.example.bookminton.data.Booking
import com.example.bookminton.data.Transaction
import com.example.bookminton.data.DataSingleton
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(navController: NavHostController) {
    val transactions = remember { DataSingleton.transactions }
    val bookings = remember { DataSingleton.bookings }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Your Transactions",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = TealBlack
                )
            )
        }
    ) { padding ->
        if (transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No transactions found")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(transactions) { transaction ->
                    // Find the corresponding booking
                    val booking = bookings.firstOrNull { it.bookingId == transaction.bookingId }
                    if (booking != null) {
                        TransactionCard(
                            transaction = transaction,
                            booking = booking
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionCard(
    transaction: Transaction,
    booking: Booking
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Transaction header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Transaction #${transaction.transactionId.take(8)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TealBlack
                )
                Text(
                    text = transaction.status,
                    color = statusGreen
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Court information
            Text(
                text = "${booking.venueName} - Court ${booking.courtNumber}",
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Date and time row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Booked Date",
                        color = Color.Gray,
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = booking.date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                        color = Color.Black
                    )
                }
                Column {
                    Text(
                        text = "Time Slot",
                        color = Color.Gray,
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = "${booking.startTime.format(DateTimeFormatter.ofPattern("hh:mm a"))} - " +
                                "${booking.endTime.format(DateTimeFormatter.ofPattern("hh:mm a"))}",
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Transaction details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Transaction Date",
                        color = Color.Gray,
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = booking.bookingDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                        color = Color.Black
                    )
                }
                Column {
                    Text(
                        text = "Total Price",
                        color = Color.Gray,
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = "Rp ${transaction.amount.toInt()}",
                        color = TealBlack,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}