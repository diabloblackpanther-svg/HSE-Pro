package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Language
import com.example.ui.theme.SafetyOrangePrimary
import com.example.ui.util.HseStrings
import kotlin.math.abs

@Composable
fun QrCodeGenerator(
    contentData: String,
    permitNumber: String,
    sizeDp: Dp = 180.dp,
    lang: Language = Language.ARABIC
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(sizeDp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .border(2.dp, SafetyOrangePrimary, RoundedCornerShape(16.dp))
                .padding(12.dp)
                .testTag("qr_code_canvas")
        ) {
            Canvas(modifier = Modifier.size(sizeDp - 24.dp)) {
                val gridSize = 15
                val cellSize = size.width / gridSize
                val darkColor = Color(0xFF0F172A)
                val hash = abs(contentData.hashCode())

                // Draw pseudo 2D QR matrix based on hash and position
                for (row in 0 until gridSize) {
                    for (col in 0 until gridSize) {
                        // Check if corner position patterns (top-left, top-right, bottom-left)
                        val isTopLeftCorner = row in 0..3 && col in 0..3
                        val isTopRightCorner = row in 0..3 && col in (gridSize - 4) until gridSize
                        val isBottomLeftCorner = row in (gridSize - 4) until gridSize && col in 0..3

                        if (isTopLeftCorner || isTopRightCorner || isBottomLeftCorner) {
                            val r = if (isTopLeftCorner) row else if (isTopRightCorner) row else row - (gridSize - 4)
                            val c = if (isTopLeftCorner) col else if (isTopRightCorner) col - (gridSize - 4) else col
                            val isOuter = r == 0 || r == 3 || c == 0 || c == 3
                            val isInner = r == 1 || r == 2
                            if (isOuter || (isInner && c in 1..2)) {
                                drawRoundRect(
                                    color = darkColor,
                                    topLeft = Offset(col * cellSize, row * cellSize),
                                    size = Size(cellSize, cellSize),
                                    cornerRadius = CornerRadius(2f, 2f)
                                )
                            }
                        } else if (row in 6..8 && col in 6..8) {
                            // Center area reserved for HSE logo
                            if (row == 7 && col == 7) {
                                drawCircle(
                                    color = SafetyOrangePrimary,
                                    center = Offset(col * cellSize + cellSize / 2, row * cellSize + cellSize / 2),
                                    radius = cellSize * 1.2f
                                )
                            }
                        } else {
                            // Data cells based on hash bit
                            val cellIndex = row * gridSize + col
                            val bit = ((hash shr (cellIndex % 31)) and 1) == 1
                            val bit2 = ((hash xor (cellIndex * 17)) % 3 == 0)
                            if (bit || bit2) {
                                drawRect(
                                    color = darkColor,
                                    topLeft = Offset(col * cellSize, row * cellSize),
                                    size = Size(cellSize * 0.9f, cellSize * 0.9f)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = permitNumber,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 12.sp
        )
        Text(
            text = HseStrings.qrCodeHeader(lang),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            fontSize = 10.sp
        )
    }
}
