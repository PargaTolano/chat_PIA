package com.fcfm.poi.pia.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.poi.pia.R
import com.fcfm.poi.pia.modelos.Integrante
import com.fcfm.poi.pia.modelos.Usuario
import kotlinx.android.synthetic.main.custom_item_integrante.view.*
import java.util.*

class IntegranteAdapter(private val userList : MutableList<Usuario>): RecyclerView.Adapter<IntegranteAdapter.IntegranteViewHolder>() {
    class IntegranteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun asignarInformacion(user: Usuario){
            itemView.tvIntegrante.text = user.email;
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntegranteViewHolder {
        return  IntegranteViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.custom_item_integrante,parent,false)
        )
    }

    override fun onBindViewHolder(holder: IntegranteViewHolder, position: Int) {
        holder.asignarInformacion(userList[position])
    }

    override fun getItemCount(): Int = userList.size

}