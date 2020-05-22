package com.example.conbasededatos.presentacion

import android.content.Context
import com.example.conbasededatos.AdaptadorLugares
import com.example.conbasededatos.AdaptadorLugaresBD
import com.example.conbasededatos.LugaresBD
import com.example.conbasededatos.modelo.GeoPunto


object Aplicacion {
    var posicionActual = GeoPunto.SIN_POSICION.copy()
    private val context: Context? = null
    //val lugaresa = LugaresBD(context)
    /*val adaptador by lazy {
        AdaptadorLugaresBD(lugares, lugares.extraeCursor())
    }
   */
    init {

        //lugares.añadeEjemplos()
    }
    fun lugares(context: Context):LugaresBD{
        return LugaresBD(context)
    }
    fun adaptador(lugares: LugaresBD, context: Context):AdaptadorLugaresBD{
        return AdaptadorLugaresBD(lugares, lugares.extraeCursor(context),context)
    }
}