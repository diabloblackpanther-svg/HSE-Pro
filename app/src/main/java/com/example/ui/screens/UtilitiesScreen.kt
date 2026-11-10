package com.example.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.WaterDrop
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entities.MeterReadingEntity
import com.example.data.local.entities.SiteEntity
import com.example.data.local.entities.UtilityMeterEntity
import com.example.model.Language
import com.example.model.UtilityType
import com.example.ui.components.MeterReadingDialog
import com.example.ui.components.SafetyBanner
import com.example.ui.components.UtilityConsumptionChart
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.HazardRed
import com.example.ui.theme.SafetyOrangePrimary
import com.example.ui.theme.SafetyYellow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun UtilitiesScreen(
    lang: Language,
    meters: List<UtilityMeterEntity>,
    readings: List<MeterReadingEntity>,
    sites: List<SiteEntity>,
    onAddReading: (meterId: Long, value: Double, readerName: String, photoUri: String, notes: String) -> Unit,
    onAddMeter: (type: String, meterNumber: String, meterName: String, locationDetail: String, initialReading: Double, baselineAvg: Double) -> Unit
) {
    var activeMeterForReading by remember { mutableStateOf<UtilityMeterEntity?>(null) }
    var showAddMeterDialog by remember { mutableStateOf(false) }

    val hasSpike = readings.any { it.isAnomalySpike }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (lang == Language.ARABIC) "إدارة الكهرباء والمياه (Utilities)" else "Utility Management",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = if (lang == Language.ARABIC) "متابعة العدادات، حساب الاستهلاك التلقائي وتنبيهات الارتفاع" else "Multi-meter tracking & anomaly detection",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                Button(
                    onClick = { showAddMeterDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = SafetyOrangePrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("add_meter_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = if (lang == Language.ARABIC) "عداد جديد" else "Add Meter", fontSize = 12.sp)
                }
            }
        }

        // Anomaly Spike Warning Banner
        if (hasSpike) {
            item {
                SafetyBanner(
                    title = if (lang == Language.ARABIC) "تنبيه ارتفاع حاد في الاستهلاك!" else "High Consumption Spike Detected!",
                    message = if (lang == Language.ARABIC) "تم رصد ارتفاع يتجاوز 135% من المعدل الطبيعي. يرجى التفتيش عن تسريب مياه أو ماس كهربائي." else "Spike over 135% of baseline detected. Inspect for leaks or electrical shorts.",
                    isWarning = true,
                    lang = lang
                )
            }
        }

        // Consumption Analytics Chart
        item {
            UtilityConsumptionChart(
                readings = readings,
                sites = sites,
                lang = lang
            )
        }

        // Meters List
        item {
            Text(
                text = if (lang == Language.ARABIC) "عدادات الموقع الحالية" else "Active Site Meters",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        items(meters) { meter ->
            MeterCardItem(
                meter = meter,
                lang = lang,
                onEnterReadingClick = { activeMeterForReading = meter }
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }

    activeMeterForReading?.let { meter ->
        MeterReadingDialog(
            meter = meter,
            lang = lang,
            onDismiss = { activeMeterForReading = null },
            onConfirm = { valVal, name, photo, notes ->
                onAddReading(meter.id, valVal, name, photo, notes)
                activeMeterForReading = null
            }
        )
    }

    if (showAddMeterDialog) {
        AddMeterDialog(
            lang = lang,
            onDismiss = { showAddMeterDialog = false },
            onConfirm = { type, num, name, loc, initVal, baseVal ->
                onAddMeter(type, num, name, loc, initVal, baseVal)
                showAddMeterDialog = false
            }
        )
    }
}

@Composable
fun MeterCardItem(
    meter: UtilityMeterEntity,
    lang: Language,
    onEnterReadingClick: () -> Unit
) {
    val isElectricity = meter.type == UtilityType.ELECTRICITY.name
    val icon = if (isElectricity) Icons.Default.FlashOn else Icons.Default.WaterDrop
    val typeColor = if (isElectricity) SafetyYellow else Color(0xFF06B6D4)
    val unit = if (isElectricity) "kWh" else "m³"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .testTag("meter_card_${meter.meterNumber}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(typeColor.copy(alpha = 0.18f))
                    ) {
                        Icon(imageVector = icon, contentDescription = null, tint = typeColor, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = meter.meterName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${meter.meterNumber} • ${meter.locationDetail}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (lang == Language.ARABIC) "آخر قراءة حالية:" else "Last Reading:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "${meter.lastReading} $unit",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Button(
                    onClick = onEnterReadingClick,
                    colors = ButtonDefaults.buttonColors(containerColor = SafetyOrangePrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("enter_reading_button_${meter.id}")
                ) {
                    Icon(imageVector = Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = if (lang == Language.ARABIC) "إدخال قراءة" else "New Reading", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun AddMeterDialog(
    lang: Language,
    onDismiss: () -> Unit,
    onConfirm: (type: String, num: String, name: String, loc: String, initReading: Double, baseline: Double) -> Unit
) {
    var selectedType by remember { mutableStateOf(UtilityType.ELECTRICITY) }
    var number by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var initialVal by remember { mutableStateOf("1000") }
    var baselineVal by remember { mutableStateOf("500") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = if (lang == Language.ARABIC) "إضافة عداد جديد للموقع" else "Add New Utility Meter",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { selectedType = UtilityType.ELECTRICITY },
                        colors = ButtonDefaults.buttonColors(containerColor = if (selectedType == UtilityType.ELECTRICITY) SafetyYellow else MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("كهرباء ⚡")
                    }

                    Button(
                        onClick = { selectedType = UtilityType.WATER },
                        colors = ButtonDefaults.buttonColors(containerColor = if (selectedType == UtilityType.WATER) Color(0xFF06B6D4) else MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("مياه 💧")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (lang == Language.ARABIC) "اسم العداد" else "Meter Name") },
                    modifier = Modifier.fillMaxWidth().testTag("add_meter_name_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = number,
                    onValueChange = { number = it },
                    label = { Text(if (lang == Language.ARABIC) "رقم العداد" else "Meter Number") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text(if (lang == Language.ARABIC) "مكان العداد" else "Location") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

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
                            if (name.isNotBlank()) {
                                onConfirm(
                                    selectedType.name,
                                    number.ifBlank { "METER-01" },
                                    name,
                                    location,
                                    initialVal.toDoubleOrNull() ?: 1000.0,
                                    baselineVal.toDoubleOrNull() ?: 500.0
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SafetyOrangePrimary),
                        modifier = Modifier.testTag("confirm_add_meter_button")
                    ) {
                        Text(if (lang == Language.ARABIC) "إضافة" else "Save")
                    }
                }
            }
        }
    }
}
