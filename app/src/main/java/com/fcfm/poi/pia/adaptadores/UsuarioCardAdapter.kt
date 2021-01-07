package com.fcfm.poi.pia.adaptadores

import android.R.color
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.poi.pia.ChatActivity
import com.fcfm.poi.pia.R
import com.fcfm.poi.pia.enums.ChatroomType
import com.fcfm.poi.pia.enums.UserConectionState
import com.fcfm.poi.pia.modelos.Chatroom
import com.fcfm.poi.pia.modelos.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.custom_item_usercard.view.*


class UsuarioCardAdapter(private val userList : MutableList<Usuario>): RecyclerView.Adapter<UsuarioCardAdapter.UsuarioCardViewHolder>() {

    private val firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance();
    private val db = FirebaseDatabase.getInstance();
    private val chatroomRef = db.getReference("chatrooms");
    private val userRef     = db.getReference("users");
    private lateinit var otherRef : DatabaseReference;
    private lateinit var userChatroomRef : DatabaseReference;
    private lateinit var otherUserChatroomRef : DatabaseReference;

    private val chatroomList = mutableListOf<Chatroom>();

    class UsuarioCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun asignarInformacion(user: Usuario){
            itemView.tvEmail.text = user.email;
            itemView.tvUID.text = user.uid;

            if(user.userConectionState == UserConectionState.Conected){
                itemView.tvConnectedState.text = "Connected";
                val color = getColor(itemView.context, R.color.colorItem3);
                itemView.tvConnectedState.setTextColor(color);
                val drawables = itemView.tvConnectedState.compoundDrawables;

                for (drawable in drawables) {
                    if (drawable != null) {
                        drawable.colorFilter = PorterDuffColorFilter(
                            getColor(
                                itemView.context,
                                R.color.colorItem3
                            ), PorterDuff.Mode.SRC_IN
                        )
                    }
                }
            }else{
                itemView.tvConnectedState.text = "Disconnected";
                val color = getColor(itemView.context, R.color.colorItem1);
                itemView.tvConnectedState.setTextColor(color);
                val drawables = itemView.tvConnectedState.compoundDrawables;

                for (drawable in drawables) {
                    if (drawable != null) {
                        drawable.colorFilter = PorterDuffColorFilter(
                            getColor(
                                itemView.context,
                                R.color.colorItem1
                            ), PorterDuff.Mode.SRC_IN
                        )
                    }
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioCardViewHolder {
        return  UsuarioCardViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.custom_item_usercard,parent,false)
        )
    }

    override fun getItemCount(): Int = userList.size;

    private fun buscarUsuario(chatroomList : List<Chatroom>, uid : String) : Chatroom?{
        for(chatroom in chatroomList){

            val arr = chatroom.participantes.keys.toTypedArray();

            if(chatroom.type == ChatroomType.DirectMessage){
                for( a in arr){
                    if(a == uid)
                        return chatroom;
                }
            }
        }
        return null;
    }

    fun initializeUserChatroomRef(){
        val myUid = firebaseAuth.currentUser!!.uid!!;
        userChatroomRef = db.getReference("users/${myUid}/chatrooms");
        userChatroomRef.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                //TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                chatroomList.clear();

                //llenar lista con el snapshot
                for(snap in snapshot.children){
                    val chatroom = snap.getValue(Chatroom::class.java) as Chatroom;

                    chatroomList.add(chatroom);
                }
            }
        });
    }

    private fun clickHandler(itemView : View, item : Usuario){
        val uid = itemView.tvUID.text.toString();

        val otherUserChatroomRef = db.getReference("users/${uid}/chatrooms");

        var found : Chatroom? = buscarUsuario(chatroomList.toList(), uid);

        //chat nuevo
        if(found == null){
            val newChatroom = chatroomRef.push();
            val nUID : String = firebaseAuth.currentUser!!.uid;

            found = Chatroom();
            found.id = newChatroom.key!!;
            found.participantes[nUID] = userList.find{it.uid == nUID}!!.email!!;
            found.participantes[uid] = userList.find{it.uid == nUID}!!.email!!;
            found.type = ChatroomType.DirectMessage;

            //Agregar a la coleccion de chatrooms
            newChatroom.setValue(found);
            //Agregar a la coleccion de chatrooms en el usuario
            val newChatroomMyUser = userChatroomRef.child(newChatroom.key!!);
            newChatroomMyUser.setValue(found);
            //Agregar a la coleccion de chatrooms del otro usuario
            val newChatroomOtherUser = otherUserChatroomRef.child(newChatroom.key!!);
            newChatroomOtherUser.setValue(found);
        }
        //ya existente
        else{
        }

        val intent = Intent(itemView.context, ChatActivity::class.java).apply {
            putExtra("chatroomId", found.id);
            putExtra("nombreUsuario", itemView.tvEmail.text.toString());
        }

        startActivity(itemView.context, intent, null);
    }

    override fun onBindViewHolder(holder: UsuarioCardViewHolder, position: Int) {

        val item = userList[position];

        holder.itemView.setOnClickListener{
            clickHandler(holder.itemView, item);
        }

        holder.asignarInformacion(userList[position]);
    }

}