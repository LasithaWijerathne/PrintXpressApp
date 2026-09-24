package com.printxpress.app.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.printxpress.app.data.model.AppNotification
import com.printxpress.app.data.repository.AuthRepository
import com.printxpress.app.data.repository.NotificationRepository
import com.printxpress.app.util.Result
import kotlinx.coroutines.launch

class NotificationsViewModel(
    private val notificationRepository: NotificationRepository = NotificationRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _notifications = MutableLiveData<Result<List<AppNotification>>>()
    val notifications: LiveData<Result<List<AppNotification>>> = _notifications

    fun startObserving() {
        val userId = authRepository.currentUserId ?: return
        viewModelScope.launch {
            notificationRepository.observeNotifications(userId).collect { result ->
                _notifications.value = result
            }
        }
    }

    fun markAsRead(notification: AppNotification) {
        if (notification.read) return
        viewModelScope.launch {
            notificationRepository.markAsRead(notification.id)
        }
    }
}
