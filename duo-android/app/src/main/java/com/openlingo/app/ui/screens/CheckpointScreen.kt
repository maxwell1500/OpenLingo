package com.openlingo.app.ui.screens


import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// -------------------------------------------------------------------------
// Checkpoint result screen — shows score, pass/fail, and missed count
// -------------------------------------------------------------------------

@Composable
internal fun CheckpointScreen(
    level: String,
    correct: Int,
    total: Int,
    passed: Boolean,
    tierLabel: String?,
    onDone: () -> Unit,
) {
    val percent = if (total > 0) (correct * 100) / total else 0
    val missed = total - correct

    val trophyScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        ),
        label = "checkpoint_trophy",
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(112.dp)
                    .scale(trophyScale)
                    .background(if (passed) Color(0xFF2E9E6B) else Color(0xFFE67E22), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = if (passed) "✅" else "📝", fontSize = 56.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "$level Checkpoint",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = if (passed) Color(0xFF2E9E6B) else Color(0xFFE67E22),
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "$correct / $total correct",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "$percent%",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            if (tierLabel != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (passed) "Placed into $tierLabel" else "Starting at $tierLabel",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (passed) Color(0xFF2E9E6B) else Color(0xFFE67E22),
                    textAlign = TextAlign.Center,
                )
            }

            if (missed > 0) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "$missed question${if (missed > 1) "s" else ""} added to your practice list",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onDone,
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth(0.7f),
            ) {
                Text(
                    // No review flow exists — the button only closes the result —
                    // so it must not promise one.
                    text = "Continue",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        CelebrationConfetti(modifier = Modifier.fillMaxSize())
    }
}
