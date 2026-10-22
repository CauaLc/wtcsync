package br.com.fiap.wtcsync.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import br.com.fiap.wtcsync.theme.WTCSYNCTheme

@Composable
fun WtcTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    errorMessage: String? = null,
    supportingText: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    enabled: Boolean = true,
    readOnly: Boolean = false,
) {
    val isError = errorMessage != null
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label?.let { { Text(it) } },
        placeholder = placeholder?.let { { Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant) } },
        isError = isError,
        supportingText = {
            // Mensagem de erro entra com animação; texto de suporte normal é estático.
            AnimatedVisibility(
                visible = isError,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            if (!isError && supportingText != null) {
                Text(
                    text = supportingText,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        enabled = enabled,
        readOnly = readOnly,
        shape = MaterialTheme.shapes.small,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            errorBorderColor = MaterialTheme.colorScheme.error,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            errorContainerColor = MaterialTheme.colorScheme.surface,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
    )
}

@Preview(showBackground = true)
@Composable
private fun WtcTextFieldPreview() {
    WTCSYNCTheme {
        androidx.compose.foundation.layout.Column(
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(
                br.com.fiap.wtcsync.theme.Spacing.sm
            )
        ) {
            WtcTextField(value = "usuario@email.com", onValueChange = {}, label = "Email")
            WtcTextField(value = "", onValueChange = {}, label = "Senha", errorMessage = "Senha inválida")
        }
    }
}
