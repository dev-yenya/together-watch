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
import com.example.together_watch.data.User
import com.example.together_watch.data.toMap
import com.example.together_watch.promise.DateBlock
import com.example.together_watch.promise.PromiseInfo
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.CompletableFuture

class MainViewModel : ViewModel() {
    var myUid = Firebase.auth.currentUser?.uid.toString()
    var selectedPromise: FetchedPromise? = null
    var mySchedules = listOf<FetchedSchedule>()
    private val _apiData = MutableLiveData<List<FetchedSchedule>>()
    val apiData: LiveData<List<FetchedSchedule>> = _apiData

    var myPromises = listOf<FetchedPromise>()
    private val _apiPromiseData = MutableLiveData<List<FetchedPromise>>()
    val apiPromiseData: LiveData<List<FetchedPromise>> = _apiPromiseData

    var users = listOf<User>()

    var promiseName: String = ""
    var promisePlace: String = ""
    var selectedDates: List<String> = emptyList()
    var startTime: String = ""
    var endTime: String = ""

    var confirmedStartTime: String = ""
    var confirmedEndTime: String = ""

    var selectedBlock: DateBlock? = null

    init {
        fetchUserData()
    }

    fun savePromiseSchedule() {
        val userRef = Firebase.firestore.collection("users")

        val promise = selectedPromise?.promise
        val members = promise?.users as List<String>
        val promiseId = selectedPromise!!.id

        viewModelScope.launch {
            promiseName = promise.name as String
            promisePlace = promise.place as String
            members.forEach { uid ->
                userRef.document(uid)
                    .collection("schedules")
                    .add(
                        Schedule(
                            name = promiseName,
                            place = promisePlace,
                            date = selectedBlock?.date.toString(),
                            startTime = confirmedStartTime,
                            endTime = confirmedEndTime,
                            isGroup = true
                        ).toMap()
                    ).addOnSuccessListener { documentRef ->
                        documentRef.update(
                            mapOf("groupId" to promiseId)
                        )
                        Log.d("promise-completion", "개인 일정에 약속 id 필드 추가")
                    }
            }

            userRef.document(myUid)
                .collection("promises")
                .document(promiseId)
                .update(
                    mapOf(
                        "status" to Status.COMPLETED,
                        "dates" to listOf(selectedBlock?.date.toString()),
                        "startTime" to confirmedStartTime,
                        "endTime" to confirmedEndTime
                    )
                )
                .addOnSuccessListener {
                    Log.d("promise-completion", "약속 상태 변경 성공")
                }.addOnFailureListener { exception ->
                    Log.d("promise-completion", "error message: ${exception.message}")
                }
        }

    }

    fun isValidTimeRange(start: String, end: String): Boolean {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val promise = selectedPromise!!.promise
        val startBoundary = LocalTime.parse(promise.startTime, formatter)
        val endBoundary = LocalTime.parse(promise.endTime, formatter)

        if (start != "" && end != "") {
            val startLocalTime = LocalTime.parse(start, formatter)
            val endLocalTime = LocalTime.parse(end, formatter)
            Log.d(
                "promise-completion",
                "[시간] 지정 가능 범위: ${startBoundary}~${endBoundary}, 실제 입력 범위: ${startLocalTime}~${endLocalTime}"
            )
            return (startBoundary.isBefore(startLocalTime) || startBoundary.equals(startLocalTime))
                    && (endBoundary.isAfter(endLocalTime) || endBoundary.equals(endLocalTime))
                    && !endLocalTime.isBefore(startLocalTime)
        }
        return false
    }

    fun isValidTime(start: String, end: String): Boolean {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        if (start != "" && end != "") {
            val startLocalTime = LocalTime.parse(start, formatter)
            val endLocalTime = LocalTime.parse(end, formatter)
            Log.d("promise-completion", "[시간] 입력 범위: ${startLocalTime}~${endLocalTime}")
            return endLocalTime.isAfter(startLocalTime)
        }
        return false
    }

    fun fetchSchedulesData() {
        viewModelScope.launch {
            Firebase.firestore.collection("users")
                .document(myUid)
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

    fun fetchPromisesData() {
        viewModelScope.launch {
            Firebase.firestore.collection("users")
                .document(myUid)
                .collection("promises")
                .get()
                .addOnSuccessListener { documents ->
                    myPromises = documents.map {
                        Log.e("it.id", it.id)
                        FetchedPromise(
                            id = it.id,
                            promise = Promise(
                                name = it.get("name").toString(),
                                ownerId = it.get("ownerId").toString(),
                                users = it.get("users") as? List<String>,
                                status = Status.valueOf(it.getString("status") ?: "ONPROGRESS"),
                                dates = it.get("dates") as? List<String>,
                                startTime = it.get("startTime").toString(),
                                endTime = it.get("endTime").toString(),
                                place = it.get("place").toString()
                            )
                        )
                    }
                    _apiPromiseData.value = myPromises
                }
        }
    }

    private fun fetchUserData() {
        viewModelScope.launch {
            Firebase.firestore.collection("users")
                .get()
                .addOnSuccessListener { documents ->
                    users = documents.map {
                        User(
                            displayName = it.get("displayName").toString(),
                            photoURL = it.get("photoURL").toString(),
                            uid = it.get("uid").toString()
                        )
                    }
                }
        }
    }

    fun savePromise(): CompletableFuture<PromiseInfo> {
        val userRef = Firebase.firestore.collection("users")
        val result = CompletableFuture<PromiseInfo>()

        userRef.document(myUid)
            .collection("promises")
            .add(
                Promise(
                    name = promiseName,
                    ownerId = myUid,
                    users = listOf(myUid),
                    status = Status.ONPROGRESS,
                    dates = selectedDates,
                    startTime = startTime,
                    endTime = endTime,
                    place = promisePlace
                ).toMap()
            ).addOnSuccessListener { promiseDocumentReference ->
                Log.d("promise", "약속 업로드 성공 ${promiseDocumentReference.id}")
                result.complete(PromiseInfo(myUid, promiseDocumentReference.id))
            }
        return result
    }

    suspend fun deletePromise(promise: FetchedPromise?, successListener: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val members = promise?.promise?.users as List<String>

            members.forEach { userId ->
                Log.d("promise-deletion", "참여자 id: $userId")
                Firebase.firestore.collection("users")
                    .document(userId)
                    .collection("schedules")
                    .whereEqualTo("groupId", promise.id)
                    .get().await()
                    .documents.forEach { snapshot ->
                        snapshot.reference.delete()
                            .addOnSuccessListener {
                                successListener.invoke()
                                Log.d("promise-deletion", "약속 참여자의 개인 일정 삭제, 참여자 수 만큼 로그 출력")
                            }
                    }
            }

            Firebase.firestore.collection("users")
                .document(myUid)
                .collection("promises")
                .document(promise.id)
                .delete()
                .addOnSuccessListener {
                    successListener.invoke()
                }

        }
    }
}