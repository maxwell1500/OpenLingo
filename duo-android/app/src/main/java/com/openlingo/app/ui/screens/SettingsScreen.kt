package com.openlingo.app.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openlingo.app.data.local.entities.UserProgressEntity
import com.openlingo.app.data.repository.LocalProgressRepository

// -------------------------------------------------------------------------
// Screen: Settings (sound, haptics, romaji, reset, about)
// -------------------------------------------------------------------------

@Composable
internal fun SettingsScreen(
    userProgress: UserProgressEntity?,
    onToggleSound: (Boolean) -> Unit,
    onToggleHaptics: (Boolean) -> Unit,
    onToggleRomaji: () -> Unit,
    onSelectThemeAccent: (String) -> Unit,
    onSelectThemeMode: (String) -> Unit,
    onSelectDailyQuestGoal: (Int) -> Unit,
    onResetProgress: () -> Unit,
    onExportBackup: ((String) -> Unit) -> Unit,
    onImportBackup: (String, (Boolean) -> Unit) -> Unit,
    onBack: () -> Unit,
) {
    var showResetConfirm by remember { androidx.compose.runtime.mutableStateOf(false) }
    var showImportDialog by remember { androidx.compose.runtime.mutableStateOf(false) }
    var importJsonText by remember { androidx.compose.runtime.mutableStateOf("") }
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("openlingo_settings", android.content.Context.MODE_PRIVATE) }
    var remindersEnabled by remember { androidx.compose.runtime.mutableStateOf(prefs.getBoolean("reminders_enabled", true)) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "←",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3D7EA6),
                modifier = Modifier.clickable(onClick = onBack).padding(8.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
        }

        SettingRow(
            emoji = "🔊",
            title = "Sound effects",
            subtitle = "Kokoro voice, chimes, fanfare",
            checked = userProgress?.soundEnabled ?: true,
            onCheckedChange = onToggleSound,
        )
        SettingRow(
            emoji = "📳",
            title = "Vibration",
            subtitle = "Haptic feedback on answers",
            checked = userProgress?.hapticsEnabled ?: true,
            onCheckedChange = onToggleHaptics,
        )
        SettingRow(
            emoji = "🈁",
            title = "Show romaji",
            subtitle = "Pronunciation hints under Japanese",
            checked = userProgress?.showRomaji ?: true,
            onCheckedChange = { onToggleRomaji() },
        )
        SettingRow(
            emoji = "🔔",
            title = "Daily reminder",
            subtitle = "7 PM nudge if you haven't practiced yet",
            checked = remindersEnabled,
            onCheckedChange = { enabled ->
                remindersEnabled = enabled
                prefs.edit().putBoolean("reminders_enabled", enabled).apply()
            },
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Daily goal (WI-13): the XP the learner has chosen to chase each day.
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "🎯", fontSize = 24.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Daily goal", fontWeight = FontWeight.Bold)
                Text(
                    text = "XP per day to finish your quest",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF777777),
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            LocalProgressRepository.DAILY_QUEST_XP_OPTIONS.forEach { goal ->
                val isSelected = (userProgress?.dailyQuestGoal ?: LocalProgressRepository.DAILY_QUEST_XP) == goal
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Color(0xFF2E9E6B).copy(alpha = 0.15f) else Color(0xFFF7F7F7))
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) Color(0xFF2E9E6B) else Color(0xFFE5E5E5),
                            shape = RoundedCornerShape(12.dp),
                        )
                        .clickable { onSelectDailyQuestGoal(goal) }
                        .padding(vertical = 10.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "$goal",
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color(0xFF2E9E6B) else Color(0xFF777777),
                    )
                }
            }
        }



        // Theme Accent Selector
        Text(
            text = "Theme Accent",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4B4B4B),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            com.openlingo.app.ui.theme.ThemeAccent.entries.forEach { accent ->
                val isSelected = userProgress?.themeAccent?.equals(accent.name, ignoreCase = true) == true ||
                    (userProgress?.themeAccent == null && accent == com.openlingo.app.ui.theme.ThemeAccent.TEAL)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) accent.swatchColor.copy(alpha = 0.15f) else Color(0xFFF7F7F7))
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) accent.swatchColor else Color(0xFFE5E5E5),
                            shape = RoundedCornerShape(12.dp),
                        )
                        .clickable { onSelectThemeAccent(accent.name) }
                        .padding(vertical = 10.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(accent.swatchColor, CircleShape)
                        )
                        Text(
                            text = accent.name.lowercase().replaceFirstChar { it.uppercase() },
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) accent.swatchColor else Color(0xFF777777),
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Appearance (System / Light / Dark)
        Text(
            text = "Appearance",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4B4B4B),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            listOf(
                com.openlingo.app.ui.theme.ThemeMode.SYSTEM to "📱 System",
                com.openlingo.app.ui.theme.ThemeMode.LIGHT to "🌞 Light",
                com.openlingo.app.ui.theme.ThemeMode.DARK to "🌙 Dark",
            ).forEach { (mode, label) ->
                val isSelected = com.openlingo.app.ui.theme.ThemeMode.fromName(userProgress?.themeMode) == mode
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Color(0xFF3D7EA6).copy(alpha = 0.15f) else Color(0xFFF7F7F7))
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) Color(0xFF3D7EA6) else Color(0xFFE5E5E5),
                            shape = RoundedCornerShape(12.dp),
                        )
                        .clickable { onSelectThemeMode(mode.name) }
                        .padding(vertical = 10.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = label,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color(0xFF3D7EA6) else Color(0xFF777777),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Backup and Restore
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            OutlinedButton(
                onClick = {
                    onExportBackup { json ->
                        val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(android.content.Intent.EXTRA_SUBJECT, "OpenLingo Backup")
                            putExtra(android.content.Intent.EXTRA_TEXT, json)
                        }
                        context.startActivity(android.content.Intent.createChooser(shareIntent, "Share OpenLingo Backup"))
                    }
                },
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(text = "📤 Export", fontWeight = FontWeight.Bold)
            }
            OutlinedButton(
                onClick = { showImportDialog = true },
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(text = "📥 Import", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedButton(
            onClick = { showResetConfirm = true },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(14.dp),
        ) {
            Text(text = "🗑 Reset all progress", color = Color(0xFFC0392B), fontWeight = FontWeight.Bold)
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "Learn offline. Own your progress.", fontWeight = FontWeight.Bold, color = Color(0xFF2E9E6B))
                Text(
                    text = "No ads, no payments, no account needed. Your progress lives only on this device — export a backup in Settings to move it to another phone.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF777777),
                )
            }
        }
    }

    if (showResetConfirm) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text(text = "Reset everything?") },
            text = { Text(text = "XP, streak, hearts, mistakes, and kana stars go back to zero. Lessons stay on the device.") },
            confirmButton = {
                TextButton(onClick = { showResetConfirm = false; onResetProgress() }) {
                    Text(text = "RESET", color = Color(0xFFC0392B), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) { Text(text = "KEEP MY PROGRESS") }
            },
        )
    }

    if (showImportDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text(text = "Import Backup") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Paste your OpenLingo backup JSON below to restore your XP, streak, and completed lessons.",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    androidx.compose.material3.OutlinedTextField(
                        value = importJsonText,
                        onValueChange = { importJsonText = it },
                        placeholder = { Text("{ \"version\": 2, ... }") },
                        modifier = Modifier.fillMaxWidth().height(140.dp),
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onImportBackup(importJsonText) { success ->
                            if (success) {
                                android.widget.Toast.makeText(context, "Progress restored! 🎉", android.widget.Toast.LENGTH_SHORT).show()
                                showImportDialog = false
                                importJsonText = ""
                            } else {
                                android.widget.Toast.makeText(context, "Invalid backup JSON ❌", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E9E6B)),
                ) {
                    Text(text = "RESTORE", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) { Text(text = "CANCEL") }
            },
        )
    }
}

@Composable
private fun SettingRow(
    emoji: String,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Text(text = emoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Bold)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = Color(0xFF777777))
            }
        }
        androidx.compose.material3.Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
