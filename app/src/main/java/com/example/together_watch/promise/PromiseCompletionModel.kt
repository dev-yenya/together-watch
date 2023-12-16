package com.example.together_watch.promise

import android.util.Log
import com.example.together_watch.data.FetchedPromise
import com.google.android.gms.tasks.RuntimeExecutionException
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.functions
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.LocalTime

data class DateBlock(
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val possibleUsers: List<String>
)

class PromiseCompletionModel {

    private lateinit var functions: FirebaseFunctions

    suspend fun makeSchedule(fetchedPromise: FetchedPromise): List<DateBlock> {
        val currentUser = Firebase.auth.currentUser
        val dateBlocks = mutableListOf<DateBlock>()
        if (! currentUser?.uid.equals(fetchedPromise.promise.ownerId)) {
            Log.d("promise-completion", "현재 사용자는 약속을 생성한 회원이 아닙니다.")
            return dateBlocks
        }

        val data = hashMapOf(
            "ownerId" to fetchedPromise.promise.ownerId,
            "groupId" to fetchedPromise.id
        )

        functions = Firebase.functions("asia-northeast3")
        functions.getHttpsCallable("getBestSchedule")
            .call(data)
            .addOnCompleteListener { task ->
                try {
                    // 호출 성공
                    val result = task.result?.data as HashMap<*, *>
                    val resultTimes = result["times"] as List<Map<String, *>>
                    resultTimes.forEach { block ->
                        dateBlocks.add(
                            DateBlock(
                                localDateValue(block["date"]!! as String),
                                localTimeValue(block["startTime"]!! as String),
                                localTimeValue(block["endTime"]!! as String),
                                block["members"] as List<String>
                            )
                        )
                    }

                } catch (e: RuntimeExecutionException) {
                    Log.e("promise-completion", "Call Firebase Cloud functions failed.${e.message}")
                }
            }.await()
        return dateBlocks
    }


    // example: Sun Jan 01 2023
    private fun localDateValue(dateString: String): LocalDate {
        val list = dateString.split(" ")
        val dayOfMonth = list[2].toInt()
        val month = when (list[1]) {
            "Jan" -> 1
            "Feb" -> 2
            "Mar" -> 3
            "Apr" -> 4
            "May" -> 5
            "Jun" -> 6
            "Jul" -> 7
            "Aug" -> 8
            "Sep" -> 9
            "Oct" -> 10
            "Nov" -> 11
            "Dec" -> 12
            else -> throw IllegalArgumentException()
        }
        val year = list[4].toInt()
        return LocalDate.of(year, month, dayOfMonth)
    }

    private fun localTimeValue(timeString: String): LocalTime {
        val list = timeString.split(":")
        return LocalTime.of(list[0].toInt(), list[1].toInt())
    }
}