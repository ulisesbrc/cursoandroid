package com.example.conbasededatos.presentacion

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import androidx.fragment.app.DialogFragment
import com.example.conbasededatos.casos_uso.CasosUsoLugar
import java.util.*

class DialogoSelectorHora : DialogFragment() {

    private var escuchador: TimePickerDialog.OnTimeSetListener? = null

    fun setOnTimeSetListener(escuchador: CasosUsoLugar) {
        this.escuchador = escuchador
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendario = Calendar.getInstance()
        val fecha = arguments?.getLong("fecha")?:System.currentTimeMillis()
        calendario.setTimeInMillis(fecha)
        val hora = calendario.get(Calendar.HOUR_OF_DAY)
        val minuto = calendario.get(Calendar.MINUTE)
        return TimePickerDialog(
            getActivity(), escuchador, hora,
            minuto, DateFormat.is24HourFormat(getActivity())
        )
    }
}