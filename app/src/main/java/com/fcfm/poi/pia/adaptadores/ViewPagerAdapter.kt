package com.fcfm.poi.pia.adaptadores

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.fcfm.poi.pia.DemoObjectFragment
import com.fcfm.poi.pia.dashBoardActivity

class ViewPagerAdapter (val fragment: FragmentActivity) : FragmentStateAdapter(fragment) {

    public val fragmentList = mutableListOf<Fragment>()

    //Constante a nivel de clase
    companion object{
        private  const val ARG_OBJECT = "object"
    }

    //cuantos fragments va a tener el swipe
    override fun getItemCount(): Int  = 3

    override fun createFragment(position: Int): Fragment {
        //Vamos a crear el fragmento
        val fragment =  DemoObjectFragment(this.fragment as dashBoardActivity);

        when(position){
            0->{
                fragment.recibirRegistrosChatoom();
            }
            1->{
                fragment.recibirRegistrosTareas();
            }
            2->{
                fragment.recibirRegistrosUsuarios();
            }
        }

        fragment.arguments = Bundle().apply {
            putInt(ARG_OBJECT, position + 1);
        }

        return fragment;
    }
}