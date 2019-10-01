package com.brounie.mijuphotos.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.brounie.mijuphotos.Auxiliaries.isEmailValid
import com.brounie.mijuphotos.Auxiliaries.shortToast
import com.brounie.mijuphotos.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var c = 0
        main_icon.setOnClickListener {
            c ++
            if(c >= 10){
                shortToast("La version es 1.3", applicationContext)
            }
        }

        main_next_btn.setOnClickListener {
            val mail = main_edittext.text.toString()
            if (mail == "") {
                shortToast("Please enter an email", this)
                return@setOnClickListener
            }

            // check if text is an email
            if (!isEmailValid(mail)) {
                shortToast("Please enter a valid email", this)
                main_edittext.text.clear()
                return@setOnClickListener
            }

            val intent = Intent(this, AddPhoto::class.java)
            intent.putExtra("email", mail)
            startActivity(intent)

        }

    }
}
