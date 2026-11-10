package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entities.PermitEntity
import com.example.model.Language
import com.example.model.PermitStatus
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.HazardRed
import com.example.ui.theme.IndustrialDarkNavy
import com.example.ui.theme.SafetyOrangePrimary
import com.example.ui.util.HseStrings

@Composable
fun QrScannerModal(
    lang: Language = Language.ARABIC,
    allPermits: List<PermitEntity>,
    onDismiss: () -> Unit,
    onPermitFound: (PermitEntity) -> Unit
) {
    var searchCode by remember { mutableStateOf("") }
    var scannedPermit by remember { mutableStateOf<PermitEntity?>(null) }
    var scanError by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "scanner")
    val laserY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laserY"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = SafetyOrangePrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (lang == Language.ARABIC) "ماسح QR للتصاريح الميدانية" else "Field Permit QR Scanner",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Simulated Scanner Box with laser beam
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(220.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(IndustrialDarkNavy)
                        .border(3.dp, SafetyOrangePrimary, RoundedCornerShape(20.dp))
                ) {
                    Canvas(modifier = Modifier.size(200.dp)) {
                        val strokeW = 4f
                        val cornerLen = 40f
                        val c = SafetyOrangePrimary

                        // Corner viewfinders
                        drawLine(c, Offset(0f, 0f), Offset(cornerLen, 0f), strokeW)
                        drawLine(c, Offset(0f, 0f), Offset(0f, cornerLen), strokeW)

                        drawLine(c, Offset(size.width, 0f), Offset(size.width - cornerLen, 0f), strokeW)
                        drawLine(c, Offset(size.width, 0f), Offset(size.width, cornerLen), strokeW)

                        drawLine(c, Offset(0f, size.height), Offset(cornerLen, size.height), strokeW)
                        drawLine(c, Offset(0f, size.height), Offset(0f, size.height - cornerLen), strokeW)

                        drawLine(c, Offset(size.width, size.height), Offset(size.width - cornerLen, size.height), strokeW)
                        drawLine(c, Offset(size.width, size.height), Offset(size.width, size.height - cornerLen), strokeW)

                        // Animated Scanning Laser line
                        val y = size.height * laserY
                        drawLine(
                            color = Color(0xFF10B981),
                            start = Offset(10f, y),
                            end = Offset(size.width - 10f, y),
                            strokeWidth = 6f
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (lang == Language.ARABIC) "وجّه الكاميرا نحو رمز QR" else "Align QR code inside frame",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quick Simulation Action Buttons for Demo
                Text(
                    text = if (lang == Language.ARABIC) "مسح سريع لتصريح تجريبي:" else "Quick demo scan:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val sample1 = allPermits.firstOrNull()
                    val sample2 = allPermits.getOrNull(1)

                    if (sample1 != null) {
                        Button(
                            onClick = {
                                scannedPermit = sample1
                                scanError = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SafetyOrangePrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("scan_sample_1")
                        ) {
                            Text(text = sample1.permitNumber, fontSize = 11.sp)
                        }
                    }

                    if (sample2 != null) {
                        Button(
                            onClick = {
                                scannedPermit = sample2
                                scanError = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("scan_sample_2")
                        ) {
                            Text(
                                text = sample2.permitNumber,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Manual Code Input Search
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchCode,
                        onValueChange = { searchCode = it },
                        placeholder = { Text(if (lang == Language.ARABIC) "أدخل رقم التصريح..." else "Enter permit #...") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("manual_qr_input"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            val match = allPermits.find { it.permitNumber.contains(searchCode, ignoreCase = true) }
                            if (match != null) {
                                scannedPermit = match
                                scanError = false
                            } else {
                                scanError = true
                                scannedPermit = null
                            }
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = SafetyOrangePrimary)
                    }
                }

                if (scanError) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (lang == Language.ARABIC) "لم يتم العثور على تصريح بهذا الرقم!" else "Permit not found!",
                        color = HazardRed,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Scanned Result Card
                scannedPermit?.let { permit ->
                    Spacer(modifier = Modifier.height(16.dp))

                    val isApprovedOrActive = permit.status == PermitStatus.APPROVED.name || permit.status == PermitStatus.IN_PROGRESS.name
                    val statusColor = if (isApprovedOrActive) EmeraldGreen else HazardRed

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.12f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = statusColor
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = permit.permitNumber,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = statusColor
                                    )
                                }
                                Text(
                                    text = permit.status,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = statusColor
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = permit.title,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "${HseStrings.applicantNameLabel(lang)}: ${permit.applicantName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                            Text(
                                text = "${HseStrings.supervisorLabel(lang)}: ${permit.supervisorName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    onPermitFound(permit)
                                    onDismiss()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = statusColor),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("open_scanned_permit_button")
                            ) {
                                Text(
                                    text = if (lang == Language.ARABIC) "عرض التفاصيل الكاملة للتصريح" else "View Full Permit Details"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
