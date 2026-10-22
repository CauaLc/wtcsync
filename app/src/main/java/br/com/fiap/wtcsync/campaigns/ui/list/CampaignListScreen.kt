package br.com.fiap.wtcsync.campaigns.ui.list

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.com.fiap.wtcsync.campaigns.domain.Campaign
import br.com.fiap.wtcsync.data.model.enums.UserRole
import br.com.fiap.wtcsync.theme.Spacing
import br.com.fiap.wtcsync.ui.components.WtcButton
import br.com.fiap.wtcsync.ui.components.WtcButtonVariant
import br.com.fiap.wtcsync.ui.components.WtcChip
import br.com.fiap.wtcsync.ui.components.WtcEmptyState
import br.com.fiap.wtcsync.ui.components.WtcErrorState
import br.com.fiap.wtcsync.ui.components.WtcLoading
import br.com.fiap.wtcsync.ui.components.WtcScaffold
import br.com.fiap.wtcsync.ui.components.WtcStatusBadge
import br.com.fiap.wtcsync.ui.components.WtcTextField
import br.com.fiap.wtcsync.ui.components.WtcTopBar

@Composable
fun CampaignListScreen(
    viewModel: CampaignListViewModel,
    onCampaignClick: (String) -> Unit,
    onCreateClick: () -> Unit,
    userRole: UserRole?,
) {
    val state by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Todas") }
    var campaignToDelete by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CampaignListEvent.DeleteError ->
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                is CampaignListEvent.DeleteSuccess ->
                    Toast.makeText(context, "Campanha excluída com sucesso", Toast.LENGTH_SHORT).show()
            }
        }
    }

    WtcScaffold(
        topBar = {
            WtcTopBar(
                title = "Campanhas",
                actions = {
                    if (userRole == UserRole.OPERATOR) {
                        IconButton(onClick = onCreateClick) {
                            Icon(Icons.Default.Add, contentDescription = "Nova campanha")
                        }
                    }
                },
            )
        },
    ) { padding ->
        when {
            state.isLoading -> WtcLoading(modifier = Modifier.padding(padding))
            state.error != null -> WtcErrorState(
                message = state.error!!,
                modifier = Modifier.padding(padding),
                onRetry = { viewModel.refresh() },
            )
            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = Spacing.lg, vertical = Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.lg),
            ) {
                item {
                    WtcTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            viewModel.onSearchQueryChanged(it)
                        },
                        placeholder = "Buscar campanhas...",
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                        listOf("Todas", "Rascunho", "Agendadas", "Enviadas").forEach { filter ->
                            WtcChip(
                                label = filter,
                                selected = selectedFilter == filter,
                                onClick = {
                                    selectedFilter = filter
                                    viewModel.onFilterSelected(filter)
                                },
                            )
                        }
                    }
                }
                if (state.isEmpty) {
                    item {
                        WtcEmptyState(
                            title = "Nenhuma campanha",
                            description = "Crie sua primeira campanha.",
                            action = if (userRole == UserRole.OPERATOR) {
                                { WtcButton("Nova campanha", onClick = onCreateClick, modifier = Modifier.fillMaxWidth()) }
                            } else null,
                        )
                    }
                } else {
                    items(state.campaigns, key = { it.id }) { campaign ->
                        CampaignCard(
                            campaign = campaign,
                            onClick = { onCampaignClick(campaign.id) },
                            onDeleteClick = { campaignToDelete = campaign.id },
                        )
                    }
                }
                item { PromoCard() }
            }
        }
    }

    if (campaignToDelete != null) {
        AlertDialog(
            onDismissRequest = { campaignToDelete = null },
            title = { Text("Excluir campanha") },
            text = { Text("Tem certeza que deseja excluir esta campanha? Esta ação não pode ser desfeita.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCampaign(campaignToDelete!!)
                        campaignToDelete = null
                    }
                ) {
                    Text("Excluir", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { campaignToDelete = null }) {
                    Text("Cancelar")
                }
            },
        )
    }
}

@Composable
private fun CampaignCard(
    campaign: Campaign,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    var showMenu by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                ) {
                    WtcStatusBadge(status = campaign.status)
                    Text(
                        text = campaign.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Box {
                    IconButton(onClick = { showMenu = true }, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Mais opções",
                            modifier = Modifier.size(16.dp),
                        )
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("Excluir", color = MaterialTheme.colorScheme.error) },
                            onClick = { showMenu = false; onDeleteClick() },
                        )
                    }
                }
            }
            Text(
                text = campaign.body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (campaign.stats.totalDelivered > 0 || campaign.stats.totalRead > 0) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
                    modifier = Modifier.padding(top = Spacing.xs),
                ) {
                    if (campaign.stats.totalDelivered > 0) {
                        Text(
                            text = "${campaign.stats.totalDelivered} entregues",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    if (campaign.stats.totalRead > 0) {
                        Text(
                            text = "${campaign.stats.totalRead} lidas",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PromoCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.secondary,
    ) {
        Column(
            modifier = Modifier.padding(Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Text(
                text = "Potencialize seus resultados",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondary,
            )
            Text(
                text = "Crie automações baseadas no comportamento do seu cliente.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.8f),
            )
            Spacer(Modifier.size(Spacing.sm))
            WtcButton(
                text = "Explorar templates",
                onClick = {},
                variant = WtcButtonVariant.Primary,
            )
        }
    }
}
