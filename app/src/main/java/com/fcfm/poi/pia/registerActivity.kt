package com.fcfm.poi.pia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_register.*

class registerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btnRegistrarse.setOnClickListener {
            val intentRegistro = Intent(this, loginActivity::class.java)
            startActivity(intentRegistro)
        }
    }
}