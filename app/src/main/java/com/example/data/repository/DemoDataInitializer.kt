package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entities.ChecklistEntity
import com.example.data.local.entities.ComplianceMatrixEntity
import com.example.data.local.entities.EquipmentPpeEntity
import com.example.data.local.entities.IncidentEntity
import com.example.data.local.entities.InspectionAuditEntity
import com.example.data.local.entities.MeterReadingEntity
import com.example.data.local.entities.PermitEntity
import com.example.data.local.entities.RiskAssessmentEntity
import com.example.data.local.entities.SiteEntity
import com.example.data.local.entities.TrainingLogEntity
import com.example.data.local.entities.UtilityMeterEntity
import com.example.model.IncidentSeverity
import com.example.model.LegalReference
import com.example.model.PermitStatus
import com.example.model.PermitType
import com.example.model.UtilityType

object DemoDataInitializer {

    suspend fun populateInitialDataIfEmpty(db: AppDatabase) {
        val siteDao = db.siteDao()
        val permitDao = db.permitDao()
        val incidentDao = db.incidentDao()
        val checklistDao = db.checklistDao()
        val riskDao = db.riskAssessmentDao()
        val auditDao = db.inspectionAuditDao()
        val meterDao = db.utilityMeterDao()
        val readingDao = db.meterReadingDao()
        val trainingDao = db.trainingLogDao()
        val equipmentDao = db.equipmentPpeDao()
        val complianceDao = db.complianceMatrixDao()

        val existingSites = siteDao.getSiteById(1)
        if (existingSites == null) {
            val now = System.currentTimeMillis()
            val dayMs = 86400000L

            // 1. Initial Sites
            val sites = listOf(
                SiteEntity(
                    id = 1,
                    nameAr = "مركز التوزيع الرئيسي - الرياض",
                    nameEn = "Main Distribution Center - Riyadh",
                    code = "DC-RYD-01",
                    location = "المنطقة الصناعية الثانية، الرياض",
                    incidentFreeDays = 148,
                    startDateTimestamp = now - (148 * dayMs),
                    recordStreakDays = 230,
                    totalManHours = 450000L,
                    isActive = true
                )
            )
            siteDao.insertSites(sites)

            // 2. Initial Work Permits
            val defaultHotWorkChecklist = """
                [
                    {"itemAr": "فحص خلو المنطقة من المواد القابلة للاشتعال بقطر 11 متر", "itemEn": "Clear 11m radius of combustible materials", "checked": true},
                    {"itemAr": "تجهيز طفايات الحريق المناسبة ومراقبة الفوهة", "itemEn": "Position suitable fire extinguishers & fire watch", "checked": true}
                ]
            """.trimIndent()

            val permits = listOf(
                PermitEntity(
                    id = 101,
                    permitNumber = "PTW-2026-8801",
                    siteId = 1,
                    type = PermitType.HOT_WORK.name,
                    title = "قص ولحام بقواعد خط السيور التلقائي 4",
                    locationDetail = "المنطقة الشرقية - منطقة التجميع A3",
                    applicantName = "م. خالد الغامدي",
                    supervisorName = "سعود الحربي",
                    hseOfficerName = "أحمد المنصور",
                    status = PermitStatus.IN_PROGRESS.name,
                    createdAt = now - (3 * 3600000L),
                    validFrom = now - (2 * 3600000L),
                    validUntil = now + (6 * 3600000L),
                    checklistJson = defaultHotWorkChecklist,
                    signaturePathData = "M 50 100 L 150 50 L 250 120",
                    notes = "تأمين مراقب حريق ملزم بالبقاء ساعة بعد انتهاء الأعمال."
                )
            )
            permitDao.insertPermits(permits)

            // 3. Initial Checklists
            val checklists = listOf(
                ChecklistEntity(
                    id = 201,
                    siteId = 1,
                    titleAr = "قائمة الفحص اليومي للرافعات الشوكية (Forklifts)",
                    titleEn = "Forklift Daily Inspection Safety Audit",
                    category = "المعدات والآليات",
                    totalItems = 12,
                    completedItems = 12,
                    lastInspected = now - (4 * 3600000L),
                    inspectedBy = "مسؤول السلامة أحمد"
                )
            )
            checklistDao.insertChecklists(checklists)

            // 4. Initial Incidents
            val incidents = listOf(
                IncidentEntity(
                    id = 301,
                    siteId = 1,
                    incidentDate = now - (148 * dayMs),
                    severity = IncidentSeverity.MINOR.name,
                    title = "انزلاق طفيف وتسرّب زيتي رصيف التنزيل 3",
                    description = "تسرب زيتي محدود من خرطوم هيدروليك رافعة شوكية، تم رش المادة الماصة وتطويق المكان فوراً.",
                    location = "رصيف التنزيل رقم 3",
                    reportedBy = "سعود الحربي",
                    streakDaysBeforeReset = 230
                )
            )
            incidentDao.insertIncidents(incidents)

            // 5. Training Logs & Toolbox Talks (TBT)
            val trainingLogs = listOf(
                TrainingLogEntity(
                    id = 801,
                    siteId = 1,
                    topicTitle = "لقاء السلامة الصباحي (Toolbox Talk): عزل الطاقة الكهروميكانيكية LOTO",
                    trainerName = "أحمد المنصور - مسئول HSE",
                    trainingDate = now - (2 * dayMs),
                    attendeesCount = 18,
                    isToolboxTalk = true,
                    certExpiryDate = now + (365 * dayMs),
                    notes = "تغطية إجراءات تركيب الأقفال والبطاقات وتحقيق صفر فولتية."
                ),
                TrainingLogEntity(
                    id = 802,
                    siteId = 1,
                    topicTitle = "دورة الإسعافات الأولية والإنعاش الرئوي CPR المعتمدة",
                    trainerName = "الهلال الأحمر / د. محمد",
                    trainingDate = now - (30 * dayMs),
                    attendeesCount = 12,
                    isToolboxTalk = false,
                    certExpiryDate = now + (335 * dayMs),
                    notes = "شهادات سارية لمدة عام لجميع المسعفين الأولين بالمستودع."
                )
            )
            trainingDao.insertTrainingLogs(trainingLogs)

            // 6. PPE & Fire Equipment Register
            val equipments = listOf(
                EquipmentPpeEntity(
                    id = 901,
                    siteId = 1,
                    equipmentName = "طفاية حريق بودرة جافة 6 كجم (Dry Powder Extinguisher)",
                    category = "FIRE_EXTINGUISHER",
                    serialNumber = "FE-RYD-9941",
                    assignedTo = "رصيف التنزيل B2",
                    inspectionStatus = "PASS",
                    lastInspectionDate = now - (10 * dayMs),
                    nextExpiryDate = now + (170 * dayMs)
                ),
                EquipmentPpeEntity(
                    id = 902,
                    siteId = 1,
                    equipmentName = "حزام أمان مزدوج للعمل على ارتفاعات (Full Body Harness)",
                    category = "PPE",
                    serialNumber = "PPE-H-3301",
                    assignedTo = "عمر باخشوين - صيانة",
                    inspectionStatus = "PASS",
                    lastInspectionDate = now - (5 * dayMs),
                    nextExpiryDate = now + (180 * dayMs)
                )
            )
            equipmentDao.insertEquipmentPpeList(equipments)

            // 7. LEGAL COMPLIANCE MATRIX (مصفوفة الالتزام القانوني والمعايير)
            val complianceItems = listOf(
                ComplianceMatrixEntity(
                    id = 1001,
                    category = "تقييم بيئة العمل والمخاطر",
                    requirementAr = "إجراء قياسات وتفتيش دوري لمخاطر بيئة العمل المادية والهندسية (الضوضاء، الإضاءة، التهوية)",
                    requirementEn = "Perform periodic physical & engineering work environment measurements",
                    referenceLaw = LegalReference.LABOR_LAW_12.name,
                    referenceClause = "قرار وزير القوى العاملة والهجرة رقم 211 لسنة 2003",
                    status = "COMPLIANT"
                ),
                ComplianceMatrixEntity(
                    id = 1002,
                    category = "لجان وأجهزة السلامة",
                    requirementAr = "تشكيل لجنة السلامة والصحة المهنية وتعيين أخصائيي وفنيي السلامة المعتمدين",
                    requirementEn = "Form HSE Committee & appoint qualified safety specialists",
                    referenceLaw = LegalReference.LABOR_LAW_12.name,
                    referenceClause = "قرار وزير القوى العاملة والهجرة رقم 126 لسنة 2003",
                    status = "COMPLIANT"
                ),
                ComplianceMatrixEntity(
                    id = 1003,
                    category = "السجل البيئي والنفايات الخطرة",
                    requirementAr = "إعداد وحفظ السجل البيئي وسجل المواد والنفايات الخطرة والتخلص الآمن منها",
                    requirementEn = "Maintain Environmental Register & Hazardous Materials/Waste Logs",
                    referenceLaw = LegalReference.ENVIRONMENT_LAW_4.name,
                    referenceClause = "قانون البيئة رقم 4 لسنة 1994 ولائحتة التنفيذية",
                    status = "COMPLIANT"
                ),
                ComplianceMatrixEntity(
                    id = 1004,
                    category = "أنظمة الإطفاء مسالك الهروب",
                    requirementAr = "التفتيش الدوري وصيانة أنظمة الإنذار المبكر والأنظمة التلقائية ومطافئ الحريق ومسالك الهروب",
                    requirementEn = "Periodic inspection of fire alarm, suppression systems & emergency exits",
                    referenceLaw = LegalReference.CIVIL_DEFENSE.name,
                    referenceClause = "اشتراطات وإكواد الحماية المدنية المصرية لمكافحة الحريق",
                    status = "COMPLIANT"
                ),
                ComplianceMatrixEntity(
                    id = 1005,
                    category = "عزل الطاقة الخطرة LOTO",
                    requirementAr = "تطبيق برنامج عزل وتأمين مصادر الطاقة الخطرة Lockout / Tagout أثناء الصيانة",
                    requirementEn = "Implement Hazardous Energy Control Program (Lockout/Tagout)",
                    referenceLaw = LegalReference.OSHA.name,
                    referenceClause = "OSHA Standard 1910.147 (Control of Hazardous Energy)",
                    status = "COMPLIANT"
                ),
                ComplianceMatrixEntity(
                    id = 1006,
                    category = "الحماية من الحريق NFPA",
                    requirementAr = "الفحص الشامل وتوزيع المطافئ اليدوية المحمولة وطرق صيانها",
                    requirementEn = "Standard for Portable Fire Extinguishers Inspection & Maintenance",
                    referenceLaw = LegalReference.NFPA.name,
                    referenceClause = "NFPA 10 (Standard for Portable Fire Extinguishers)",
                    status = "COMPLIANT"
                )
            )
            complianceDao.insertComplianceItems(complianceItems)
        }
    }
}
