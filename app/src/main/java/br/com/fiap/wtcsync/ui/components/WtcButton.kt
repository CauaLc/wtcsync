package br.com.fiap.wtcsync.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.fiap.wtcsync.theme.WTCSYNCTheme
import br.com.fiap.wtcsync.theme.WtcMotion

enum class WtcButtonVariant { Primary, Secondary, Outline, Text }

@Composable
fun WtcButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: WtcButtonVariant = WtcButtonVariant.Primary,
    isLoading: Boolean = false,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val baseModifier = modifier
        .height(52.dp)
        .pressScale(interactionSource)
        .alpha(if (enabled) 1f else 0.38f)

    when (variant) {
        WtcButtonVariant.Primary -> Button(
            onClick = onClick,
            enabled = enabled && !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.primary,
                disabledContentColor = MaterialTheme.colorScheme.onPrimary,
            ),
            shape = MaterialTheme.shapes.medium,
            interactionSource = interactionSource,
            modifier = baseModifier,
        ) { ButtonContent(text = text, isLoading = isLoading, contentColor = MaterialTheme.colorScheme.onPrimary) }

        WtcButtonVariant.Secondary -> Button(
            onClick = onClick,
            enabled = enabled && !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
                disabledContainerColor = MaterialTheme.colorScheme.secondary,
                disabledContentColor = MaterialTheme.colorScheme.onSecondary,
            ),
            shape = MaterialTheme.shapes.medium,
            interactionSource = interactionSource,
            modifier = baseModifier,
        ) { ButtonContent(text = text, isLoading = isLoading, contentColor = MaterialTheme.colorScheme.onSecondary) }

        WtcButtonVariant.Outline -> OutlinedButton(
            onClick = onClick,
            enabled = enabled && !isLoading,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
            shape = MaterialTheme.shapes.medium,
            interactionSource = interactionSource,
            modifier = baseModifier,
        ) { ButtonContent(text = text, isLoading = isLoading, contentColor = MaterialTheme.colorScheme.onSurface) }

        WtcButtonVariant.Text -> TextButton(
            onClick = onClick,
            enabled = enabled && !isLoading,
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.primary,
            ),
            interactionSource = interactionSource,
            modifier = modifier,
        ) { ButtonContent(text = text, isLoading = isLoading, contentColor = MaterialTheme.colorScheme.primary) }
    }
}

@Composable
private fun ButtonContent(text: String, isLoading: Boolean, contentColor: Color) {
    Crossfade(
        targetState = isLoading,
        animationSpec = tween(WtcMotion.SHORT),
        label = "buttonLoading",
    ) { loading ->
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = contentColor,
                strokeWidth = 2.dp,
            )
        } else {
            Text(text = text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WtcButtonPreview() {
    WTCSYNCTheme {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
        ) {
            WtcButton("Conectar", onClick = {}, modifier = Modifier.fillMaxWidth())
            WtcButton("Cancelar", onClick = {}, variant = WtcButtonVariant.Secondary, modifier = Modifier.fillMaxWidth())
            WtcButton("Esqueci a senha", onClick = {}, variant = WtcButtonVariant.Text)
        }
    }
}
