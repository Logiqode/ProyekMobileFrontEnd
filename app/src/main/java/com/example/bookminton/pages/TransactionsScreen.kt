package com.example.bookminton.pages

import androidx.compose.foundation.background
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
import com.example.bookminton.dataModel.Transaction
import com.example.bookminton.sampleData.SampleData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(navController: NavHostController) {
    var transactions by remember { mutableStateOf(SampleData.sampleTransactions) }

    // Main container with SoftTeal background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftTeal)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top app bar with LightBlue background
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
                    containerColor = LightBlue
                ),
                modifier = Modifier.statusBarsPadding()
            )

            // Content area
            if (transactions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No transactions found")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp) // Increased spacing
                ) {
                    items(transactions) { transaction ->
                        TransactionCard(transaction = transaction)
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionCard(transaction: Transaction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        shape = RoundedCornerShape(16.dp)
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
                    text = "Transaction #${transaction.id.take(8)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = LightBlue // Consistent with home screen
                )
                Text(
                    text = transaction.status,
                    color = statusGreen // Gray for consistency
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Court information
            Text(
                text = "${transaction.booking.courtName} - Court ${transaction.booking.courtNumber}",
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
                        text = transaction.booking.date.toString(),
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
                        text = "${transaction.booking.startTime} - ${transaction.booking.endTime}",
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Order date and price
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
                        text = transaction.booking.bookingDate.toString(),
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
                        text = "$${transaction.booking.price}",
                        color = LightBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}