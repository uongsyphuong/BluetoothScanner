package com.usphuong.bluetoothscanner.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.usphuong.bluetoothscanner.data.model.Resource
import com.usphuong.bluetoothscanner.data.model.User
import com.usphuong.bluetoothscanner.data.repository.UserRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    val errorLiveData = MutableLiveData<String>()
    val userInfoLiveData = MutableLiveData<User>()

    fun getUserInfo() {
        viewModelScope.launch {
            val response = userRepository.getUserInfo()
            if (response.status == Resource.Status.SUCCESS) {
                userInfoLiveData.postValue(response.data)
            } else {
                errorLiveData.value = response.message
            }
        }
    }
}
