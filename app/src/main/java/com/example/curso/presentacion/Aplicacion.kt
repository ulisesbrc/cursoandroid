package com.example.curso.presentacion

import com.example.curso.AdaptadorLugares
import com.example.curso.datos.LugaresLista
import com.example.curso.modelo.GeoPunto

object Aplicacion {
    var posicionActual = GeoPunto.SIN_POSICION.copy()
    val lugares = LugaresLista()
    val adaptador = AdaptadorLugares(lugares)
    init {
        lugares.añadeEjemplos()
    }
}