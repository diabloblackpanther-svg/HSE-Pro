package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sites")
data class SiteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nameAr: String,
    val nameEn: String,
    val code: String,
    val location: String,
    val incidentFreeDays: Int,
    val startDateTimestamp: Long,
    val recordStreakDays: Int,
    val totalManHours: Long = 250000L,
    val isActive: Boolean = true
)

@Entity(tableName = "permits")
data class PermitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val permitNumber: String,
    val siteId: Long,
    val type: String,
    val title: String,
    val locationDetail: String,
    val applicantName: String,
    val supervisorName: String,
    val hseOfficerName: String,
    val status: String,
    val createdAt: Long,
    val validFrom: Long,
    val validUntil: Long,
    val checklistJson: String,
    val signaturePathData: String = "",
    val notes: String = ""
)

@Entity(tableName = "incidents")
data class IncidentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val siteId: Long,
    val incidentDate: Long,
    val severity: String,
    val title: String,
    val description: String,
    val location: String,
    val reportedBy: String,
    val streakDaysBeforeReset: Int,
    val photoUri: String = "",
    val fiveWhysJson: String = "",
    val fishboneJson: String = "",
    val capaJson: String = "",
    val lostTimeDays: Int = 0
)

@Entity(tableName = "checklists")
data class ChecklistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val siteId: Long,
    val titleAr: String,
    val titleEn: String,
    val category: String,
    val totalItems: Int,
    val completedItems: Int,
    val lastInspected: Long,
    val inspectedBy: String
)

@Entity(tableName = "risk_assessments")
data class RiskAssessmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val siteId: Long,
    val taskTitle: String,
    val location: String,
    val assessorName: String,
    val initialLikelihood: Int,
    val initialSeverity: Int,
    val initialRiskScore: Int,
    val residualLikelihood: Int,
    val residualSeverity: Int,
    val residualRiskScore: Int,
    val hazardsJson: String,
    val controlMeasuresJson: String,
    val createdAt: Long
)

@Entity(tableName = "inspection_audits")
data class InspectionAuditEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val siteId: Long,
    val auditTitle: String,
    val category: String,
    val auditorName: String,
    val compliancePercentage: Int,
    val passedItems: Int,
    val totalItems: Int,
    val findingsJson: String,
    val auditDate: Long
)

@Entity(tableName = "utility_meters")
data class UtilityMeterEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val siteId: Long,
    val type: String,
    val meterNumber: String,
    val meterName: String,
    val locationDetail: String,
    val lastReading: Double,
    val baselineAvgConsumption: Double
)

@Entity(tableName = "meter_readings")
data class MeterReadingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val meterId: Long,
    val siteId: Long,
    val readingValue: Double,
    val consumptionDelta: Double,
    val readingDate: Long,
    val readerName: String,
    val photoUri: String = "",
    val isAnomalySpike: Boolean = false,
    val notes: String = ""
)

@Entity(tableName = "training_logs")
data class TrainingLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val siteId: Long,
    val topicTitle: String,
    val trainerName: String,
    val trainingDate: Long,
    val attendeesCount: Int,
    val isToolboxTalk: Boolean = false,
    val certExpiryDate: Long = 0L,
    val notes: String = ""
)

@Entity(tableName = "equipment_ppe")
data class EquipmentPpeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val siteId: Long,
    val equipmentName: String,
    val category: String, // PPE / FIRE_EXTINGUISHER / EMERGENCY
    val serialNumber: String,
    val assignedTo: String,
    val inspectionStatus: String, // PASS / EXPIRED / NEEDS_SERVICE
    val lastInspectionDate: Long,
    val nextExpiryDate: Long
)

@Entity(tableName = "compliance_matrix")
data class ComplianceMatrixEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String,
    val requirementAr: String,
    val requirementEn: String,
    val referenceLaw: String,
    val referenceClause: String,
    val status: String // COMPLIANT / PENDING / NON_COMPLIANT
)
