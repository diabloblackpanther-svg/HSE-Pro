package com.example.model

enum class Language(val code: String, val displayName: String) {
    ARABIC("ar", "العربية"),
    ENGLISH("en", "English")
}

enum class UserRole(val labelAr: String, val labelEn: String) {
    ADMIN("مدير الموقع", "Site Admin"),
    SAFETY_OFFICER("مسؤول السلامة والصحة المهنية", "HSE Officer"),
    SUPERVISOR("مشرف العمليات", "Site Supervisor"),
    OBSERVER("مراقب / مشاهد", "Site Observer");

    fun label(lang: Language) = if (lang == Language.ARABIC) labelAr else labelEn
}
