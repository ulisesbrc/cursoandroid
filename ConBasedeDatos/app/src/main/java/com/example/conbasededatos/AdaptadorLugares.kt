package com.example.conbasededatos

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.conbasededatos.modelo.GeoPunto
import com.example.conbasededatos.modelo.Lugar
import com.example.conbasededatos.modelo.TipoLugar
import com.example.conbasededatos.presentacion.Aplicacion
import com.example.conbasededatos.presentacion.Aplicacion.adaptador
import kotlinx.android.synthetic.main.elemento_lista.view.*

open class AdaptadorLugares(val lugares: LugaresBD, var cursor: Cursor, val context: Context) :
    RecyclerView.Adapter<AdaptadorLugares.ViewHolder>() {
    lateinit var onClick: (View) -> Unit
    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun personaliza(lugar: Lugar, onClick: (View) -> Unit) = with(itemView){
            nombre.text = lugar.nombre
            direccion.text = lugar.direccion
            setOnClickListener{ onClick(itemView) }
            foto.setImageResource(when (lugar.tipoLugar) {
                TipoLugar.RESTAURANTE -> R.drawable.restaurante
                TipoLugar.BAR -> R.drawable.bar
                TipoLugar.COPAS -> R.drawable.copas
                TipoLugar.ESPECTACULO -> R.drawable.espectaculos
                TipoLugar.HOTEL -> R.drawable.hotel
                TipoLugar.COMPRAS -> R.drawable.compras
                TipoLugar.EDUCACION -> R.drawable.educacion
                TipoLugar.DEPORTE -> R.drawable.deporte
                TipoLugar.NATURALEZA -> R.drawable.naturaleza
                TipoLugar.GASOLINERA -> R.drawable.gasolinera
                TipoLugar.OTROS -> R.drawable.otros
            })
           // foto.setImageResource(R.drawable.prueba_background)
            foto.setScaleType(ImageView.ScaleType.FIT_END)
            valoracion.rating = lugar.valoracion
            var pos = Aplicacion.posicionActual
            if (pos== GeoPunto.SIN_POSICION || lugar.posicion==GeoPunto.SIN_POSICION) {
                distancia.text = "... Km"
            } else {
                val d = pos.distancia(lugar.posicion).toInt()
                distancia.text = if (d < 2000) "$d m"
                else          "${(d / 1000)} Km"

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.elemento_lista, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, posicion: Int) {
      //  val lugar = lugares.elemento(posicion)
        val lugar = adaptador(lugares,context).lugarPosicion(posicion)
        holder.personaliza(lugar,onClick)
    }

    override fun getItemCount() = cursor.getCount(); //lugares.tamaño()
}