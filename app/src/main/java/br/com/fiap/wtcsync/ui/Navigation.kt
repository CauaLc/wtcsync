package br.com.fiap.wtcsync.ui

import android.app.Application
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import br.com.fiap.wtcsync.campaigns.ui.create.CreateCampaignScreen
import br.com.fiap.wtcsync.campaigns.ui.create.CreateCampaignViewModel
import br.com.fiap.wtcsync.campaigns.ui.create.CreateCampaignViewModelFactory
import br.com.fiap.wtcsync.campaigns.ui.detail.CampaignDetailScreen
import br.com.fiap.wtcsync.campaigns.ui.detail.CampaignDetailViewModel
import br.com.fiap.wtcsync.campaigns.ui.detail.CampaignDetailViewModelFactory
import br.com.fiap.wtcsync.campaigns.ui.client.ClientCampaignScreen
import br.com.fiap.wtcsync.campaigns.ui.list.CampaignListScreen
import br.com.fiap.wtcsync.campaigns.ui.list.CampaignListViewModel
import br.com.fiap.wtcsync.campaigns.ui.list.CampaignListViewModelFactory
import br.com.fiap.wtcsync.segments.ui.create.CreateSegmentScreen
import br.com.fiap.wtcsync.segments.ui.create.CreateSegmentViewModel
import br.com.fiap.wtcsync.segments.ui.create.CreateSegmentViewModelFactory
import br.com.fiap.wtcsync.theme.WtcMotion
import br.com.fiap.wtcsync.ui.auth.AuthViewModel
import br.com.fiap.wtcsync.ui.auth.AuthViewModelFactory
import br.com.fiap.wtcsync.ui.auth.EntryScreen
import br.com.fiap.wtcsync.ui.auth.LoginScreen
import br.com.fiap.wtcsync.ui.auth.RegisterScreen
import br.com.fiap.wtcsync.ui.components.BottomNavTab
import br.com.fiap.wtcsync.ui.components.WtcBottomNavBar
import br.com.fiap.wtcsync.ui.components.WtcScaffold
import br.com.fiap.wtcsync.ui.crm.ChatListScreen
import br.com.fiap.wtcsync.ui.crm.ClientFormScreen
import br.com.fiap.wtcsync.ui.crm.ClientesScreen
import br.com.fiap.wtcsync.ui.crm.SegmentosScreen
import br.com.fiap.wtcsync.ui.messages.MessageScreen
import br.com.fiap.wtcsync.data.model.enums.UserRole

@Composable
fun Navigation(navController: NavHostController) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(application)
    )
    val role by authViewModel.currentRole.collectAsState()

    val slideEnter  = slideInHorizontally(tween(WtcMotion.LONG)) { it / 4 } + fadeIn(tween(WtcMotion.LONG))
    val slideExit   = slideOutHorizontally(tween(WtcMotion.LONG)) { -it / 4 } + fadeOut(tween(WtcMotion.MEDIUM))
    val slidePopEnter  = slideInHorizontally(tween(WtcMotion.LONG)) { -it / 4 } + fadeIn(tween(WtcMotion.LONG))
    val slidePopExit   = slideOutHorizontally(tween(WtcMotion.LONG)) { it / 4 } + fadeOut(tween(WtcMotion.MEDIUM))
    val authToHome  = fadeIn(tween(WtcMotion.LONG)) + scaleIn(tween(WtcMotion.LONG), initialScale = 0.98f)
    val authExitOut = fadeOut(tween(WtcMotion.MEDIUM))

    NavHost(
        navController = navController,
        startDestination = "entry",
        enterTransition  = { slideEnter },
        exitTransition   = { slideExit },
        popEnterTransition  = { slidePopEnter },
        popExitTransition   = { slidePopExit },
    ) {
        composable(
            "entry",
            exitTransition = { authExitOut },
        ) {
            EntryScreen(
                onLoginClick = { navController.navigate("login") },
                onRegisterClick = { navController.navigate("register") },
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("entry") { inclusive = true }
                    }
                }
            )
        }
        composable(
            "login",
            enterTransition  = { slideEnter },
            exitTransition   = { authExitOut },
            popExitTransition = { slidePopExit },
        ) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("entry") { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            "register",
            enterTransition  = { slideEnter },
            exitTransition   = { authExitOut },
            popExitTransition = { slidePopExit },
        ) {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            "home",
            enterTransition = { authToHome },
            exitTransition  = { authExitOut },
            popEnterTransition = { authToHome },
            popExitTransition  = { authExitOut },
        ) {
            HomeScreen(
                navController = navController,
                userRole = role
            )
        }
        composable(
            route = "message_screen/{clienteId}/{contactName}",
            arguments = listOf(
                navArgument("clienteId") { type = NavType.StringType },
                navArgument("contactName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val clienteId = backStackEntry.arguments?.getString("clienteId") ?: ""
            val contactName = backStackEntry.arguments?.getString("contactName") ?: "Contato"
            MessageScreen(
                clienteId = clienteId,
                contactName = contactName,
                navController = navController
            )
        }
        composable("campaigns") {
            val listViewModel: CampaignListViewModel = viewModel(
                factory = CampaignListViewModelFactory(application)
            )
            CampaignListScreen(
                viewModel = listViewModel,
                onCampaignClick = { id -> navController.navigate("campaigns/$id") },
                onCreateClick = { navController.navigate("campaigns/create") },
                userRole = role
            )
        }
        composable(
            route = "campaigns/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            val detailViewModel: CampaignDetailViewModel = viewModel(
                factory = CampaignDetailViewModelFactory(application, id)
            )
            CampaignDetailScreen(
                viewModel = detailViewModel,
                onBackClick = { navController.popBackStack() },
                userRole = role
            )
        }
        composable("campaigns/create") {
            val createViewModel: CreateCampaignViewModel = viewModel(
                factory = CreateCampaignViewModelFactory(application)
            )
            CreateCampaignScreen(
                viewModel = createViewModel,
                onBackClick = { navController.popBackStack() },
                onSuccess = {
                    navController.navigate("campaigns") {
                        popUpTo("campaigns") { inclusive = true }
                    }
                }
            )
        }
        composable("clientes/novo") {
            ClientFormScreen(
                clienteId = null,
                onSave = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }
        composable(
            route = "clientes/{id}/editar",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStack ->
            ClientFormScreen(
                clienteId = backStack.arguments?.getString("id"),
                onSave = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }
        composable("segments/create") {
            val createViewModel: CreateSegmentViewModel = viewModel(
                factory = CreateSegmentViewModelFactory(application)
            )
            CreateSegmentScreen(
                viewModel = createViewModel,
                onBackClick = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun HomeScreen(
    navController: NavHostController,
    userRole: UserRole?
) {
    val availableTabs = if (userRole == UserRole.OPERATOR || userRole == UserRole.CLIENTE) {
        BottomNavTab.entries
    } else {
        BottomNavTab.entries.filter { it != BottomNavTab.CAMPANHAS }
    }

    var selectedTab by rememberSaveable { mutableStateOf(BottomNavTab.CHAT) }

    if (selectedTab !in availableTabs) {
        selectedTab = BottomNavTab.CHAT
    }

    WtcScaffold(
        bottomBar = {
            WtcBottomNavBar(
                tabs = availableTabs,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { padding ->
        AnimatedContent(
            targetState = selectedTab,
            modifier = Modifier.padding(padding).fillMaxSize(),
            transitionSpec = {
                fadeIn(tween(WtcMotion.MEDIUM)) togetherWith fadeOut(tween(WtcMotion.SHORT))
            },
            label = "tabContent",
        ) { tab ->
            when (tab) {
                BottomNavTab.CLIENTES -> ClientesScreen(
                    onCreateClick = { navController.navigate("clientes/novo") },
                    onClienteClick = { id -> navController.navigate("clientes/$id/editar") }
                )
                BottomNavTab.SEGMENTOS -> SegmentosScreen(
                    application = navController.context.applicationContext as Application,
                    userRole = userRole,
                    onCreateClick = { navController.navigate("segments/create") }
                )
                BottomNavTab.CAMPANHAS -> {
                    when (userRole) {
                        UserRole.OPERATOR -> {
                            val listViewModel: CampaignListViewModel = viewModel(
                                factory = CampaignListViewModelFactory(
                                    navController.context.applicationContext as Application
                                )
                            )
                            val lifecycleOwner = LocalLifecycleOwner.current
                            var isFirstResume by rememberSaveable { mutableStateOf(true) }
                            DisposableEffect(lifecycleOwner) {
                                val observer = LifecycleEventObserver { _, event ->
                                    if (event == Lifecycle.Event.ON_RESUME) {
                                        if (!isFirstResume) {
                                            listViewModel.refresh()
                                        }
                                        isFirstResume = false
                                    }
                                }
                                lifecycleOwner.lifecycle.addObserver(observer)
                                onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
                            }
                            CampaignListScreen(
                                viewModel = listViewModel,
                                onCampaignClick = { id ->
                                    navController.navigate("campaigns/$id")
                                },
                                onCreateClick = {
                                    navController.navigate("campaigns/create")
                                },
                                userRole = userRole
                            )
                        }
                        UserRole.CLIENTE -> {
                            ClientCampaignScreen(
                                application = navController.context.applicationContext as Application
                            )
                        }
                        else -> {}
                    }
                }
                BottomNavTab.CHAT -> ChatListScreen(
                    onChatClick = { id, name ->
                        navController.navigate(
                            "message_screen/${Uri.encode(id)}/${Uri.encode(name)}"
                        )
                    },
                    onCreateClick = { navController.navigate("clientes/novo") }
                )
            }
        }
    }
}