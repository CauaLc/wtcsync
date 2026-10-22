package br.com.fiap.wtcsync.ui.crm

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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.wtcsync.WtcSyncApp
import br.com.fiap.wtcsync.data.remote.dto.ClientDto
import br.com.fiap.wtcsync.theme.Spacing
import br.com.fiap.wtcsync.ui.components.WtcAvatar
import br.com.fiap.wtcsync.ui.components.WtcButton
import br.com.fiap.wtcsync.ui.components.WtcButtonVariant
import br.com.fiap.wtcsync.ui.components.WtcChip
import br.com.fiap.wtcsync.ui.components.WtcScaffold
import br.com.fiap.wtcsync.ui.components.WtcTextField
import br.com.fiap.wtcsync.ui.components.WtcTopBar
import br.com.fiap.wtcsync.viewmodel.ChatListViewModel
import br.com.fiap.wtcsync.viewmodel.ChatListViewModelFactory

@Composable
fun ChatListScreen(
    onChatClick: (clienteId: String, contactName: String) -> Unit = { _, _ -> },
    onCreateClick: () -> Unit = {},
) {
    val app = LocalContext.current.applicationContext as WtcSyncApp
    val viewModel: ChatListViewModel = viewModel(
        factory = ChatListViewModelFactory(
            clientApi = app.apiService.clientApi,
            messageApi = app.apiService.messageApi,
            sessionManager = app.sessionManager,
            context = app,
        )
    )
    val uiState by viewModel.uiState.collectAsState()
    var isSearching by remember { mutableStateOf(false) }
    var selectedPill by remember { mutableStateOf("Todas") }

    val displayedClients = when (selectedPill) {
        "Não lidas" -> uiState.filteredClients.filter { (uiState.unreadCounts[it.id] ?: 0) > 0 }
        else        -> uiState.filteredClients
    }

    WtcScaffold(
        topBar = {
            WtcTopBar(
                title = "Mensagens",
                actions = {
                    IconButton(onClick = { isSearching = !isSearching }) {
                        Icon(Icons.Default.Search, contentDescription = "Buscar")
                    }
                    IconButton(onClick = onCreateClick) {
                        Icon(Icons.Default.Add, contentDescription = "Nova conversa")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            if (isSearching) {
                WtcTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    placeholder = "Buscar contato...",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
                )
            }

            Row(
                modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.md),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                listOf("Todas", "Não lidas").forEach { pill ->
                    WtcChip(
                        label = pill,
                        selected = selectedPill == pill,
                        onClick = { selectedPill = pill },
                    )
                }
            }

            when {
                uiState.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
                uiState.error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Erro: ${uiState.error}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                displayedClients.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhuma conversa encontrada", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                else -> LazyColumn(modifier = Modifier.weight(1f)) {
                    items(displayedClients, key = { it.id }) { cliente ->
                        ChatConversationItem(
                            cliente = cliente,
                            lastMessage = uiState.lastMessages[cliente.id] ?: "",
                            unreadCount = uiState.unreadCounts[cliente.id] ?: 0,
                            onClick = { onChatClick(cliente.id, cliente.name) },
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                    item { PerformanceCard() }
                }
            }
        }
    }
}

@Composable
private fun ChatConversationItem(
    cliente: ClientDto,
    lastMessage: String,
    unreadCount: Int,
    onClick: () -> Unit,
) {
    val hasUnread = unreadCount > 0
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(Spacing.lg),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WtcAvatar(name = cliente.name, size = 44.dp, showOnlineIndicator = true)
        Spacer(Modifier.width(Spacing.md))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = cliente.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = lastMessage.ifBlank { cliente.email },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (hasUnread) {
            Spacer(Modifier.width(Spacing.sm))
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(MaterialTheme.colorScheme.primary, androidx.compose.foundation.shape.CircleShape),
            )
        }
    }
}

@Composable
private fun PerformanceCard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.lg),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Column(modifier = Modifier.padding(horizontal = Spacing.xl, vertical = Spacing.lg)) {
            Text(
                text = "Desempenho da semana",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Spacer(Modifier.size(Spacing.sm))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "24",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Spacer(Modifier.width(Spacing.sm))
                Text(
                    text = "Novas conversas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}
