package com.fcfm.poi.pia.adaptadores

import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.poi.pia.CrearGrupoActivity
import com.fcfm.poi.pia.R
import com.fcfm.poi.pia.modelos.Usuario
import kotlinx.android.synthetic.main.custom_item_integrante.view.*

class IntegranteAdapter(private val userList : MutableList<Usuario>, private var activity: CrearGrupoActivity): RecyclerView.Adapter<IntegranteAdapter.IntegranteViewHolder>() {
    class IntegranteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun asignarInformacion(user: Usuario){
            itemView.tvEmail.text = user.email;
            itemView.tvUID.text = user.uid;
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntegranteViewHolder {
        return  IntegranteViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.custom_item_integrante,parent,false)
        )
    }

    private fun clickHandler(item : Usuario, itemView: View){
        val c = itemView.cbAgregado.isChecked;

        if(c){

            val user = activity.integrantes.find{ it.uid == itemView.tvUID.text.toString()}!!;
            activity.integrantes.remove(user);

            itemView.cbAgregado.isChecked = false;

            //Cambiar Estilo
            itemView.btnAddAtGroup.background =  itemView.context.resources.getDrawable(R.color.colorItem2, null);
            itemView.btnAddAtGroup.text = "AGREGAR";
        }
        else{
            val userUID : String = itemView.tvUID.text.toString();
            val user = activity.listaIntegrantes.find { user-> user.uid == userUID };


            if( user != null){
                activity.integrantes.add(user);

                itemView.cbAgregado.isChecked = true;

                //Cambiar Estilo
                itemView.btnAddAtGroup.background =  itemView.context.resources.getDrawable(R.color.colorItem1, null);
                itemView.btnAddAtGroup.text = "ELIMINAR";
            }
            else
            {
                throw Error("Usuario no existente en lista");
            }

        }
    }

    override fun onBindViewHolder(holder: IntegranteViewHolder, position: Int) {
        val item = userList[position];

        holder.itemView.btnAddAtGroup.setOnClickListener{
            clickHandler(item, holder.itemView);
        }

        holder.asignarInformacion(userList[position]);
    }

    override fun getItemCount(): Int = userList.size

}