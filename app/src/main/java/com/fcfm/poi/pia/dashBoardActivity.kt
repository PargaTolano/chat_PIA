package com.fcfm.poi.pia

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

class dashBoardActivity : AppCompatActivity() {

    private val chatroomList = mutableListOf<Chatroom>()
    private val adaptador : ChatroomAdapter = ChatroomAdapter(chatroomList)
    private val database  = FirebaseDatabase.getInstance()
    private val chatRoomRef = database.getReference("chatrooms")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board)

        rv_chats.adapter = adaptador
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
