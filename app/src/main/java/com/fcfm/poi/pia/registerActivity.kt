package com.fcfm.poi.pia

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import java.net.URI
import java.util.*

class registerActivity : AppCompatActivity() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    lateinit var  filepath: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btnRegistrarse.setOnClickListener {
            autenticarSign()
            uploadFile()
        }

        btnFoto.setOnClickListener{
            startFileChooser()
        }
    }

    private fun startFileChooser(){
        var i = Intent()
        i.setType("image/*")
        i.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(i,"Escoge una imagen"),111)
    }

    private fun uploadFile(){
        //Para subir archivos
        if(filepath!=null){
            var pd = ProgressDialog(this)
            pd.setTitle("Subiendo")
            pd.show()

            var imageRef= FirebaseStorage.getInstance().reference.child("images/foto")
            imageRef.putFile(filepath)
                .addOnSuccessListener{p0 ->
                    pd.dismiss()
                    Toast.makeText(applicationContext,"Archivo subido",Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener{p0 ->
                    pd.dismiss()
                    Toast.makeText(applicationContext,p0.message,Toast.LENGTH_LONG ).show()
                }
                .addOnProgressListener{p0 ->
                    var progress = (100.0*p0.bytesTransferred) / p0.totalByteCount
                    pd.setMessage("Subiendo ${progress.toInt()}%)")
                }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==111 && resultCode== Activity.RESULT_OK && data!= null){
            filepath = data.data!!
            var  bitmap = MediaStore.Images.Media.getBitmap(contentResolver,filepath)
            imgProfileR.setImageBitmap(bitmap)
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

                            firebaseAuth.currentUser?.updateProfile(profileUpdates)
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