package com.example.conbasededatos

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils.isEmpty
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.conbasededatos.casos_uso.CasosUsoLugar
import com.example.conbasededatos.modelo.Lugar
import com.example.conbasededatos.presentacion.Aplicacion
import com.example.conbasededatos.presentacion.Aplicacion.adaptador
import kotlinx.android.synthetic.main.vista_lugar.*
import java.text.DateFormat
import java.util.*

class VistaLugarActivity : AppCompatActivity() {
        val lugares by lazy { Aplicacion.lugares(this) }
        //var lugares = LugaresBD(this)
        val adaptador by lazy { Aplicacion.adaptador(lugares,this) }
        val usoLugar by lazy { CasosUsoLugar(this, lugares,adaptador) }
        var pos = 0
        lateinit var lugar: Lugar
    val RESULTADO_EDITAR = 1
    val RESULTADO_GALERIA = 2  //poner antes de la clase
    val RESULTADO_FOTO = 3
    var uri = ""
    private var _id: Int = -1
    private lateinit var uriUltimaFoto: Uri
    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.vista_lugar)
            pos = intent.extras?.getInt("pos", 0) ?: 0
            _id = adaptador.idPosicion(pos)
            //lugar = lugares.elemento(pos)
            lugar = adaptador.lugarPosicion(pos)

        actualizaVistas()
        }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.vista_lugar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.accion_compartir -> {
                usoLugar.compartir(lugar)
                return true
            }
            R.id.accion_llegar -> {
                usoLugar.verMapa(lugar)
                return true
            }
            R.id.accion_editar -> {
                usoLugar.editar(pos,RESULTADO_EDITAR)
                return true
            }
            R.id.accion_borrar -> {
                borrar()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int,
                                  data: Intent?) {
        super.onActivityResult(requestCode,resultCode,data)
        if (requestCode == RESULTADO_EDITAR) {
            lugar = lugares.elemento(_id)
            pos = adaptador.posicionId(_id)
            actualizaVistas()
            scrollView1.invalidate()
        } else if (requestCode == RESULTADO_GALERIA) {
            if (resultCode == RESULT_OK) {
                usoLugar.ponerFoto(pos, data?.dataString ?: "", foto)
                uri = data?.dataString?:""
            } else {
                Toast.makeText(this, "Foto no cargada", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == RESULTADO_FOTO) {
            if (resultCode == Activity.RESULT_OK && uriUltimaFoto!=null) {
                lugar.foto = uriUltimaFoto.toString()
                usoLugar.ponerFoto(pos, lugar.foto, foto);
            } else {
                Toast.makeText(this, "Error en captura", Toast.LENGTH_LONG).show()
            }
        }
    }
    fun actualizaVistas(){
            nombre.text = lugar.nombre
           // logo_tipo.imageResource = lugar.tipoLugar.recurso
            tipo.text = lugar.tipoLugar.texto
            direccion.text = lugar.direccion
            if (lugar.telefono == 0) {
                telefono.setVisibility(View.GONE)
            } else {
                telefono.setVisibility(View.VISIBLE)
                telefono.setText(Integer.toString(lugar.telefono))
            }
            if ( isEmpty(lugar.direccion)) {
                direccion.setVisibility(View.GONE)
            } else {
                direccion.setVisibility(View.VISIBLE)
                direccion.setText(lugar.direccion)
            }
            if ( isEmpty(lugar.url)) {
                url.setVisibility(View.GONE)
            } else {
                url.setVisibility(View.VISIBLE)
                url.setText(lugar.url)
            }
            if ( isEmpty(lugar.comentarios)) {
                comentario.setVisibility(View.GONE)
            } else {
                comentario.setVisibility(View.VISIBLE)
                comentario.setText(lugar.comentarios)
            }
            //telefono.text = Integer.toString(lugar.telefono)
            //url.text = lugar.url
            //comentario.text = lugar.comentarios
            fecha.text = DateFormat.getDateInstance().format(Date(lugar.fecha))
            hora.text = DateFormat.getTimeInstance().format(Date(lugar.fecha))
            valoracion.setRating(lugar.valoracion)
            valoracion.setOnRatingBarChangeListener {
                    ratingBar, valor, fromUser ->
                lugar.valoracion = valor
                usoLugar.actualizaPosLugar(pos, lugar, this)
                pos = adaptador.posicionId(_id)
            }

        usoLugar.visualizarFoto(lugar, foto,uri, this)
        }
    fun borrar(){
        AlertDialog.Builder(this)
        .setTitle("Borrado de lugar")
        .setMessage("¿Estás seguro que quieres eliminar este lugar?")
       // .setView(entrada)
        .setPositiveButton("Confirmar")  { dialog, whichButton ->
            //usoLugar.borrar(pos)
            //lugares.borrar(pos)
            val _id = adaptador.idPosicion(pos)
            usoLugar.borrar(_id)
            //return true
            //usoLugar.mostrar(pos)
            // Toast.makeText(this id, Toast.LENGTH_SHORT).show()
        }
        .setNegativeButton("Cancelar", null)
        .show()
    }
    fun editar(view: View? = null) {
       // usoLugar.editar(pos)
       // finish()
    }
    fun verMapa(view: View) = usoLugar.verMapa(lugar)

    fun llamarTelefono(view: View) = usoLugar.llamarTelefono(lugar)

    fun verPgWeb(view: View) = usoLugar.verPgWeb(lugar)
    fun ponerDeGaleria(view: View)= usoLugar.ponerDeGaleria(RESULTADO_GALERIA)
    fun tomarFoto(view: View) {
        uriUltimaFoto = usoLugar.tomarFoto(RESULTADO_FOTO)
    }
    fun eliminarFoto(view: View) = usoLugar.ponerFoto(pos, "", foto)
}
