package br.com.fiap.wtcsync.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

private val LightColorScheme = lightColorScheme(
    primary              = WtcPrimary,
    onPrimary            = WtcOnPrimary,
    primaryContainer     = WtcPrimaryContainer,
    onPrimaryContainer   = WtcOnPrimaryContainer,
    secondary            = WtcSecondary,
    onSecondary          = WtcOnSecondary,
    secondaryContainer   = WtcSecondaryContainer,
    onSecondaryContainer = WtcOnSecondaryContainer,
    tertiary             = WtcTertiary,
    onTertiary           = WtcOnTertiary,
    tertiaryContainer    = WtcTertiaryContainer,
    onTertiaryContainer  = WtcOnTertiaryContainer,
    background           = WtcBackground,
    onBackground         = WtcOnBackground,
    surface              = WtcSurface,
    onSurface            = WtcOnSurface,
    surfaceVariant       = WtcSurfaceVariant,
    onSurfaceVariant     = WtcOnSurfaceVariant,
    outline              = WtcOutline,
    outlineVariant       = WtcOutlineVariant,
    error                = WtcError,
    onError              = WtcOnError,
    errorContainer       = WtcErrorContainer,
    onErrorContainer     = WtcOnErrorContainer,
    scrim                = WtcScrim,
    inverseSurface       = WtcInverseSurface,
    inverseOnSurface     = WtcInverseOnSurface,
    inversePrimary       = WtcInversePrimary,
    surfaceTint          = WtcSurfaceTint,
)

private val DarkColorScheme = darkColorScheme(
    primary              = WtcPrimary,
    onPrimary            = WtcOnPrimary,
    primaryContainer     = Color(0xFF3D2C00),
    onPrimaryContainer   = WtcPrimaryContainer,
    secondary            = WtcDarkOnSurface,
    onSecondary          = WtcSecondary,
    secondaryContainer   = Color(0xFF2E2E2E),
    onSecondaryContainer = WtcDarkOnSurface,
    tertiary             = Color(0xFFFFCC02),
    onTertiary           = Color(0xFF3A2E00),
    tertiaryContainer    = Color(0xFF5C4000),
    onTertiaryContainer  = WtcPrimaryContainer,
    background           = WtcDarkBackground,
    onBackground         = WtcDarkOnSurface,
    surface              = WtcDarkSurface,
    onSurface            = WtcDarkOnSurface,
    surfaceVariant       = WtcDarkSurfaceVariant,
    onSurfaceVariant     = WtcDarkOnSurfaceVariant,
    outline              = WtcDarkOutline,
    outlineVariant       = WtcDarkOutlineVariant,
    error                = Color(0xFFEF9A9A),
    onError              = Color(0xFF7B0000),
    errorContainer       = Color(0xFFC62828),
    onErrorContainer     = Color(0xFFFFCDD2),
    scrim                = WtcScrim,
    inverseSurface       = WtcSurface,
    inverseOnSurface     = WtcOnSurface,
    inversePrimary       = WtcPrimary,
    surfaceTint          = WtcPrimary,
)

// Accessor para as cores estendidas (success/info/warning) fora do M3.
// Uso: WtcTheme.colors.successContainer
object WtcTheme {
    val colors: WtcExtendedColors
        @Composable
        get() = LocalWtcExtendedColors.current
}

@Composable
fun WTCSYNCTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme    = if (darkTheme) DarkColorScheme else LightColorScheme
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors
    val reduceMotion   = rememberReduceMotion()

    // Configura ícones da status bar para combinar com o fundo claro/escuro.
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowInsetsControllerCompat(window, view).apply {
                isAppearanceLightStatusBars     = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalWtcExtendedColors provides extendedColors,
        LocalReduceMotion      provides reduceMotion,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = Typography,
            shapes      = WtcShapes,
            content     = content,
        )
    }
}
