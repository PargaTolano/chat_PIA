package com.fcfm.poi.pia

import android.content.Intent
import android.os.Bundle
import android.view.ActionMode
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentViewHolder
import androidx.viewpager2.widget.ViewPager2
import com.fcfm.poi.pia.adaptadores.AssignmentAdapter
import com.fcfm.poi.pia.adaptadores.ChatroomAdapter
import com.fcfm.poi.pia.adaptadores.UsuarioCardAdapter
import com.fcfm.poi.pia.adaptadores.ViewPagerAdapter
import com.fcfm.poi.pia.enums.UserConectionState
import com.fcfm.poi.pia.modelos.Assignment
import com.fcfm.poi.pia.modelos.Chatroom
import com.fcfm.poi.pia.modelos.Usuario
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_dash_board.*
import kotlinx.android.synthetic.main.fragment_demo_object.*
import kotlinx.android.synthetic.main.fragment_demo_object.view.*

class dashBoardActivity : AppCompatActivity() {


    //private val adaptadorUsuarios :
    private lateinit var nombreUsuario: String

    private val adapter by lazy { ViewPagerAdapter(this) }

    private lateinit var rv_dash : RecyclerView;

    private lateinit var pagerMain : ViewPager2;

    private val firbaseAuth = FirebaseAuth.getInstance();

    private val currUser = FirebaseAuth.getInstance().currentUser;

    private val userRef = FirebaseDatabase.getInstance().getReference("users").apply {
        addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                users.clear();
                for(snap in snapshot.children){
                    val user = snap.getValue(Usuario::class.java) as Usuario;
                    users.add(user);
                }
            }

        })
    }
    private  val users = mutableListOf<Usuario>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board)

        val pagerMain =  findViewById<ViewPager2>(R.id.pager)
        pagerMain.adapter =  this.adapter;

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
        tabLayoutMediator.attach();

        nombreUsuario = intent.getStringExtra("nombreUsuario") ?: "sin_nombre";

        tv_nameUser.text = nombreUsuario;

        btnTareas.setOnClickListener {
            val intent = Intent(this, CreacionTareas::class.java);
            startActivity(intent);
        }

        btnCrearGrupo.setOnClickListener{
            val intent = Intent(this, CrearGrupoActivity::class.java);
            startActivity(intent);
        }
    }
}
