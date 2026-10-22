# WTCSync Design System

Referência canônica de tokens, componentes e regras de motion para o app Android WTCSync.

---

## 1. Cores

### Tokens M3 (via `MaterialTheme.colorScheme`)

| Token | Claro | Uso |
|---|---|---|
| `primary` | `#FFBF03` | Botão primário, item ativo da nav, FAB, foco de campo |
| `onPrimary` | `#1A1A1A` | Texto/ícone sobre amarelo — **sempre preto** |
| `primaryContainer` | `#FFF3CC` | Chip selecionado, indicador da nav, fundo de seleção |
| `onPrimaryContainer` | `#5C4300` | Texto sobre container amarelo |
| `secondary` | `#1A1A1A` | Botão secundário sólido, ícones fortes |
| `onSecondary` | `#FFFFFF` | |
| `background` | `#FAF8F3` | Fundo de todas as telas |
| `surface` | `#FFFFFF` | Cards, campos, sheets, top bar |
| `surfaceVariant` | `#F3F0E8` | Fundo de inputs, bolhas recebidas, skeleton |
| `onSurface` | `#1A1A1A` | Texto principal |
| `onSurfaceVariant` | `#6B6558` | Texto secundário, placeholders, ícones inativos |
| `outline` | `#E6E1D6` | Bordas de 1dp e divisores |
| `outlineVariant` | `#EFEBE3` | Divisores internos |
| `error` / `errorContainer` | `#C62828` / `#FDECEA` | Erros |

### Cores estendidas (via `WtcTheme.colors`)

Acesso: `WtcTheme.colors.successContainer`, `WtcTheme.colors.onInfoContainer`, etc.

| Semântica | Container | onContainer |
|---|---|---|
| **success** (enviado, ativo) | `#E8F5E9` | `#1B5E20` |
| **info** (agendado, prospect) | `#E8F1FD` | `#0D47A1` |
| **warning** (rascunho, pendente) | `#FFF6DB` | `#5C4000` |

**Regra:** Nenhuma cor `Color(0x...)` fora de `theme/`. Toda cor vem de `MaterialTheme.colorScheme` ou `WtcTheme.colors`.

---

## 2. Tipografia

Fontes: **Nunito** (títulos) + **Roboto** (corpo/labels). Declaradas em `theme/Type.kt`.

| Estilo | Fonte | Tamanho / altura | Peso |
|---|---|---|---|
| `headlineMedium` | Nunito | 28 / 34 | Bold |
| `headlineSmall` | Nunito | 24 / 30 | Bold |
| `titleLarge` | Nunito | 20 / 26 | SemiBold |
| `titleMedium` | Roboto | 16 / 22 | Medium |
| `bodyLarge` | Roboto | 16 / 24 | Normal |
| `bodyMedium` | Roboto | 14 / 20 | Normal |
| `bodySmall` | Roboto | 12 / 16 | Normal |
| `labelLarge` | Roboto | 14 / 20 | Medium (botões) |
| `labelMedium` | Roboto | 12 / 16 | Medium (chips, badges, nav) |
| `labelSmall` | Roboto | 11 / 14 | Medium |

**Regra:** Nenhuma tela usa `fontSize = XX.sp` solto. Sempre `MaterialTheme.typography.*`.

---

## 3. Formas (`MaterialTheme.shapes`)

Definidas em `theme/Shape.kt` via `WtcShapes`.

| Shape | Raio | Uso |
|---|---|---|
| `extraSmall` | 4dp | |
| `small` | **8dp** | Chips, badges, campos |
| `medium` | **12dp** | Botões, cards, bolhas de mensagem |
| `large` | **20dp** | Bottom sheets, dialogs |
| `CircleShape` | 50% | Avatares, FAB, botão de envio |

**Regra:** Nenhum `RoundedCornerShape(Xdp)` fora dos tokens acima.

---

## 4. Espaçamento (`Spacing`)

Definido em `theme/Spacing.kt`.

```
xs  = 4dp   sm  = 8dp   md  = 12dp
lg  = 16dp  xl  = 24dp  xxl = 32dp
```

- Padding horizontal de tela: `Spacing.lg` (16dp)
- Entre seções: `Spacing.xl` (24dp)
- Entre itens de lista: `Spacing.sm` a `Spacing.md` (8–12dp)
- Área tocável mínima: 48dp

---

## 5. Elevação

- Padrão: **0dp** com separação por cor de superfície + borda `outline` de 1dp.
- Sombra apenas em FAB e bottom sheet (no máximo 2–3dp).

---

## 6. Componentes (`ui/components/`)

Todos com prefixo `Wtc`, sem cores hardcoded, com `@Preview`.

| Componente | Substitui | Notas |
|---|---|---|
| `WtcScaffold` | `Scaffold` direto | `background`, `SnackbarHost`, `contentWindowInsets` corretos |
| `WtcTopBar` | Headers `Row + drawBehind` | Título `titleLarge`, ArrowBack automirrored, divisor ao rolar |
| `WtcButton` | `Button` manual | Variantes: `Primary`, `Secondary`, `Outline`, `Text`; estado `loading` com spinner |
| `WtcTextField` | `OutlinedTextField` manual | Borda animada no foco, suporte a erro com `AnimatedVisibility` |
| `WtcCard` | `Surface + clickable` | Borda 1dp, press-scale ao toque |
| `WtcListItem` | Rows manuais | Avatar, título/subtítulo, trailing |
| `WtcStatusBadge` | `StatusBadge` | Ponto 6dp, tokens success/info/warning; "Ativo/Prospect/Inativo" |
| `WtcChip` | `TagChip` | Selecionado = `primaryContainer`, padrão = `surfaceVariant` |
| `WtcAvatar` | `ClientAvatar` | Fundo `primaryContainer`, inicial Nunito Bold |
| `WtcBottomNavBar` | `BottomNavBar` | Indicador pill animado, sem borda 2dp, labels sentence case |
| `WtcEmptyState` | Estados inline | Ícone 48dp, título, descrição, ação opcional |
| `WtcErrorState` | Estados inline | Mensagem + botão "Tentar novamente" |
| `WtcLoading` | `CircularProgressIndicator` | Skeleton/shimmer para listas |

---

## 7. Motion

Tokens em `theme/Motion.kt`.

```kotlin
WtcMotion.SHORT  = 150ms   // hover/press, cor, ícones
WtcMotion.MEDIUM = 250ms   // entrada de itens, fade
WtcMotion.LONG   = 350ms   // transição entre telas
```

### Regras de animação

| Contexto | Animação |
|---|---|
| **Navegação** (avançar) | `slideInHorizontally { it/10 } + fadeIn` (LONG, Emphasized) |
| **Navegação** (voltar) | `slideInHorizontally { -it/20 } + fadeIn` (LONG, Emphasized) |
| **Home → fluxo auth** | `fadeIn + scaleIn(0.98f)` |
| **Troca de abas** | `AnimatedContent` com `fadeIn(MEDIUM) + fadeOut(SHORT)` |
| **Bottom nav indicator** | `animateColorAsState + animateDpAsState` (SHORT) |
| **Mensagens novas** | `fadeIn + slideInVertically { it/4 } + scaleIn(0.96f)` |
| **Listas (1ª carga)** | Stagger 30ms por item, limitado aos 8 primeiros |
| **Loading → conteúdo** | `AnimatedContent` / `Crossfade` (MEDIUM) |
| **Press feedback** | `scale 1→0.98` via `Modifier.pressScale()` (SHORT) |
| **Erro de formulário** | `AnimatedVisibility(expandVertically + fadeIn)` |
| **FAB** | `AnimatedVisibility(scaleIn/scaleOut + fade)` ao rolar |

**Acessibilidade:** `LocalReduceMotion` — se `ANIMATOR_DURATION_SCALE == 0`, desativa stagger e scale, mantém apenas fades curtos.

---

## 8. Feedback ao usuário

| Situação | Componente |
|---|---|
| Resultado de ação (salvar, enviar, falhar) | `WtcSnackbar` (variante sucesso/erro/info) |
| Confirmação destrutiva | `AlertDialog` |
| ~~Toast~~ | **Proibido** — substituído por Snackbar |
| Inline error em formulário | Campo vermelho + texto animado abaixo |
