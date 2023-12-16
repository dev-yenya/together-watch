package com.example.together_watch.promise

import android.util.Log
import com.example.together_watch.data.FetchedPromise
import com.example.together_watch.ui.MainViewModel
import com.google.android.gms.tasks.RuntimeExecutionException
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.functions
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.tasks.await
import java.lang.StringBuilder
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class DateBlock(
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val possibleUsers: List<String>
) {
    override fun toString(): String {
        val builder = StringBuilder()
        builder.append(date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")))
            .append(" (")
            .append(getKorDayOfWeek(date.dayOfWeek))
            .append(") ")
            .append(startTime.format(DateTimeFormatter.ofPattern("H:mm")))
            .append(" ~ ")
            .append(endTime.format(DateTimeFormatter.ofPattern("H:mm")))
        return builder.toString()
    }

    private fun getKorDayOfWeek(day: DayOfWeek): String {
        return when(day) {
            DayOfWeek.MONDAY -> "월"
            DayOfWeek.TUESDAY -> "화"
            DayOfWeek.WEDNESDAY -> "수"
            DayOfWeek.THURSDAY -> "목"
            DayOfWeek.FRIDAY -> "금"
            DayOfWeek.SATURDAY -> "토"
            DayOfWeek.SUNDAY -> "일"
        }
    }
}

class PromiseCompletionModel {

    private lateinit var functions: FirebaseFunctions
    private val blocks = mutableListOf<DateBlock>()

    suspend fun makeSchedule(fetchedPromise: FetchedPromise): MutableList<DateBlock>{
        val currentUser = Firebase.auth.currentUser
        if (! currentUser?.uid.equals(fetchedPromise.promise.ownerId)) {
            Log.d("promise-completion", "현재 사용자는 약속을 생성한 회원이 아닙니다.")
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
                        blocks.add(
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
                Log.d("promise-completion", "함수 호출 결과: $blocks")
            }.await()
        return blocks
    }


    // example: Sun Jan 01 2023
    private fun localDateValue(dateString: String): LocalDate {
        val list = dateString.split("-")
        val dayOfMonth = list[2].toInt()
        val month = list[1].toInt()
        val year = list[0].toInt()
        return LocalDate.of(year, month, dayOfMonth)
    }

    private fun localTimeValue(timeString: String): LocalTime {
        val list = timeString.split(":")
        return LocalTime.of(list[0].toInt(), list[1].toInt())
    }
}