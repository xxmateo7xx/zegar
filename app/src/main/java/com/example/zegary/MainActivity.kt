package com.example.zegary

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zegary.ui.theme.ZegaryTheme

const val EXTRA_ZONES = "com.example.zegary.EXTRA_ZONES"

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Lab6_Lifecycle", "MainActivity: onCreate")
        enableEdgeToEdge()
        setContent {
            ZegaryTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ZoneSelectionScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("Lab6_Lifecycle", "MainActivity: onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("Lab6_Lifecycle", "MainActivity: onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("Lab6_Lifecycle", "MainActivity: onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("Lab6_Lifecycle", "MainActivity: onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Lab6_Lifecycle", "MainActivity: onDestroy")
    }
}

@Composable
fun ZoneSelectionScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val availableZones = listOf(
        "Europe/Warsaw",
        "Europe/London",
        "America/New_York",
        "Asia/Tokyo",
        "Australia/Sydney"
    )

    var selectedZones by rememberSaveable {
        mutableStateOf(listOf("Europe/Warsaw", "Europe/London", "America/New_York"))
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Wybierz strefy czasowe",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            availableZones.forEach { zone ->
                val isChecked = zone in selectedZones
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedZones = if (isChecked) {
                                selectedZones - zone
                            } else {
                                selectedZones + zone
                            }
                        }
                        .padding(vertical = 8.dp)
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = null
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = zone, fontSize = 18.sp)
                }
            }
        }

        Button(
            onClick = {
                val intent = Intent(context, ClockActivity::class.java).apply {
                    putStringArrayListExtra(EXTRA_ZONES, ArrayList(selectedZones))
                }
                context.startActivity(intent)
            },
            enabled = selectedZones.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(text = "Pokaż zegary", fontSize = 16.sp)
        }
    }
}