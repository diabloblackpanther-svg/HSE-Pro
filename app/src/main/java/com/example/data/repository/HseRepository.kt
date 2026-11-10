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
import kotlinx.coroutines.flow.Flow

class HseRepository(private val db: AppDatabase) {

    private val siteDao = db.siteDao()
    private val permitDao = db.permitDao()
    private val incidentDao = db.incidentDao()
    private val checklistDao = db.checklistDao()
    private val riskDao = db.riskAssessmentDao()
    private val auditDao = db.inspectionAuditDao()
    private val meterDao = db.utilityMeterDao()
    private val readingDao = db.meterReadingDao()
    private val trainingDao = db.trainingLogDao()
    private val equipmentDao = db.equipmentPpeDao()
    private val complianceDao = db.complianceMatrixDao()

    fun getAllSites(): Flow<List<SiteEntity>> = siteDao.getAllSites()

    suspend fun getSiteById(siteId: Long): SiteEntity? = siteDao.getSiteById(siteId)

    suspend fun insertSite(site: SiteEntity): Long = siteDao.insertSite(site)

    suspend fun updateSite(site: SiteEntity) = siteDao.updateSite(site)

    suspend fun updateIncidentCounter(siteId: Long, days: Int, startTimestamp: Long) {
        siteDao.updateIncidentCounter(siteId, days, startTimestamp)
    }

    suspend fun recordIncidentAndResetCounter(
        siteId: Long,
        previousStreak: Int,
        severity: String,
        title: String,
        description: String,
        location: String,
        reportedBy: String,
        photoUri: String = "",
        fiveWhysJson: String = "",
        fishboneJson: String = "",
        capaJson: String = "",
        lostTimeDays: Int = 0
    ) {
        siteDao.resetIncidentCounter(siteId, previousStreak)

        val incident = IncidentEntity(
            siteId = siteId,
            incidentDate = System.currentTimeMillis(),
            severity = severity,
            title = title,
            description = description,
            location = location,
            reportedBy = reportedBy,
            streakDaysBeforeReset = previousStreak,
            photoUri = photoUri,
            fiveWhysJson = fiveWhysJson,
            fishboneJson = fishboneJson,
            capaJson = capaJson,
            lostTimeDays = lostTimeDays
        )
        incidentDao.insertIncident(incident)
    }

    suspend fun updateIncident(incident: IncidentEntity) = incidentDao.updateIncident(incident)

    fun getPermitsForSite(siteId: Long): Flow<List<PermitEntity>> = permitDao.getPermitsForSite(siteId)

    fun getAllPermits(): Flow<List<PermitEntity>> = permitDao.getAllPermits()

    suspend fun getPermitById(permitId: Long): PermitEntity? = permitDao.getPermitById(permitId)

    suspend fun insertPermit(permit: PermitEntity): Long = permitDao.insertPermit(permit)

    suspend fun updatePermit(permit: PermitEntity) = permitDao.updatePermit(permit)

    suspend fun updatePermitStatus(permitId: Long, status: String) = permitDao.updatePermitStatus(permitId, status)

    suspend fun updateSignature(permitId: Long, signature: String) = permitDao.updateSignature(permitId, signature)

    fun getIncidentsForSite(siteId: Long): Flow<List<IncidentEntity>> = incidentDao.getIncidentsForSite(siteId)

    suspend fun getIncidentById(incidentId: Long): IncidentEntity? = incidentDao.getIncidentById(incidentId)

    fun getChecklistsForSite(siteId: Long): Flow<List<ChecklistEntity>> = checklistDao.getChecklistsForSite(siteId)

    suspend fun updateChecklist(checklist: ChecklistEntity) = checklistDao.updateChecklist(checklist)

    suspend fun insertChecklist(checklist: ChecklistEntity): Long = checklistDao.insertChecklist(checklist)

    fun getRiskAssessmentsForSite(siteId: Long): Flow<List<RiskAssessmentEntity>> = riskDao.getRiskAssessmentsForSite(siteId)

    suspend fun insertRiskAssessment(assessment: RiskAssessmentEntity): Long = riskDao.insertRiskAssessment(assessment)

    fun getInspectionAuditsForSite(siteId: Long): Flow<List<InspectionAuditEntity>> = auditDao.getInspectionAuditsForSite(siteId)

    suspend fun insertInspectionAudit(audit: InspectionAuditEntity): Long = auditDao.insertInspectionAudit(audit)

    fun getMetersForSite(siteId: Long): Flow<List<UtilityMeterEntity>> = meterDao.getMetersForSite(siteId)

    fun getReadingsForSite(siteId: Long): Flow<List<MeterReadingEntity>> = readingDao.getReadingsForSite(siteId)

    suspend fun addMeterReading(
        meterId: Long,
        siteId: Long,
        newReadingValue: Double,
        readerName: String,
        photoUri: String = "",
        notes: String = ""
    ) {
        val meter = meterDao.getMeterById(meterId) ?: return
        val delta = (newReadingValue - meter.lastReading).coerceAtLeast(0.0)
        val isAnomaly = delta > (meter.baselineAvgConsumption * 1.35)

        val newReading = MeterReadingEntity(
            meterId = meterId,
            siteId = siteId,
            readingValue = newReadingValue,
            consumptionDelta = delta,
            readingDate = System.currentTimeMillis(),
            readerName = readerName,
            photoUri = photoUri,
            isAnomalySpike = isAnomaly,
            notes = notes
        )

        readingDao.insertReading(newReading)
        meterDao.updateLastReading(meterId, newReadingValue)
    }

    suspend fun insertUtilityMeter(meter: UtilityMeterEntity): Long = meterDao.insertMeter(meter)

    // Phase 5: Training, Equipment & Compliance
    fun getTrainingLogsForSite(siteId: Long): Flow<List<TrainingLogEntity>> = trainingDao.getTrainingLogsForSite(siteId)

    suspend fun insertTrainingLog(log: TrainingLogEntity): Long = trainingDao.insertTrainingLog(log)

    fun getEquipmentPpeForSite(siteId: Long): Flow<List<EquipmentPpeEntity>> = equipmentDao.getEquipmentPpeForSite(siteId)

    suspend fun insertEquipmentPpe(equipment: EquipmentPpeEntity): Long = equipmentDao.insertEquipmentPpe(equipment)

    fun getAllComplianceItems(): Flow<List<ComplianceMatrixEntity>> = complianceDao.getAllComplianceItems()
}
