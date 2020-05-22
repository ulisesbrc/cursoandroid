package com.example.curso

import com.example.curso.datos.LugaresLista
import com.example.curso.modelo.GeoPunto
import com.example.curso.modelo.Lugar
import com.example.curso.modelo.TipoLugar

internal object Principal {
    @JvmStatic
    fun main(main: Array<String>) {
        val lugar = Lugar(
            "Escuela Politécnica Superior de Gandía",
            "C/ Paranimf, 1 46730 Gandia (SPAIN)",
            GeoPunto(-0.166093, 38.995656),
            TipoLugar.RESTAURANTE, "Uno de los mejores lugares para formarse.",
            962849300, "3"
        )

        val ListaMutable : MutableList<String> = mutableListOf("unos", "dos", "tres", "cuatro")
        ListaMutable[3]= "cambio 3"

        var lugares = LugaresLista()
        lugares.añadeEjemplos()
        for (i in 0 until lugares.tamaño()) {
            System.out.println(lugares.elemento(i).toString())
        }
        println("Lugar $lugar")
    }
}