package com.beershop.pos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beershop.pos.data.local.dao.UserDao
import com.beershop.pos.data.local.entity.UserEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserManagementState(
    val users: List<UserEntity> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class UserManagementViewModel @Inject constructor(
    private val userDao: UserDao
) : ViewModel() {

    private val _state = MutableStateFlow(UserManagementState())
    val state: StateFlow<UserManagementState> = _state.asStateFlow()

    init {
        loadUsers()
    }

    private fun loadUsers() {
        viewModelScope.launch {
            userDao.getAllUsers().collect { users ->
                _state.update { it.copy(users = users, isLoading = false) }
            }
        }
    }

    fun addUser(username: String, password: String, displayName: String, role: String) {
        viewModelScope.launch {
            val user = UserEntity(
                username = username,
                password = password,  // In production, hash this!
                displayName = displayName,
                role = role
            )
            userDao.insertUser(user)
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            userDao.deactivateUser(userId)
        }
    }
}