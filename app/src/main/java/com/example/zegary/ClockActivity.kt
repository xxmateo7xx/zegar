package com.example.zegary

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zegary.ui.theme.ZegaryTheme
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs

private val TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

class ClockActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Lab6_Lifecycle", "ClockActivity: onCreate")

        val zones = intent?.getStringArrayListExtra(EXTRA_ZONES) ?: emptyList()

        enableEdgeToEdge()
        setContent {
            ZegaryTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WorldClocksScreen(
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
fun WorldClocksScreen(zones: List<String>, modifier: Modifier = Modifier) {
    val dateFormatter = remember {
        DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.getDefault())
    }

    val nowInstant = Instant.now()
    val localZone = ZoneId.systemDefault()
    val localTime = ZonedDateTime.ofInstant(nowInstant, localZone)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Zegary światowe",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Czas lokalny (${localZone.id})",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = localTime.format(TIME_FORMATTER),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = localTime.format(dateFormatter),
                    fontSize = 14.sp
                )
            }
        }

        if (zones.isEmpty()) {
            Text(text = "Brak wybranych stref", fontSize = 18.sp)
        } else {
            zones.forEach { zoneIdStr ->
                val zoneId = ZoneId.of(zoneIdStr)
                val zoneTime = ZonedDateTime.ofInstant(nowInstant, zoneId)
                val diffText = formatOffsetDifference(
                    localOffsetSeconds = localTime.offset.totalSeconds,
                    targetOffsetSeconds = zoneTime.offset.totalSeconds
                )

                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = zoneIdStr,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = zoneTime.format(dateFormatter),
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Różnica: $diffText",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = zoneTime.format(TIME_FORMATTER),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

private fun formatOffsetDifference(localOffsetSeconds: Int, targetOffsetSeconds: Int): String {
    val diffSeconds = targetOffsetSeconds - localOffsetSeconds
    if (diffSeconds == 0) return "0 h"

    val hours = diffSeconds / 3600
    val minutes = (abs(diffSeconds) % 3600) / 60

    return if (minutes == 0) {
        String.format(Locale.US, "%+d h", hours)
    } else {
        String.format(Locale.US, "%+d:%02d h", hours, minutes)
    }
}