package com.example.bookminton.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bookminton.pages.BookingScreen
import com.example.bookminton.pages.HomeScreen
import com.example.bookminton.pages.TransactionsScreen
import com.example.bookminton.pages.BookingFormScreen

sealed class Screen(val route: String, val icon: ImageVector, val label: String) {
    object Home : Screen("home", Icons.Default.Home, "Home")
    object Transactions : Screen("transactions", Icons.Default.Email, "Transactions")
    object Booking : Screen("booking", Icons.Default.DateRange, "Booking")
    object BookingForm : Screen("booking/{courtName}/{courtAddress}", Icons.Default.Edit, "Booking Form"){
        fun createRoute(courtName: String, courtAddress: String) = "booking/$courtName/$courtAddress"
    }
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
        composable(
            Screen.BookingForm.route,
            arguments = listOf(
                navArgument("courtName") { type = NavType.StringType },
                navArgument("courtAddress") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val courtName = backStackEntry.arguments?.getString("courtName") ?: ""
            val courtAddress = backStackEntry.arguments?.getString("courtAddress") ?: ""
            BookingFormScreen(
                navController,
                courtName,
                courtAddress
            )
        }
    }
}