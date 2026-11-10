package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entities.UtilityMeterEntity
import com.example.model.Language
import com.example.ui.theme.HazardRed
import com.example.ui.theme.SafetyOrangePrimary

@Composable
fun MeterReadingDialog(
    meter: UtilityMeterEntity,
    lang: Language,
    onDismiss: () -> Unit,
    onConfirm: (readingValue: Double, readerName: String, photoUri: String, notes: String) -> Unit
) {
    var readingInput by remember { mutableStateOf("") }
    var readerName by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var photoCaptured by remember { mutableStateOf(false) }

    val newReading = readingInput.toDoubleOrNull() ?: 0.0
    val deltaConsumption = (newReading - meter.lastReading).coerceAtLeast(0.0)
    val isSpike = deltaConsumption > (meter.baselineAvgConsumption * 1.35)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Speed, contentDescription = null, tint = SafetyOrangePrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (lang == Language.ARABIC) "إدخال قراءة العداد" else "Enter Meter Reading",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "${meter.meterName} (${meter.meterNumber})",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = SafetyOrangePrimary
                )
                Text(
                    text = "${if (lang == Language.ARABIC) "القراءة السابقة:" else "Last Reading:"} ${meter.lastReading}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = readingInput,
                    onValueChange = { readingInput = it },
                    label = { Text(if (lang == Language.ARABIC) "القراءة الجديدة الحالية" else "Current Reading") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("meter_reading_input"),
                    singleLine = true
                )

                if (newReading > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${if (lang == Language.ARABIC) "الاستهلاك المحسوب:" else "Calculated Consumption:"} +$deltaConsumption",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isSpike) HazardRed else MaterialTheme.colorScheme.onSurface
                    )
                }

                if (isSpike) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = HazardRed.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = HazardRed)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (lang == Language.ARABIC) "تنبيه: ارتفاع حاد في الاستهلاك مقارنة بالمعدل الطبيعي!" else "Warning: Consumption spike detected!",
                                style = MaterialTheme.typography.labelSmall,
                                color = HazardRed,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = readerName,
                    onValueChange = { readerName = it },
                    label = { Text(if (lang == Language.ARABIC) "اسم قارئ العداد" else "Reader Name") },
                    modifier = Modifier.fillMaxWidth().testTag("meter_reader_name_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Camera Photo Capture Button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, tint = SafetyOrangePrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (photoCaptured) (if (lang == Language.ARABIC) "تم التقاط صورة العداد ✓" else "Meter photo captured ✓")
                                else (if (lang == Language.ARABIC) "تصوير العداد للكاميرا" else "Capture Meter Photo"),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Button(
                            onClick = { photoCaptured = true },
                            colors = ButtonDefaults.buttonColors(containerColor = SafetyOrangePrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("capture_meter_photo_button")
                        ) {
                            Icon(imageVector = Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(14.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text(if (lang == Language.ARABIC) "إلغاء" else "Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (newReading > 0) {
                                onConfirm(
                                    newReading,
                                    readerName.ifBlank { "سعود الحربي" },
                                    if (photoCaptured) "meter_photo_${meter.id}.jpg" else "",
                                    notes
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SafetyOrangePrimary),
                        modifier = Modifier.testTag("save_reading_button")
                    ) {
                        Text(if (lang == Language.ARABIC) "حفظ القراءة" else "Save Reading")
                    }
                }
            }
        }
    }
}
