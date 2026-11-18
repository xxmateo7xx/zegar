package com.example.zegary

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.zegary.ui.theme.ZegaryTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs

private val TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

@Serializable
data object ZoneSelectionKey : NavKey

@Serializable
data class ClocksKey(val zones: List<String>) : NavKey

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Lab6_Lifecycle", "MainActivity: onCreate")
        enableEdgeToEdge()
        setContent {
            ZegaryTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ZegaryNavApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ZegaryNavApp(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(ZoneSelectionKey)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        modifier = modifier,
        entryProvider = entryProvider {
            entry<ZoneSelectionKey> {
                ZoneSelectionScreen(
                    onShowClocks = { selectedZones ->
                        backStack.add(ClocksKey(selectedZones))
                    }
                )
            }
            entry<ClocksKey> { key ->
                WorldClocksScreen(zones = key.zones)
            }
        }
    )
}

@Composable
fun ZoneSelectionScreen(
    onShowClocks: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
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
            onClick = { onShowClocks(selectedZones) },
            enabled = selectedZones.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(text = "Pokaż zegary", fontSize = 16.sp)
        }
    }
}

@Composable
fun WorldClocksScreen(zones: List<String>, modifier: Modifier = Modifier) {
    val dateFormatter = remember {
        DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.getDefault())
    }

    var nowInstant by remember { mutableStateOf(Instant.now()) }
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (isActive) {
                nowInstant = Instant.now()
                Log.d("Lab6_Tick", "Odświeżono zegary: $nowInstant")
                val millisToNextSecond = 1000L - (System.currentTimeMillis() % 1000L)
                delay(millisToNextSecond)
            }
        }
    }

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