package com.example.curso.presentacion

import com.example.curso.AdaptadorLugares
import com.example.curso.datos.LugaresLista

object Aplicacion {
    val lugares = LugaresLista()
    val adaptador = AdaptadorLugares(lugares)
    init {
        lugares.añadeEjemplos()
    }
}