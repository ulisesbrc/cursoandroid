package com.example.curso.casos_uso

import android.app.Activity
import android.content.Intent
import com.example.curso.AcercaDeActivity

class CasosUsoActividades(val actividad: Activity)  {
    fun lanzarAcerdaDe(){
        val i = Intent(actividad, AcercaDeActivity::class.java)
        actividad.startActivity(i);
    }
}