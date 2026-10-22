package br.com.fiap.wtcsync.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ── Semantic brand tokens (use these everywhere) ─────────────────────────────

val WtcPrimary            = Color(0xFFFFBF03) // Amarelo da marca
val WtcOnPrimary          = Color(0xFF1A1A1A) // Texto/ícone sobre amarelo — sempre preto
val WtcPrimaryContainer   = Color(0xFFFFF3CC) // Fundo suave de seleção, chip ativo, nav indicator
val WtcOnPrimaryContainer = Color(0xFF5C4300) // Texto sobre container amarelo

val WtcSecondary            = Color(0xFF1A1A1A) // Botão secundário sólido, ícones fortes
val WtcOnSecondary          = Color(0xFFFFFFFF)
val WtcSecondaryContainer   = Color(0xFF2E2E2E)
val WtcOnSecondaryContainer = Color(0xFFFFFFFF)

val WtcTertiary            = Color(0xFF745B00)
val WtcOnTertiary          = Color(0xFFFFFFFF)
val WtcTertiaryContainer   = Color(0xFFFFF3CC)
val WtcOnTertiaryContainer = Color(0xFF3A2E00)

val WtcBackground   = Color(0xFFFAF8F3) // Fundo de todas as telas
val WtcOnBackground = Color(0xFF1A1A1A)

val WtcSurface          = Color(0xFFFFFFFF) // Cards, campos, sheets, top bar
val WtcOnSurface        = Color(0xFF1A1A1A) // Texto principal
val WtcSurfaceVariant   = Color(0xFFF3F0E8) // Fundo de inputs, bolhas recebidas, skeleton
val WtcOnSurfaceVariant = Color(0xFF6B6558) // Texto secundário, placeholders, ícones inativos
val WtcSurfaceContainer = Color(0xFFF3F0E8)
val WtcSurfaceTint      = Color(0xFFFFBF03)

val WtcOutline        = Color(0xFFE6E1D6) // Bordas de 1dp e divisores sutis
val WtcOutlineVariant = Color(0xFFEFEBE3) // Divisores internos

val WtcError            = Color(0xFFC62828)
val WtcOnError          = Color(0xFFFFFFFF)
val WtcErrorContainer   = Color(0xFFFDECEA)
val WtcOnErrorContainer = Color(0xFF7B0000)

val WtcScrim            = Color(0xFF000000)
val WtcInverseSurface   = Color(0xFF1C1B18)
val WtcInverseOnSurface = Color(0xFFEDEBE6)
val WtcInversePrimary   = Color(0xFFFFBF03)

// ── Dark theme tokens (usados apenas em Theme.kt) ─────────────────────────────

val WtcDarkBackground       = Color(0xFF121210)
val WtcDarkSurface          = Color(0xFF1C1B18)
val WtcDarkSurfaceVariant   = Color(0xFF2A2820)
val WtcDarkOnSurface        = Color(0xFFEDEBE6)
val WtcDarkOnSurfaceVariant = Color(0xFF9B9283)
val WtcDarkOutline          = Color(0xFF4A4740)
val WtcDarkOutlineVariant   = Color(0xFF3A3830)

// ── Extended colors: success / info / warning (não existem no M3 padrão) ─────

@Immutable
data class WtcExtendedColors(
    val success: Color,
    val onSuccess: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,
    val info: Color,
    val onInfo: Color,
    val infoContainer: Color,
    val onInfoContainer: Color,
    val warning: Color,
    val onWarning: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
)

val LightExtendedColors = WtcExtendedColors(
    success            = Color(0xFF2E7D32),
    onSuccess          = Color(0xFFFFFFFF),
    successContainer   = Color(0xFFE8F5E9),
    onSuccessContainer = Color(0xFF1B5E20),
    info               = Color(0xFF1565C0),
    onInfo             = Color(0xFFFFFFFF),
    infoContainer      = Color(0xFFE8F1FD),
    onInfoContainer    = Color(0xFF0D47A1),
    warning            = Color(0xFF8A6100),
    onWarning          = Color(0xFFFFFFFF),
    warningContainer   = Color(0xFFFFF6DB),
    onWarningContainer = Color(0xFF5C4000),
)

val DarkExtendedColors = WtcExtendedColors(
    success            = Color(0xFF66BB6A),
    onSuccess          = Color(0xFF003909),
    successContainer   = Color(0xFF1B5E20),
    onSuccessContainer = Color(0xFFC8E6C9),
    info               = Color(0xFF64B5F6),
    onInfo             = Color(0xFF003366),
    infoContainer      = Color(0xFF0D47A1),
    onInfoContainer    = Color(0xFFBBDEFB),
    warning            = Color(0xFFFFCC02),
    onWarning          = Color(0xFF3A2E00),
    warningContainer   = Color(0xFF5C4000),
    onWarningContainer = Color(0xFFFFF3CC),
)

val LocalWtcExtendedColors = staticCompositionLocalOf { LightExtendedColors }
