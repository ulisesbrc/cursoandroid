package com.example.conbasededatos.presentacion

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.conbasededatos.casos_uso.CasosUsoLugar
import java.util.*

class DialogoSelectorFecha : DialogFragment() {

    private var escuchador: DatePickerDialog.OnDateSetListener? = null

    fun setOnDateSetListener(escuchador: CasosUsoLugar) {
        this.escuchador = escuchador
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendario = Calendar.getInstance()
        val fecha = getArguments()?.getLong("fecha")?:0L
        calendario.setTimeInMillis(fecha)
        val año = calendario.get(Calendar.YEAR)
        val mes = calendario.get(Calendar.MONTH)
        val dia = calendario.get(Calendar.DAY_OF_MONTH)
       return DatePickerDialog(this.requireContext(), escuchador, año, mes, dia)
    }
}