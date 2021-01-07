package com.fcfm.poi.pia.adaptadores

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.poi.pia.R
import com.fcfm.poi.pia.Utilities.ImageUtilities
import com.fcfm.poi.pia.modelos.Mensaje
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.custom_item_mensaje.view.*
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter(private val listaMensajes: MutableList<Mensaje>, private val chatroomId: String) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun asignarInformacion(mensaje: Mensaje, chatroomId: String) {

            val database  = FirebaseDatabase.getInstance();
            val chatroomRef = database.getReference("chatrooms");
            var chatRef = chatroomRef.child("${chatroomId}/chat");

            itemView.tvUsuario.text = mensaje.de
            itemView.tvMensaje.text = mensaje.contenido

            val fechaFromater = SimpleDateFormat("dd/MM/yyyy - HH:mm:ss", Locale.getDefault())
            val fechaconFormato =  fechaFromater.format(Date(mensaje.timeStamp as Long))

            itemView.tvFecha.text =  fechaconFormato

            var fileRef = FirebaseStorage.getInstance().reference.child("chatroom/"+chatRef+"/"+mensaje.id+"pic.jpg")
            //var fileRef = FirebaseStorage.getInstance().reference.child("chatroom/"+chatRef+"pic.jpg")
            val maxDownloadSize = 5L * 1024 * 1024
            fileRef.getBytes(maxDownloadSize)
                .addOnSuccessListener { task ->
                mensaje.archivo = task
                val imageView : ImageView = itemView.findViewById(R.id.tvFile)
                if (mensaje.archivo != null)
                {
                    imageView.setImageBitmap(ImageUtilities.getBitMapFromByteArray(mensaje.archivo!!))
                }
                }
                .addOnFailureListener {
                    mensaje.archivo = null;
                    itemView.tvFile.visibility =  View.GONE;
                }

            val params = itemView.contenedorMensaje.layoutParams

            if (mensaje.esMio) {

                val newParams = FrameLayout.LayoutParams(
                    params.width,
                    params.height,
                    Gravity.END
                )
                itemView.contenedorMensaje.layoutParams = newParams

                itemView.tvUsuario.visibility = View.GONE;

            } else {

                val newParams = FrameLayout.LayoutParams(
                    params.width,
                    params.height,
                    Gravity.START
                )
                itemView.contenedorMensaje.layoutParams = newParams
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {

        return ChatViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.custom_item_mensaje, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {

        holder.asignarInformacion(listaMensajes[position], chatroomId)
    }

    override fun getItemCount(): Int = listaMensajes.size
}