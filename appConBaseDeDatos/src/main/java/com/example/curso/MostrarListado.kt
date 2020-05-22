package com.example.curso

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.curso.casos_uso.CasosUsoActividades
import com.example.curso.casos_uso.CasosUsoLugar
import com.example.curso.presentacion.Aplicacion
import com.example.curso.presentacion.Aplicacion.adaptador
import kotlinx.android.synthetic.main.content_main.*

class MostrarListado: AppCompatActivity() {
    val actividad by lazy { CasosUsoActividades(this) }
    val lugares by lazy { Aplicacion.lugares }
    val usoLugar by lazy { CasosUsoLugar(this, lugares) }
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
            val pos = recycler_view.getChildAdapterPosition(it)
            usoLugar.mostrar(pos)
        }
    }
}