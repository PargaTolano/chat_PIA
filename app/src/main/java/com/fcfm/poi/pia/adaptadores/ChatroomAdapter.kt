package com.fcfm.poi.pia.adaptadores

import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.poi.pia.ChatActivity
import com.fcfm.poi.pia.R
import com.fcfm.poi.pia.modelos.Chatroom
import com.fcfm.poi.pia.modelos.Mensaje
import kotlinx.android.synthetic.main.custom_item_group.view.*
import kotlinx.android.synthetic.main.custom_item_group.view.tvUID
import kotlinx.android.synthetic.main.custom_item_integrante.view.*
import kotlinx.android.synthetic.main.custom_item_integrante.view.tvEmail
import kotlinx.android.synthetic.main.custom_item_mensaje.view.*
import kotlinx.android.synthetic.main.custom_item_usercard.view.*
import java.text.SimpleDateFormat
import java.util.*

class ChatroomAdapter(private val chatroomList: MutableList<Chatroom>) :
    RecyclerView.Adapter<ChatroomAdapter.ChatViewHolder>() {

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun asignarInformacion(chatroom: Chatroom) {

            itemView.tvGroupName.text = chatroom.nombre;
            itemView.tvUID.text = chatroom.id;
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatroomAdapter.ChatViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.custom_item_group, parent, false)
        )
    }

    override fun getItemCount(): Int = chatroomList.size

    fun clickHandler(item : Chatroom, itemView: View){
        val intent = Intent(itemView.context, ChatActivity::class.java).apply {
            putExtra("chatroomId", item.id);
            putExtra("nombreUsuario", item.nombre);
        };

        startActivity(itemView.context, intent, null);
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.asignarInformacion(chatroomList[position])

        val item = chatroomList[position];

        holder.itemView.setOnClickListener{
            clickHandler(item, holder.itemView);
        }

        holder.asignarInformacion(chatroomList[position]);
    }

}