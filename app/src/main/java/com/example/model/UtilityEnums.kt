package com.example.model

enum class UtilityType(
    val labelAr: String,
    val labelEn: String,
    val unitAr: String,
    val unitEn: String,
    val hexColor: String
) {
    ELECTRICITY("عداد الكهرباء", "Electricity Meter", "كيلوواط.ساعة (kWh)", "kWh", "#EAB308"),
    WATER("عداد المياه", "Water Meter", "متر مكعب (m³)", "m³", "#06B6D4");

    fun label(lang: Language) = if (lang == Language.ARABIC) labelAr else labelEn
    fun unit(lang: Language) = if (lang == Language.ARABIC) unitAr else unitEn
}

enum class MeterAlertStatus(
    val labelAr: String,
    val labelEn: String,
    val hexColor: String
) {
    NORMAL("استهلاك طبيعي", "Normal Consumption", "#10B981"),
    HIGH_SPIKE("ارتفاع حاد في الاستهلاك!", "Consumption Spike Alert!", "#F97316"),
    LEAK_WARNING("تحذير تسريب / ماس!", "Leak / Short Warning!", "#EF4444");

    fun label(lang: Language) = if (lang == Language.ARABIC) labelAr else labelEn
}
