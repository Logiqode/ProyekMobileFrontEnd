package com.example.bookminton.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.bookminton.ui.theme.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.example.bookminton.data.Booking
import com.example.bookminton.data.BookingStatus
import com.example.bookminton.data.DataSingleton
import com.example.bookminton.navigation.Screen
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(navController: NavHostController) {
    val bookings by remember { mutableStateOf(DataSingleton.bookings) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Teal)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f / 4f)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(TealBlack, Teal)
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.BottomStart)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "User",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = "Welcome back,",
                    color = Color.White,
                    fontSize = 18.sp
                )
                Text(
                    text = "User",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Bottom section (3/4 of screen)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(3f / 4f)
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(Aquamarine)
                .padding(24.dp)
        ) {
            if (bookings.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "You have no upcoming bookings",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                Text(
                    text = "Your Upcoming Bookings",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(bookings.take(3)) { booking ->
                        BookingCard(booking = booking)
                    }
                }
            }

            // This ensures the button stays at the bottom
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.navigate(Screen.Booking.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TealBlack,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Book Now",
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun BookingCard(
    booking: Booking,
    onCardClick: (() -> Unit)? = null // Optional click handler
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onCardClick != null) { onCardClick?.invoke()},
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${booking.venueName} - ${booking.courtNumber}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                // Status Badge
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when (booking.status) {
                                BookingStatus.UPCOMING -> Color(0xFF2196F3).copy(alpha = 0.2f)
                                BookingStatus.COMPLETED -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = booking.status.name.lowercase().replaceFirstChar { it.titlecase() },
                        style = MaterialTheme.typography.labelSmall,
                        color = when (booking.status) {
                            BookingStatus.UPCOMING -> Color(0xFF2196F3)
                            BookingStatus.COMPLETED -> Color(0xFF4CAF50)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Details Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Date",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = booking.date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Column {
                    Text(
                        text = "Time",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${booking.startTime.format(DateTimeFormatter.ofPattern("hh:mm a"))} - " +
                                "${booking.endTime.format(DateTimeFormatter.ofPattern("hh:mm a"))}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Transaction reference (if needed)
            if (onCardClick != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Booking ID: ${booking.bookingId.take(8)}...",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}