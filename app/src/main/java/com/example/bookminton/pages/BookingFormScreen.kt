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
import androidx.compose.material3.Icon
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import kotlinx.coroutines.delay
import java.time.format.DateTimeFormatter
import com.example.bookminton.data.DataSingleton
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import java.time.temporal.ChronoUnit
import com.example.bookminton.helper.BookingFormHelper

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
    val context = LocalContext.current
    var dateError by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var startTime by remember { mutableStateOf(LocalTime.MIN) }
    var endTime by remember { mutableStateOf(LocalTime.MIN) }
    var selectedSport by remember { mutableStateOf(court.sports.firstOrNull()?.sport) }
    val showToast = remember { mutableStateOf(false) }
    val toastMessage = remember { mutableStateOf("") }
    val isSuccess = remember { mutableStateOf(false) }
    var countdown by remember { mutableIntStateOf(0) }
    val focusManager = LocalFocusManager.current

    // Derived values
    val existingBookings = remember(selectedDate, courtId) {
        derivedStateOf {
            DataSingleton.bookings.filter {
                it.courtId == courtId && it.date == selectedDate
            }
        }.value
    }

    // Time options
    val allTimeOptions = remember(venue) {
        val startHour = venue.openHours.first.hour
        val endHour = venue.openHours.second.hour
        (startHour..endHour).map { hour -> LocalTime.of(hour, 0) }
    }

    // Calculate price
    val price = remember(selectedSport, startTime, endTime) {
        BookingFormHelper.calculatePrice(
            selectedSport,
            court.sports,
            startTime,
            endTime
        )
    }

    fun checkAvailability(start: LocalTime, end: LocalTime): Boolean {
        // Basic validation
        if (end <= start) return false
        if (start < venue.openHours.first || end > venue.openHours.second) return false

        // Same-day booking rules
        if (BookingFormHelper.isSameDayBooking(selectedDate)) {
            val earliestTime = BookingFormHelper.calculateEarliestAvailableTime()
            if (start < earliestTime) return false
        }

        // Check against existing bookings
        return existingBookings.none { booking ->
            (start >= booking.startTime && start < booking.endTime) ||
                    (end > booking.startTime && end <= booking.endTime) ||
                    (start <= booking.startTime && end >= booking.endTime)
        }
    }

    LaunchedEffect(selectedDate) {
        startTime = LocalTime.MIN
        endTime = LocalTime.MIN
    }

    // Toast handler
    if (showToast.value) {
        LaunchedEffect(showToast.value) {
            countdown = 3
            while (countdown > 0) {
                toastMessage.value = when {
                    isSuccess.value -> "Booking Succesful! You will be redirected in $countdown..."
                    else -> toastMessage.value
                }
                delay(1000)
                countdown--
            }
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
                    containerColor = TealBlack
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding())
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { focusManager.clearFocus() }
                .background(Aquamarine)
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
                            color = TealBlack
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
                                            selectedContainerColor = TealBlack.copy(alpha = 0.2f),
                                            selectedLabelColor = TealBlack
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
                            Text("Select", color = TealBlack)
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
                    // Start Time Selection
                    TimeSelectionCard(
                        modifier = Modifier.weight(1f),
                        label = "Start Time",
                        selectedTime = startTime,
                        timeOptions = allTimeOptions,
                        onTimeSelected = { selectedStart ->
                            startTime = selectedStart
                            endTime = allTimeOptions.firstOrNull { end ->
                                end > selectedStart && checkAvailability(selectedStart, end)
                            } ?: selectedStart.plusHours(1)
                        },
                        isTimeAvailable = { potentialStart ->
                            val earliestTime = if (BookingFormHelper.isSameDayBooking(selectedDate)) {
                                BookingFormHelper.calculateEarliestAvailableTime()
                            } else {
                                venue.openHours.first
                            }

                            potentialStart >= earliestTime && allTimeOptions.any { end ->
                                end > potentialStart && checkAvailability(potentialStart, end)
                            }
                        },
                        isSameDayBooking = BookingFormHelper.isSameDayBooking(selectedDate),
                        selectedDate = selectedDate
                    )

                    // End Time Selection
                    TimeSelectionCard(
                        modifier = Modifier.weight(1f),
                        label = "End Time",
                        selectedTime = endTime,
                        timeOptions = allTimeOptions.filter { it > startTime },
                        onTimeSelected = { selectedEnd ->
                            endTime = selectedEnd
                        },
                        isTimeAvailable = { potentialEnd -> checkAvailability(startTime, potentialEnd) },
                        isSameDayBooking = BookingFormHelper.isSameDayBooking(selectedDate),
                        selectedDate = selectedDate
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Price Summary
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = WhiteTan),
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
                                color = TealBlack
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Confirm Button
                Button(
                    onClick = {
                        // Validate all required fields
                        dateError = selectedDate == null
                        val timeError = startTime == LocalTime.MIN || endTime == LocalTime.MIN

                        if (dateError || timeError || selectedSport == null) {
                            toastMessage.value = "Please complete all fields"
                            isSuccess.value = false
                            showToast.value = true
                            return@Button
                        }

                        // Final availability check with all parameters
                        if (!BookingFormHelper.isTimeRangeAvailable(
                                start = startTime,
                                end = endTime,
                                courtStatus = court.status,
                                venueHours = venue.openHours,
                                existingBookings = existingBookings,
                                selectedDate = selectedDate
                            )) {
                            toastMessage.value = "This time slot is no longer available. Please select another time."
                            isSuccess.value = false
                            showToast.value = true
                            return@Button
                        }

                        // Check minimum booking duration (e.g., 1 hour)
                        if (ChronoUnit.HOURS.between(startTime, endTime) < 1) {
                            toastMessage.value = "Minimum booking duration is 1 hour"
                            isSuccess.value = false
                            showToast.value = true
                            return@Button
                        }

                        if (BookingFormHelper.isSameDayBooking(selectedDate)) {
                            val currentTime = LocalTime.now()
                            val bufferTime = currentTime.plusHours(1)
                            val nextFullHour = if (bufferTime.minute > 0) {
                                bufferTime.withMinute(0).plusHours(1)
                            } else {
                                bufferTime.withMinute(0)
                            }

                            if (startTime < nextFullHour) {
                                toastMessage.value = "Same-day bookings must be at least one hour ahead. Earliest available: $nextFullHour"
                                isSuccess.value = false
                                showToast.value = true
                                return@Button
                            }
                        }

                        // All checks passed - create booking
                        DataSingleton.createBooking(
                            venueId = venueId,
                            courtId = courtId,
                            sport = selectedSport!!,
                            date = selectedDate!!,
                            startTime = startTime,
                            endTime = endTime
                        )

                        isSuccess.value = true
                        showToast.value = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TealBlack,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = selectedDate != null &&
                            startTime != LocalTime.MIN &&
                            endTime != LocalTime.MIN &&
                            selectedSport != null
                ) {
                    Text("Confirm Booking", fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun TimeSelectionCard(
    modifier: Modifier = Modifier,
    label: String,
    selectedTime: LocalTime,
    timeOptions: List<LocalTime>,
    onTimeSelected: (LocalTime) -> Unit,
    isTimeAvailable: (LocalTime) -> Boolean,
    isSameDayBooking: Boolean,
    selectedDate: LocalDate?
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
                        val available = isTimeAvailable(time)
                        val tooEarly = isSameDayBooking &&
                                time < BookingFormHelper.calculateEarliestAvailableTime()

                        DropdownMenuItem(
                            text = {
                                Text(
                                    time.format(DateTimeFormatter.ofPattern("HH:mm")) +
                                            when {
                                                isSameDayBooking && time < BookingFormHelper.calculateEarliestAvailableTime() ->
                                                    " (Unavailable)"
                                                !isTimeAvailable(time) -> " (Unavailable)"
                                                else -> ""
                                            },
                                    color = when {
                                        isSameDayBooking && time < BookingFormHelper.calculateEarliestAvailableTime() -> Color.Gray
                                        !isTimeAvailable(time) -> Color.Gray
                                        else -> Color.Black
                                    }
                                )
                            },
                            onClick = {
                                if (available && !tooEarly) {
                                    onTimeSelected(time)
                                    expanded = false
                                }
                            },
                            enabled = available && !tooEarly
                        )
                    }
                }
            }
        }
    }
}