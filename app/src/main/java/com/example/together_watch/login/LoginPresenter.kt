package com.example.together_watch.login

import android.util.Log
import com.google.android.gms.tasks.RuntimeExecutionException
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult
import com.google.firebase.functions.functions

class LoginPresenter(
    private var activity : LoginContract.View?
): LoginContract.Presenter {

    private lateinit var auth: FirebaseAuth
    private lateinit var functions: FirebaseFunctions

    override fun callKakaoLoginFunction(accessToken: String): Task<HttpsCallableResult> {
        functions = Firebase.functions("asia-northeast3")

        val data = hashMapOf(
            "token" to accessToken
        )

        return functions
            .getHttpsCallable("kakaoCustomAuth")
            .call(data)
            .addOnCompleteListener { task ->
                try {
                    // 호출 성공
                    val result = task.result?.data as HashMap<*, *>
                    var mKey: String? = null
                    for (key in result.keys) {
                        mKey = key.toString()
                    }
                    val customToken = result[mKey!!].toString()

                    firebaseAuthWithKakao(customToken)
                } catch (e: RuntimeExecutionException) {
                    Log.e("cloud-functions", "Call Firebase Cloud functions failed.${e.message}")
                }
            }
    }

    private fun firebaseAuthWithKakao(customToken: String) {
        auth = Firebase.auth
        auth.signInWithCustomToken(customToken).addOnCompleteListener { result ->
            if (result.isSuccessful) {
                Log.d("custom-token", "로그인 -> $customToken")
            } else {
                Log.e("custom-token", "로그인 최종 실패")
            }
        }
    }
}