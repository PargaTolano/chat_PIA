package com.fcfm.poi.pia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TableLayout
import android.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.fcfm.poi.pia.adaptadores.ChatroomAdapter
import com.fcfm.poi.pia.adaptadores.ViewPagerAdapter
import com.fcfm.poi.pia.modelos.Chatroom
import com.fcfm.poi.pia.modelos.Mensaje
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_dash_board.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_demo_object.*

class dashBoardActivity : AppCompatActivity() {

    private val chatroomList = mutableListOf<Chatroom>()
    private val adaptador : ChatroomAdapter = ChatroomAdapter(chatroomList)
    private val adaptadorUsuarios :
    private lateinit var nombreUsuario: String
    private val database  = FirebaseDatabase.getInstance()
    private val chatRoomRef = database.getReference("chatrooms")
    private val adapter by lazy { ViewPagerAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board)

        val pagerMain =  findViewById<ViewPager2>(R.id.pager)
        pagerMain.adapter =  this.adapter

        val tab_layoutMain = findViewById<TabLayout>(R.id.tab_layout)

        val tabLayoutMediator = TabLayoutMediator(tab_layoutMain, pagerMain, TabLayoutMediator.TabConfigurationStrategy { tab, position ->
            when(position){
                0->{
                    tab.text="Grupos"
                    tab.setIcon(R.drawable.ic_group)
                }
                1->{
                    tab.text="Tareas"
                    tab.setIcon(R.drawable.ic_task)
                }
                2->{
                    tab.text="Chats"
                    tab.setIcon(R.drawable.ic_chat)


                }
            }
        })
        tabLayoutMediator.attach()

        nombreUsuario = intent.getStringExtra("nombreUsuario") ?: "sin_nombre"

        tv_nameUser.text = nombreUsuario

        //rvDash.adapter = adaptador

        /*btnCrearGrupo.setOnClickListener {
            //val intentLogin = Intent(this,registerActivity::class.java)
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("nombreUsuario", nombreUsuario)
            startActivity(intent)
        }*/

        btnTareas.setOnClickListener {
            val intent = Intent(this, CreacionTareas::class.java);
            startActivity(intent);
        }


        btnCrearGrupo.setOnClickListener{
            val intent = Intent(this, CrearGrupoActivity::class.java);
            startActivity(intent);
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
