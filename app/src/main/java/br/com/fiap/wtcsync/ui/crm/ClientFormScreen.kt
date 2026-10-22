package br.com.fiap.wtcsync.ui.crm

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.wtcsync.WtcSyncApp
import br.com.fiap.wtcsync.theme.Spacing
import br.com.fiap.wtcsync.ui.components.WtcButton
import br.com.fiap.wtcsync.ui.components.WtcButtonVariant
import br.com.fiap.wtcsync.ui.components.WtcChip
import br.com.fiap.wtcsync.ui.components.WtcScaffold
import br.com.fiap.wtcsync.ui.components.WtcTextField
import br.com.fiap.wtcsync.ui.components.WtcTopBar
import br.com.fiap.wtcsync.viewmodel.ClientFormViewModel
import br.com.fiap.wtcsync.viewmodel.ClientFormViewModelFactory

@Composable
fun ClientFormScreen(
    clienteId: String?,
    onSave: () -> Unit,
    onCancel: () -> Unit,
) {
    val app = LocalContext.current.applicationContext as WtcSyncApp
    val viewModel: ClientFormViewModel = viewModel(
        key = clienteId ?: "new",
        factory = ClientFormViewModelFactory(app.apiService.clientApi, clienteId),
    )
    val uiState by viewModel.uiState.collectAsState()
    var showTagDialog by remember { mutableStateOf(false) }
    var tagInput by remember { mutableStateOf("") }

    LaunchedEffect(uiState.saved) {
        if (uiState.saved) onSave()
    }

    if (showTagDialog) {
        AlertDialog(
            onDismissRequest = { showTagDialog = false; tagInput = "" },
            title = {
                Text(
                    text = "Adicionar tag",
                    style = MaterialTheme.typography.titleMedium,
                )
            },
            text = {
                WtcTextField(
                    value = tagInput,
                    onValueChange = { tagInput = it },
                    label = "Tag",
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.addTag(tagInput)
                    tagInput = ""
                    showTagDialog = false
                }) {
                    Text(
                        text = "Adicionar",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showTagDialog = false; tagInput = "" }) {
                    Text(text = "Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
        )
    }

    WtcScaffold(
        topBar = {
            WtcTopBar(
                title = if (clienteId == null) "Novo cliente" else "Editar cliente",
                onBackClick = onCancel,
                actions = {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp).padding(end = Spacing.sm),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        TextButton(onClick = { viewModel.save() }) {
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
                .padding(padding),
        ) {
            if (uiState.error != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.errorContainer,
                ) {
                    Text(
                        text = uiState.error!!,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = Spacing.xl, vertical = Spacing.sm),
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Spacing.xl, vertical = Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.xl),
            ) {
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp,
                    shadowElevation = 1.dp,
                ) {
                    Column(
                        modifier = Modifier.padding(Spacing.xl),
                        verticalArrangement = Arrangement.spacedBy(Spacing.xl),
                    ) {
                        // Avatar placeholder
                        Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                            Surface(
                                modifier = Modifier.size(80.dp),
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        modifier = Modifier.size(40.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                            Surface(
                                modifier = Modifier
                                    .size(28.dp)
                                    .align(Alignment.BottomEnd),
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    )
                                }
                            }
                        }

                        FormField(label = "Nome completo") {
                            WtcTextField(
                                value = uiState.name,
                                onValueChange = { viewModel.onNameChange(it) },
                                placeholder = "Ex: João Silva",
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }

                        FormField(label = "Email") {
                            WtcTextField(
                                value = uiState.email,
                                onValueChange = { viewModel.onEmailChange(it) },
                                placeholder = "contato@empresa.com",
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }

                        FormField(label = "Telefone") {
                            WtcTextField(
                                value = uiState.phone,
                                onValueChange = { viewModel.onPhoneChange(it) },
                                placeholder = "+55 (11) 99999-9999",
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }

                        FormField(label = "Status") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                            ) {
                                listOf("Active", "Inactive", "Prospect").forEach { option ->
                                    val isSelected = uiState.status == option
                                    Surface(
                                        onClick = { viewModel.onStatusChange(option) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(44.dp),
                                        shape = MaterialTheme.shapes.small,
                                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                                else MaterialTheme.colorScheme.surfaceVariant,
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = option,
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                                                        else MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    text = "Lead score",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Text(
                                    text = "${uiState.score.toInt()}%",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                            Slider(
                                value = uiState.score,
                                onValueChange = { viewModel.onScoreChange(it) },
                                valueRange = 0f..100f,
                                colors = SliderDefaults.colors(
                                    thumbColor = MaterialTheme.colorScheme.primary,
                                    activeTrackColor = MaterialTheme.colorScheme.primary,
                                    inactiveTrackColor = MaterialTheme.colorScheme.outlineVariant,
                                ),
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text("Frio", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Quente", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        FormField(label = "Tags e segmentos") {
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                            ) {
                                uiState.tags.forEach { tag ->
                                    WtcChip(
                                        label = tag,
                                        selected = true,
                                        onRemove = { viewModel.removeTag(tag) },
                                    )
                                }
                                WtcChip(
                                    label = "+ Adicionar",
                                    onClick = { showTagDialog = true },
                                )
                            }
                        }

                        FormField(label = "Notas do cliente") {
                            WtcTextField(
                                value = uiState.notes,
                                onValueChange = { viewModel.onNotesChange(it) },
                                placeholder = "Escreva observações importantes aqui...",
                                singleLine = false,
                                minLines = 4,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FormField(label: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        content()
    }
}
