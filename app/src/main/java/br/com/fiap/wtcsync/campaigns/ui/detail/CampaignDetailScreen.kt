package br.com.fiap.wtcsync.campaigns.ui.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.fiap.wtcsync.R
import br.com.fiap.wtcsync.campaigns.domain.Campaign
import br.com.fiap.wtcsync.data.model.enums.UserRole
import br.com.fiap.wtcsync.theme.Spacing
import br.com.fiap.wtcsync.ui.components.WtcButton
import br.com.fiap.wtcsync.ui.components.WtcButtonVariant
import br.com.fiap.wtcsync.ui.components.WtcErrorState
import br.com.fiap.wtcsync.ui.components.WtcLoading
import br.com.fiap.wtcsync.ui.components.WtcScaffold
import br.com.fiap.wtcsync.ui.components.WtcStatusBadge
import br.com.fiap.wtcsync.ui.components.WtcTopBar
import coil.compose.rememberAsyncImagePainter
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Composable
fun CampaignDetailScreen(
    viewModel: CampaignDetailViewModel,
    onBackClick: () -> Unit,
    userRole: UserRole? = null,
) {
    val state by viewModel.uiState.collectAsState()

    WtcScaffold(
        topBar = { WtcTopBar(title = "Campanha", onBackClick = onBackClick) },
    ) { padding ->
        when {
            state.isLoading -> WtcLoading(modifier = Modifier.padding(padding))
            state.error != null -> WtcErrorState(
                message = state.error!!,
                modifier = Modifier.padding(padding),
                onRetry = { viewModel.loadCampaign() },
            )
            state.campaign != null -> CampaignDetailContent(
                campaign = state.campaign!!,
                userRole = userRole,
                isScheduling = state.isScheduling,
                isSending = state.isSending,
                actionError = state.actionError,
                actionSuccess = state.actionSuccess,
                onSchedule = { viewModel.scheduleCampaign(it) },
                onSend = { viewModel.sendCampaign() },
                onClearFeedback = { viewModel.clearActionFeedback() },
                modifier = Modifier.padding(padding),
            )
        }
    }
}

@Composable
private fun CampaignDetailContent(
    campaign: Campaign,
    userRole: UserRole?,
    isScheduling: Boolean,
    isSending: Boolean,
    actionError: String?,
    actionSuccess: String?,
    onSchedule: (String) -> Unit,
    onSend: () -> Unit,
    onClearFeedback: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showScheduleDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.lg, vertical = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.xl),
    ) {
        if (campaign.mediaUrl != null) {
            CampaignBanner(mediaUrl = campaign.mediaUrl)
        }

        CampaignInfoSection(campaign = campaign)

        if (campaign.body.isNotBlank()) {
            BodySection(body = campaign.body)
        }

        StatsSection(campaign = campaign)

        if (actionError != null) {
            FeedbackMessage(text = actionError, isError = true, onDismiss = onClearFeedback)
        }
        if (actionSuccess != null) {
            FeedbackMessage(text = actionSuccess, isError = false, onDismiss = onClearFeedback)
        }

        if (userRole == UserRole.OPERATOR && campaign.status.lowercase() in listOf("draft", "scheduled")) {
            CampaignActions(
                status = campaign.status,
                isScheduling = isScheduling,
                isSending = isSending,
                onScheduleClick = { showScheduleDialog = true },
                onSendClick = onSend,
            )
        }

        if (campaign.actions.isNotEmpty()) {
            ActionButtonsSection(campaign = campaign)
        }

        FooterSection(campaign = campaign)
    }

    if (showScheduleDialog) {
        ScheduleDateTimeDialog(
            onConfirm = { scheduledAt ->
                showScheduleDialog = false
                onSchedule(scheduledAt)
            },
            onDismiss = { showScheduleDialog = false },
        )
    }
}

@Composable
private fun CampaignBanner(mediaUrl: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(193.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = mediaUrl,
                    error = painterResource(id = R.drawable.publi_image),
                    placeholder = painterResource(id = R.drawable.publi_image),
                ),
                contentDescription = "Banner da campanha",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }
    }
}

@Composable
private fun CampaignInfoSection(campaign: Campaign) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        WtcStatusBadge(status = campaign.status)
        Text(
            text = campaign.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = "Criado por: ${campaign.createdBy}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun BodySection(body: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Text(
                text = "Mensagem",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun StatsSection(campaign: Campaign) {
    val stats = campaign.stats
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Text(
                text = "Estatísticas",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                StatCard(label = "Alvo", value = "${stats.totalTargeted}")
                StatCard(label = "Entregues", value = "${stats.totalDelivered}")
                StatCard(label = "Lidas", value = "${stats.totalRead}")
                StatCard(label = "Falhas", value = "${stats.totalFailed}")
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun CampaignActions(
    status: String,
    isScheduling: Boolean,
    isSending: Boolean,
    onScheduleClick: () -> Unit,
    onSendClick: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        if (status.lowercase() == "draft") {
            WtcButton(
                text = if (isScheduling) "Agendando..." else "Agendar",
                onClick = onScheduleClick,
                isLoading = isScheduling,
                enabled = !isScheduling && !isSending,
                variant = WtcButtonVariant.Outline,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        WtcButton(
            text = if (isSending) "Enviando..." else "Enviar agora",
            onClick = onSendClick,
            isLoading = isSending,
            enabled = !isScheduling && !isSending,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun FeedbackMessage(text: String, isError: Boolean, onDismiss: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        color = if (isError) MaterialTheme.colorScheme.errorContainer
                else MaterialTheme.colorScheme.primaryContainer,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(Spacing.md),
            style = MaterialTheme.typography.bodySmall,
            color = if (isError) MaterialTheme.colorScheme.onErrorContainer
                    else MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}

@Composable
private fun ActionButtonsSection(campaign: Campaign) {
    val openUrl = rememberOpenUrl()
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        campaign.actions.forEach { action ->
            val url = campaign.actionUrls[action.action]
            Surface(
                onClick = { url?.let { openUrl(it) } },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primaryContainer,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = Spacing.xl),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(Spacing.sm))
                    Text(
                        text = action.title,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        }
    }
}

@Composable
private fun FooterSection(campaign: Campaign) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceVariant,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.xs),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(12.dp),
                )
                Text(
                    text = "Criado em ${campaign.createdAt.take(10)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleDateTimeDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var step by remember { mutableStateOf(0) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis() + 86400000L,
    )
    val timePickerState = rememberTimePickerState(
        initialHour = 10,
        initialMinute = 0,
        is24Hour = true,
    )

    if (step == 0) {
        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = { TextButton(onClick = { step = 1 }) { Text("Próximo") } },
            dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (step == 1 && datePickerState.selectedDateMillis != null) {
        AlertDialog(
            onDismissRequest = { step = 0 },
            title = { Text("Selecione o horário") },
            text = { TimePicker(state = timePickerState) },
            confirmButton = {
                TextButton(
                    onClick = {
                        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
                        sdf.timeZone = TimeZone.getTimeZone("UTC")
                        val cal = java.util.Calendar.getInstance()
                        cal.timeInMillis = datePickerState.selectedDateMillis!!
                        cal.set(java.util.Calendar.HOUR_OF_DAY, timePickerState.hour)
                        cal.set(java.util.Calendar.MINUTE, timePickerState.minute)
                        cal.set(java.util.Calendar.SECOND, 0)
                        cal.set(java.util.Calendar.MILLISECOND, 0)
                        onConfirm(sdf.format(cal.time))
                    }
                ) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(onClick = { step = 0 }) { Text("Voltar") }
            },
        )
    }
}

@Composable
private fun rememberOpenUrl(): (String) -> Unit {
    val context = androidx.compose.ui.platform.LocalContext.current
    return { url ->
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
        context.startActivity(intent)
    }
}
