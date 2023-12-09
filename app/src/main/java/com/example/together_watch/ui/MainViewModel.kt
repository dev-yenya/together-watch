package com.example.together_watch.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.together_watch.data.FetchedPromise
import com.example.together_watch.data.FetchedSchedule
import com.example.together_watch.data.Promise
import com.example.together_watch.data.Schedule
import com.example.together_watch.data.Status
import com.example.together_watch.data.toMap
import com.example.together_watch.promise.PromiseInfo
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CompletableFuture

class MainViewModel : ViewModel() {
    var mySchedules = listOf<FetchedSchedule>()
    private val _apiData = MutableLiveData<List<FetchedSchedule>>()
    val apiData: LiveData<List<FetchedSchedule>> = _apiData

    var myPromises = listOf<FetchedPromise>()
    private val _apiPromiseData = MutableLiveData<List<FetchedPromise>>()
    val apiPromiseData: LiveData<List<FetchedPromise>> = _apiPromiseData

    var promiseName: String = ""
    var promisePlace: String = ""
    var selectedDates: List<String> = emptyList()
    var startTime: String = ""
    var endTime: String = ""

    fun fetchSchedulesData() {
        viewModelScope.launch {
            val userId = Firebase.auth.currentUser?.uid.toString()
            Firebase.firestore.collection("users")
                .document(userId)
                .collection("schedules")
                .get()
                .addOnSuccessListener { documents ->
                    mySchedules = documents.map {
                        FetchedSchedule(
                            id = it.id,
                            schedule = Schedule(
                                name = it.get("name").toString(),
                                place = it.get("place").toString(),
                                date = it.get("date").toString(),
                                startTime = it.get("startTime").toString(),
                                endTime = it.get("endTime").toString(),
                                isGroup = it.get("isGroup").toString() == "true",
                            )
                        )
                    }
                    _apiData.value = mySchedules
                }
        }
    }

     fun savePromise(): CompletableFuture<PromiseInfo> {
         val userId = Firebase.auth.currentUser?.uid.toString()
         val userRef = Firebase.firestore.collection("users")
         val result = CompletableFuture<PromiseInfo>()

         userRef.document(userId)
             .collection("promises")
             .add(
                 Promise(
                     name = promiseName,
                     ownerId = userId,
                     users = listOf(userId),
                     status = Status.ONPROGRESS,
                     dates = selectedDates,
                     startTime = startTime,
                     endTime = endTime,
                     place = promisePlace
                 ).toMap()
             ).addOnSuccessListener { promiseDocumentReference ->
                 Log.d("promise", "약속 업로드 성공 ${promiseDocumentReference.id}")
                 result.complete(PromiseInfo(userId, promiseDocumentReference.id))
             }
         return result
     }

    fun fetchOnProgressPromisesData() {
        viewModelScope.launch {
            val userId = Firebase.auth.currentUser?.uid.toString()
            Firebase.firestore.collection("users")
                .document(userId)
                .collection("promises")
                .get()
                .addOnSuccessListener { documents ->
                    myPromises = documents.map {
                        FetchedPromise(
                            id = it.id,
                            promise = Promise(
                                name = it.get("name").toString(),
                                ownerId = it.get("ownerId").toString(),
                                users = it.get("users") as? List<String> ?: emptyList(),
                                status = it.get("status") as Status,
                                dates = it.get("dates") as? List<String> ?: emptyList(),
                                startTime = it.get("startTime").toString(),
                                endTime = it.get("enfTime").toString(),
                                place = it.get("place").toString()
                            )
                        )
                    }
                    _apiPromiseData.value = myPromises
                }
        }
    }
}