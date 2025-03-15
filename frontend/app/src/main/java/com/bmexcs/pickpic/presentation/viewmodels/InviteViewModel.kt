package com.bmexcs.pickpic.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmexcs.pickpic.data.services.EventApiService
import com.bmexcs.pickpic.data.sources.AuthDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class InviteViewModel @Inject constructor(
    private val eventApiService: EventApiService,
    private val authDataSource: AuthDataSource // Inject AuthDataSource
) : ViewModel() {
    private val _emailList = MutableStateFlow<List<String>>(emptyList())
    val emailList: StateFlow<List<String>> = _emailList
    val TAG = "InviteViewModel"


    fun addEmail(email: String) {
        _emailList.value += email
    }

    fun removeEmail(email: String) {
        _emailList.value -= email
    }

    fun confirmInvites(emailList: List<String>, eventId: String) {
        viewModelScope.launch {
            val token = authDataSource.getIdToken() ?: throw Exception("No user token")
            val success = eventApiService.inviteUserToEvent(eventId, emailList, token)
            if (success) {
                Log.d(TAG, "Users invited successfully")
            } else {
                Log.e(TAG, "Failed to invite users")
            }
        }
    }
}
