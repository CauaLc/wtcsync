package br.com.fiap.wtcsync.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.fiap.wtcsync.theme.Spacing
import br.com.fiap.wtcsync.theme.WTCSYNCTheme
import br.com.fiap.wtcsync.theme.WtcMotion

// ── Estados de tela ──────────────────────────────────────────────────────────

sealed interface ScreenState<out T> {
    data object Loading : ScreenState<Nothing>
    data class Error(val message: String) : ScreenState<Nothing>
    data class Empty(val message: String? = null) : ScreenState<Nothing>
    data class Content<T>(val data: T) : ScreenState<T>
}

@Composable
fun <T> WtcScreenState(
    state: ScreenState<T>,
    onRetry: () -> Unit = {},
    emptyIcon: ImageVector = Icons.Outlined.Inbox,
    emptyTitle: String = "Nenhum item encontrado",
    emptyDescription: String = "",
    loadingContent: @Composable () -> Unit = { WtcLoading() },
    content: @Composable (T) -> Unit,
) {
    AnimatedContent(
        targetState = state,
        transitionSpec = {
            fadeIn(tween(WtcMotion.MEDIUM)) togetherWith fadeOut(tween(WtcMotion.SHORT))
        },
        label = "screenState",
    ) { s ->
        when (s) {
            is ScreenState.Loading -> loadingContent()
            is ScreenState.Error   -> WtcErrorState(message = s.message, onRetry = onRetry)
            is ScreenState.Empty   -> WtcEmptyState(
                icon = emptyIcon,
                title = emptyTitle,
                description = s.message ?: emptyDescription,
            )
            is ScreenState.Content -> content(s.data)
        }
    }
}

@Composable
fun WtcEmptyState(
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Outlined.Inbox,
    title: String = "Nenhum item",
    description: String = "",
    action: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(Spacing.lg))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        if (description.isNotBlank()) {
            Spacer(Modifier.height(Spacing.sm))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
        if (action != null) {
            Spacer(Modifier.height(Spacing.xl))
            action()
        }
    }
}

@Composable
fun WtcErrorState(
    message: String,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Outlined.ErrorOutline,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.error,
        )
        Spacer(Modifier.height(Spacing.lg))
        Text(
            text = "Algo deu errado",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(Spacing.sm))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(Spacing.xl))
        WtcButton(text = "Tentar novamente", onClick = onRetry)
    }
}

// Skeleton shimmer para listas — replica o formato de WtcListItem.
@Composable
fun WtcLoading(
    itemCount: Int = 5,
    modifier: Modifier = Modifier,
) {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant,
        MaterialTheme.colorScheme.surface,
        MaterialTheme.colorScheme.surfaceVariant,
    )
    val transition = rememberInfiniteTransition(label = "shimmer")
    val offset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(1200),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmerOffset",
    )
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(offset - 200f, 0f),
        end = Offset(offset, 0f),
    )

    Column(modifier = modifier.fillMaxSize().padding(horizontal = Spacing.lg, vertical = Spacing.lg)) {
        repeat(itemCount) {
            SkeletonListItem(brush = brush)
            Spacer(Modifier.height(Spacing.md))
        }
    }
}

@Composable
private fun SkeletonListItem(brush: Brush) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(brush, RoundedCornerShape(50)),
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(modifier = Modifier.fillMaxWidth(0.6f).height(14.dp).background(brush, RoundedCornerShape(4.dp)))
            Box(modifier = Modifier.fillMaxWidth(0.4f).height(11.dp).background(brush, RoundedCornerShape(4.dp)))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WtcLoadingPreview() {
    WTCSYNCTheme { WtcLoading(itemCount = 4) }
}

@Preview(showBackground = true)
@Composable
private fun WtcEmptyStatePreview() {
    WTCSYNCTheme {
        WtcEmptyState(
            title = "Nenhuma campanha",
            description = "Crie sua primeira campanha.",
            action = { WtcButton("Nova campanha", onClick = {}) },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WtcErrorStatePreview() {
    WTCSYNCTheme { WtcErrorState("Falha ao carregar dados do servidor.") }
}
