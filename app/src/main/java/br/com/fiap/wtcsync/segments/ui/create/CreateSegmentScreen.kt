package br.com.fiap.wtcsync.segments.ui.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.fiap.wtcsync.theme.Spacing
import br.com.fiap.wtcsync.theme.WtcTheme
import br.com.fiap.wtcsync.ui.components.WtcChip
import br.com.fiap.wtcsync.ui.components.WtcScaffold
import br.com.fiap.wtcsync.ui.components.WtcTextField
import br.com.fiap.wtcsync.ui.components.WtcTopBar

@Composable
fun CreateSegmentScreen(
    viewModel: CreateSegmentViewModel,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    val extColors = WtcTheme.colors

    LaunchedEffect(state.success) {
        if (state.success) {
            viewModel.resetSuccess()
            onSuccess()
        }
    }

    WtcScaffold(
        topBar = {
            WtcTopBar(
                title = "Novo segmento",
                onBackClick = onBackClick,
                actions = {
                    if (state.isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(20.dp)
                                .padding(end = Spacing.sm),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        TextButton(
                            onClick = { viewModel.submit() },
                            enabled = !state.isSubmitting,
                        ) {
                            Text(
                                text = "Salvar",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.xl, vertical = Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.xl),
        ) {
            state.error?.let { errorMsg ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.errorContainer,
                ) {
                    Text(
                        text = errorMsg,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(Spacing.md),
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(
                    text = "Nome do segmento",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                WtcTextField(
                    value = state.name,
                    onValueChange = viewModel::onNameChanged,
                    placeholder = "Ex: Clientes VIP 2024",
                    errorMessage = state.nameError,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(
                    text = "Descrição",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                WtcTextField(
                    value = state.description,
                    onValueChange = viewModel::onDescriptionChanged,
                    placeholder = "Descreva o propósito deste segmento...",
                    singleLine = false,
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp,
            ) {
                Column(
                    modifier = Modifier.padding(Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.lg),
                ) {
                    Text(
                        text = "Filtros de seleção",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                        Text(
                            text = "Tags",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                            state.tags.forEach { tag ->
                                WtcChip(
                                    label = tag,
                                    selected = true,
                                    onRemove = { viewModel.onRemoveTag(tag) },
                                )
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                        Text(
                            text = "Status",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                            listOf("Todos", "Active", "Inactive").forEach { option ->
                                WtcChip(
                                    label = option,
                                    selected = state.status == option,
                                    onClick = { viewModel.onStatusChanged(option) },
                                )
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = "Score mínimo",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = "${state.minScore}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                        Slider(
                            value = state.minScore.toFloat(),
                            onValueChange = { viewModel.onScoreChanged(it.toInt()) },
                            valueRange = 0f..100f,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary,
                                inactiveTrackColor = MaterialTheme.colorScheme.outlineVariant,
                            ),
                        )
                    }
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp,
            ) {
                Row(
                    modifier = Modifier.padding(Spacing.lg),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            text = "Pré-visualização",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = "Clientes incluídos: ${state.clientCount}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    Text(
                        text = "Ver lista",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = extColors.infoContainer,
            ) {
                Text(
                    text = "Segmentos dinâmicos são atualizados automaticamente quando um novo cliente atende aos critérios acima.",
                    modifier = Modifier.padding(Spacing.lg),
                    style = MaterialTheme.typography.bodySmall,
                    color = extColors.onInfoContainer,
                )
            }

            Spacer(Modifier.height(Spacing.lg))
        }
    }
}
