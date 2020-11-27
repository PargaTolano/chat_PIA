package com.fcfm.poi.pia.adaptadores

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.poi.pia.R
import com.fcfm.poi.pia.modelos.Chatroom
import com.fcfm.poi.pia.modelos.Mensaje
import kotlinx.android.synthetic.main.custom_item_mensaje.view.*
import java.text.SimpleDateFormat
import java.util.*

class ChatroomAdapter(private val chatroomList: MutableList<Chatroom>) :
    RecyclerView.Adapter<ChatroomAdapter.ChatViewHolder>() {

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun asignarInformacion(chatroom: Chatroom) {

            /*
            itemView.tvUsuario.text = chatroom.nombre
            itemView.tvMensaje.text = chatroom.participantes[0]
            */


           /* val params = itemView.contenedorMensaje.layoutParams

            if (mensaje.esMio) {

                val newParams = FrameLayout.LayoutParams(
                    params.width,
                    params.height,
                    Gravity.END
                )
                itemView.contenedorMensaje.layoutParams = newParams

            } else {

                val newParams = FrameLayout.LayoutParams(
                    params.width,
                    params.height,
                    Gravity.START
                )*/
                //itemView.contenedorMensaje.layoutParams = newParams
            //}
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatroomAdapter.ChatViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.custom_item_mensaje, parent, false)
        )
    }

    override fun getItemCount(): Int = chatroomList.size

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.asignarInformacion(chatroomList[position])
    }

}