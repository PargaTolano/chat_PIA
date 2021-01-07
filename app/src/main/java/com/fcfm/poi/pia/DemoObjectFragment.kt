package com.fcfm.poi.pia

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.poi.pia.adaptadores.AssignmentAdapter
import com.fcfm.poi.pia.adaptadores.ChatroomAdapter
import com.fcfm.poi.pia.adaptadores.UsuarioCardAdapter
import com.fcfm.poi.pia.enums.ChatroomType
import com.fcfm.poi.pia.modelos.Assignment
import com.fcfm.poi.pia.modelos.Chatroom
import com.fcfm.poi.pia.modelos.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_demo_object.*

/**
 * A simple [Fragment] subclass.
 */
class DemoObjectFragment(private val activity : dashBoardActivity) : Fragment() {

    private val firebaseAuth = FirebaseAuth.getInstance();
    private val currUser = firebaseAuth.currentUser!!;
    private val db  = FirebaseDatabase.getInstance()

    private val chatRoomRef     = db.getReference("chatrooms")
    private val userRef         = db.getReference("users")
    private val assignmentRef   = db.getReference("assignments")

    private val chatroomList = mutableListOf<Chatroom>();
    private val assignmentList = mutableListOf<Assignment>();
    private val userList = mutableListOf<Usuario>();

    private val chatroomAdapter   = ChatroomAdapter     (chatroomList);
    private val assignmentAdapter = AssignmentAdapter   (assignmentList);

    companion object{
        private  const val ARG_OBJECT ="object"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_demo_object, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Esto es un get si contiene el argumento tal
        arguments?.takeIf { it.containsKey(ARG_OBJECT)}?.apply {

            val tv_nombre: TextView = view.findViewById(R.id.tvFragmentTitle);
            val rv_view:RecyclerView = view.findViewById(R.id.rvDash);

            when(this[ARG_OBJECT]){
                1->{
                    tv_nombre.text = "Grupos";
                    rv_view.adapter = chatroomAdapter;
                }
                2->{
                    tv_nombre.text = "Tareas";
                    rv_view.adapter = assignmentAdapter;
                }
                3->{
                    tv_nombre.text = "Chats";
                    rv_view.adapter = UsuarioCardAdapter( activity.users.filter { it.uid != currUser.uid }.toMutableList(), activity)
                        .apply { initializeUserChatroomRef() };
                }
            }
        }
    }

    fun recibirRegistrosChatoom() {

        chatRoomRef.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                chatroomList.clear()
                for (snap in snapshot.children)
                {
                    val chatroom: Chatroom = snap.getValue(Chatroom::class.java) as Chatroom

                    if(chatroom.type == ChatroomType.Group){
                        chatroomList.add(chatroom)
                    }
                }

                if(chatroomList.size > 0) {
                    chatroomAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun recibirRegistrosUsuarios(){
        userRef.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear();
                for (snap in snapshot.children)
                {
                    val user: Usuario = snap.getValue(Usuario::class.java) as Usuario;

                    if(user.uid != FirebaseAuth.getInstance().currentUser!!.uid)
                        userList.add(user);
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun recibirRegistrosTareas(){
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