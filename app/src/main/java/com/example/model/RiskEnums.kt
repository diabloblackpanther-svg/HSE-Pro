package com.example.model

enum class RiskLikelihood(val level: Int, val labelAr: String, val labelEn: String) {
    RARE(1, "نادر (Rare)", "Rare (1)"),
    UNLIKELY(2, "غير محتمل (Unlikely)", "Unlikely (2)"),
    POSSIBLE(3, "محتمل (Possible)", "Possible (3)"),
    LIKELY(4, "مرجح (Likely)", "Likely (4)"),
    ALMOST_CERTAIN(5, "مؤكد تقريباً (Almost Certain)", "Almost Certain (5)");

    fun label(lang: Language) = if (lang == Language.ARABIC) labelAr else labelEn
}

enum class RiskSeverityScore(val level: Int, val labelAr: String, val labelEn: String) {
    NEGLIGIBLE(1, "طفيف جداً (Negligible)", "Negligible (1)"),
    MINOR(2, "طفيف (Minor)", "Minor (2)"),
    MODERATE(3, "متوسط (Moderate)", "Moderate (3)"),
    MAJOR(4, "جسيم (Major)", "Major (4)"),
    CATASTROPHIC(5, "كارثي (Catastrophic)", "Catastrophic (5)");

    fun label(lang: Language) = if (lang == Language.ARABIC) labelAr else labelEn
}

enum class RiskLevel(val labelAr: String, val labelEn: String, val hexColor: String) {
    LOW("منخفض (Low Risk)", "Low Risk", "#10B981"),
    MEDIUM("متوسط (Medium Risk)", "Medium Risk", "#F59E0B"),
    HIGH("عالي (High Risk)", "High Risk", "#F97316"),
    EXTREME("حرِج (Extreme Risk)", "Extreme Risk", "#EF4444");

    fun label(lang: Language) = if (lang == Language.ARABIC) labelAr else labelEn

    companion object {
        fun fromScore(score: Int): RiskLevel {
            return when {
                score <= 4 -> LOW
                score <= 9 -> MEDIUM
                score <= 15 -> HIGH
                else -> EXTREME
            }
        }
    }
}

enum class CapaStatus(val labelAr: String, val labelEn: String, val hexColor: String) {
    PENDING("قيد الانتظار", "Pending", "#94A3B8"),
    IN_PROGRESS("قيد التنفيذ", "In Progress", "#F59E0B"),
    COMPLETED("مكتمل ومغلق", "Completed", "#10B981"),
    OVERDUE("متأخر!", "Overdue!", "#EF4444");

    fun label(lang: Language) = if (lang == Language.ARABIC) labelAr else labelEn
}
