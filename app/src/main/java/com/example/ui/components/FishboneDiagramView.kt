package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Rule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Language
import com.example.ui.theme.SafetyOrangePrimary
import org.json.JSONObject

@Composable
fun FishboneDiagramView(
    fishboneJson: String,
    lang: Language = Language.ARABIC,
    onFishboneCategoryChanged: (category: String, text: String) -> Unit
) {
    val json = try {
        JSONObject(fishboneJson)
    } catch (e: Exception) {
        JSONObject()
    }

    val categories = listOf(
        Triple("people", if (lang == Language.ARABIC) "عنصر العنصر البشري (People)" else "People (Manpower)", Icons.Default.People),
        Triple("machinery", if (lang == Language.ARABIC) "المعدات والآليات (Machinery)" else "Machinery (Equipment)", Icons.Default.Build),
        Triple("material", if (lang == Language.ARABIC) "المواد المستعملة (Material)" else "Material", Icons.Default.Category),
        Triple("method", if (lang == Language.ARABIC) "طريقة العمل (Method)" else "Method (Process)", Icons.Default.Rule),
        Triple("environment", if (lang == Language.ARABIC) "بيئة العمل (Environment)" else "Environment", Icons.Default.Eco)
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccountTree,
                    contentDescription = null,
                    tint = SafetyOrangePrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (lang == Language.ARABIC) "مخطط عظمة السمكة / إيشيكاوا (Fishbone)" else "Fishbone (Ishikawa) Diagram",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            categories.forEach { (key, title, icon) ->
                val textValue = json.optString(key, "")

                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = SafetyOrangePrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            fontSize = 11.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = textValue,
                        onValueChange = { onFishboneCategoryChanged(key, it) },
                        placeholder = { Text(if (lang == Language.ARABIC) "أدخل السبب المتعلق بـ $title..." else "Enter cause for $key...", fontSize = 11.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("fishbone_input_$key"),
                        singleLine = true
                    )
                }
            }
        }
    }
}
