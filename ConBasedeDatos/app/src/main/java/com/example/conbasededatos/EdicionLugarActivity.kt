package com.example.conbasededatos

import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.conbasededatos.casos_uso.CasosUsoLugar
import com.example.conbasededatos.modelo.Lugar
import com.example.conbasededatos.modelo.TipoLugar
import com.example.conbasededatos.presentacion.Aplicacion
import com.example.conbasededatos.presentacion.Aplicacion.lugares
import kotlinx.android.synthetic.main.edicion_lugar.*
import kotlinx.android.synthetic.main.vista_lugar.*
import java.lang.Integer.parseInt


class EdicionLugarActivity : AppCompatActivity() {
    val lugares by lazy { Aplicacion.lugares(this) }
    val adaptador by lazy { Aplicacion.adaptador(lugares,this) }
    val usoLugar by lazy { CasosUsoLugar(this,null, lugares,adaptador) }
    var pos = 0
    lateinit var lugar: Lugar
    private var _id: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edicion_lugar)
        pos = intent.extras?.getInt("pos", 0) ?: 0
        _id = intent.extras?.getInt("_id", -1) ?: -1
        //_id = adaptador.idPosicion(pos)
        lugar = if (_id !== -1) lugares.elemento(_id)
        else            adaptador.lugarPosicion(pos)
        //var lugarop = intent.extras?.getInt("lugar", 0) ?: 0
        //lugar = lugares.elemento(pos)
        //lugar = Aplicacion.adaptador(lugares,this).lugarPosicion(pos)
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
            //if (_id!=-1) lugares.borrar(_id)
            usoLugar.mostrar(pos)

            true
        }
        R.id.accion_guardar -> {
            finish()
            guardar()

            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
    override fun onRestart() {
        super.onRestart()
        finish()
        // Toast.makeText(this, "onRestart", Toast.LENGTH_SHORT).show()
        // mp?.start();
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
        usoLugar.visualizarFoto(lugar, edit_foto,"",this)
        edit_valoracion.setRating(lugar.valoracion)
        edit_valoracion.setOnRatingBarChangeListener {
                ratingBar, valor, fromUser ->
            lugar.valoracion = valor
            usoLugar.actualizaPosLugar(pos, lugar, this)
            pos = adaptador.posicionId(_id)
        }
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
        //lugares.actualiza(pos, lugaraux).
        //val _id = adaptador.idPosicion(pos)
        if (_id==-1) _id = adaptador.idPosicion(pos)

        usoLugar.actualiza(_id, lugaraux, this)
        //usoLugar.actualiza(pos, lugaraux)
        //mostrar y verificar los ardenes de los registros y la cantidad a mostrar
        /*
        if(pos!=0){
            usoLugar.mostrar(pos)
        } else {
            usoLugar.mostrar(_id-1)
        } */

        usoLugar.mostrar(pos)

    }
}