package com.mcs.dojomovie.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mcs.dojomovie.R
import com.mcs.dojomovie.database.DatabaseHelper

class LoginPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val logoimg = findViewById<ImageView>(R.id.logoimg)
        val phoneInput = findViewById<EditText>(R.id.phonenumber)
        val passInput = findViewById<EditText>(R.id.password)
        val RegText = findViewById<TextView>(R.id.registerbtn)
        val loginbtn = findViewById<Button>(R.id.loginbtn)
        val regisText = "Didn’t have an account? Register here!"
        val spannableString = SpannableString(regisText)
        val context = this
        val db = DatabaseHelper(context)

        val clickableSpan = object : ClickableSpan(){
            override fun onClick(widget: View) {
                val intent1 = Intent( this@LoginPage, RegisterPage::class.java)
                startActivity(intent1)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.RED
                ds.isUnderlineText = false
            }
        }

        val startIdx = regisText.indexOf("Register here!")
        val endIdx = startIdx + "Register here!".length

        spannableString.setSpan(clickableSpan, startIdx, endIdx, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        RegText.text = spannableString

        RegText.movementMethod = LinkMovementMethod.getInstance()

        loginbtn.setOnClickListener{
            var data = db.getUser()
            val user = data.find{it.phone == phoneInput.text.toString() && it.password == passInput.text.toString()}
            val checkPhone = data.find {it.phone == phoneInput.text.toString()}

            if(user != null){
                val sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                with(sharedPreferences.edit()) {
                    putInt("LOGGED_IN_USER_ID", user.id)
                    putString("LOGGED_IN_USER_PHONE", user.phone)
                    putBoolean("IS_USER_LOGGED_IN", true)
                    apply()
                }

                val intent = Intent(this@LoginPage, OTPPage::class.java)
                startActivity(intent)
            }
            else if(checkPhone != null){
                Toast.makeText(this, "Wrong password", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this, "Phone number incorrect", Toast.LENGTH_SHORT).show()
            }
        }
    }
}