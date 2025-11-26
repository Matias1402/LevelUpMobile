package cl.duoc.levelupmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.levelupmobile.data.local.datastore.PreferencesManager
import com.example.levelupmobile.ui.navigation.AppNavigation
import com.example.levelupmobile.ui.navigation.Screen
import com.example.levelupmobile.ui.theme.LevelUpGamerTheme
// import kotlinx.coroutines.flow.first  <-- YA NO NECESITAS ESTO

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LevelUpGamerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    val preferencesManager = remember { PreferencesManager(context) }
                    var startDestination by remember { mutableStateOf<String?>(null) }

                    LaunchedEffect(Unit) {
                        // CAMBIO AQUÍ: Verificamos si hay token directamente
                        val token = preferencesManager.getToken()

                        // Si el token no es nulo ni vacío, el usuario está logueado
                        val isLoggedIn = !token.isNullOrEmpty()

                        startDestination = if (isLoggedIn) {
                            Screen.Home.route
                        } else {
                            Screen.Login.route
                        }
                    }

                    startDestination?.let { destination ->
                        AppNavigation(startDestination = destination)
                    }
                }
            }
        }
    }
}