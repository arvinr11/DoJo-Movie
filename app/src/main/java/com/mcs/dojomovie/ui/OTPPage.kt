package com.mcs.dojomovie.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mcs.dojomovie.MainActivity
import com.mcs.dojomovie.R

class OTPPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_otp_page)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backbutt = findViewById<ImageButton>(R.id.backtoregis)
        val otpver = findViewById<TextView>(R.id.Otptext)
        val textDes = findViewById<TextView>(R.id.Otptextdesc)
        val otpInput = findViewById<EditText>(R.id.otpinput)

        val confbtn = findViewById<Button>(R.id.confotp)

        fun generateRandom(length: Int): String {
            val charPool: List<Char> = ('0'..'9').toList()

            return (1..length)
                .map{ kotlin.random.Random.nextInt(0, charPool.size) }
                .map(charPool::get)
                .joinToString("")
        }

        var otp = generateRandom(6)

        Toast.makeText(this, "OTP: $otp", Toast.LENGTH_SHORT).show()

        confbtn.setOnClickListener{
            if (otpInput.text.toString() == otp){
                val intent4 = Intent(this@OTPPage, MainActivity::class.java)
                startActivity(intent4)
            } else {
                Toast.makeText(this, "OTP: $otp", Toast.LENGTH_SHORT).show()
            }
        }
    }
}