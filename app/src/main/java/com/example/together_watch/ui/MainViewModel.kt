package com.example.together_watch.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.together_watch.data.Schedule
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    var mySchedule = listOf<Schedule>()
    private val _apiData = MutableLiveData<List<Schedule>>()
    val apiData: LiveData<List<Schedule>> = _apiData

    fun fetchSchedulesData() {
        viewModelScope.launch {
            val userId = Firebase.auth.currentUser?.uid ?: ""
            Firebase.firestore.collection("users")
                .document(userId)
                .collection("schedules")
                .get()
                .addOnSuccessListener { documents ->
                    mySchedule = documents.map {
                        Schedule(
                            name = it.get("name").toString(),
                            place = it.get("place").toString(),
                            date = it.get("date").toString(),
                            startTime = it.get("startTime").toString(),
                            endTime = it.get("endTime").toString(),
                            isGroup = it.get("isGroup").toString() == "true",
                        )
                    }
                    _apiData.value = mySchedule
                }
        }
    }
}