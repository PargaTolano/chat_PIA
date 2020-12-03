package com.fcfm.poi.pia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class registerActivity : AppCompatActivity() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        print(firebaseAuth.toString())

        btnRegistrarse.setOnClickListener {
            autenticarSign()
        }
    }

    private fun revisarAutenticacion()
    {
        if(firebaseAuth.currentUser != null)
        {
            //Abrimos activity de tareas
            val intentChat = Intent(this, ChatActivity::class.java)
            intentChat.putExtra("nombreUsuario", txtUser.text.toString())

            startActivity(intentChat)
        }
    }

    private fun autenticarSign()
    {
        val correo = txtUser.text.toString()
        val contrasena = txtPass.text.toString()

        if(correo.isEmpty())
            mostrarMensaje("Falta Correo!!")
        else if(contrasena.isEmpty())
            mostrarMensaje("Falta Contrasena!!")
        else
        {
            firebaseAuth.createUserWithEmailAndPassword(correo, contrasena)
                .addOnCompleteListener(this,
                    OnCompleteListener<AuthResult?> { task ->

                        if(task.isSuccessful){ //Se creo correctamente la cuenta
                            var nickname = txtUser.text.toString()

                            if(nickname.isEmpty())
                            {
                                nickname = "user-${Date().toString()}"
                            }

                            val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(nickname).build()

                            FirebaseAuth.getInstance().currentUser?.updateProfile(profileUpdates)
                                ?.addOnCompleteListener { task2 ->
                                    if(!task2.isSuccessful)
                                    {
                                        mostrarMensaje("No se pudo asignar un nickname")
                                    }
                                    revisarAutenticacion()
                                }
                        }
                        else //Hubo error al crear la cuenta
                        {
                            mostrarMensaje("Error al crear cuenta")
                        }
                    })
        }
    }

    private fun mostrarMensaje(msj: String)
    {
        Toast.makeText(this, msj, Toast.LENGTH_SHORT).show()
    }
}