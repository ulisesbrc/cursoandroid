package com.example.conbasededatos.datos
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.conbasededatos.modelo.Lugar
import com.example.conbasededatos.modelo.TipoLugar

class LugaresLista(val contexto: Context) :
    SQLiteOpenHelper(contexto, "lugares", null, 1), RepositorioLugares {
    val listaLugares= mutableListOf<Lugar>()

    override fun elemento(id: Int): Lugar {
        return listaLugares[id]
    }

    override fun añade(lugar: Lugar) {
        listaLugares.add(lugar)
    }

    override fun nuevo(): Int {
        val lugar = Lugar(
            "Nuevo lugar",
            tipoLugar = TipoLugar.RESTAURANTE
        )
        listaLugares.add(lugar)
        return listaLugares.size - 1
    }

    override fun borrar(id: Int) {
        listaLugares.removeAt(id)
    }

    override fun tamaño(): Int {
        return listaLugares.size
    }
    override fun actualiza(id: Int, lugar: Lugar) {
        listaLugares[id] = lugar
    }

    override fun onCreate(db: SQLiteDatabase?) {
        TODO("Not yet implemented")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }
}
