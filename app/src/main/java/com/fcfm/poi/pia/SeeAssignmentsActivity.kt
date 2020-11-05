package com.fcfm.poi.pia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.poi.pia.adaptadores.AssignmentAdapter
import com.fcfm.poi.pia.modelos.Assignment
import kotlinx.android.synthetic.main.activity_see_assignments.*

class SeeAssignmentsActivity : AppCompatActivity() {

    private val listaAssignments = mutableListOf<Assignment>()
    private val adaptador = AssignmentAdapter(listaAssignments)
    private lateinit var recycler : RecyclerView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_see_assignments)

        recyclerviewAssignments.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        recyclerviewAssignments.adapter = adaptador;
    }
}
