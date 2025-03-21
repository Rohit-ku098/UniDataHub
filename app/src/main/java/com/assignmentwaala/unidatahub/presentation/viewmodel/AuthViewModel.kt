package com.assignmentwaala.unidatahub.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignmentwaala.unidatahub.MainActivity.Companion.TAG
import com.assignmentwaala.unidatahub.common.AuthStatus
import com.assignmentwaala.unidatahub.domain.models.UserModel
import com.assignmentwaala.unidatahub.domain.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: Repository): ViewModel() {

    private val _authStatus = MutableStateFlow<AuthStatus<UserModel>>(AuthStatus.Empty)
    val authStatus = _authStatus.asStateFlow()

    val isAuthenticated = MutableStateFlow(true)

    init {
        Log.d(TAG, "AuthViewModel init called")
        Log.d(TAG, "authStatus:- ${authStatus.value}")
    }

    fun getCurrentUser() {
        viewModelScope.launch {
        repository.getCurrentUser()
            .collect {
                _authStatus.value = it
                isAuthenticated.value = it is AuthStatus.Authenticated
            }
        }
    }


    fun login(email: String, password: String) {
        viewModelScope.launch {
            repository.login(email, password)
                .collect {
                    _authStatus.value = it
                    isAuthenticated.value = it is AuthStatus.Authenticated
                }
        }
    }

    fun signup(username: String, email: String, password: String, role: String) {
        viewModelScope.launch {
            repository.signup(username, email, password, role)
                .collect {
                    _authStatus.value = it
                    isAuthenticated.value = it is AuthStatus.Authenticated

                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
                .collect {
                    _authStatus.value = it
                    isAuthenticated.value = it is AuthStatus.Authenticated

                }
        }
    }
}