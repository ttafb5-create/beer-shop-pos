package com.beershop.pos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beershop.pos.data.local.entity.UserEntity
import com.beershop.pos.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
    val isLoggedIn: Boolean = false,
    val currentUser: UserEntity? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val users: List<UserEntity> = emptyList()
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.getAllUsers().collect { users ->
                _authState.update { it.copy(users = users) }
            }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _authState.update { it.copy(isLoading = true, error = null) }
            authRepository.login(username, password)
                .onSuccess { user ->
                    _authState.update {
                        it.copy(
                            isLoggedIn = true,
                            currentUser = user,
                            isLoading = false
                        )
                    }
                }
                .onFailure { e ->
                    _authState.update {
                        it.copy(isLoading = false, error = e.message)
                    }
                }
        }
    }

    fun createUser(username: String, password: String, displayName: String, role: String) {
        viewModelScope.launch {
            _authState.update { it.copy(isLoading = true, error = null) }
            authRepository.createUser(username, password, displayName, role)
                .onFailure { e ->
                    _authState.update { it.copy(isLoading = false, error = e.message) }
                }
                .onSuccess {
                    _authState.update { it.copy(isLoading = false) }
                }
        }
    }

    fun logout() {
        _authState.update { AuthState() }
    }

    fun clearError() {
        _authState.update { it.copy(error = null) }
    }

    fun hasPermission(action: com.beershop.pos.data.local.entity.Action): Boolean {
        val role = _authState.value.currentUser?.role ?: return false
        return com.beershop.pos.data.local.entity.UserRole.hasPermission(role, action)
    }
}
