package br.com.fiap.wtcsync.segments.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.fiap.wtcsync.segments.data.dto.SegmentDto
import br.com.fiap.wtcsync.theme.Spacing
import br.com.fiap.wtcsync.ui.components.WtcButton
import br.com.fiap.wtcsync.ui.components.WtcButtonVariant
import br.com.fiap.wtcsync.ui.components.WtcChip
import br.com.fiap.wtcsync.ui.components.WtcEmptyState
import br.com.fiap.wtcsync.ui.components.WtcErrorState
import br.com.fiap.wtcsync.ui.components.WtcLoading
import br.com.fiap.wtcsync.ui.components.WtcScaffold
import br.com.fiap.wtcsync.ui.components.WtcTextField
import br.com.fiap.wtcsync.ui.components.WtcTopBar

@Composable
fun SegmentListScreen(
    segments: List<SegmentDto> = emptyList(),
    isLoading: Boolean = false,
    error: String? = null,
    showCreateButton: Boolean = true,
    onCreateClick: () -> Unit = {},
    onSegmentClick: (String) -> Unit = {},
    onRetry: () -> Unit = {},
) {
    var searchQuery by remember { mutableStateOf("") }
    val filtered = segments.filter { it.name.contains(searchQuery, ignoreCase = true) }

    WtcScaffold(
        topBar = {
            WtcTopBar(
                title = "Segmentos",
                actions = {
                    if (showCreateButton) {
                        IconButton(onClick = onCreateClick) {
                            Icon(Icons.Default.Add, contentDescription = "Novo segmento")
                        }
                    }
                },
            )
        },
    ) { padding ->
        when {
            isLoading -> WtcLoading(modifier = Modifier.padding(padding))
            error != null -> WtcErrorState(
                message = error,
                modifier = Modifier.padding(padding),
                onRetry = onRetry,
            )
            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = Spacing.lg, vertical = Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                item {
                    WtcTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = "Buscar segmentos...",
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                if (filtered.isEmpty()) {
                    item {
                        WtcEmptyState(
                            title = "Nenhum segmento",
                            description = "Organize sua base de contatos por interesses ou localização.",
                            action = if (showCreateButton) {
                                { WtcButton("Novo segmento", onClick = onCreateClick, modifier = Modifier.fillMaxWidth()) }
                            } else null,
                        )
                    }
                } else {
                    items(filtered, key = { it.id }) { segment ->
                        SegmentCard(segment = segment, onClick = { onSegmentClick(segment.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun SegmentCard(segment: SegmentDto, onClick: () -> Unit) {
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
                verticalAlignment = Alignment.Top,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                ) {
                    Text(
                        text = segment.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "${segment.clientCount} clientes",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    segment.tags.take(2).forEach { tag ->
                        WtcChip(label = tag, selected = true)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                WtcButton(
                    text = "Ver",
                    onClick = onClick,
                    variant = WtcButtonVariant.Outline,
                    modifier = Modifier.weight(1f),
                )
                IconButton(
                    onClick = onClick,
                    modifier = Modifier.size(52.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Enviar campanha",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}
