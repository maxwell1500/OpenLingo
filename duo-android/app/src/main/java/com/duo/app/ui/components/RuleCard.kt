package com.duo.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duo.app.grammar.GrammarFocus

/**
 * The rule a `ChallengeEntity` teaches, shown above the answer options.
 *
 * Deliberately *not* an option card: it uses the app's note/insight palette
 * (blue left rule + pale blue fill) rather than the option-card white/selection
 * blue, carries a "RULE" label, and only its [onDismiss] button is clickable —
 * tapping the body does nothing, so it can never be mistaken for a choice.
 */
@Composable
fun RuleCard(
    ruleText: String,
    grammaticalFocus: String? = null,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FF)),
        border = BorderStroke(1.5.dp, Color(0xFFB8DCFF)),
    ) {
        Column(
            modifier = Modifier.padding(start = 14.dp, end = 14.dp, top = 10.dp, bottom = 6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = GrammarFocus.label(grammaticalFocus)?.let { "RULE • $it" } ?: "RULE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B6FB8),
                )
                TextButton(onClick = onDismiss) {
                    Text(
                        text = "GOT IT",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1CB0F6),
                    )
                }
            }
            Text(
                text = ruleText,
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp,
                color = Color(0xFF1F3A52),
            )
        }
    }
}
