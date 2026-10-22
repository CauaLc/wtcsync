package br.com.fiap.wtcsync.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.fiap.wtcsync.theme.WTCSYNCTheme
import br.com.fiap.wtcsync.theme.WtcMotion

// Usa o enum BottomNavTab definido em BottomNavBar.kt para não duplicar a declaração.
// Em Fase 3 o enum será movido aqui e BottomNavBar.kt será removido.

@Composable
fun WtcBottomNavBar(
    tabs: List<BottomNavTab> = BottomNavTab.entries,
    selectedTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Column {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline,
                thickness = 1.dp,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                tabs.forEach { tab ->
                    WtcNavItem(
                        tab = tab,
                        isSelected = tab == selectedTab,
                        onClick = { onTabSelected(tab) },
                    )
                }
            }
        }
    }
}

@Composable
private fun WtcNavItem(
    tab: BottomNavTab,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val indicatorColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                      else MaterialTheme.colorScheme.surface,
        animationSpec = tween(WtcMotion.SHORT),
        label = "navIndicatorColor",
    )
    val indicatorWidth by animateDpAsState(
        targetValue = if (isSelected) 64.dp else 0.dp,
        animationSpec = tween(WtcMotion.SHORT),
        label = "navIndicatorWidth",
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                      else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(WtcMotion.SHORT),
        label = "navContentColor",
    )

    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(contentAlignment = Alignment.Center) {
                // Indicador pill animado atrás do ícone
                Surface(
                    modifier = Modifier
                        .width(indicatorWidth)
                        .height(32.dp),
                    shape = MaterialTheme.shapes.medium,
                    color = indicatorColor,
                ) {}
                // Ícone (filled quando ativo, same icon com opacidade reduzida quando inativo)
                Icon(
                    imageVector = tab.icon,
                    contentDescription = tab.label,
                    tint = contentColor,
                )
            }
            Text(
                text = tab.label,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WtcBottomNavBarPreview() {
    WTCSYNCTheme {
        WtcBottomNavBar(
            selectedTab = BottomNavTab.CHAT,
            onTabSelected = {},
        )
    }
}
