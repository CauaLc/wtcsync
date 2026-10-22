package br.com.fiap.wtcsync.ui.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import br.com.fiap.wtcsync.R
import br.com.fiap.wtcsync.data.model.enums.UserRole
import br.com.fiap.wtcsync.theme.Spacing
import br.com.fiap.wtcsync.ui.components.WtcButton
import br.com.fiap.wtcsync.ui.components.WtcScaffold
import br.com.fiap.wtcsync.ui.components.WtcTextField
import br.com.fiap.wtcsync.ui.components.WtcTopBar
import br.com.fiap.wtcsync.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: (String) -> Unit = {},
    onBackClick: () -> Unit,
) {
    val context = LocalContext.current
    val userState by viewModel.userState.collectAsState()
    val isLoading = userState is Resource.Loading
    val scrollState = rememberScrollState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var role by remember { mutableStateOf(UserRole.CLIENTE) }
    var expanded by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(userState) {
        when (val state = userState) {
            is Resource.Success -> {
                viewModel.resetState()
                onRegisterSuccess(state.data?.email ?: "")
            }
            is Resource.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    WtcScaffold(
        topBar = { WtcTopBar(title = "Cadastro", onBackClick = onBackClick) },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(horizontal = Spacing.xl, vertical = Spacing.lg),
        ) {
            Image(
                painter = painterResource(id = R.drawable.wtcsync_logo),
                contentDescription = null,
                modifier = Modifier.size(60.dp),
            )

            Spacer(Modifier.height(Spacing.xl))

            Text(
                text = "Nome",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = Spacing.xs),
            )
            WtcTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = "João Silva",
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(Spacing.lg))

            Text(
                text = "Tipo de usuário",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = Spacing.xs),
            )
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { if (!isLoading) expanded = !expanded },
            ) {
                WtcTextField(
                    value = if (role == UserRole.CLIENTE) "Cliente" else "Atendente",
                    onValueChange = {},
                    readOnly = true,
                    enabled = !isLoading,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    DropdownMenuItem(
                        text = { Text("Cliente") },
                        onClick = { role = UserRole.CLIENTE; expanded = false },
                    )
                    DropdownMenuItem(
                        text = { Text("Atendente") },
                        onClick = { role = UserRole.ATENDENTE; expanded = false },
                    )
                }
            }

            Spacer(Modifier.height(Spacing.lg))

            Text(
                text = "Email",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = Spacing.xs),
            )
            WtcTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = "JoeDoe@WTC.com",
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(Spacing.lg))

            Text(
                text = "Senha",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = Spacing.xs),
            )
            WtcTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = "••••••••••",
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                enabled = !isLoading,
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Ocultar senha" else "Mostrar senha",
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(Spacing.lg))

            Text(
                text = "Confirmar senha",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = Spacing.xs),
            )
            WtcTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = "••••••••••",
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                enabled = !isLoading,
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (confirmPasswordVisible) "Ocultar senha" else "Mostrar senha",
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(Spacing.xxl))

            WtcButton(
                text = "Cadastrar",
                onClick = {
                    when {
                        name.isBlank() ->
                            Toast.makeText(context, "Por favor, insira seu nome", Toast.LENGTH_SHORT).show()
                        email.isBlank() ->
                            Toast.makeText(context, "Por favor, insira seu email", Toast.LENGTH_SHORT).show()
                        password.isBlank() ->
                            Toast.makeText(context, "Por favor, insira sua senha", Toast.LENGTH_SHORT).show()
                        password.length < 6 ->
                            Toast.makeText(context, "A senha deve ter pelo menos 6 caracteres", Toast.LENGTH_SHORT).show()
                        password != confirmPassword ->
                            Toast.makeText(context, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
                        else -> viewModel.register(email, password, name, role)
                    }
                },
                isLoading = isLoading,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(Spacing.xl))
        }
    }
}
