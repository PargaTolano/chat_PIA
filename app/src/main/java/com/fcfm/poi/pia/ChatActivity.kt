package com.fcfm.poi.pia

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import com.fcfm.poi.pia.adaptadores.ChatAdapter
import com.fcfm.poi.pia.modelos.Imagen
import com.fcfm.poi.pia.modelos.Mensaje
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_chat.*
import java.io.ByteArrayOutputStream

class ChatActivity : AppCompatActivity() {

    private val listaMensajes = mutableListOf<Mensaje>()
    private lateinit var nombreUsuario: String
    private lateinit var chatroomId : String
    private lateinit var adaptador: ChatAdapter
    private var filepath : Uri = Uri.EMPTY
    private var file : ByteArray? = null
    private val database  = FirebaseDatabase.getInstance()
    private val chatroomRef = database.getReference("chatrooms")
    private lateinit var chatRef : DatabaseReference

    private val currUser = FirebaseAuth.getInstance().currentUser


    companion object{
        //image pick code
        private val IMAGE_PICK_CODE = 1000
        //Permission code
        private val PERMISSION_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        nombreUsuario = intent.getStringExtra("nombreUsuario") ?: "sin_nombre"

        chatroomId  = intent.getStringExtra("chatroomId") ?: ""

        adaptador = ChatAdapter(listaMensajes, chatroomId)

        if(chatroomId.isEmpty()){
            finish()
        }

        chatRef = chatroomRef.child("${chatroomId}/chat")

        nombreUChat.text = nombreUsuario

        rvChat.adapter = adaptador

        btnEnviar.setOnClickListener {

            val mensaje = txtMensaje.text.toString()
            if (mensaje.isNotEmpty()) {

                txtMensaje.text.clear()

                if(filepath != Uri.EMPTY)
                {
                    enviarMensaje(Mensaje("", mensaje, file, currUser?.email!! , ServerValue.TIMESTAMP))
                }
                else{
                    enviarMensaje(Mensaje("", mensaje, null, currUser?.email!! , ServerValue.TIMESTAMP))
                }
                filepath = Uri.EMPTY
            }
        }

        tarea.setOnClickListener {
            //val intentLogin = Intent(this,registerActivity::class.java)
            val intent = Intent(this, CreacionTareas::class.java)
            startActivity(intent)
        }

        goBack.setOnClickListener {
            //val intentLogin = Intent(this,registerActivity::class.java)
            finish()
            //val intent = Intent(this, dashBoardActivity::class.java)
            //startActivity(intent)
        }

        imageView.setOnClickListener {
            startFileChooser()
            //uploadFile()
        }

        listaMensajes.clear()
        recibirMensajes()
    }

    private fun startFileChooser()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            var boolDo:Boolean =  false
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED){
                //permission denied
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                //show popup to request runtime permission
                requestPermissions(permissions, PERMISSION_CODE)
            }
            else{
                //permission already granted
                boolDo =  true

            }


            if(boolDo == true){
                pickImageFromGallery()
            }

        }else{
            pickImageFromGallery()
        }
    }


    private fun pickImageFromGallery()
    {

        val intent  =  Intent()
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.type = "image/*"
        startActivityForResult(Intent.createChooser(intent,"Selecciona"),
            ChatActivity.IMAGE_PICK_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==ChatActivity.IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK && data != null )
        {

            filepath= data.data!!
            var  photo = MediaStore.Images.Media.getBitmap(contentResolver,filepath)
            //val photo: Bitmap? =  data?.extras?.get("data") as Bitmap
            val stream = ByteArrayOutputStream()
            //Bitmap.CompressFormat agregar el formato desado, estoy usando aqui jpeg
            photo?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            file =  stream.toByteArray()
        }
    }

    private fun uploadFile(id: String)
    {
        if(filepath!=Uri.EMPTY)
        {
            var pd=ProgressDialog(this);
            pd.setTitle("Uploading")
            pd.show()

            var imageRef = FirebaseStorage.getInstance().reference.child("chatroom/"+chatRef+"/"+id+"pic.jpg")
            imageRef.putFile(filepath)
                .addOnSuccessListener { p0 ->
                    pd.dismiss()
                    Toast.makeText(applicationContext, "File Upload", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener{ p0 ->
                    pd.dismiss()
                    Toast.makeText(applicationContext, p0.message, Toast.LENGTH_LONG).show()
                }
                .addOnProgressListener { p0 ->
                    var progress: Double = (100.0 * p0.bytesTransferred)/p0.totalByteCount
                    pd.setMessage("Uploaded ${progress.toInt()}%")
                }
        }
    }

    private fun enviarMensaje(mensaje: Mensaje) {

        val mensajeFireBase = chatRef.push()
        mensaje.id = mensajeFireBase.key ?: ""

        uploadFile(mensaje.id)

        mensajeFireBase.setValue(mensaje)

        listaMensajes.clear()
    }

    private fun recibirMensajes() {
        chatRef.addValueEventListener(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                listaMensajes.clear()

                for (snap in snapshot.children)
                {
                    val mensaje: Mensaje = snap.getValue(Mensaje::class.java) as Mensaje

                    if(mensaje.de == currUser?.email!!){
                        mensaje.esMio = true
                    }

                    listaMensajes.add(mensaje)
                }

                if(listaMensajes.size > 0) {
                    adaptador.notifyDataSetChanged()

                }
            }

            override fun onCancelled(error: DatabaseError) {


            }
        })
    }
}