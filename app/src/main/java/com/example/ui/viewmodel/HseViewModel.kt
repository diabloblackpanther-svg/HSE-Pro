package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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
import com.example.data.repository.DemoDataInitializer
import com.example.data.repository.HseRepository
import com.example.model.Language
import com.example.model.PermitStatus
import com.example.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class HseViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = HseRepository(db)

    private val _language = MutableStateFlow(Language.ARABIC)
    val language: StateFlow<Language> = _language.asStateFlow()

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _currentRole = MutableStateFlow(UserRole.ADMIN)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    private val _selectedSiteId = MutableStateFlow(1L)
    val selectedSiteId: StateFlow<Long> = _selectedSiteId.asStateFlow()

    val sites: StateFlow<List<SiteEntity>> = repository.getAllSites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val permits: StateFlow<List<PermitEntity>> = _selectedSiteId
        .flatMapLatest { siteId -> repository.getPermitsForSite(siteId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val incidents: StateFlow<List<IncidentEntity>> = _selectedSiteId
        .flatMapLatest { siteId -> repository.getIncidentsForSite(siteId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val checklists: StateFlow<List<ChecklistEntity>> = _selectedSiteId
        .flatMapLatest { siteId -> repository.getChecklistsForSite(siteId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val riskAssessments: StateFlow<List<RiskAssessmentEntity>> = _selectedSiteId
        .flatMapLatest { siteId -> repository.getRiskAssessmentsForSite(siteId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val inspectionAudits: StateFlow<List<InspectionAuditEntity>> = _selectedSiteId
        .flatMapLatest { siteId -> repository.getInspectionAuditsForSite(siteId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val utilityMeters: StateFlow<List<UtilityMeterEntity>> = _selectedSiteId
        .flatMapLatest { siteId -> repository.getMetersForSite(siteId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val meterReadings: StateFlow<List<MeterReadingEntity>> = _selectedSiteId
        .flatMapLatest { siteId -> repository.getReadingsForSite(siteId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trainingLogs: StateFlow<List<TrainingLogEntity>> = _selectedSiteId
        .flatMapLatest { siteId -> repository.getTrainingLogsForSite(siteId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val equipmentList: StateFlow<List<EquipmentPpeEntity>> = _selectedSiteId
        .flatMapLatest { siteId -> repository.getEquipmentPpeForSite(siteId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val complianceItems: StateFlow<List<ComplianceMatrixEntity>> = repository.getAllComplianceItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            DemoDataInitializer.populateInitialDataIfEmpty(db)
        }
    }

    fun toggleLanguage() {
        _language.value = if (_language.value == Language.ARABIC) Language.ENGLISH else Language.ARABIC
    }

    fun toggleTheme() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun selectRole(role: UserRole) {
        _currentRole.value = role
    }

    fun selectSite(siteId: Long) {
        _selectedSiteId.value = siteId
    }

    fun recordIncident(
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
        viewModelScope.launch {
            val siteId = _selectedSiteId.value
            val currentSite = repository.getSiteById(siteId)
            val previousStreak = currentSite?.incidentFreeDays ?: 0

            repository.recordIncidentAndResetCounter(
                siteId = siteId,
                previousStreak = previousStreak,
                severity = severity,
                title = title,
                description = description,
                location = location,
                reportedBy = reportedBy,
                photoUri = photoUri,
                fiveWhysJson = fiveWhysJson,
                fishboneJson = fishboneJson,
                capaJson = capaJson,
                lostTimeDays = lostTimeDays
            )
        }
    }

    fun updateIncidentAnalysis(
        incidentId: Long,
        fiveWhysJson: String,
        fishboneJson: String,
        capaJson: String,
        photoUri: String = ""
    ) {
        viewModelScope.launch {
            val existing = repository.getIncidentById(incidentId) ?: return@launch
            val updated = existing.copy(
                fiveWhysJson = fiveWhysJson,
                fishboneJson = fishboneJson,
                capaJson = capaJson,
                photoUri = if (photoUri.isNotBlank()) photoUri else existing.photoUri
            )
            repository.updateIncident(updated)
        }
    }

    fun adjustCounterDays(newDays: Int) {
        viewModelScope.launch {
            val siteId = _selectedSiteId.value
            val now = System.currentTimeMillis()
            val dayMs = 86400000L
            val newStartTimestamp = now - (newDays * dayMs)
            repository.updateIncidentCounter(siteId, newDays, newStartTimestamp)
        }
    }

    fun createPermit(
        permitNumber: String,
        type: String,
        title: String,
        locationDetail: String,
        applicantName: String,
        supervisorName: String,
        hseOfficerName: String,
        validFrom: Long,
        validUntil: Long,
        checklistItems: List<String>,
        notes: String
    ) {
        viewModelScope.launch {
            val jsonArray = JSONArray()
            checklistItems.forEach { item ->
                val obj = JSONObject()
                obj.put("itemAr", item)
                obj.put("itemEn", item)
                obj.put("checked", false)
                jsonArray.put(obj)
            }

            val permit = PermitEntity(
                permitNumber = permitNumber,
                siteId = _selectedSiteId.value,
                type = type,
                title = title,
                locationDetail = locationDetail,
                applicantName = applicantName,
                supervisorName = supervisorName,
                hseOfficerName = hseOfficerName,
                status = PermitStatus.DRAFT.name,
                createdAt = System.currentTimeMillis(),
                validFrom = validFrom,
                validUntil = validUntil,
                checklistJson = jsonArray.toString(),
                notes = notes
            )
            repository.insertPermit(permit)
        }
    }

    fun updatePermitStatus(permitId: Long, newStatus: PermitStatus) {
        viewModelScope.launch {
            repository.updatePermitStatus(permitId, newStatus.name)
        }
    }

    fun savePermitSignature(permitId: Long, signaturePath: String) {
        viewModelScope.launch {
            repository.updateSignature(permitId, signaturePath)
        }
    }

    fun toggleChecklistItem(checklistId: Long, itemIndex: Int) {
        viewModelScope.launch {
            val checklist = checklists.value.find { it.id == checklistId } ?: return@launch
            val updatedCompleted = (checklist.completedItems + 1).coerceAtMost(checklist.totalItems)
            val updated = checklist.copy(
                completedItems = if (checklist.completedItems >= checklist.totalItems) 0 else updatedCompleted,
                lastInspected = System.currentTimeMillis()
            )
            repository.updateChecklist(updated)
        }
    }

    fun addNewSite(nameAr: String, nameEn: String, code: String, location: String) {
        viewModelScope.launch {
            val newSite = SiteEntity(
                nameAr = nameAr,
                nameEn = nameEn,
                code = code,
                location = location,
                incidentFreeDays = 0,
                startDateTimestamp = System.currentTimeMillis(),
                recordStreakDays = 0,
                isActive = true
            )
            val id = repository.insertSite(newSite)
            _selectedSiteId.value = id
        }
    }

    fun createRiskAssessment(
        taskTitle: String,
        location: String,
        assessorName: String,
        initialLikelihood: Int,
        initialSeverity: Int,
        residualLikelihood: Int,
        residualSeverity: Int,
        hazardsJson: String,
        controlMeasures: String
    ) {
        viewModelScope.launch {
            val assessment = RiskAssessmentEntity(
                siteId = _selectedSiteId.value,
                taskTitle = taskTitle,
                location = location,
                assessorName = assessorName,
                initialLikelihood = initialLikelihood,
                initialSeverity = initialSeverity,
                initialRiskScore = initialLikelihood * initialSeverity,
                residualLikelihood = residualLikelihood,
                residualSeverity = residualSeverity,
                residualRiskScore = residualLikelihood * residualSeverity,
                hazardsJson = hazardsJson,
                controlMeasuresJson = controlMeasures,
                createdAt = System.currentTimeMillis()
            )
            repository.insertRiskAssessment(assessment)
        }
    }

    fun createInspectionAudit(
        auditTitle: String,
        category: String,
        auditorName: String,
        compliancePercentage: Int,
        passedItems: Int,
        totalItems: Int,
        findingsJson: String
    ) {
        viewModelScope.launch {
            val audit = InspectionAuditEntity(
                siteId = _selectedSiteId.value,
                auditTitle = auditTitle,
                category = category,
                auditorName = auditorName,
                compliancePercentage = compliancePercentage,
                passedItems = passedItems,
                totalItems = totalItems,
                findingsJson = findingsJson,
                auditDate = System.currentTimeMillis()
            )
            repository.insertInspectionAudit(audit)
        }
    }

    fun addMeterReading(
        meterId: Long,
        newReadingValue: Double,
        readerName: String,
        photoUri: String = "",
        notes: String = ""
    ) {
        viewModelScope.launch {
            repository.addMeterReading(
                meterId = meterId,
                siteId = _selectedSiteId.value,
                newReadingValue = newReadingValue,
                readerName = readerName,
                photoUri = photoUri,
                notes = notes
            )
        }
    }

    fun addUtilityMeter(
        type: String,
        meterNumber: String,
        meterName: String,
        locationDetail: String,
        initialReading: Double,
        baselineAvg: Double
    ) {
        viewModelScope.launch {
            val newMeter = UtilityMeterEntity(
                siteId = _selectedSiteId.value,
                type = type,
                meterNumber = meterNumber,
                meterName = meterName,
                locationDetail = locationDetail,
                lastReading = initialReading,
                baselineAvgConsumption = baselineAvg
            )
            repository.insertUtilityMeter(newMeter)
        }
    }

    // Phase 5: Training & PPE methods
    fun addTrainingLog(
        topicTitle: String,
        trainerName: String,
        attendeesCount: Int,
        isToolboxTalk: Boolean,
        certExpiryDays: Int
    ) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val dayMs = 86400000L
            val log = TrainingLogEntity(
                siteId = _selectedSiteId.value,
                topicTitle = topicTitle,
                trainerName = trainerName,
                trainingDate = now,
                attendeesCount = attendeesCount,
                isToolboxTalk = isToolboxTalk,
                certExpiryDate = now + (certExpiryDays * dayMs)
            )
            repository.insertTrainingLog(log)
        }
    }

    fun addEquipmentPpe(
        equipmentName: String,
        category: String,
        serialNumber: String,
        assignedTo: String,
        status: String
    ) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val dayMs = 86400000L
            val eq = EquipmentPpeEntity(
                siteId = _selectedSiteId.value,
                equipmentName = equipmentName,
                category = category,
                serialNumber = serialNumber,
                assignedTo = assignedTo,
                inspectionStatus = status,
                lastInspectionDate = now,
                nextExpiryDate = now + (180 * dayMs)
            )
            repository.insertEquipmentPpe(eq)
        }
    }

    // Phase 5: Backup & Restore JSON
    fun exportBackupJson(): String {
        val root = JSONObject()
        root.put("app", "HSE Pro")
        root.put("version", 5)
        root.put("timestamp", System.currentTimeMillis())

        val sitesArray = JSONArray()
        sites.value.forEach { s ->
            val obj = JSONObject()
            obj.put("id", s.id)
            obj.put("nameAr", s.nameAr)
            obj.put("nameEn", s.nameEn)
            obj.put("code", s.code)
            obj.put("incidentFreeDays", s.incidentFreeDays)
            sitesArray.put(obj)
        }
        root.put("sites", sitesArray)

        return root.toString(2)
    }
}
