package com.fcfm.poi.pia

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import com.fcfm.poi.pia.adaptadores.ChatAdapter
import com.fcfm.poi.pia.modelos.Imagen
import com.fcfm.poi.pia.modelos.Mensaje
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_login.*

class ChatActivity : AppCompatActivity() {

    private val listaMensajes = mutableListOf<Mensaje>();
    private val adaptador = ChatAdapter(listaMensajes);
    private lateinit var nombreUsuario: String;
    private lateinit var chatroomId : String;
    private lateinit var filepath : Uri;
    private val database  = FirebaseDatabase.getInstance();
    private val chatroomRef = database.getReference("chatrooms");
    private lateinit var chatRef : DatabaseReference;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        nombreUsuario = intent.getStringExtra("nombreUsuario") ?: "sin_nombre";

        chatroomId  = intent.getStringExtra("chatroomId") ?: "";

        if(chatroomId.isEmpty()){
            finish();
        }

        chatRef = chatroomRef.child(chatroomId).child("chats");

        nombreUChat.text = nombreUsuario;

        rvChat.adapter = adaptador;

        btnEnviar.setOnClickListener {

            val mensaje = txtMensaje.text.toString()
            if (mensaje.isNotEmpty()) {

                txtMensaje.text.clear()

                enviarMensaje(Mensaje("", mensaje, nombreUsuario, ServerValue.TIMESTAMP))
            }
        }

        tarea.setOnClickListener {
            //val intentLogin = Intent(this,registerActivity::class.java)
            val intent = Intent(this, CreacionTareas::class.java)
            startActivity(intent)
        }

        goBack.setOnClickListener {
            //val intentLogin = Intent(this,registerActivity::class.java)
            finish();
            //val intent = Intent(this, dashBoardActivity::class.java)
            //startActivity(intent)
        }

        imageView.setOnClickListener {
            startFileChooser()
            uploadFile()
        }

        recibirMensajes()
    }

    private fun startFileChooser()
    {
        var i = Intent()
        i.setType("image/*")
        i.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(i, "Choose picture"), 111)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==111 && resultCode == Activity.RESULT_OK && data != null )
        {
            filepath =  data.data!!
            var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filepath)
            enviarFoto(Imagen("", bitmap))
            imageView.setImageBitmap(bitmap)
        }
    }

    private fun uploadFile()
    {
        if(filepath!=null)
        {
            var pd=ProgressDialog(this);
            pd.setTitle("Uploading")
            pd.show()

            var imageRef = FirebaseStorage.getInstance().reference.child("chatroom/"+chatRef+"pic.jpg")
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

    private fun enviarFoto(imagen: Imagen) {

        val mensajeFireBase = chatRef.push()
        imagen.id = mensajeFireBase.key ?: ""

        mensajeFireBase.setValue(imagen)
    }

    private fun enviarMensaje(mensaje: Mensaje) {

        val mensajeFireBase = chatRef.push()
        mensaje.id = mensajeFireBase.key ?: ""

        mensajeFireBase.setValue(mensaje)
    }

    private fun recibirMensajes() {
        chatRef.addValueEventListener(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                listaMensajes.clear();
                for (snap in snapshot.children)
                {
                    val mensaje: Mensaje = snap.getValue(Mensaje::class.java) as Mensaje;

                    if(mensaje.de == nombreUsuario){
                        mensaje.esMio = true;
                    }

                    listaMensajes.add(mensaje);
                }

                if(listaMensajes.size > 0) {
                    adaptador.notifyDataSetChanged();
                }
            }

            override fun onCancelled(error: DatabaseError) {


            }
        })
    }
}