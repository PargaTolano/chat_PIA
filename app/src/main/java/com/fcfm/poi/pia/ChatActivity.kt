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
import com.fcfm.poi.pia.utils.MessageEncrypter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_login.*
import java.net.URI
import kotlin.system.measureNanoTime

class ChatActivity : AppCompatActivity() {

    private val listaMensajes = mutableListOf<Mensaje>();
    private val adaptador = ChatAdapter(listaMensajes);
    private lateinit var nombreUsuario: String;
    private lateinit var chatroomId : String;
    private var filepath : Uri = Uri.EMPTY;
    private val database  = FirebaseDatabase.getInstance();
    private val chatroomRef = database.getReference("chatrooms");
    private lateinit var chatRef : DatabaseReference;

    private val currUser = FirebaseAuth.getInstance().currentUser;
    private val encrypter = MessageEncrypter("myPass");


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        nombreUsuario = intent.getStringExtra("nombreUsuario") ?: "sin_nombre";

        chatroomId  = intent.getStringExtra("chatroomId") ?: "";

        if(chatroomId.isEmpty()){
            finish();
        }

        chatRef = chatroomRef.child("${chatroomId}/chat");

        nombreUChat.text = nombreUsuario;

        rvChat.adapter = adaptador;

        btnEnviar.setOnClickListener {

            val mensaje = txtMensaje.text.toString()
            if (mensaje.isNotEmpty()) {

                txtMensaje.text.clear()

                enviarMensaje(Mensaje("", encrypter.encrypt(mensaje.toByteArray()), currUser?.email!! , ServerValue.TIMESTAMP));
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
        if(filepath!=Uri.EMPTY)
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
        mensaje.id = mensajeFireBase.key ?: "";

        mensajeFireBase.setValue(mensaje)
    }

    private fun recibirMensajes() {
        chatRef.addValueEventListener(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                listaMensajes.clear();
                for (snap in snapshot.children)
                {
                    val mensaje: Mensaje = snap.getValue(Mensaje::class.java) as Mensaje;

                    if(mensaje.de == currUser?.email!!){
                        mensaje.esMio = true;
                    }

                    mensaje.contenido = encrypter.decrypt(mensaje.contenido);
                    System.out.println("Mensaje Decriptado en Chat : " + mensaje.contenido);

                    listaMensajes.add(mensaje);
                }

                if(listaMensajes.size > 0) {
                    adaptador.notifyDataSetChanged();
                }
            }

            override fun onCancelled(error: DatabaseError) {
                System.out.println("CHAT ACTIVITY : ERROR DATABASE MENSAJE LISTENER");
            }
        })
    }
}