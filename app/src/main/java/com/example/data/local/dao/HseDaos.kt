package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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

@Dao
interface SiteDao {
    @Query("SELECT * FROM sites ORDER BY id ASC")
    fun getAllSites(): Flow<List<SiteEntity>>

    @Query("SELECT * FROM sites WHERE id = :siteId")
    suspend fun getSiteById(siteId: Long): SiteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSite(site: SiteEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSites(sites: List<SiteEntity>)

    @Update
    suspend fun updateSite(site: SiteEntity)

    @Query("UPDATE sites SET incidentFreeDays = 0, recordStreakDays = MAX(recordStreakDays, :previousStreak) WHERE id = :siteId")
    suspend fun resetIncidentCounter(siteId: Long, previousStreak: Int)

    @Query("UPDATE sites SET incidentFreeDays = :days, startDateTimestamp = :startTimestamp WHERE id = :siteId")
    suspend fun updateIncidentCounter(siteId: Long, days: Int, startTimestamp: Long)
}

@Dao
interface PermitDao {
    @Query("SELECT * FROM permits WHERE siteId = :siteId ORDER BY createdAt DESC")
    fun getPermitsForSite(siteId: Long): Flow<List<PermitEntity>>

    @Query("SELECT * FROM permits ORDER BY createdAt DESC")
    fun getAllPermits(): Flow<List<PermitEntity>>

    @Query("SELECT * FROM permits WHERE id = :permitId")
    suspend fun getPermitById(permitId: Long): PermitEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPermit(permit: PermitEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPermits(permits: List<PermitEntity>)

    @Update
    suspend fun updatePermit(permit: PermitEntity)

    @Query("UPDATE permits SET status = :status WHERE id = :permitId")
    suspend fun updatePermitStatus(permitId: Long, status: String)

    @Query("UPDATE permits SET signaturePathData = :signature WHERE id = :permitId")
    suspend fun updateSignature(permitId: Long, signature: String)
}

@Dao
interface IncidentDao {
    @Query("SELECT * FROM incidents WHERE siteId = :siteId ORDER BY incidentDate DESC")
    fun getIncidentsForSite(siteId: Long): Flow<List<IncidentEntity>>

    @Query("SELECT * FROM incidents ORDER BY incidentDate DESC")
    fun getAllIncidents(): Flow<List<IncidentEntity>>

    @Query("SELECT * FROM incidents WHERE id = :incidentId")
    suspend fun getIncidentById(incidentId: Long): IncidentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncident(incident: IncidentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncidents(incidents: List<IncidentEntity>)

    @Update
    suspend fun updateIncident(incident: IncidentEntity)
}

@Dao
interface ChecklistDao {
    @Query("SELECT * FROM checklists WHERE siteId = :siteId ORDER BY id ASC")
    fun getChecklistsForSite(siteId: Long): Flow<List<ChecklistEntity>>

    @Query("SELECT * FROM checklists ORDER BY id ASC")
    fun getAllChecklists(): Flow<List<ChecklistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklist(checklist: ChecklistEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklists(checklists: List<ChecklistEntity>)

    @Update
    suspend fun updateChecklist(checklist: ChecklistEntity)
}

@Dao
interface RiskAssessmentDao {
    @Query("SELECT * FROM risk_assessments WHERE siteId = :siteId ORDER BY createdAt DESC")
    fun getRiskAssessmentsForSite(siteId: Long): Flow<List<RiskAssessmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRiskAssessment(assessment: RiskAssessmentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRiskAssessments(assessments: List<RiskAssessmentEntity>)
}

@Dao
interface InspectionAuditDao {
    @Query("SELECT * FROM inspection_audits WHERE siteId = :siteId ORDER BY auditDate DESC")
    fun getInspectionAuditsForSite(siteId: Long): Flow<List<InspectionAuditEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInspectionAudit(audit: InspectionAuditEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInspectionAudits(audits: List<InspectionAuditEntity>)
}

@Dao
interface UtilityMeterDao {
    @Query("SELECT * FROM utility_meters WHERE siteId = :siteId ORDER BY id ASC")
    fun getMetersForSite(siteId: Long): Flow<List<UtilityMeterEntity>>

    @Query("SELECT * FROM utility_meters WHERE id = :meterId")
    suspend fun getMeterById(meterId: Long): UtilityMeterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeter(meter: UtilityMeterEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeters(meters: List<UtilityMeterEntity>)

    @Query("UPDATE utility_meters SET lastReading = :newReading WHERE id = :meterId")
    suspend fun updateLastReading(meterId: Long, newReading: Double)
}

@Dao
interface MeterReadingDao {
    @Query("SELECT * FROM meter_readings WHERE siteId = :siteId ORDER BY readingDate DESC")
    fun getReadingsForSite(siteId: Long): Flow<List<MeterReadingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReading(reading: MeterReadingEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReadings(readings: List<MeterReadingEntity>)
}

@Dao
interface TrainingLogDao {
    @Query("SELECT * FROM training_logs WHERE siteId = :siteId ORDER BY trainingDate DESC")
    fun getTrainingLogsForSite(siteId: Long): Flow<List<TrainingLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrainingLog(log: TrainingLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrainingLogs(logs: List<TrainingLogEntity>)
}

@Dao
interface EquipmentPpeDao {
    @Query("SELECT * FROM equipment_ppe WHERE siteId = :siteId ORDER BY id ASC")
    fun getEquipmentPpeForSite(siteId: Long): Flow<List<EquipmentPpeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipmentPpe(equipment: EquipmentPpeEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipmentPpeList(equipments: List<EquipmentPpeEntity>)
}

@Dao
interface ComplianceMatrixDao {
    @Query("SELECT * FROM compliance_matrix ORDER BY id ASC")
    fun getAllComplianceItems(): Flow<List<ComplianceMatrixEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComplianceItems(items: List<ComplianceMatrixEntity>)
}
