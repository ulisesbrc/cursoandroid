package com.example.conbasededatos.casos_uso

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.conbasededatos.AdaptadorLugaresBD
import com.example.conbasededatos.LugaresBD
import com.example.conbasededatos.R
import com.example.conbasededatos.presentacion.DialogoSelectorFecha
import java.text.DateFormat
import java.util.*

class CasosUsoLugarFecha(
    override val actividad: FragmentActivity,
    override val fragment: Fragment,
    override val lugares: LugaresBD,
    override val adaptador: AdaptadorLugaresBD
) : CasosUsoLugar(actividad, fragment ,lugares, adaptador), TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    override fun onTimeSet(vista: TimePicker?, hora: Int, minuto: Int) {
        val calendario = Calendar.getInstance()
        calendario.setTimeInMillis(lugar.fecha)
        calendario.set(Calendar.HOUR_OF_DAY, hora)
        calendario.set(Calendar.MINUTE, minuto)
        lugar.fecha = calendario.getTimeInMillis()
        actualizaPosLugar(pos, lugar, actividad)
        val textView = actividad.findViewById<TextView>(R.id.hora)
        textView.text= DateFormat.getTimeInstance().format(Date(lugar.fecha))
    }

    override fun onDateSet(view: DatePicker, año: Int, mes: Int, dia: Int) {
        val calendario = Calendar.getInstance()
        calendario.timeInMillis = lugar.fecha
        calendario.set(Calendar.YEAR, año)
        calendario.set(Calendar.MONTH, mes)
        calendario.set(Calendar.DAY_OF_MONTH, dia)
        lugar.fecha = calendario.timeInMillis
        actualizaPosLugar(pos, lugar, actividad)
        val textView = actividad.findViewById<TextView>(R.id.fecha)
        textView.text = java.text.DateFormat.getDateInstance().format(Date(lugar.fecha))
    }
}