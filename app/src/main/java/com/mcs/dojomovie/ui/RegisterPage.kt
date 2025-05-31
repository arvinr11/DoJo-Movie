package com.mcs.dojomovie.ui

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
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mcs.dojomovie.R
import com.mcs.dojomovie.database.DatabaseHelper
import com.mcs.dojomovie.model.User

class RegisterPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val phoneInput = findViewById<EditText>(R.id.phonenumber)
        val passInput = findViewById<EditText>(R.id.password)
        val confPass = findViewById<EditText>(R.id.confirmpass)
        val LogText = findViewById<TextView>(R.id.logbtn)
        val regisbtn = findViewById<Button>(R.id.regisbtn)
        val loginText = "Already have account? Login here!"
        val spannableString = SpannableString(loginText)
        val context = this

        val clickableSpan = object : ClickableSpan(){
            override fun onClick(widget: View) {
                val intent2 = Intent( this@RegisterPage, LoginPage::class.java)
                startActivity(intent2)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.RED
                ds.isUnderlineText = false
            }
        }

        val startIdx = loginText.indexOf("Login here!")
        val endIdx = startIdx + "Login here!".length

        spannableString.setSpan(clickableSpan, startIdx, endIdx, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        LogText.text = spannableString

        LogText.movementMethod = LinkMovementMethod.getInstance()

        regisbtn.setOnClickListener{
            val passLen = passInput.text.length
            val phoneNum = phoneInput.text.toString().trim()
            val pass = passInput.text.toString()
            val cPass = confPass.text.toString()

            if(passLen < 8 ){
                Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show()
            }
            else if(pass != cPass){
                Toast.makeText(this, "Password are not the same", Toast.LENGTH_SHORT).show()
            }
            else{
                val newUser = User()
                newUser.phone = phoneNum
                newUser.password = pass
                val db = DatabaseHelper(context)
                db.insertUser(newUser)
                val intent3 = Intent(this@RegisterPage, LoginPage::class.java)
                startActivity(intent3)
            }
        }
    }
}