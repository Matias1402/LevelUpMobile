package cl.duoc.levelupmobile.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Catalog : Screen("catalog")
    object ProductDetail : Screen("product_detail/{productCode}") {
        fun createRoute(productCode: String) = "product_detail/$productCode"
    }
    object Cart : Screen("cart")
    object Profile : Screen("profile")
    object Camera : Screen("camera")
    object EventMap : Screen("event_map")
}