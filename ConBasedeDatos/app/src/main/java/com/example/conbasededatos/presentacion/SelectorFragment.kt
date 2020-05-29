package com.example.conbasededatos.presentacion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.conbasededatos.R
import com.example.conbasededatos.casos_uso.CasosUsoLugar

class SelectorFragment : Fragment() {
    val lugares by lazy {Aplicacion.lugares(requireActivity()) }
    val adaptador by lazy { Aplicacion.adaptador(lugares,requireActivity()) }
    val usoLugar by lazy { CasosUsoLugar(requireActivity(),this, lugares, adaptador) }
    lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflador: LayoutInflater, contenedor:
    ViewGroup?, savedInstanceState: Bundle? ): View? {
        val vista =
            inflador.inflate(R.layout.fragment_selector,contenedor,false)
        recyclerView = vista.findViewById(R.id.recyclerView)
        return vista
    }

    override fun onActivityCreated(state: Bundle?) {
        super.onActivityCreated(state)
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
           // layoutManager = GridLayoutManager(context,2)
            adapter = adaptador
        }
        adaptador.onClick = {
            val pos = it.tag as Int
            usoLugar.mostrar(pos)
        }
    }
}