package com.example.conbasededatos

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.conbasededatos.casos_uso.CasosUsoActividades
import com.example.conbasededatos.casos_uso.CasosUsoLugar
import com.example.conbasededatos.presentacion.Aplicacion
import com.example.conbasededatos.presentacion.Aplicacion.adaptador
import kotlinx.android.synthetic.main.content_main.*

class MostrarListado: AppCompatActivity() {
    val actividad by lazy { CasosUsoActividades(this) }
    //val lugares by lazy { Aplicacion.lugares }
    val lugares by lazy { Aplicacion.lugares(this) }
    val adaptador by lazy { Aplicacion.adaptador(lugares,this) }
    val usoLugar by lazy { CasosUsoLugar(this, lugares, adaptador) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setContentView(R.layout.content_main)

        recycler_view.apply {
            setHasFixedSize(true)
           // layoutManager = GridLayoutManager(this@ScrollingActivity,2)
           layoutManager = LinearLayoutManager(this@MostrarListado)
            adapter = adaptador
        }
        adaptador.onClick  =  {
           // val pos = recycler_view.getChildAdapterPosition(it)
            val pos = it.tag as Int
            usoLugar.mostrar(pos)
        }
    }
}