package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.ChecklistDao
import com.example.data.local.dao.ComplianceMatrixDao
import com.example.data.local.dao.EquipmentPpeDao
import com.example.data.local.dao.IncidentDao
import com.example.data.local.dao.InspectionAuditDao
import com.example.data.local.dao.MeterReadingDao
import com.example.data.local.dao.PermitDao
import com.example.data.local.dao.RiskAssessmentDao
import com.example.data.local.dao.SiteDao
import com.example.data.local.dao.TrainingLogDao
import com.example.data.local.dao.UtilityMeterDao
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

@Database(
    entities = [
        SiteEntity::class,
        PermitEntity::class,
        IncidentEntity::class,
        ChecklistEntity::class,
        RiskAssessmentEntity::class,
        InspectionAuditEntity::class,
        UtilityMeterEntity::class,
        MeterReadingEntity::class,
        TrainingLogEntity::class,
        EquipmentPpeEntity::class,
        ComplianceMatrixEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun siteDao(): SiteDao
    abstract fun permitDao(): PermitDao
    abstract fun incidentDao(): IncidentDao
    abstract fun checklistDao(): ChecklistDao
    abstract fun riskAssessmentDao(): RiskAssessmentDao
    abstract fun inspectionAuditDao(): InspectionAuditDao
    abstract fun utilityMeterDao(): UtilityMeterDao
    abstract fun meterReadingDao(): MeterReadingDao
    abstract fun trainingLogDao(): TrainingLogDao
    abstract fun equipmentPpeDao(): EquipmentPpeDao
    abstract fun complianceMatrixDao(): ComplianceMatrixDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hse_pro_database.db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
