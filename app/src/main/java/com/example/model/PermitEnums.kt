package com.example.model

enum class PermitType(
    val titleAr: String,
    val titleEn: String,
    val iconName: String,
    val hexColor: String
) {
    HOT_WORK(
        titleAr = "أعمال ساخنة (لحام / قطعية)",
        titleEn = "Hot Work (Welding/Cutting)",
        iconName = "LocalFireDepartment",
        hexColor = "#F97316"
    ),
    HEIGHTS(
        titleAr = "عمل على ارتفاعات (سقالات/أسطح)",
        titleEn = "Working at Heights",
        iconName = "Height",
        hexColor = "#3B82F6"
    ),
    CONFINED_SPACE(
        titleAr = "دخول فراغات محصورة (خزانات/أنفاق)",
        titleEn = "Confined Space Entry",
        iconName = "MeetingRoom",
        hexColor = "#8B5CF6"
    ),
    ELECTRICAL_LOTO(
        titleAr = "أعمال كهربائية وتأمين (LOTO)",
        titleEn = "Electrical & LOTO",
        iconName = "FlashOn",
        hexColor = "#EAB308"
    ),
    EXCAVATION(
        titleAr = "أعمال الحفر وتأمين التربة",
        titleEn = "Excavation & Trenching",
        iconName = "Terrain",
        hexColor = "#A16207"
    ),
    CRANES(
        titleAr = "رافعات ورفع الأحمال الثقيلة",
        titleEn = "Cranes & Heavy Lifting",
        iconName = "Construction",
        hexColor = "#06B6D4"
    ),
    GENERAL(
        titleAr = "تصريح عمل عام",
        titleEn = "General Work Permit",
        iconName = "Assignment",
        hexColor = "#64748B"
    );

    fun title(lang: Language) = if (lang == Language.ARABIC) titleAr else titleEn
}

enum class PermitStatus(
    val labelAr: String,
    val labelEn: String,
    val hexColor: String
) {
    DRAFT("مسودة / طلب", "Draft / Requested", "#94A3B8"),
    REVIEWED("تحت المراجعة", "Under Review", "#3B82F6"),
    APPROVED("معتمد", "Approved", "#10B981"),
    IN_PROGRESS("قيد التنفيذ", "In Progress", "#F59E0B"),
    CLOSED("مغلق بنجاح", "Closed", "#64748B"),
    EXPIRED("منتهي الصلاحية", "Expired", "#EF4444");

    fun label(lang: Language) = if (lang == Language.ARABIC) labelAr else labelEn
}

enum class IncidentSeverity(
    val labelAr: String,
    val labelEn: String,
    val hexColor: String
) {
    MINOR("وشيك / طفيف (Near Miss)", "Near Miss / Minor", "#F59E0B"),
    MODERATE("متوسط / إسعافات أولية", "First Aid / Moderate", "#F97316"),
    SEVERE("جسيم / توقف العمل", "Severe / Work Stoppage", "#EF4444");

    fun label(lang: Language) = if (lang == Language.ARABIC) labelAr else labelEn
}
