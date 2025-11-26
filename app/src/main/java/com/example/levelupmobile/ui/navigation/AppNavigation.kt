package com.example.levelupmobile.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.levelupmobile.ui.cart.CartScreen
import com.example.levelupmobile.ui.auth.LoginScreen
import com.example.levelupmobile.ui.auth.RegisterScreen
import com.example.levelupmobile.ui.camera.CameraScreen
import com.example.levelupmobile.ui.catalog.CatalogScreen
import com.example.levelupmobile.ui.catalog.ProductDetailScreen
import com.example.levelupmobile.ui.home.HomeScreen
import com.example.levelupmobile.ui.map.EventMapScreen
import com.example.levelupmobile.ui.profile.ProfileScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToCatalog = {
                    navController.navigate(Screen.Catalog.route)
                },
                onNavigateToCart = {
                    navController.navigate(Screen.Cart.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToProduct = { productCode ->
                    navController.navigate(Screen.ProductDetail.createRoute(productCode))
                },
                onNavigateToEventMap = {
                    navController.navigate(Screen.EventMap.route)
                }
            )
        }

        composable(Screen.Catalog.route) {
            CatalogScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToProduct = { productCode ->
                    navController.navigate(Screen.ProductDetail.createRoute(productCode))
                }
            )
        }

        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(navArgument("productCode") { type = NavType.StringType })
        ) { backStackEntry ->
            val productCode = backStackEntry.arguments?.getString("productCode") ?: ""
            ProductDetailScreen(
                productCode = productCode,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCart = { navController.navigate(Screen.Cart.route) }
            )
        }

        composable(Screen.Cart.route) {
            CartScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        // --- AQUÍ ESTABA EL ERROR: CORREGIDO ---
        composable(Screen.Profile.route) {
            ProfileScreen(
                // Ahora solo pasamos la función para ir al login tras cerrar sesión
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        // Limpiamos la pila para que no pueda volver atrás
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        // ---------------------------------------

        composable(Screen.Camera.route) {
            CameraScreen(
                onPhotoTaken = { uri ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("new_profile_pic_uri", uri.toString())

                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.EventMap.route) {
            EventMapScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}