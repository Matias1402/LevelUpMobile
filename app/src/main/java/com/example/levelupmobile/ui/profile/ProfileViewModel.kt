package cl.duoc.levelupmobile.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.levelupmobile.data.local.database.AppDatabase
import cl.duoc.levelupmobile.data.local.datastore.PreferencesManager
import cl.duoc.levelupmobile.data.local.entities.User
import cl.duoc.levelupmobile.data.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val userRepository = UserRepository(database.userDao())
    private val preferencesManager = PreferencesManager(application)

    val currentUser: StateFlow<User?> = preferencesManager.userIdFlow
        .filterNotNull()
        .flatMapLatest { userId ->
            userRepository.getUserById(userId)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun updateUser(user: User) {
        viewModelScope.launch {
            userRepository.updateUser(user)
        }
    }

    fun logout() {
        viewModelScope.launch {
            preferencesManager.logout()
        }
    }
}