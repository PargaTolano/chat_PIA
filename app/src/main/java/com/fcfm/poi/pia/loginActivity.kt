package com.fcfm.poi.pia

import android.R.attr.password
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*


class loginActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firebaseAuth = FirebaseAuth.getInstance()

        btnIniciar.setOnClickListener {
            autenticarLogin()
        }

        viewRegistrate.setOnClickListener {
            //val intentLogin = Intent(this,registerActivity::class.java)
            val intent = Intent(this, CreacionTareas::class.java)
            startActivity(intent)
        }
    }

    private fun revisarAutenticacion()
    {
        if(firebaseAuth.currentUser != null)
        {
            //Abrimos activity de tareas
            val intentChat = Intent(this, ChatActivity::class.java)
            intentChat.putExtra("nombreUsuario", txtUsuario.text.toString())

            startActivity(intentChat)
        }
    }

    private fun autenticarLogin()
    {
        val correo = txtUsuario.text.toString()
        val contrasena = txtPassword.text.toString()

        if(correo.isEmpty())
            mostrarMensaje("Falta Correo!!")
        else if(contrasena.isEmpty())
            mostrarMensaje("Falta Contrasena!!")
        else
        {
            firebaseAuth.signInWithEmailAndPassword(correo, contrasena)
                .addOnCompleteListener(this,
                    OnCompleteListener<AuthResult?> { task ->
                        if (task.isSuccessful) {
                            revisarAutenticacion()
                        } else {
                            mostrarMensaje("No se pudo iniciar sesion!!")
                        }
                    })
        }
    }

    private fun mostrarMensaje(msj: String)
    {
        Toast.makeText(this, msj, Toast.LENGTH_SHORT).show()
    }
}