package com.openlingo.app.ui.screens


import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// -------------------------------------------------------------------------
// Screen 3: Lesson Complete (Celebration, XP Earned, Saved Locally)
// -------------------------------------------------------------------------

@Composable
internal fun LessonCompleteScreen(
    pointsGained: Int,
    perfectBonus: Int,
    onContinue: () -> Unit,
) {
    // XP count-up dopamine: 0 -> pointsGained over ~1s.
    var shownXp by remember { mutableIntStateOf(0) }
    LaunchedEffect(pointsGained) {
        val steps = 20
        repeat(steps) { i ->
            kotlinx.coroutines.delay(50)
            shownXp = (pointsGained * (i + 1)) / steps
        }
    }
    // Trophy pop-in with a bouncy spring.
    val trophyScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        ),
        label = "trophy",
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
                    .background(Color(0xFFE8A93C), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "🏆", fontSize = 60.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Lesson Complete!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E9E6B),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "+$shownXp XP earned and saved to your device",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE8A93C),
                textAlign = TextAlign.Center,
            )
            if (perfectBonus > 0) {
                Text(
                    text = "✨ FLAWLESS! +$perfectBonus bonus XP",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE67E22),
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E9E6B)),
            ) {
                Text(text = "CONTINUE LEARNING", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
        // Confetti rain overlay (pure Canvas, ~2.5s, zero dependencies).
        CelebrationConfetti(modifier = Modifier.fillMaxSize())
    }
}

private data class ConfettiParticle(
    val x: Float,
    val y0: Float,
    val speed: Float,
    val size: Float,
    val color: Color,
    val drift: Float,
)

@Composable
internal fun CelebrationConfetti(modifier: Modifier = Modifier) {
    var lifetime by remember { mutableStateOf(0) }
    val particles = remember {
        val rng = kotlin.random.Random(System.currentTimeMillis())
        val palette = listOf(
            Color(0xFF2E9E6B), Color(0xFF3D7EA6), Color(0xFFE8A93C),
            Color(0xFFC0392B), Color(0xFFCE82FF), Color(0xFFE67E22),
        )
        List(90) {
            ConfettiParticle(
                x = rng.nextFloat(),
                y0 = -rng.nextFloat() * 0.25f,
                speed = 0.35f + rng.nextFloat() * 0.45f,
                size = 8f + rng.nextFloat() * 14f,
                color = palette[rng.nextInt(palette.size)],
                drift = (rng.nextFloat() - 0.5f) * 0.2f,
            )
        }
    }
    LaunchedEffect(Unit) {
        repeat(50) {
            kotlinx.coroutines.delay(50)
            lifetime++
        }
    }
    if (lifetime < 50) {
        Canvas(modifier = modifier) {
            val t = lifetime / 50f
            particles.forEach { p ->
                val y = (p.y0 + t * p.speed) % 1.2f
                drawCircle(
                    color = p.color,
                    radius = p.size,
                    center = androidx.compose.ui.geometry.Offset(
                        x = (p.x + t * p.drift + 1f) % 1f * size.width,
                        y = y * size.height,
                    ),
                    alpha = 1f - t * t,
                )
            }
        }
    }
}
