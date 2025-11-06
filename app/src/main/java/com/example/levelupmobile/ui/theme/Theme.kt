package cl.duoc.levelupmobile.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val LevelUpColorScheme = darkColorScheme(
    primary = ElectricBlue,
    secondary = NeonGreen,
    tertiary = DarkGray,
    background = Black,
    surface = DarkGray,
    onPrimary = Black,
    onSecondary = Black,
    onTertiary = White,
    onBackground = White,
    onSurface = White,
    error = ErrorRed,
    onError = White
)

@Composable
fun LevelUpGamerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LevelUpColorScheme,
        typography = Typography,
        content = content
    )
}