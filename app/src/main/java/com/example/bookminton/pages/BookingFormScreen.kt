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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingFormScreen(
    navController: NavHostController,
    courtName: String,
    courtAddress: String
) {
    var courtNumber by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var startTime by remember { mutableStateOf(LocalTime.of(8, 0)) }
    var endTime by remember { mutableStateOf(LocalTime.of(9, 0)) }
    val showToast = remember { mutableStateOf(false) }
    val toastMessage = remember { mutableStateOf("") }
    val isSuccess = remember { mutableStateOf(true) }
    val context = LocalContext.current
    var courtNumberError by remember { mutableStateOf(false) }

    // Time options
    val timeOptions = remember {
        (8..22).map { hour ->
            LocalTime.of(hour, 0)
        }
    }

    if (showToast.value) {
        LaunchedEffect(Unit) {
            delay(3500)
            showToast.value = false
            if (isSuccess.value) {
                navController.navigate(Screen.Transactions.route) {
                    popUpTo(Screen.Home.route)
                }
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
                .padding(padding)
                .background(SoftTeal)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Toast notification
                if (showToast.value) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isSuccess.value) statusGreen else statusRed)
                            .padding(8.dp),
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

                // Court Information
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = courtName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = LightBlue
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Address: $courtAddress",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Court Number Input
                OutlinedTextField(
                    value = courtNumber,
                    onValueChange = {
                        courtNumber = it
                        courtNumberError = it.isBlank()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Court Number") },
                    isError = courtNumberError,
                    supportingText = {
                        if (courtNumberError) {
                            Text("Court number is required", color = Color.Red)
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LightBlue,
                        unfocusedBorderColor = if (courtNumberError) Color.Red else Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Date Picker
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Date: $selectedDate",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(
                        onClick = {
                            DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                                },
                                selectedDate.year,
                                selectedDate.monthValue - 1,
                                selectedDate.dayOfMonth
                            ).apply {
                                datePicker.minDate = System.currentTimeMillis()
                                show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LightBlue,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Select Date")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Time Selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Start Time", style = MaterialTheme.typography.labelSmall)
                        TimeDropdown(
                            selectedTime = startTime,
                            onTimeSelected = { startTime = it },
                            timeOptions = timeOptions
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text("End Time", style = MaterialTheme.typography.labelSmall)
                        TimeDropdown(
                            selectedTime = endTime,
                            onTimeSelected = { endTime = it },
                            timeOptions = timeOptions.filter { it.isAfter(startTime) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Price Display
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total Price",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$30.0",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = LightBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Confirm Button
                Button(
                    onClick = {
                        if (courtNumber.isBlank()) {
                            courtNumberError = true
                            toastMessage.value = "Please fill in court number"
                            isSuccess.value = false
                            showToast.value = true
                        } else {
                            val booking = Booking(
                                courtName = courtName,
                                courtNumber = courtNumber,
                                date = selectedDate,
                                startTime = startTime,
                                endTime = endTime,
                                price = 30.0
                            )

                            DataSingleton.addBooking(booking)

                            toastMessage.value = "Booking successful. You will be redirected to the Transactions page in 3 seconds."
                            isSuccess.value = true
                            showToast.value = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LightBlue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Confirm Booking", fontSize = 18.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeDropdown(
    selectedTime: LocalTime,
    onTimeSelected: (LocalTime) -> Unit,
    timeOptions: List<LocalTime>
) {
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    Box(modifier = Modifier.fillMaxWidth()) {
        // This Box will handle the click events
        Box(
            modifier = Modifier.clickable { expanded = true }
        ) {
            OutlinedTextField(
                value = selectedTime.toString(),
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select time")
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LightBlue,
                    unfocusedBorderColor = Color.Gray
                ),
                // Remove the onClick parameter and make it read-only
                enabled = false,
                interactionSource = interactionSource
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            timeOptions.forEach { time ->
                DropdownMenuItem(
                    text = { Text(time.format(DateTimeFormatter.ofPattern("HH:mm"))) },
                    onClick = {
                        onTimeSelected(time)
                        expanded = false
                    }
                )
            }
        }
    }
}