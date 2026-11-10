package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entities.PermitEntity
import com.example.model.Language
import com.example.ui.components.QrScannerModal
import com.example.ui.screens.ComplianceMatrixScreen
import com.example.ui.screens.CreatePermitScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.IncidentsScreen
import com.example.ui.screens.InspectionsScreen
import com.example.ui.screens.PermitDetailScreen
import com.example.ui.screens.PermitsScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.RiskAssessmentScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SitesAndRolesScreen
import com.example.ui.screens.TrainingAndEquipmentScreen
import com.example.ui.screens.UtilitiesScreen
import com.example.ui.theme.HSEProTheme
import com.example.ui.theme.SafetyOrangePrimary
import com.example.ui.util.HseStrings
import com.example.ui.viewmodel.HseViewModel

import androidx.compose.material.icons.filled.GridView
import com.example.ui.screens.MoreHubScreen

enum class Screen {
    DASHBOARD,
    PERMITS,
    PERMIT_DETAIL,
    CREATE_PERMIT,
    INCIDENTS,
    UTILITIES,
    COMPLIANCE_MATRIX,
    TRAINING_EQUIPMENT,
    REPORTS,
    RISK_ASSESSMENT,
    INSPECTIONS,
    SITES,
    SETTINGS,
    MORE
}

class MainActivity : ComponentActivity() {
    private val viewModel: HseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val language by viewModel.language.collectAsStateWithLifecycle()
            val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
            val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
            val selectedSiteId by viewModel.selectedSiteId.collectAsStateWithLifecycle()
            val sites by viewModel.sites.collectAsStateWithLifecycle()
            val permits by viewModel.permits.collectAsStateWithLifecycle()
            val incidents by viewModel.incidents.collectAsStateWithLifecycle()
            val checklists by viewModel.checklists.collectAsStateWithLifecycle()
            val riskAssessments by viewModel.riskAssessments.collectAsStateWithLifecycle()
            val inspectionAudits by viewModel.inspectionAudits.collectAsStateWithLifecycle()
            val utilityMeters by viewModel.utilityMeters.collectAsStateWithLifecycle()
            val meterReadings by viewModel.meterReadings.collectAsStateWithLifecycle()
            val trainingLogs by viewModel.trainingLogs.collectAsStateWithLifecycle()
            val equipmentList by viewModel.equipmentList.collectAsStateWithLifecycle()
            val complianceItems by viewModel.complianceItems.collectAsStateWithLifecycle()

            val layoutDirection = if (language == Language.ARABIC) LayoutDirection.Rtl else LayoutDirection.Ltr

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                HSEProTheme(darkTheme = isDarkMode) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        HSEProApp(
                            language = language,
                            isDarkMode = isDarkMode,
                            viewModel = viewModel,
                            sites = sites,
                            selectedSiteId = selectedSiteId,
                            permits = permits,
                            incidents = incidents,
                            checklists = checklists,
                            riskAssessments = riskAssessments,
                            inspectionAudits = inspectionAudits,
                            utilityMeters = utilityMeters,
                            meterReadings = meterReadings,
                            trainingLogs = trainingLogs,
                            equipmentList = equipmentList,
                            complianceItems = complianceItems
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HSEProApp(
    language: Language,
    isDarkMode: Boolean,
    viewModel: HseViewModel,
    sites: List<com.example.data.local.entities.SiteEntity>,
    selectedSiteId: Long,
    permits: List<PermitEntity>,
    incidents: List<com.example.data.local.entities.IncidentEntity>,
    checklists: List<com.example.data.local.entities.ChecklistEntity>,
    riskAssessments: List<com.example.data.local.entities.RiskAssessmentEntity>,
    inspectionAudits: List<com.example.data.local.entities.InspectionAuditEntity>,
    utilityMeters: List<com.example.data.local.entities.UtilityMeterEntity>,
    meterReadings: List<com.example.data.local.entities.MeterReadingEntity>,
    trainingLogs: List<com.example.data.local.entities.TrainingLogEntity>,
    equipmentList: List<com.example.data.local.entities.EquipmentPpeEntity>,
    complianceItems: List<com.example.data.local.entities.ComplianceMatrixEntity>
) {
    var currentScreen by remember { mutableStateOf(Screen.DASHBOARD) }
    var selectedPermitDetail by remember { mutableStateOf<PermitEntity?>(null) }
    var showQrScannerModal by remember { mutableStateOf(false) }

    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val currentSite = sites.find { it.id == selectedSiteId } ?: sites.firstOrNull()

    val moreSubScreens = setOf(
        Screen.COMPLIANCE_MATRIX,
        Screen.TRAINING_EQUIPMENT,
        Screen.UTILITIES,
        Screen.RISK_ASSESSMENT,
        Screen.INSPECTIONS,
        Screen.SITES,
        Screen.SETTINGS,
        Screen.MORE
    )

    Scaffold(
        bottomBar = {
            if (currentScreen != Screen.CREATE_PERMIT && currentScreen != Screen.PERMIT_DETAIL) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    val navItems = listOf(
                        Triple(Screen.DASHBOARD, HseStrings.navDashboard(language), Icons.Default.Home),
                        Triple(Screen.PERMITS, HseStrings.navPermits(language), Icons.Default.Assignment),
                        Triple(Screen.INCIDENTS, HseStrings.navIncidents(language), Icons.Default.ReportProblem),
                        Triple(Screen.REPORTS, HseStrings.navReports(language), Icons.Default.Description),
                        Triple(Screen.MORE, HseStrings.navMore(language), Icons.Default.GridView)
                    )

                    navItems.forEach { (screen, label, icon) ->
                        val isSelected = if (screen == Screen.MORE) {
                            currentScreen in moreSubScreens
                        } else {
                            currentScreen == screen
                        }
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentScreen = screen },
                            icon = { Icon(imageVector = icon, contentDescription = label) },
                            label = { Text(text = label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = SafetyOrangePrimary,
                                selectedTextColor = SafetyOrangePrimary,
                                indicatorColor = SafetyOrangePrimary.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.testTag("nav_tab_${screen.name}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
                when (screen) {
                    Screen.DASHBOARD -> {
                        DashboardScreen(
                            lang = language,
                            role = currentRole,
                            sites = sites,
                            selectedSiteId = selectedSiteId,
                            permits = permits,
                            incidents = incidents,
                            onSelectSite = { viewModel.selectSite(it) },
                            onRecordIncident = { severity, title, desc, loc, rep ->
                                viewModel.recordIncident(severity, title, desc, loc, rep)
                            },
                            onAdjustCounter = { viewModel.adjustCounterDays(it) },
                            onOpenQrScanner = { showQrScannerModal = true },
                            onCreatePermitClick = { currentScreen = Screen.CREATE_PERMIT },
                            onPermitClick = { permit ->
                                selectedPermitDetail = permit
                                currentScreen = Screen.PERMIT_DETAIL
                            }
                        )
                    }

                    Screen.PERMITS -> {
                        PermitsScreen(
                            lang = language,
                            role = currentRole,
                            permits = permits,
                            onSelectPermit = { permit ->
                                selectedPermitDetail = permit
                                currentScreen = Screen.PERMIT_DETAIL
                            },
                            onCreatePermitClick = { currentScreen = Screen.CREATE_PERMIT }
                        )
                    }

                    Screen.PERMIT_DETAIL -> {
                        selectedPermitDetail?.let { permit ->
                            PermitDetailScreen(
                                permit = permit,
                                lang = language,
                                role = currentRole,
                                onBack = { currentScreen = Screen.PERMITS },
                                onUpdateStatus = { newStatus ->
                                    viewModel.updatePermitStatus(permit.id, newStatus)
                                    selectedPermitDetail = permit.copy(status = newStatus.name)
                                },
                                onSaveSignature = { sigPath ->
                                    viewModel.savePermitSignature(permit.id, sigPath)
                                }
                            )
                        } ?: run {
                            currentScreen = Screen.PERMITS
                        }
                    }

                    Screen.CREATE_PERMIT -> {
                        CreatePermitScreen(
                            lang = language,
                            onBack = { currentScreen = Screen.PERMITS },
                            onCreatePermit = { num, type, title, loc, app, sup, hse, from, until, chk, notes ->
                                viewModel.createPermit(num, type, title, loc, app, sup, hse, from, until, chk, notes)
                            }
                        )
                    }

                    Screen.INCIDENTS -> {
                        IncidentsScreen(
                            lang = language,
                            role = currentRole,
                            incidents = incidents,
                            onRecordNewIncidentClick = { currentScreen = Screen.DASHBOARD },
                            onUpdateAnalysis = { incId, whys, fish, capa, photo ->
                                viewModel.updateIncidentAnalysis(incId, whys, fish, capa, photo)
                            }
                        )
                    }

                    Screen.COMPLIANCE_MATRIX -> {
                        ComplianceMatrixScreen(
                            lang = language,
                            complianceItems = complianceItems
                        )
                    }

                    Screen.TRAINING_EQUIPMENT -> {
                        TrainingAndEquipmentScreen(
                            lang = language,
                            trainingLogs = trainingLogs,
                            equipmentList = equipmentList,
                            onAddTrainingLog = { topic, trainer, count, isTbt, exp ->
                                viewModel.addTrainingLog(topic, trainer, count, isTbt, exp)
                            },
                            onAddEquipmentPpe = { name, cat, serial, assigned, status ->
                                viewModel.addEquipmentPpe(name, cat, serial, assigned, status)
                            }
                        )
                    }

                    Screen.UTILITIES -> {
                        UtilitiesScreen(
                            lang = language,
                            meters = utilityMeters,
                            readings = meterReadings,
                            sites = sites,
                            onAddReading = { mId, valVal, name, photo, notes ->
                                viewModel.addMeterReading(mId, valVal, name, photo, notes)
                            },
                            onAddMeter = { type, num, name, loc, initVal, base ->
                                viewModel.addUtilityMeter(type, num, name, loc, initVal, base)
                            }
                        )
                    }

                    Screen.REPORTS -> {
                        ReportsScreen(
                            lang = language,
                            site = currentSite,
                            permits = permits,
                            incidents = incidents,
                            audits = inspectionAudits,
                            readings = meterReadings
                        )
                    }

                    Screen.RISK_ASSESSMENT -> {
                        RiskAssessmentScreen(
                            lang = language,
                            riskAssessments = riskAssessments,
                            onCreateRiskAssessment = { title, loc, assessor, iL, iS, rL, rS, haz, ctrls ->
                                viewModel.createRiskAssessment(title, loc, assessor, iL, iS, rL, rS, haz, ctrls)
                            }
                        )
                    }

                    Screen.INSPECTIONS -> {
                        InspectionsScreen(
                            lang = language,
                            inspectionAudits = inspectionAudits,
                            onCreateInspectionAudit = { title, cat, auditor, score, passed, total, findings ->
                                viewModel.createInspectionAudit(title, cat, auditor, score, passed, total, findings)
                            }
                        )
                    }

                    Screen.SITES -> {
                        SitesAndRolesScreen(
                            lang = language,
                            currentRole = currentRole,
                            sites = sites,
                            selectedSiteId = selectedSiteId,
                            onSelectSite = { viewModel.selectSite(it) },
                            onSelectRole = { viewModel.selectRole(it) },
                            onAddNewSite = { ar, en, code, loc ->
                                viewModel.addNewSite(ar, en, code, loc)
                            }
                        )
                    }

                    Screen.SETTINGS -> {
                        SettingsScreen(
                            lang = language,
                            isDarkMode = isDarkMode,
                            onToggleLanguage = { viewModel.toggleLanguage() },
                            onToggleTheme = { viewModel.toggleTheme() },
                            onExportBackup = { viewModel.exportBackupJson() }
                        )
                    }

                    Screen.MORE -> {
                        MoreHubScreen(
                            lang = language,
                            onNavigate = { target -> currentScreen = target }
                        )
                    }
                }
            }

            if (showQrScannerModal) {
                QrScannerModal(
                    lang = language,
                    allPermits = permits,
                    onDismiss = { showQrScannerModal = false },
                    onPermitFound = { scanned ->
                        selectedPermitDetail = scanned
                        currentScreen = Screen.PERMIT_DETAIL
                    }
                )
            }
        }
    }
}
