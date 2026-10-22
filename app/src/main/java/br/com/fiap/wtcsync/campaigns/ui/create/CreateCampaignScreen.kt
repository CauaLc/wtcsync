package br.com.fiap.wtcsync.campaigns.ui.create

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.com.fiap.wtcsync.theme.Spacing
import br.com.fiap.wtcsync.ui.components.WtcButton
import br.com.fiap.wtcsync.ui.components.WtcScaffold
import br.com.fiap.wtcsync.ui.components.WtcTextField
import br.com.fiap.wtcsync.ui.components.WtcTopBar

@Composable
fun CreateCampaignScreen(
    viewModel: CreateCampaignViewModel,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.success) {
        if (state.success) {
            viewModel.resetSuccess()
            onSuccess()
        }
    }

    WtcScaffold(
        topBar = {
            WtcTopBar(
                title = "Nova campanha",
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
                                text = "Enviar",
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

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.lg)) {
                WtcTextField(
                    value = state.title,
                    onValueChange = viewModel::onTitleChanged,
                    label = "Título da campanha",
                    placeholder = "Ex: Promoção de Verão",
                    errorMessage = state.titleError,
                    modifier = Modifier.fillMaxWidth(),
                )
                WtcTextField(
                    value = state.body,
                    onValueChange = viewModel::onBodyChanged,
                    label = "Mensagem",
                    placeholder = "Digite o conteúdo da sua notificação...",
                    errorMessage = state.bodyError,
                    singleLine = false,
                    minLines = 3,
                    maxLines = 6,
                    modifier = Modifier.fillMaxWidth(),
                )
                WtcTextField(
                    value = state.segmentId,
                    onValueChange = viewModel::onSegmentIdChanged,
                    label = "Segmento ID",
                    placeholder = "Ex: seg-123",
                    errorMessage = state.segmentError,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(
                    text = "Banner da campanha (URL)",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                WtcTextField(
                    value = state.mediaUrl,
                    onValueChange = viewModel::onMediaUrlChanged,
                    placeholder = "https://cdn.exemplo.com/banner.png",
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(
                    text = "Deeplink / URL de destino",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                WtcTextField(
                    value = state.deeplink,
                    onValueChange = viewModel::onDeeplinkChanged,
                    placeholder = "https://app.wtcsync.com/promo",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            ActionButtonsSection(
                actions = state.actions,
                onActionChanged = viewModel::onActionChanged,
                onActionTitleChanged = viewModel::onActionTitleChanged,
                onActionUrlChanged = viewModel::onActionUrlChanged,
                onAddAction = viewModel::addAction,
                onRemoveAction = viewModel::removeAction,
            )

            Spacer(Modifier.height(Spacing.sm))

            WtcButton(
                text = "Criar campanha",
                onClick = { viewModel.submit() },
                isLoading = state.isSubmitting,
                enabled = !state.isSubmitting,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(Spacing.lg))
        }
    }
}

@Composable
private fun ActionButtonsSection(
    actions: List<ActionField>,
    onActionChanged: (Int, String) -> Unit,
    onActionTitleChanged: (Int, String) -> Unit,
    onActionUrlChanged: (Int, String) -> Unit,
    onAddAction: () -> Unit,
    onRemoveAction: (Int) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(
            modifier = Modifier.padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Botões de ação",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                TextButton(onClick = onAddAction) {
                    Text(
                        text = "+ Adicionar",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            actions.forEachIndexed { index, action ->
                ActionFieldItem(
                    index = index,
                    action = action,
                    onActionChanged = { onActionChanged(index, it) },
                    onTitleChanged = { onActionTitleChanged(index, it) },
                    onUrlChanged = { onActionUrlChanged(index, it) },
                    onRemove = { onRemoveAction(index) },
                    canRemove = actions.size > 1,
                )
            }
        }
    }
}

@Composable
private fun ActionFieldItem(
    index: Int,
    action: ActionField,
    onActionChanged: (String) -> Unit,
    onTitleChanged: (String) -> Unit,
    onUrlChanged: (String) -> Unit,
    onRemove: () -> Unit,
    canRemove: Boolean,
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "Botão ${index + 1}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (canRemove) {
                IconButton(onClick = onRemove, modifier = Modifier.size(20.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remover",
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
        WtcTextField(
            value = action.action,
            onValueChange = onActionChanged,
            label = "ID da ação",
            placeholder = "Ex: btn1",
            modifier = Modifier.fillMaxWidth(),
        )
        WtcTextField(
            value = action.title,
            onValueChange = onTitleChanged,
            label = "Texto do botão",
            placeholder = "Comprar agora",
            modifier = Modifier.fillMaxWidth(),
        )
        WtcTextField(
            value = action.url,
            onValueChange = onUrlChanged,
            label = "URL de destino",
            placeholder = "https://exemplo.com/acao",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
