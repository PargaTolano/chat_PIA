package com.fcfm.poi.pia.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.poi.pia.R
import com.fcfm.poi.pia.modelos.Usuario
import kotlinx.android.synthetic.main.custom_item_usercard.view.*

class UsuarioCardAdapter(private val userList : MutableList<Usuario>): RecyclerView.Adapter<UsuarioCardAdapter.UsuarioCardViewHolder>() {
    class UsuarioCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun asignarInformacion(user: Usuario){
            itemView.tvEmail.text = user.email;
            itemView.tvLastMessage.text = user.uid;
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioCardViewHolder {
        return  UsuarioCardViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.custom_item_usercard,parent,false)
        )
    }

    override fun getItemCount(): Int = userList.size;

    override fun onBindViewHolder(holder: UsuarioCardViewHolder, position: Int) {
        holder.asignarInformacion(userList[position]);
    }

}