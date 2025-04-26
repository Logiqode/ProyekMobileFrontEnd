package com.example.bookminton.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bookminton.pages.BookingScreen
import com.example.bookminton.pages.HomeScreen
import com.example.bookminton.pages.TransactionsScreen

sealed class Screen(val route: String, val icon: ImageVector, val label: String) {
    object Home : Screen("home", Icons.Default.Home, "Home")
    object Transactions : Screen("transactions", Icons.Default.Email, "Transactions")
    object Booking : Screen("booking", Icons.Default.DateRange, "Booking")
}
@Composable
fun BadmintonApp(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(Screen.Transactions.route) {
            TransactionsScreen(navController)
        }
        composable(Screen.Booking.route) {
            BookingScreen(navController)
        }
    }
}