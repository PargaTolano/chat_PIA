package com.fcfm.poi.pia

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_correo.*

class activity_correo : AppCompatActivity() {

    private lateinit var emails : MutableList<String>;
    private var asunto  : String = "";
    private var mensaje : String = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_correo)

        emails = intent.getStringArrayExtra("usuarios").toMutableList();

        if(emails.size == 0)
            finish();

        asignarBtn.setOnClickListener{

            asunto = textAsunto.text.toString();
            mensaje = mlTextEmailBody.text.toString();

            val intentCorreo = Intent(Intent.ACTION_SENDTO);

            intentCorreo.putExtra(Intent.EXTRA_EMAIL, emails.toTypedArray());
            intentCorreo.putExtra(Intent.EXTRA_SUBJECT, asunto);
            intentCorreo.putExtra(Intent.EXTRA_TEXT, mensaje);
            intentCorreo.data = Uri.parse("mailto:");
            if(intentCorreo.resolveActivity(packageManager) != null){
                startActivity(intentCorreo);
            }
            else{
                Toast.makeText(this, "Baja una app de correos", Toast.LENGTH_SHORT).show();
            }
        }
    }


}