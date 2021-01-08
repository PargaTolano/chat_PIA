package com.fcfm.poi.pia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fcfm.poi.pia.adaptadores.ChatAdapter
import com.fcfm.poi.pia.adaptadores.IntegranteAdapter
import com.fcfm.poi.pia.enums.ChatroomType
import com.fcfm.poi.pia.modelos.Chatroom
import com.fcfm.poi.pia.modelos.Integrante
import com.fcfm.poi.pia.modelos.Mensaje
import com.fcfm.poi.pia.modelos.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_crear_grupo.*

class CrearGrupoActivity : AppCompatActivity() {

    private lateinit var firebaseAuth : FirebaseAuth;

    val listaIntegrantes = mutableListOf<Usuario>();
    val mapaIntegrantes  = mutableMapOf<String,String>();
    private val adaptador = IntegranteAdapter(listaIntegrantes,this);

    private val db = FirebaseDatabase.getInstance();
    private val currUser = FirebaseAuth.getInstance().currentUser;

    private val userRef = db.getReference("users");
    private val chatroomsRef = db.getReference("chatrooms");

    var integrantes : MutableList<Usuario> = mutableListOf();
    var usuarios = mutableListOf<String>();
    var correos = mutableListOf<String>();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_grupo);

        mapaIntegrantes[currUser!!.uid] = currUser!!.email!!;

        rv_integrantes.adapter = adaptador;

        firebaseAuth = FirebaseAuth.getInstance();

        initUsers();

        crearGrupoBTN.setOnClickListener{
            if(grupoNombre.text.isNotEmpty() && grupoNombre.text.isNotBlank() && integrantes.size > 1){
                llenarMapa();
                createGroupChatroom();
                finish();
            }
        }
    }

    private fun createGroupChatroom(){
        val newChatroom = chatroomsRef.push();

        val chatroomInfo = Chatroom(
            newChatroom.key!!,
            mapaIntegrantes,
            grupoNombre.text.toString(),
            ChatroomType.Group
        );

        newChatroom.setValue(chatroomInfo);

        addRoomToUsers(integrantes, chatroomInfo);
    }

    private fun addRoomToUsers(userList : List<Usuario>, chatroom: Chatroom){
        for(user in userList){
            val userChatsRef =userRef.child("${user.uid}/chatrooms");

            val newChatroom = userChatsRef.push();
            newChatroom.setValue(chatroom);
        }
    }

    private fun llenarMapa(){
        for( i in 0..(integrantes.size-1)){
            mapaIntegrantes[integrantes[i].uid] = integrantes[i].email;
        }
    }

    private fun initUsers() {

        userRef.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                //TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                listaIntegrantes.clear();
                for(snap in snapshot.children){

                    val user : Usuario = snap.getValue(Usuario::class.java) as Usuario;

                    if (firebaseAuth.currentUser?.uid!! != user.uid){
                        listaIntegrantes.add(user);
                    }
                }

                if(listaIntegrantes.size > 0) {
                    adaptador.notifyDataSetChanged();
                    rv_integrantes.adapter;
                }
            }
        })
    }
}