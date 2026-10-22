package br.com.fiap.wtcsync.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.fiap.wtcsync.theme.Spacing
import br.com.fiap.wtcsync.theme.WTCSYNCTheme

@Composable
fun WtcCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val baseModifier = modifier.pressScale(interactionSource)

    if (onClick != null) {
        Surface(
            onClick = onClick,
            modifier = baseModifier,
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
            interactionSource = interactionSource,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.lg),
                content = content,
            )
        }
    } else {
        Surface(
            modifier = modifier,
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.lg),
                content = content,
            )
        }
    }
}

@Composable
fun WtcListItem(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val baseModifier = modifier
        .fillMaxWidth()
        .pressScale(interactionSource)

    val rowContent: @Composable () -> Unit = {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (leading != null) {
                leading()
                Spacer(Modifier.width(Spacing.md))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            if (trailing != null) {
                Spacer(Modifier.width(Spacing.sm))
                trailing()
            }
        }
    }

    if (onClick != null) {
        Surface(
            onClick = onClick,
            modifier = baseModifier,
            color = MaterialTheme.colorScheme.surface,
            interactionSource = interactionSource,
            content = rowContent,
        )
    } else {
        Surface(
            modifier = modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            content = rowContent,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WtcCardPreview() {
    WTCSYNCTheme {
        Column(
            modifier = Modifier.padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            WtcCard(onClick = {}) {
                Text("Título do card", style = MaterialTheme.typography.titleMedium)
                Text("Subtítulo do card", style = MaterialTheme.typography.bodyMedium)
            }
            WtcListItem(
                title = "João Silva",
                subtitle = "joao@email.com",
                trailing = { Text("Score 85") },
                onClick = {}
            )
        }
    }
}
