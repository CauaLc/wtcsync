package br.com.fiap.wtcsync.ui.crm

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.wtcsync.WtcSyncApp
import br.com.fiap.wtcsync.data.remote.dto.ClientDto
import br.com.fiap.wtcsync.theme.Spacing
import br.com.fiap.wtcsync.ui.components.WtcAvatar
import br.com.fiap.wtcsync.ui.components.WtcChip
import br.com.fiap.wtcsync.ui.components.WtcScaffold
import br.com.fiap.wtcsync.ui.components.WtcStatusBadge
import br.com.fiap.wtcsync.ui.components.WtcTextField
import br.com.fiap.wtcsync.ui.components.WtcTopBar
import br.com.fiap.wtcsync.viewmodel.ClientesViewModel
import br.com.fiap.wtcsync.viewmodel.ClientesViewModelFactory

@Composable
fun ClientesScreen(
    onCreateClick: () -> Unit = {},
    onClienteClick: (id: String) -> Unit = {},
) {
    val app = LocalContext.current.applicationContext as WtcSyncApp
    val viewModel: ClientesViewModel = viewModel(
        factory = ClientesViewModelFactory(app.apiService.clientApi)
    )
    val uiState by viewModel.uiState.collectAsState()

    WtcScaffold(
        topBar = {
            WtcTopBar(
                title = "Clientes",
                actions = {
                    IconButton(onClick = onCreateClick) {
                        Icon(Icons.Default.Add, contentDescription = "Novo cliente")
                    }
                },
            )
        },
    ) { padding ->
        when {
            uiState.isLoading -> br.com.fiap.wtcsync.ui.components.WtcLoading(
                modifier = Modifier.padding(padding)
            )
            uiState.error != null -> br.com.fiap.wtcsync.ui.components.WtcErrorState(
                message = uiState.error!!,
                modifier = Modifier.padding(padding),
                onRetry = { viewModel.loadClients() },
            )
            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                item {
                    Spacer(Modifier.height(Spacing.lg))
                    WtcTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.onSearchChanged(it) },
                        placeholder = "Buscar por nome ou email",
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(Spacing.md))
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                        listOf("Todos", "Active", "Inactive", "Prospect").forEach { filter ->
                            WtcChip(
                                label = filter,
                                selected = uiState.selectedFilter == filter,
                                onClick = { viewModel.onFilterChanged(filter) },
                            )
                        }
                    }
                    Spacer(Modifier.height(Spacing.lg))
                }

                if (uiState.clients.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "Nenhum cliente encontrado",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                } else {
                    items(uiState.clients, key = { it.id }) { cliente ->
                        ClientCard(cliente = cliente, onClick = { onClienteClick(cliente.id) })
                        Spacer(Modifier.height(Spacing.md))
                    }
                }
                item { Spacer(Modifier.height(Spacing.lg)) }
            }
        }
    }
}

@Composable
private fun ClientCard(cliente: ClientDto, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        shadowElevation = 1.dp,
    ) {
        Row(modifier = Modifier.padding(Spacing.lg), verticalAlignment = Alignment.CenterVertically) {
            WtcAvatar(name = cliente.name, size = 52.dp, shape = RoundedCornerShape(8.dp))
            Spacer(Modifier.width(Spacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = cliente.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(Modifier.width(Spacing.sm))
                    WtcStatusBadge(status = cliente.status)
                }
                Text(
                    text = cliente.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Spacing.sm)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                ) {
                    cliente.tags?.take(3)?.forEach { tag ->
                        WtcChip(label = tag)
                    }
                }
            }
            Spacer(Modifier.width(Spacing.sm))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Score ${cliente.score ?: 0}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}
