package com.fcfm.poi.pia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fcfm.poi.pia.adaptadores.ChatAdapter
import com.fcfm.poi.pia.adaptadores.IntegranteAdapter
import com.fcfm.poi.pia.modelos.Integrante
import com.fcfm.poi.pia.modelos.Mensaje
import com.fcfm.poi.pia.modelos.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_crear_grupo.*

class CrearGrupoActivity : AppCompatActivity() {

    private lateinit var firebaseAuth : FirebaseAuth;

    private val listaIntegrantes = mutableListOf<Usuario>();
    private val adaptador = IntegranteAdapter(listaIntegrantes);

    private val db = FirebaseDatabase.getInstance();

    private val userRef = db.getReference("users");

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_grupo);

        rv_integrantes.adapter = adaptador;

        firebaseAuth = FirebaseAuth.getInstance();

        initUsers();
    }

    private fun initUsers() {

        userRef.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                listaIntegrantes.clear();
                for(snap in snapshot.children){

                    val email : String = snap.child("email").value.toString();
                    val uid : String = snap.child("uid").value.toString();

                    val user : Usuario = Usuario(uid,email);

                    if (firebaseAuth.currentUser?.uid!! != uid){
                        listaIntegrantes.add(user);
                    }
                }

                if(listaIntegrantes.size > 0) {
                    adaptador.notifyDataSetChanged();
                    rv_integrantes.adapter;
                }
            }
        })
    }
}