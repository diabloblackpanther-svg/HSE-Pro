package com.example.ui.util

import com.example.model.Language

object HseStrings {

    fun appName(lang: Language) = if (lang == Language.ARABIC) "HSE Pro" else "HSE Pro"
    fun tagLine(lang: Language) = if (lang == Language.ARABIC) "المساعد الذكي للسلامة والصحة المهنية" else "Health, Safety & Environment Assistant"

    // Navigation
    fun navDashboard(lang: Language) = if (lang == Language.ARABIC) "الرئيسية" else "Dashboard"
    fun navPermits(lang: Language) = if (lang == Language.ARABIC) "التصاريح" else "Permits"
    fun navIncidents(lang: Language) = if (lang == Language.ARABIC) "الحوادث" else "Incidents"
    fun navUtilities(lang: Language) = if (lang == Language.ARABIC) "الكهرباء/المياه" else "Utilities"
    fun navCompliance(lang: Language) = if (lang == Language.ARABIC) "مصفوفة الالتزام" else "Compliance"
    fun navChecklists(lang: Language) = if (lang == Language.ARABIC) "قوائم الفحص" else "Checklists"
    fun navTraining(lang: Language) = if (lang == Language.ARABIC) "التدريب/PPE" else "Training"
    fun navReports(lang: Language) = if (lang == Language.ARABIC) "التقارير" else "Reports"
    fun navRisk(lang: Language) = if (lang == Language.ARABIC) "المخاطر JSA" else "Risk JSA"
    fun navInspections(lang: Language) = if (lang == Language.ARABIC) "التفتيش" else "Audits"
    fun navSites(lang: Language) = if (lang == Language.ARABIC) "المواقع" else "Sites"
    fun navSettings(lang: Language) = if (lang == Language.ARABIC) "الإعدادات" else "Settings"
    fun navMore(lang: Language) = if (lang == Language.ARABIC) "المزيد" else "More"

    // Dashboard
    fun incidentFreeDaysTitle(lang: Language) = if (lang == Language.ARABIC) "أيام بدون إصابات أو حوادث" else "Incident-Free Days"
    fun recordStreak(lang: Language) = if (lang == Language.ARABIC) "الرقم القياسي السابق:" else "Record Streak:"
    fun recordIncidentButton(lang: Language) = if (lang == Language.ARABIC) "تسجيل حادث / إصابة" else "Record Incident"
    fun adjustCounterButton(lang: Language) = if (lang == Language.ARABIC) "تعديل العداد" else "Adjust Counter"
    fun activePermitsHeader(lang: Language) = if (lang == Language.ARABIC) "تصاريح العمل النشطة حالياً" else "Currently Active Permits"
    fun activeChecklistsHeader(lang: Language) = if (lang == Language.ARABIC) "قوائم الفحص والتدقيق" else "Checklists & Audits"
    fun recentIncidentsHeader(lang: Language) = if (lang == Language.ARABIC) "سجل الحوادث الأخير" else "Recent Incidents Log"
    fun currentSite(lang: Language) = if (lang == Language.ARABIC) "الموقع الحالي" else "Current Site"
    fun currentRole(lang: Language) = if (lang == Language.ARABIC) "الدور الحالي" else "Current Role"
    fun scanQrButton(lang: Language) = if (lang == Language.ARABIC) "مسح QR التصريح" else "Scan Permit QR"
    fun createPermitFab(lang: Language) = if (lang == Language.ARABIC) "تصريح جديد" else "New Permit"

    // Dialogs
    fun recordIncidentDialogTitle(lang: Language) = if (lang == Language.ARABIC) "تسجيل حادث وتصفير العداد" else "Record Incident & Reset Counter"
    fun incidentTitleLabel(lang: Language) = if (lang == Language.ARABIC) "عنوان الحادث / البلاغ" else "Incident Title"
    fun incidentDescLabel(lang: Language) = if (lang == Language.ARABIC) "وصف الحادث والأسباب" else "Description & Causes"
    fun incidentLocationLabel(lang: Language) = if (lang == Language.ARABIC) "مكان الحادث بالضبط" else "Exact Location"
    fun incidentSeverityLabel(lang: Language) = if (lang == Language.ARABIC) "مستوى الخطورة" else "Severity Level"
    fun reportedByLabel(lang: Language) = if (lang == Language.ARABIC) "مُبلغ الحادث" else "Reported By"
    fun confirmResetButton(lang: Language) = if (lang == Language.ARABIC) "تصفير العداد وحفظ البلاغ" else "Reset Counter & Save"
    fun cancelButton(lang: Language) = if (lang == Language.ARABIC) "إلغاء" else "Cancel"
    fun saveButton(lang: Language) = if (lang == Language.ARABIC) "حفظ" else "Save"

    // Adjust Counter Dialog
    fun adjustCounterDialogTitle(lang: Language) = if (lang == Language.ARABIC) "تعديل عدد أيام السلامة" else "Adjust Safety Counter Days"
    fun daysInputLabel(lang: Language) = if (lang == Language.ARABIC) "عدد الأيام الحالية" else "Current Days Count"

    // Permits
    fun searchPermitsPlaceholder(lang: Language) = if (lang == Language.ARABIC) "بحث برقم التصريح، العنوان، أو المكان..." else "Search by permit #, title, location..."
    fun permitTypeLabel(lang: Language) = if (lang == Language.ARABIC) "نوع التصريح" else "Permit Type"
    fun permitNumberLabel(lang: Language) = if (lang == Language.ARABIC) "رقم التصريح" else "Permit Number"
    fun validFromLabel(lang: Language) = if (lang == Language.ARABIC) "صالح من" else "Valid From"
    fun validUntilLabel(lang: Language) = if (lang == Language.ARABIC) "صالح حتى" else "Valid Until"
    fun applicantNameLabel(lang: Language) = if (lang == Language.ARABIC) "طالب التصريح" else "Applicant Name"
    fun supervisorLabel(lang: Language) = if (lang == Language.ARABIC) "مشرف الموقع" else "Site Supervisor"
    fun hseOfficerLabel(lang: Language) = if (lang == Language.ARABIC) "مسؤول السلامة" else "HSE Officer"
    fun safetyChecklistHeader(lang: Language) = if (lang == Language.ARABIC) "قائمة الفحص والسلامة" else "Safety Verification Checklist"
    fun signatureHeader(lang: Language) = if (lang == Language.ARABIC) "التوقيع بالإصبع / الاعتماد" else "Finger Signature Approval"
    fun signHereInstruction(lang: Language) = if (lang == Language.ARABIC) "وقع بإصبعك داخل الإطار أدناه" else "Sign with your finger inside box below"
    fun clearSignatureButton(lang: Language) = if (lang == Language.ARABIC) "مسح التوقيع" else "Clear Signature"
    fun saveSignatureButton(lang: Language) = if (lang == Language.ARABIC) "حفظ التوقيع" else "Save Signature"
    fun qrCodeHeader(lang: Language) = if (lang == Language.ARABIC) "رمز QR للتأكد الميداني" else "Field Verification QR Code"
    fun changeStatusButton(lang: Language) = if (lang == Language.ARABIC) "تغيير حالة التصريح" else "Change Status"

    // Workflow actions
    fun reviewAction(lang: Language) = if (lang == Language.ARABIC) "إرسال للمراجعة" else "Submit for Review"
    fun approveAction(lang: Language) = if (lang == Language.ARABIC) "اعتماد التصريح" else "Approve Permit"
    fun startAction(lang: Language) = if (lang == Language.ARABIC) "بدء التنفيذ الميداني" else "Start Field Execution"
    fun closeAction(lang: Language) = if (lang == Language.ARABIC) "إغلاق التصريح" else "Close Permit"

    // Create Permit
    fun createPermitTitle(lang: Language) = if (lang == Language.ARABIC) "اصدار تصريح عمل جديد" else "Issue New Work Permit"
    fun permitTitleLabel(lang: Language) = if (lang == Language.ARABIC) "عنوان العمل / المهمة" else "Job / Task Title"

    // Checklists
    fun checklistProgress(completed: Int, total: Int, lang: Language) =
        if (lang == Language.ARABIC) "تم فحص $completed من إجمالي $total عنصر" else "$completed of $total items checked"
    fun inspectChecklistButton(lang: Language) = if (lang == Language.ARABIC) "إجراء الفحص" else "Perform Audit"

    // Settings
    fun languageSettingTitle(lang: Language) = if (lang == Language.ARABIC) "لغة التطبيق" else "App Language"
    fun themeSettingTitle(lang: Language) = if (lang == Language.ARABIC) "المظهر (فاتح / داكن)" else "Theme (Light / Dark)"
    fun notificationSettingTitle(lang: Language) = if (lang == Language.ARABIC) "تنبيهات انتهاء التصاريح" else "Permit Expiry Alerts"
    fun resetDemoDataButton(lang: Language) = if (lang == Language.ARABIC) "إعادة تحميل البيانات التجريبية" else "Reset Demo Data"

    // Expiry Alerts
    fun hoursLeft(hours: Long, lang: Language) =
        if (lang == Language.ARABIC) "متبقي $hours ساعة" else "$hours hrs remaining"
    fun expiredNotice(lang: Language) = if (lang == Language.ARABIC) "منتهي الصلاحية!" else "EXPIRED!"
}
