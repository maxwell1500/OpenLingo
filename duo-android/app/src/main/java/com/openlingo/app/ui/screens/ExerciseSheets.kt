package com.openlingo.app.ui.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openlingo.app.data.repository.LocalProgressRepository

@Composable
internal fun OutOfHeartsSheet(
    onRefill: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = "💔", fontSize = 72.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Out of hearts!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFC0392B),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Refill FREE — Learn offline. Own your progress.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF4B4B4B),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(28.dp))
        Button(
            onClick = onRefill,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC0392B)),
        ) {
            Text(text = "❤️ REFILL HEARTS FREE", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = onExit) {
            Text(
                text = "Exit lesson",
                fontWeight = FontWeight.Bold,
                color = Color(0xFFAFAFAF),
            )
        }
    }
}

/**
 * What the top-bar heart badge opens.
 *
 * Refilling is free and always allowed, so this is not a paywall and does not
 * pretend to be one — it is the place that says what hearts *are*, which the
 * badge never could. The previous affordance was a green `+` glyph overlapping
 * the heart and a 1.25x pulse that never stopped; both announced urgency the
 * learner could do nothing about, because the refill was already free.
 *
 * Practice is offered only when it can actually do something: with no queued
 * mistakes there is nothing to re-ask, and a button that starts a session would
 * silently do nothing. [mistakeCount] is passed in rather than read from a flow
 * so the sheet renders purely from what it was handed, like every other overlay
 * in this file.
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
internal fun HeartRefillSheet(
    hearts: Int,
    mistakeCount: Int,
    onRefill: () -> Unit,
    onPracticeMistakes: () -> Unit,
    onDismiss: () -> Unit,
) {
    androidx.compose.material3.ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = "❤️ $hearts / ${LocalProgressRepository.MAX_HEARTS}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFC0392B),
            )
            Text(
                text = "Hearts protect your learning streak. Learn offline. " +
                    "Practice to earn hearts or refill anytime.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF4B4B4B),
            )

            Spacer(modifier = Modifier.height(6.dp))

            Button(
                onClick = onRefill,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC0392B)),
            ) {
                Text(
                    text = "Refill to Full (FREE)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )
            }

            if (mistakeCount > 0 || hearts < LocalProgressRepository.MAX_HEARTS) {
                OutlinedButton(
                    onClick = onPracticeMistakes,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Text(
                        text = "Practice Mistakes (+1 ❤️)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF2E9E6B),
                    )
                }
            }

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "Close",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFAFAFAF),
                )
            }
        }
    }
}
