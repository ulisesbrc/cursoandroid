package com.example.conbasededatos.presentacion

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils.isEmpty
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.conbasededatos.R
import com.example.conbasededatos.casos_uso.CasosUsoLugar
import com.example.conbasededatos.modelo.Lugar
import kotlinx.android.synthetic.main.vista_lugar.*
import java.text.DateFormat
import java.util.*

class VistaLugarFragment : Fragment() {
        val lugares by lazy { Aplicacion.lugares(requireActivity()) }
        //var lugares = LugaresBD(this)
        val adaptador by lazy { Aplicacion.adaptador(lugares,requireActivity()) }
        val usoLugar by lazy { CasosUsoLugar(requireActivity(),this, lugares,adaptador) }
        var pos = 0
        lateinit var lugar: Lugar
    val RESULTADO_EDITAR = 1
    val RESULTADO_GALERIA = 2  //poner antes de la clase
    val RESULTADO_FOTO = 3
    var uri = ""
    var _id: Int = -1
    private lateinit var uriUltimaFoto: Uri
    override fun onCreateView(inflador: LayoutInflater, contenedor: ViewGroup?,
                              savedInstanceState: Bundle? ): View? {
        setHasOptionsMenu(true)

        val vista = inflador.inflate(R.layout.vista_lugar, contenedor, false)
        return vista
    }


    override fun onActivityCreated(state: Bundle?) {
        super.onActivityCreated(state)
        pos = activity?.intent?.extras?.getInt("pos", 0) ?: 0
        _id = adaptador.idPosicion(pos)
        //lugar = lugares.elemento(pos)
        if(_id != -1){
            lugar = adaptador.lugarPosicion(pos)
            actualizaVistas()
        } else {
            Toast.makeText(activity, "No hay items", Toast.LENGTH_LONG).show();
            activity?.finish()
        }
        logo_hora.setOnClickListener { usoLugar.cambiarHora(pos) }
        hora.setOnClickListener { usoLugar.cambiarHora(pos) }
        logo_fecha.setOnClickListener { usoLugar.cambiarFecha(pos) }
        fecha.setOnClickListener { usoLugar.cambiarFecha(pos) }

    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.vista_lugar, menu)
        super.onCreateOptionsMenu(menu, inflater)
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
               // requireActivity().finish()
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

            if(scrollView1 != null) scrollView1.invalidate()
        } else if (requestCode == RESULTADO_GALERIA) {

            if (resultCode == RESULT_OK) {
                usoLugar.ponerFoto(pos, data?.dataString ?: "", foto)
                uri = data?.dataString?:""
            } else {
                Toast.makeText(activity, "Foto no cargada", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == RESULTADO_FOTO) {
            if (resultCode == Activity.RESULT_OK && uriUltimaFoto!=null) {
                lugar.foto = uriUltimaFoto.toString()
                usoLugar.ponerFoto(pos, lugar.foto, foto);
            } else {
                Toast.makeText(activity, "Error en captura", Toast.LENGTH_LONG).show()
            }
        }
    }
    fun actualizaVistas(){
        if (adaptador.itemCount == 0) return

        // Toast.makeText(activity, "numero $pos", Toast.LENGTH_LONG).show()
        lugar = adaptador.lugarPosicion(pos)
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
                usoLugar.actualizaPosLugar(pos, lugar, requireActivity())
                pos = adaptador.posicionId(_id)
            }
        barra_url.setOnClickListener { usoLugar.verPgWeb(lugar) }
        archivo.setOnClickListener { usoLugar.ponerDeGaleria(RESULTADO_GALERIA) }
        camara.setOnClickListener { uriUltimaFoto = usoLugar.tomarFoto(RESULTADO_FOTO) }
        borrar_foto.setOnClickListener { usoLugar.ponerFoto(pos, "", foto)}
        telefono.setOnClickListener{ usoLugar.llamarTelefono(lugar) }


        usoLugar.visualizarFoto(lugar, foto,uri, requireActivity())
        }
    fun borrar(){
        AlertDialog.Builder(requireActivity())
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
