package com.example.myapplicationtest

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplicationtest.databinding.ActivityMainBinding
import com.imfarrik.customdatepicker.CustomDatePickerYear


class MainActivity : AppCompatActivity() {

    private lateinit var vb: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = ActivityMainBinding.inflate(layoutInflater)
        setContentView(vb.root)

        vb.btn.setOnClickListener {
            showNewDialog()
        }

    }

    private fun showNewDialog() {
        val dialog = Dialog(this)
        dialog.apply {
            setContentView(R.layout.custom_dialog_calendar)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        val btnX = dialog.findViewById<TextView>(R.id.textView20)
        val btnDone = dialog.findViewById<TextView>(R.id.textView21)
        val calendar = dialog.findViewById<CustomDatePickerYear>(R.id.calendar)

        btnX.setOnClickListener {
            dialog.cancel()
        }

        btnDone.setOnClickListener {
            Toast.makeText(this, calendar.fullDate, Toast.LENGTH_SHORT).show()
        }


        dialog.show()

    }
}