package com.example.conbasededatos

import android.content.Context
import android.database.Cursor
import com.example.conbasededatos.datos.LugaresLista
import com.example.conbasededatos.modelo.Lugar
import com.example.conbasededatos.presentacion.Aplicacion.lugares

class AdaptadorLugaresBD(lugares: LugaresBD, cursor: Cursor, contexto: Context): AdaptadorLugares(lugares,cursor,contexto){

    fun lugarPosicion(posicion: Int): Lugar {
        cursor.moveToPosition(posicion)
        return (lugares as LugaresBD).extraeLugar(cursor)
    }

    fun idPosicion(posicion: Int): Int {
        cursor.moveToPosition(posicion)
        if (cursor.count>0) return cursor.getInt(0)
        else                return -1

    }

    override fun onBindViewHolder(holder: AdaptadorLugares.ViewHolder,
                                  posicion: Int) {
        val lugar = lugarPosicion(posicion)
        holder.personaliza(lugar, onClick)
        holder.view.tag = posicion
    }

    override fun getItemCount(): Int {
        return cursor.getCount()
    }
}