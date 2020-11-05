package com.fcfm.poi.pia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.txtUsuario
import kotlinx.android.synthetic.main.activity_main.*

class loginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnIniciar.setOnClickListener {

            val nombreUsuario = txtUsuario.text.toString()

            if (nombreUsuario.isEmpty()) {

                Toast.makeText(this, "Falta usuario", Toast.LENGTH_SHORT).show()
            } else {

                val intentChat = Intent(this, ChatActivity::class.java)
                intentChat.putExtra("nombreUsuario", nombreUsuario)

                startActivity(intentChat)
            }
        }

        viewRegistrate.setOnClickListener {
           val intentLogin = Intent(this,registerActivity::class.java)
            startActivity(intentLogin)
        }
    }
}