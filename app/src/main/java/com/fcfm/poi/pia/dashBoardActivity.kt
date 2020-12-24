package com.fcfm.poi.pia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TableLayout
import android.widget.Toast
import android.widget.Toolbar
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.fcfm.poi.pia.adaptadores.AssignmentAdapter
import com.fcfm.poi.pia.adaptadores.ChatroomAdapter
import com.fcfm.poi.pia.adaptadores.UsuarioCardAdapter
import com.fcfm.poi.pia.adaptadores.ViewPagerAdapter
import com.fcfm.poi.pia.modelos.Assignment
import com.fcfm.poi.pia.modelos.Chatroom
import com.fcfm.poi.pia.modelos.Mensaje
import com.fcfm.poi.pia.modelos.Usuario
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_dash_board.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_demo_object.*

class dashBoardActivity : AppCompatActivity() {


    //private val adaptadorUsuarios :
    private lateinit var nombreUsuario: String
    private val database  = FirebaseDatabase.getInstance()
    private val chatRoomRef = database.getReference("chatrooms")
    private val userRef = database.getReference("users")
    private val assignmentRef = database.getReference("assignments")
    private val adapter by lazy { ViewPagerAdapter(this) }

    private val chatroomList = mutableListOf<Chatroom>();
    private val assignmentList = mutableListOf<Assignment>();
    private val userList = mutableListOf<Usuario>();

    private val chatroomAdapter   = ChatroomAdapter     (chatroomList);
    private val assignmentAdapter = AssignmentAdapter   (assignmentList);
    private val userAdapter       = UsuarioCardAdapter  (userList);

    private lateinit var rv_dash : RecyclerView;

    private lateinit var pagerMain : ViewPager2;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board)

        val pagerMain =  findViewById<ViewPager2>(R.id.pager)
        pagerMain.adapter =  this.adapter;

        val childrenList = pagerMain.children.toList();

        rv_dash = childrenList[0] as RecyclerView;

        userAdapter.initializeUserChatroomRef();

        val tab_layoutMain = findViewById<TabLayout>(R.id.tab_layout)

        val tabLayoutMediator = TabLayoutMediator(tab_layoutMain, pagerMain, TabLayoutMediator.TabConfigurationStrategy { tab, position ->
            when(position){
                0->{
                    tab.text="Grupos"
                    tab.setIcon(R.drawable.ic_group)

                    //adaptador de grupos
                    rv_dash.adapter = chatroomAdapter;
                }
                1->{
                    tab.text="Tareas"
                    tab.setIcon(R.drawable.ic_task)

                    //adaptador de tareas
                    rv_dash.adapter = assignmentAdapter;
                }
                2->{
                    tab.text="Chats"
                    tab.setIcon(R.drawable.ic_chat)

                    //adaptador de chats
                    rv_dash.adapter = userAdapter;
                }
            }
        })
        tabLayoutMediator.attach()

        nombreUsuario = intent.getStringExtra("nombreUsuario") ?: "sin_nombre"

        tv_nameUser.text = nombreUsuario

        btnChatDemo.setOnClickListener{
            Toast.makeText(this, "Clickeado prro", Toast.LENGTH_SHORT).show();
            val intent = Intent( this, ChatActivity::class.java);
            startActivity(intent);
        }

        btnTareas.setOnClickListener {
            val intent = Intent(this, CreacionTareas::class.java);
            startActivity(intent);
        }

        btnCrearGrupo.setOnClickListener{
            val intent = Intent(this, CrearGrupoActivity::class.java);
            startActivity(intent);
        }

        recibirRegistrosChatoom();
        recibirRegistrosUsuarios();
        recibirRegistrosTareas();
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
                    chatroomAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun recibirRegistrosUsuarios(){
        userRef.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear();
                for (snap in snapshot.children)
                {
                    val user: Usuario = snap.getValue(Usuario::class.java) as Usuario;

                    if(user.uid != FirebaseAuth.getInstance().currentUser!!.uid)
                        userList.add(user);
                }

                if(userList.size > 0) {
                    userAdapter.notifyDataSetChanged();
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun recibirRegistrosTareas(){
        assignmentRef.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                assignmentList.clear()
                for (snap in snapshot.children)
                {
                    val assignment: Assignment = snap.getValue(Assignment::class.java) as Assignment

                    assignmentList.add(assignment)
                }

                if(assignmentList.size > 0) {
                    assignmentAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}
