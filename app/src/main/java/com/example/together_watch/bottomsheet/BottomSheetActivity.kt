package com.example.together_watch.bottomsheet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.together_watch.R
import com.example.together_watch.databinding.ActivityBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class BottomSheetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding = ActivityBottomSheetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBottomSheetCreate.setOnClickListener {
            val bottomSheetView = layoutInflater.inflate(R.layout.dialog_bottom_sheet_create, null)
            val bottomSheetDialog = BottomSheetDialog(this@BottomSheetActivity, R.style.DialogStyle).apply {
                setContentView(bottomSheetView)
                show()
            }
            // TODO : switch 값 가져와서 bottoSheet 연장하기
            // TODO : MVP 패턴에 맞게 코드 수정하기
            // TODO : 값 받아와서 형식에 맞게 저장하기까지
        }

        binding.btnBottomSheetUpdateDelete.setOnClickListener {
            val bottomSheetView = layoutInflater.inflate(R.layout.dialog_bottom_sheet_update_and_delete, null)
            val bottomSheetDialog = BottomSheetDialog(this@BottomSheetActivity)
            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()
        }
    }
}