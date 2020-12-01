package com.fcfm.poi.pia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toolbar
import com.fcfm.poi.pia.adaptadores.ChatroomAdapter
import com.fcfm.poi.pia.modelos.Chatroom
import com.fcfm.poi.pia.modelos.Mensaje
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_dash_board.*
import kotlinx.android.synthetic.main.activity_login.*

class dashBoardActivity : AppCompatActivity() {

    private val chatroomList = mutableListOf<Chatroom>()
    private val adaptador : ChatroomAdapter = ChatroomAdapter(chatroomList)
    private lateinit var nombreUsuario: String
    private val database  = FirebaseDatabase.getInstance()
    private val chatRoomRef = database.getReference("chatrooms")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board)

        nombreUsuario = intent.getStringExtra("nombreUsuario") ?: "sin_nombre"

        tv_nameUser.text = nombreUsuario

        rv_chats.adapter = adaptador

        btnCrearGrupo.setOnClickListener {
            //val intentLogin = Intent(this,registerActivity::class.java)
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("nombreUsuario", nombreUsuario)
            startActivity(intent)
        }

        btnTareas.setOnClickListener {
            //val intentLogin = Intent(this,registerActivity::class.java)
            val intent = Intent(this, CreacionTareas::class.java)
            startActivity(intent)
        }

        recibirRegistrosChatoom()
    }

    private fun recibirRegistrosChatoom() {

        chatRoomRef.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                chatroomList.clear()
                for (snap in snapshot.children)
                {
                    val chatroom: Chatroom = snap.getValue(Chatroom::class.java) as Chatroom

                    chatroomList.add(chatroom)
                }

                if(chatroomList.size > 0) {
                    adaptador.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}
