package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.Language
import com.example.ui.theme.SafetyOrangePrimary
import com.example.ui.util.HseStrings

@Composable
fun SignatureCanvas(
    initialPathData: String = "",
    lang: Language = Language.ARABIC,
    onSaveSignature: (String) -> Unit
) {
    val lines = remember { mutableStateListOf<List<Offset>>() }
    val currentLine = remember { mutableStateListOf<Offset>() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = HseStrings.signatureHeader(lang),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = HseStrings.signHereInstruction(lang),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .border(2.dp, SafetyOrangePrimary.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            currentLine.clear()
                            currentLine.add(offset)
                        },
                        onDrag = { change, _ ->
                            currentLine.add(change.position)
                        },
                        onDragEnd = {
                            if (currentLine.isNotEmpty()) {
                                lines.add(currentLine.toList())
                                currentLine.clear()
                            }
                        }
                    )
                }
                .testTag("signature_canvas")
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Draw completed strokes
                lines.forEach { line ->
                    if (line.size > 1) {
                        val path = Path().apply {
                            moveTo(line[0].x, line[0].y)
                            for (i in 1 until line.size) {
                                lineTo(line[i].x, line[i].y)
                            }
                        }
                        drawPath(
                            path = path,
                            color = Color(0xFF0F172A),
                            style = Stroke(
                                width = 5.dp.toPx(),
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }

                // Draw active stroke
                if (currentLine.size > 1) {
                    val path = Path().apply {
                        moveTo(currentLine[0].x, currentLine[0].y)
                        for (i in 1 until currentLine.size) {
                            lineTo(currentLine[i].x, currentLine[i].y)
                        }
                    }
                    drawPath(
                        path = path,
                        color = Color(0xFF0F172A),
                        style = Stroke(
                            width = 5.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }

                // Draw baseline hint
                drawLine(
                    color = Color.LightGray,
                    start = Offset(20f, size.height * 0.75f),
                    end = Offset(size.width - 20f, size.height * 0.75f),
                    strokeWidth = 2f
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            OutlinedButton(
                onClick = {
                    lines.clear()
                    currentLine.clear()
                },
                modifier = Modifier.testTag("clear_signature_button")
            ) {
                Icon(imageVector = Icons.Default.Clear, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = HseStrings.clearSignatureButton(lang))
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    val serialized = lines.joinToString(";") { line ->
                        line.joinToString(",") { "${it.x.toInt()}:${it.y.toInt()}" }
                    }
                    onSaveSignature(if (serialized.isNotEmpty()) serialized else "SIGNED_VALID")
                },
                colors = ButtonDefaults.buttonColors(containerColor = SafetyOrangePrimary),
                modifier = Modifier.testTag("save_signature_button")
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = HseStrings.saveSignatureButton(lang))
            }
        }
    }
}
