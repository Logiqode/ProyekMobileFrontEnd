package com.example.bookminton

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.bookminton.navigation.BadmintonApp
import com.example.bookminton.navigation.BottomNavigationBar
import androidx.navigation.NavHostController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    ScaffoldWithBottomNav(navController)
                }
            }
        }
    }
}

@Composable
fun ScaffoldWithBottomNav(navController: NavHostController) {
    androidx.compose.material3.Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { padding ->
        BadmintonApp(
            navController = navController,
            modifier = Modifier.padding(padding)

        )
    }
}