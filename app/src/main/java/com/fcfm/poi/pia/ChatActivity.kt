package com.fcfm.poi.pia

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.media.AudioManager
import android.media.ToneGenerator
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fcfm.poi.pia.adaptadores.ChatAdapter
import com.fcfm.poi.pia.modelos.Mensaje
import com.fcfm.poi.pia.utils.MessageEncrypter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_chat.*
import java.io.ByteArrayOutputStream

class ChatActivity : AppCompatActivity() {

    private var usersInChat = mutableListOf<String>();
    private var emailsInChat = mutableListOf<String>();
    private val listaMensajes = mutableListOf<Mensaje>();
    private lateinit var nombreUsuario: String;
    private lateinit var chatroomId : String;
    private lateinit var adaptador: ChatAdapter;
    private var filepath : Uri = Uri.EMPTY;
    private var file : ByteArray? = null;
    private val database  = FirebaseDatabase.getInstance();
    private val chatroomRef = database.getReference("chatrooms");
    private lateinit var chatRef : DatabaseReference;
    private val currUser = FirebaseAuth.getInstance().currentUser;
    private val encrypter = MessageEncrypter("myPass");
    private lateinit var currentChatroomRef : DatabaseReference;
    private lateinit var usersInChatRef : DatabaseReference;


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

        currentChatroomRef = chatroomRef.child(chatroomId).apply {
            addValueEventListener(object: ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    System.out.println("Chat Activity : error on db connection");
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    //userMap.clear()
                    //for(snap in snapshot.children){
                    //    val p : Pair<String, String> = snap.getValue(Pair::class.java) as Pair<String,String>;
                    //    userMap[p.first] = p.second;
                    //}
                }

            })
        };
        chatRef = currentChatroomRef.child("chat");
        usersInChatRef = currentChatroomRef.child("participantes").apply {
            addValueEventListener(object: ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    System.out.println("Chat Activity : error on db connection");
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    emailsInChat.clear();
                    val temp  =  snapshot.getValue() as MutableMap<String,String>;
                    usersInChat = temp!!.keys.toMutableList();
                    emailsInChat = temp!!.values.toMutableList();
                }
            });
        }

        chatRef = chatroomRef.child("${chatroomId}/chat");

        nombreUChat.text = nombreUsuario

        rvChat.adapter = adaptador

        btnEnviar.setOnClickListener {

            val mensaje = txtMensaje.text.toString()
            if (mensaje.isNotEmpty()) {

                txtMensaje.text.clear()

                if(filepath != Uri.EMPTY)
                {
                    enviarMensaje(Mensaje("", encrypter.encrypt(mensaje.toByteArray()), file, currUser?.email!! , ServerValue.TIMESTAMP))
                }
                else{
                    enviarMensaje(Mensaje("", encrypter.encrypt(mensaje.toByteArray()), null, currUser?.email!! , ServerValue.TIMESTAMP))
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
            finish();
        }

        imageView.setOnClickListener {
            startFileChooser();
        }

        emailUser.setOnClickListener{
            val toneGen1 = ToneGenerator(AudioManager.STREAM_MUSIC, 100);
            toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);

            val intent = Intent(this, activity_correo::class.java).apply {
                val emails : Array<String> = emailsInChat.filter { it != currUser!!.email }.toTypedArray();
                putExtra("usuarios", emails);
            };
            startActivity(intent)
        }

        tarea.setOnClickListener{

            val intent = Intent(this, CreacionTareas::class.java).apply {
                putExtra("chatroomId", chatroomId);
                putExtra("usuarios", usersInChat.filter { it != currUser!!.uid }.toTypedArray());
            }
            startActivity(intent);
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
        mensaje.id = mensajeFireBase.key ?: "";

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

                    mensaje.contenido = encrypter.decrypt(mensaje.contenido);
                    /*System.out.println("Mensaje Decriptado en Chat : " + mensaje.contenido);*/

                    listaMensajes.add(mensaje);
                }

                if(listaMensajes.size > 0) {
                    adaptador.notifyDataSetChanged()

                }
            }

            override fun onCancelled(error: DatabaseError) {
                System.out.println("CHAT ACTIVITY : ERROR DATABASE MENSAJE LISTENER");
            }
        })
    }
}