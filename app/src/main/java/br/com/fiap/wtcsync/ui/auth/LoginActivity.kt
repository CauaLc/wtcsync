package br.com.fiap.wtcsync.ui.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import br.com.fiap.wtcsync.R
import br.com.fiap.wtcsync.theme.Spacing
import br.com.fiap.wtcsync.ui.components.WtcButton
import br.com.fiap.wtcsync.ui.components.WtcScaffold
import br.com.fiap.wtcsync.ui.components.WtcTextField
import br.com.fiap.wtcsync.ui.components.WtcTopBar
import br.com.fiap.wtcsync.util.NetworkUtils
import br.com.fiap.wtcsync.util.Resource

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: (String) -> Unit = {},
    onBackClick: () -> Unit,
) {
    val context = LocalContext.current
    val userState by viewModel.userState.collectAsState()
    val isLoading = userState is Resource.Loading

    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    LaunchedEffect(userState) {
        when (val state = userState) {
            is Resource.Success -> {
                viewModel.resetState()
                onLoginSuccess(state.data?.email ?: "")
            }
            is Resource.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    WtcScaffold(
        topBar = { WtcTopBar(title = "Login", onBackClick = onBackClick) },
    ) { padding ->
        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = Spacing.xl, vertical = Spacing.lg),
        ) {
            Image(
                painter = painterResource(id = R.drawable.wtcsync_logo),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .padding(bottom = Spacing.md),
            )

            Spacer(Modifier.height(Spacing.lg))

            Text(
                text = "Email",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(bottom = Spacing.xs),
            )
            WtcTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = "JoeDoe@WTC.com",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(Spacing.lg))

            Text(
                text = "Senha",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(bottom = Spacing.xs),
            )
            WtcTextField(
                value = senha,
                onValueChange = { senha = it },
                placeholder = "••••••••••",
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(Spacing.sm))

            WtcButton(
                text = "Esqueceu a senha?",
                onClick = {},
                variant = br.com.fiap.wtcsync.ui.components.WtcButtonVariant.Text,
            )

            Spacer(Modifier.height(Spacing.xl))

            WtcButton(
                text = "Conectar",
                onClick = {
                    if (NetworkUtils.isConnected(context)) {
                        viewModel.login(email, senha)
                    } else {
                        Toast.makeText(context, "Sem conexão com a internet", Toast.LENGTH_SHORT).show()
                    }
                },
                isLoading = isLoading,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
