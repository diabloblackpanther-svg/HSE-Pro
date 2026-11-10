package com.example.model

enum class LegalReference(
    val codeName: String,
    val titleAr: String,
    val titleEn: String,
    val hexColor: String
) {
    LABOR_LAW_12(
        "LABOR_LAW_12",
        "قانون العمل المصري رقم 12 لسنة 2003 والقرارات الوزارية 211 و 126",
        "Egyptian Labor Law No. 12 / 2003 & Ministerial Decrees",
        "#10B981"
    ),
    ENVIRONMENT_LAW_4(
        "ENV_LAW_4",
        "قانون البيئة المصري رقم 4 لسنة 1994 ولائحته التنفيذية",
        "Egyptian Environmental Law No. 4 / 1994",
        "#059669"
    ),
    CIVIL_DEFENSE(
        "CIVIL_DEFENSE",
        "اشتراطات وإكواد الحماية المدنية المصرية لمكافحة الحريق",
        "Egyptian Civil Defense Fire Code Requirements",
        "#EF4444"
    ),
    ISO_45001(
        "ISO_45001",
        "المواصفة الدولية ISO 45001:2018 لنظام إدارة السلامة والصحة المهنية",
        "ISO 45001:2018 Occupational Health & Safety Standard",
        "#F59E0B"
    ),
    ISO_14001(
        "ISO_14001",
        "المواصفة الدولية ISO 14001:2015 لنظام الإدارة البيئية",
        "ISO 14001:2015 Environmental Management Standard",
        "#06B6D4"
    ),
    OSHA(
        "OSHA",
        "معايير السلامة والصحة المهنية الأمريكية OSHA 1910 / 1926",
        "US OSHA Standards (General Industry & Construction)",
        "#F97316"
    ),
    NFPA(
        "NFPA",
        "الأكواد الدولية للحماية من الحريق NFPA Codes (NFPA 10, 13, 70, 101)",
        "NFPA Codes (National Fire Protection Association)",
        "#DC2626"
    );

    fun title(lang: Language) = if (lang == Language.ARABIC) titleAr else titleEn
}
