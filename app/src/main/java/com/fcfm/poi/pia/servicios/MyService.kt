package com.fcfm.poi.pia.servicios

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.fcfm.poi.pia.enums.UserConectionState
import com.fcfm.poi.pia.modelos.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MyService : Service() {

    private val firebaseAuth = FirebaseAuth.getInstance();
    private val currUser = firebaseAuth.currentUser;
    private val db = FirebaseDatabase.getInstance();
    private val userRef = db.getReference("users").apply {
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
    private val users = mutableListOf<Usuario>()

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent);

        val myUserRef = userRef.child(currUser!!.uid);
        val user = users.find{user-> user.uid == currUser!!.uid}!!;
        user.userConectionState = UserConectionState.Conected;

        myUserRef.setValue(user);
    }
}