package br.com.fiap.wtcsync.campaigns.ui.client

import android.app.Application
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
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.wtcsync.R
import br.com.fiap.wtcsync.WtcSyncApp
import br.com.fiap.wtcsync.campaigns.data.CampaignRepository
import br.com.fiap.wtcsync.campaigns.domain.Campaign
import br.com.fiap.wtcsync.theme.Spacing
import br.com.fiap.wtcsync.ui.components.WtcEmptyState
import br.com.fiap.wtcsync.ui.components.WtcErrorState
import br.com.fiap.wtcsync.ui.components.WtcLoading
import br.com.fiap.wtcsync.ui.components.WtcScaffold
import br.com.fiap.wtcsync.ui.components.WtcStatusBadge
import br.com.fiap.wtcsync.ui.components.WtcTopBar
import br.com.fiap.wtcsync.util.Resource
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ClientCampaignUiState(
    val campaign: Campaign? = null,
    val isLoading: Boolean = false,
    val isEmpty: Boolean = false,
    val error: String? = null
)

class ClientCampaignViewModel(
    private val repository: CampaignRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientCampaignUiState(isLoading = true))
    val uiState: StateFlow<ClientCampaignUiState> = _uiState

    init {
        loadLastCampaign()
    }

    fun loadLastCampaign() {
        viewModelScope.launch {
            _uiState.value = ClientCampaignUiState(isLoading = true)
            when (val result = repository.listCampaigns()) {
                is Resource.Success -> {
                    val campaigns = result.data ?: emptyList()
                    val sentCampaigns = campaigns
                        .filter { it.status.equals("sent", ignoreCase = true) }
                        .sortedByDescending { it.createdAt }

                    val lastCampaign = sentCampaigns.firstOrNull()
                    if (lastCampaign != null) {
                        _uiState.value = ClientCampaignUiState(campaign = lastCampaign)
                    } else {
                        _uiState.value = ClientCampaignUiState(isEmpty = true)
                    }
                }
                is Resource.Error -> {
                    _uiState.value = ClientCampaignUiState(
                        error = result.message ?: "Erro ao carregar campanhas"
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }
}

class ClientCampaignViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val app = application as WtcSyncApp
        return ClientCampaignViewModel(
            repository = app.campaignRepository
        ) as T
    }
}

@Composable
fun ClientCampaignScreen(application: Application) {
    val viewModel: ClientCampaignViewModel = viewModel(
        factory = ClientCampaignViewModelFactory(application)
    )
    val state by viewModel.uiState.collectAsState()

    WtcScaffold(
        topBar = { WtcTopBar(title = "Campanha Recebida") },
    ) { padding ->
        when {
            state.isLoading -> WtcLoading(modifier = Modifier.padding(padding))
            state.error != null -> WtcErrorState(
                message = state.error!!,
                modifier = Modifier.padding(padding),
                onRetry = { viewModel.loadLastCampaign() },
            )
            state.isEmpty -> WtcEmptyState(
                title = "Nenhuma campanha recebida",
                description = "Você ainda não recebeu nenhuma campanha.",
                modifier = Modifier.padding(padding),
            )
            state.campaign != null -> CampaignDetailView(
                campaign = state.campaign!!,
                modifier = Modifier.padding(padding),
            )
        }
    }
}

@Composable
private fun CampaignDetailView(campaign: Campaign, modifier: Modifier = Modifier) {
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

        if (campaign.actions.isNotEmpty()) {
            ActionButtonsSection(campaign = campaign)
        }

        FooterSection(campaign = campaign)

        Spacer(Modifier.height(Spacing.lg))
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
private fun rememberOpenUrl(): (String) -> Unit {
    val context = androidx.compose.ui.platform.LocalContext.current
    return { url ->
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
        context.startActivity(intent)
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
