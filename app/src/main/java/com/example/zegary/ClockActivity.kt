package com.example.zegary

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zegary.ui.theme.ZegaryTheme

class ClockActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Lab6_Lifecycle", "ClockActivity: onCreate")

        val zones = intent.getStringArrayListExtra(EXTRA_ZONES) ?: emptyList()

        enableEdgeToEdge()
        setContent {
            ZegaryTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ZonesPreviewScreen(
                        zones = zones,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("Lab6_Lifecycle", "ClockActivity: onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("Lab6_Lifecycle", "ClockActivity: onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("Lab6_Lifecycle", "ClockActivity: onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("Lab6_Lifecycle", "ClockActivity: onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Lab6_Lifecycle", "ClockActivity: onDestroy")
    }
}

@Composable
fun ZonesPreviewScreen(zones: List<String>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Przekazane strefy czasowe",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (zones.isEmpty()) {
            Text(text = "Brak wybranych stref", fontSize = 18.sp)
        } else {
            zones.forEach { zone ->
                Text(
                    text = "• $zone",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
        }
    }
}