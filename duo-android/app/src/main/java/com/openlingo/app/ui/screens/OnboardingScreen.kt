package com.openlingo.app.ui.screens


import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// -------------------------------------------------------------------------
// First-run onboarding: 3 pages, shown once until dismissed.
// -------------------------------------------------------------------------

@Composable
internal fun OnboardingPager(
    onDone: () -> Unit,
    onTakePlacement: () -> Unit,
) {
    var page by remember { androidx.compose.runtime.mutableStateOf(0) }
    val pages = listOf(
        Triple("🦫", "Learn offline. Own your progress.", "Spanish + Japanese lessons work 100% offline with OpenLingo. No ads, no payments, no account needed."),
        Triple("❤️", "Hearts refill free", "Mistakes cost a heart. Tap the ❤️ badge anytime for a free refill — every midnight refills to full too."),
        Triple("💾", "Yours to take anywhere", "All progress lives on your device. Export a backup file from Settings and import it on any phone — no account, no cloud."),
    )
    val (emoji, title, body) = pages[page.coerceIn(pages.indices)]
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text(
                    text = "Skip",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3D7EA6),
                    modifier = Modifier.clickable(onClick = onDone).padding(8.dp),
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(text = emoji, fontSize = 72.sp)
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF777777),
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                pages.indices.forEach { i ->
                    Box(
                        modifier = Modifier
                            .size(if (i == page) 10.dp else 8.dp)
                            .background(
                                if (i == page) Color(0xFF2E9E6B) else Color(0xFFE5E5E5),
                                CircleShape,
                            ),
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            if (page == pages.lastIndex) {
                Button(
                    onClick = onTakePlacement,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3D7EA6)),
                ) {
                    Text(
                        text = "🎯 TAKE PLACEMENT TEST",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onDone,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E9E6B)),
                ) {
                    Text(
                        text = "START FROM BEGINNING ✓",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                    )
                }
            } else {
                Button(
                    onClick = { page++ },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E9E6B)),
                ) {
                    Text(
                        text = "NEXT",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    )
                }
            }
        }
    }
}
