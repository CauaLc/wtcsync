package br.com.fiap.wtcsync.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.fiap.wtcsync.theme.WTCSYNCTheme
import br.com.fiap.wtcsync.theme.WtcTheme
import androidx.compose.ui.Modifier

@Composable
fun WtcStatusBadge(status: String?) {
    val extColors = WtcTheme.colors
    val (bg, fg, label) = resolveBadge(status, extColors)

    Surface(
        shape = MaterialTheme.shapes.small,
        color = bg,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(fg, CircleShape),
            )
            Spacer(Modifier.width(5.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = fg,
            )
        }
    }
}

private data class BadgeSpec(val bg: Color, val fg: Color, val label: String)

private fun resolveBadge(status: String?, ext: br.com.fiap.wtcsync.theme.WtcExtendedColors): BadgeSpec =
    when (status?.lowercase()) {
        // Status de cliente
        "active", "ativo"         -> BadgeSpec(ext.successContainer, ext.onSuccessContainer, "Ativo")
        "prospect"                -> BadgeSpec(ext.infoContainer, ext.onInfoContainer, "Prospect")
        "inactive", "inativo"     -> BadgeSpec(ext.warningContainer, ext.onWarningContainer, "Inativo")
        // Status de campanha
        "sent", "enviada"         -> BadgeSpec(ext.successContainer, ext.onSuccessContainer, "Enviada")
        "scheduled", "agendada"   -> BadgeSpec(ext.infoContainer, ext.onInfoContainer, "Agendada")
        "draft", "rascunho"       -> BadgeSpec(ext.warningContainer, ext.onWarningContainer, "Rascunho")
        else                      -> BadgeSpec(ext.warningContainer, ext.onWarningContainer, status ?: "—")
    }

@Preview(showBackground = true)
@Composable
private fun WtcStatusBadgePreview() {
    WTCSYNCTheme {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
            WtcStatusBadge("active")
            WtcStatusBadge("prospect")
            WtcStatusBadge("inactive")
            WtcStatusBadge("sent")
            WtcStatusBadge("scheduled")
            WtcStatusBadge("draft")
        }
    }
}
