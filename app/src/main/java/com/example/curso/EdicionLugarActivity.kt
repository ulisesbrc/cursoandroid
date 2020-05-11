package com.example.curso

import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.curso.casos_uso.CasosUsoLugar
import com.example.curso.modelo.Lugar
import com.example.curso.modelo.TipoLugar
import com.example.curso.presentacion.Aplicacion
import kotlinx.android.synthetic.main.edicion_lugar.*
import kotlinx.android.synthetic.main.vista_lugar.*
import java.lang.Integer.parseInt


class EdicionLugarActivity : AppCompatActivity() {
    val lugares by lazy { Aplicacion.lugares }
    val usoLugar by lazy { CasosUsoLugar(this, lugares) }
    var pos = 0
    lateinit var lugar: Lugar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edicion_lugar)
        pos = intent.extras?.getInt("pos", 0) ?: 0
        //var lugarop = intent.extras?.getInt("lugar", 0) ?: 0
        //lugares.añadeEjemplos()
        lugar = lugares.elemento(pos)
        val adaptador = ArrayAdapter<String>(this,
            android.R.layout.simple_spinner_item,
            lugar.tipoLugar.getNombres()
        )
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        edit_tipo.adapter = adaptador
        edit_tipo.setSelection(lugar.tipoLugar.ordinal)
        actualizaVistas()
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_edicion, menu)
        return true
    }
override fun onOptionsItemSelected(item: MenuItem): Boolean {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.

    return when (item.itemId) {
        //R.id.action_settings -> true
        R.id.accion_cancelar -> {
            finish()
            //usoLugar.mostrar(pos)
            true
        }
        R.id.accion_guardar -> {
            guardar()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
    fun actualizaVistas(){
        edit_nombre.setText(lugar.nombre)
        // logo_tipo.imageResource = lugar.tipoLugar.recurso
        //tipo.text = lugar.tipoLugar.textoval
        edit_direccion.setText(lugar.direccion) // = editable_direccion
        if (lugar.telefono == 0) {
            //edit_telefono.setVisibility(View.GONE)
        } else {
            edit_telefono.setVisibility(View.VISIBLE)
            edit_telefono.setText(Integer.toString(lugar.telefono))
        }
        if (TextUtils.isEmpty(lugar.direccion)) {
            edit_direccion.setVisibility(View.GONE)
        } else {
            edit_direccion.setVisibility(View.VISIBLE)
            edit_direccion.setText(lugar.direccion)
        }
        if (TextUtils.isEmpty(lugar.url)) {
            edit_url.setVisibility(View.GONE)
        } else {
            edit_url.setVisibility(View.VISIBLE)
            edit_url.setText(lugar.url)
        }
        if (TextUtils.isEmpty(lugar.comentarios)) {
            edit_comentario.setVisibility(View.GONE)
        } else {
            edit_comentario.setVisibility(View.VISIBLE)
            edit_comentario.setText(lugar.comentarios)
        }
        usoLugar.visualizarFoto(lugar, edit_foto,"")
        //telefono.text = Integer.toString(lugar.telefono)
        //url.text = lugar.url
        //comentario.text = lugar.comentarios
        //fecha.text = DateFormat.getDateInstance().format(Date(lugar.fecha))
        //hora.text = DateFormat.getTimeInstance().format(Date(lugar.fecha))
        //valoracion.rating = lugar.valoracion
        /*valoracion.setOnRatingBarChangeListener {
                ratingBar, valor, fromUser -> lugar.valoracion = valor
        }*/
    }
    fun guardar(view: View? = null){
        val nombreedit = edit_nombre.getText().toString()
        val direccionedit = edit_direccion.getText().toString()
        val telefonoedit = parseInt(edit_telefono.getText().toString())
        val urledit = edit_url.getText().toString()
        val comentarioedit = edit_comentario.getText().toString()

        val item =edit_tipo.getSelectedItemPosition()
        val tipolugar = TipoLugar.values()[item]
        val lugaraux = Lugar(
            nombreedit,
            direccionedit,
            lugar.posicion,
            tipolugar,
            lugar.foto,
            telefonoedit,
            urledit,
            comentarioedit,
            lugar.fecha,
            lugar.valoracion
        )
        //lugares.actualiza(pos, lugaraux)
        usoLugar.actualiza(pos, lugaraux)
        usoLugar.mostrar(pos)
    }
}