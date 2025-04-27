package com.example.bookminton.pages

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.foundation.shape.RoundedCornerShape
import java.time.LocalDate
import java.time.LocalTime
import com.example.bookminton.ui.theme.*
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Icon
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import com.example.bookminton.data.Booking
import com.example.bookminton.navigation.Screen
import kotlinx.coroutines.delay
import java.time.format.DateTimeFormatter
import com.example.bookminton.data.DataSingleton
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.input.ImeAction
import java.time.temporal.ChronoUnit
import com.example.bookminton.data.Sport
import com.example.bookminton.data.Court

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingFormScreen(
    navController: NavHostController,
    venueId: String,
    courtId: String
) {
    // Get venue and court data
    val venue = remember { DataSingleton.venues.firstOrNull { it.venueId == venueId } }
    val court = remember { venue?.courts?.firstOrNull { it.courtId == courtId } }

    // Fallback if data not found
    if (venue == null || court == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Court information not available")
        }
        return
    }

    // State variables
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var startTime by remember { mutableStateOf(LocalTime.MIN) }
    var endTime by remember { mutableStateOf(LocalTime.MIN) }
    var selectedSport by remember { mutableStateOf<Sport?>(court.sports.firstOrNull()?.sport) }
    val showToast = remember { mutableStateOf(false) }
    val toastMessage = remember { mutableStateOf("") }
    val isSuccess = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var dateError by remember { mutableStateOf(false) }

    // Time options based on venue hours
    val timeOptions = remember(venue) {
        val startHour = venue.openHours.first.hour
        val endHour = venue.openHours.second.hour
        (startHour..endHour).map { hour -> LocalTime.of(hour, 0) }
    }

    // Calculate price
    val price = remember(selectedSport, startTime, endTime) {
        selectedSport?.let { sport ->
            court.sports.firstOrNull { it.sport == sport }?.let { pricing ->
                ChronoUnit.HOURS.between(startTime, endTime).toDouble() * pricing.pricePerHour
            }
        } ?: 0.0
    }

    // Toast handler
    if (showToast.value) {
        LaunchedEffect(Unit) {
            delay(3500)
            showToast.value = false
            if (isSuccess.value) {
                navController.popBackStack()
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Book Court",
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
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { focusManager.clearFocus() }
                .background(SoftTeal)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Toast notification
                if (showToast.value) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isSuccess.value) statusGreen else statusRed)
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = toastMessage.value,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Court Information Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = venue.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = LightBlue
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Court ${court.courtNumber}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = venue.address,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sport Selection (if court has multiple sports)
                if (court.sports.size > 1) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(
                                text = "Select Sport",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray,
                                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                            )
                            LazyRow(
                                modifier = Modifier.padding(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(court.sports) { sportPricing ->
                                    FilterChip(
                                        selected = selectedSport == sportPricing.sport,
                                        onClick = { selectedSport = sportPricing.sport },
                                        label = {
                                            Text(
                                                "${sportPricing.sport.name} (Rp ${sportPricing.pricePerHour.toInt()}/hour)"
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = LightBlue.copy(alpha = 0.2f),
                                            selectedLabelColor = LightBlue
                                        )
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                // Date Picker
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Booking Date",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                            Text(
                                text = selectedDate?.format(DateTimeFormatter.ofPattern("EEE, MMM d"))
                                    ?: "Select date",
                                color = if (selectedDate == null) Color.LightGray else Color.Black
                            )
                            if (dateError) {
                                Text(
                                    text = "Please select a date",
                                    color = Color.Red,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                        TextButton(
                            onClick = {
                                DatePickerDialog(
                                    context,
                                    { _, year, month, dayOfMonth ->
                                        selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                                        dateError = false
                                    },
                                    LocalDate.now().year,
                                    LocalDate.now().monthValue - 1,
                                    LocalDate.now().dayOfMonth
                                ).apply {
                                    datePicker.minDate = System.currentTimeMillis()
                                    show()
                                }
                            }
                        ) {
                            Text("Select", color = LightBlue)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Time Selection
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TimeSelectionCard(
                        modifier = Modifier.weight(1f),
                        label = "Start Time",
                        selectedTime = startTime,
                        timeOptions = timeOptions.filter {
                            it.isBefore(venue.openHours.second)
                        },
                        onTimeSelected = {
                            startTime = it
                            if (endTime.isBefore(it) || endTime == it) {
                                endTime = it.plusHours(1)
                            }
                        }
                    )

                    TimeSelectionCard(
                        modifier = Modifier.weight(1f),
                        label = "End Time",
                        selectedTime = endTime,
                        timeOptions = timeOptions.filter {
                            // Must be after start time and within venue hours
                            it.isAfter(startTime) && it <= venue.openHours.second
                        },
                        onTimeSelected = { endTime = it }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Price Summary
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Cream),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Booking Summary",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Duration")
                            Text("${ChronoUnit.HOURS.between(startTime, endTime)} hours")
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Price/hour")
                            Text("Rp ${selectedSport?.let {
                                court.sports.firstOrNull { s -> s.sport == it }?.pricePerHour?.toInt()
                            }}")
                        }
                        Divider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = Color.Gray.copy(alpha = 0.2f)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total Price",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Rp ${price.toInt()}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = LightBlue
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Confirm Button
                Button(
                    onClick = {
                        dateError = selectedDate == null

                        if (!dateError && selectedSport != null) {
                            DataSingleton.createBooking(
                                venueId = venueId,
                                courtId = courtId,
                                sport = selectedSport!!,
                                date = selectedDate!!,
                                startTime = startTime,
                                endTime = endTime
                            )
                            toastMessage.value = "Booking successful!"
                            isSuccess.value = true
                            showToast.value = true
                        } else {
                            toastMessage.value = "Please complete all fields"
                            isSuccess.value = false
                            showToast.value = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LightBlue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = selectedDate != null && startTime != LocalTime.MIN && endTime != LocalTime.MIN
                ) {
                    Text("Confirm Booking", fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun TimeSelectionCard(
    modifier: Modifier = Modifier,
    label: String,
    selectedTime: LocalTime,
    timeOptions: List<LocalTime>,
    onTimeSelected: (LocalTime) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
                    .padding(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (selectedTime == LocalTime.MIN) "Select"
                        else selectedTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                        color = if (selectedTime == LocalTime.MIN) Color.LightGray else Color.Black
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = if (selectedTime == LocalTime.MIN) Color.LightGray else Color.Gray
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    timeOptions.forEach { time ->
                        DropdownMenuItem(
                            text = {
                                Text(time.format(DateTimeFormatter.ofPattern("HH:mm")))
                            },
                            onClick = {
                                onTimeSelected(time)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}