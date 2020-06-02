package com.example.conbasededatos.casos_uso

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.*
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.conbasededatos.*
import com.example.conbasededatos.modelo.GeoPunto
import com.example.conbasededatos.modelo.Lugar
import com.example.conbasededatos.presentacion.*
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.text.DateFormat
import java.util.*


open class CasosUsoLugar(open val actividad: FragmentActivity,
                    open val fragment: Fragment?,
                    open val lugares: LugaresBD,
                    open val adaptador: AdaptadorLugaresBD
) :  TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    // OPERACIONES BÁSICAS
    fun mostrar(pos: Int) {
        var fragmentVista  = obtenerFragmentVista()
        if (fragmentVista != null) {
            fragmentVista.pos = pos
            fragmentVista._id = adaptador.idPosicion(pos)
            fragmentVista.actualizaVistas()
        } else {
            val i = Intent(actividad, VistaLugarActivity::class.java)
            i.putExtra("pos", pos)
            actividad.startActivity(i)
        }
    }

    fun obtenerFragmentVista(): VistaLugarFragment? {
        val manejador = actividad.supportFragmentManager
        return manejador.findFragmentById(R.id.vista_lugar_fragment) as
                VistaLugarFragment?
    }
    fun editar(pos: Int,codidoSolicitud: Int) {
        /*if (obtenerFragmentSelector() == null) {
            actividad.finish()
        } else {*/
            val i = Intent(actividad, EdicionLugarActivity::class.java)
            i.putExtra("pos", pos);
            fragment?.startActivityForResult(i, codidoSolicitud)
              ?:actividad.startActivityForResult(i, codidoSolicitud)
       // }
        //actividad.finish()

        //actividad.startActivityForResult(i, codidoSolicitud);
    }
    fun borrar(id: Int) {
        lugares.borrar(id)
        adaptador.cursor = lugares.extraeCursor(actividad)
        adaptador.notifyDataSetChanged()
        if (adaptador.itemCount > 0 ) {
            if (obtenerFragmentSelector() == null) {
                actividad.finish()
            } else {
                mostrar(0)
            }
        } else {
            actividad.finish()
        }
       // actividad.finish()
    }
    fun obtenerFragmentSelector(): SelectorFragment? {
        val manejador = actividad.supportFragmentManager
        return manejador.findFragmentById(R.id.selector_fragment) as
                SelectorFragment?
    }
    fun actualiza(id:Int, lugar: Lugar, contexto: Context){
        lugares.actualiza(id, lugar)
        adaptador.cursor = lugares.extraeCursor(contexto)
        adaptador.notifyDataSetChanged()
        //actividad.finish()
    }
    // INTENCIONES
    fun compartir(lugar: Lugar) = actividad.startActivity(
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, lugar.nombre + "-"+ lugar.url)
        })

    fun llamarTelefono(lugar: Lugar) = actividad.startActivity(
        Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + lugar.telefono)))

    fun verPgWeb(lugar: Lugar) = actividad.startActivity(
        Intent(Intent.ACTION_VIEW, Uri.parse(lugar.url)))

    fun verMapa(lugar: Lugar) {
        val lat = lugar.posicion.latitud
        val lon = lugar.posicion.longitud
        val uri = if (lugar.posicion != GeoPunto.SIN_POSICION)
            Uri.parse("geo:$lat,$lon")
        else Uri.parse("geo:0,0?q=" + lugar.direccion)
        actividad.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }
    // FOTOGRAFÍAS
    fun ponerDeGaleria(RESULTADO_GALERIA: Int) {
        val action= if (android.os.Build.VERSION.SDK_INT >= 19) { // API 19 - Kitkat
            Intent.ACTION_OPEN_DOCUMENT
        } else {
            Intent.ACTION_PICK
        }
        val i = Intent(action, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        fragment?.startActivityForResult(i, RESULTADO_GALERIA)
            ?:actividad.startActivityForResult(i, RESULTADO_GALERIA)
        //actividad.startActivityForResult(i, RESULTADO_GALERIA)
    }
    fun ponerFoto(pos: Int, uri: String, imageView: ImageView) {
        //val lugar = lugares.elemento(pos)
        val lugar = Aplicacion.adaptador(lugares,actividad).lugarPosicion(pos)
        lugar.foto = uri ?: ""
        visualizarFoto(lugar, imageView, uri,actividad)
        actualizaPosLugar(pos, lugar, actividad)
    }

    fun visualizarFoto(lugar: Lugar, imageView: ImageView, uri: String, context: Context) {
        if (!(lugar.foto == null || lugar.foto.isEmpty())) {
           // imageView.setImageURI(Uri.parse(uri))
            if(uri !== ""){
                imageView.setImageBitmap(reduceBitmap(context, uri, 10,   10));
            } else {
                imageView.setImageBitmap(reduceBitmap(context, lugar.foto, 10,   10));
            }

        } else {
            imageView.setImageBitmap(null)
        }
    }
    fun tomarFoto(codidoSolicitud: Int): Uri {
        /*try { */
            val file = File.createTempFile(
                "img_" + System.currentTimeMillis() / 1000, ".jpg",
                actividad.getExternalFilesDir(Environment.DIRECTORY_PICTURES) )
            val uriUltimaFoto = if (Build.VERSION.SDK_INT >= 24)
                FileProvider.getUriForFile(
                    actividad, "com.example.conbasededatos.fileProvider", file )
            else Uri.fromFile(file)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriUltimaFoto)

        fragment?.startActivityForResult(intent, codidoSolicitud)
            ?:actividad.startActivityForResult(intent, codidoSolicitud)
             return uriUltimaFoto
       /* } catch (ex: IOException) {
            Toast.makeText(actividad, "Error al crear fichero de imagen",
                Toast.LENGTH_LONG).show()
            //return null
        } */
    }
    private fun reduceBitmap(
        contexto: Context, uri: String,
        maxAncho: Int, maxAlto: Int
    ): Bitmap? {
        return try {
            var input: InputStream? = null
            val u = Uri.parse(uri)
            input = if (u.scheme == "http" || u.scheme == "https") {
                URL(uri).openStream()
            } else {
                contexto.getContentResolver().openInputStream(u)
            }
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            options.inSampleSize = Math.max(
                Math.ceil(options.outWidth / maxAncho.toDouble()),
                Math.ceil(options.outHeight / maxAlto.toDouble())
            ).toInt()
            options.inJustDecodeBounds = false
            BitmapFactory.decodeStream(input, null, options)
        } catch (e: FileNotFoundException) {
            Toast.makeText(
                contexto, "Fichero/recurso de imagen no encontrado",
                Toast.LENGTH_LONG
            ).show()
            e.printStackTrace()
            null
        } catch (e: IOException) {
            Toast.makeText(
                contexto, "Error accediendo a imagen",
                Toast.LENGTH_LONG
            ).show()
            e.printStackTrace()
            null
        }
    }
    fun actualizaPosLugar(pos: Int, lugar: Lugar, contexto: Context) {
        val id = adaptador.idPosicion(pos)
        //guardar(id, lugar);  //
        actualiza(id, lugar,contexto)
    }
    fun nuevo() {
        val _id = lugares.nuevo()
        val posicion = Aplicacion.posicionActual
        if (posicion != GeoPunto.SIN_POSICION) {
            val lugar = lugares.elemento(_id)
            lugar.posicion = posicion
            lugares.actualiza(_id, lugar)
        }
        val i = Intent(actividad, EdicionLugarActivity::class.java)
        i.putExtra("_id", _id)
        actividad.startActivity(i)
    }
    var pos: Int = -1
    lateinit var lugar: Lugar

    fun cambiarHora(pos: Int) {//, textView: TextView
        lugar = adaptador.lugarPosicion(pos)
        this.pos = pos
        val dialogo = DialogoSelectorHora()
        dialogo.setOnTimeSetListener(this)
        val args = Bundle()
        args.putLong("fecha", lugar.fecha)
        dialogo.setArguments(args)
        dialogo.show(actividad.supportFragmentManager, "selectorHora")
    }

    override fun onTimeSet(vista: TimePicker?, hora: Int, minuto: Int) {
        val calendario = Calendar.getInstance()
        calendario.setTimeInMillis(lugar.fecha)
        calendario.set(Calendar.HOUR_OF_DAY, hora)
        calendario.set(Calendar.MINUTE, minuto)
        lugar.fecha = calendario.getTimeInMillis()
        actualizaPosLugar(pos, lugar,actividad)
        val textView = actividad.findViewById<TextView>(R.id.hora)
        textView.text= DateFormat.getTimeInstance().format(Date(lugar.fecha))
    }
    fun cambiarFecha(pos: Int) {
        lugar = adaptador.lugarPosicion(pos)
        this.pos = pos
        val dialogo = DialogoSelectorFecha()
        dialogo.setOnDateSetListener(this)
        val args = Bundle()
        args.putLong("fecha", lugar.fecha)
        dialogo.setArguments(args)
        dialogo.show(actividad.supportFragmentManager, "selectorFecha")
    }

    override fun onDateSet(view: DatePicker, año: Int, mes: Int, dia: Int) {
        val calendario = Calendar.getInstance()
        calendario.timeInMillis = lugar.fecha
        calendario.set(Calendar.YEAR, año)
        calendario.set(Calendar.MONTH, mes)
        calendario.set(Calendar.DAY_OF_MONTH, dia)
        lugar.fecha = calendario.timeInMillis
        actualizaPosLugar(pos, lugar,actividad)
        val textView = actividad.findViewById<TextView>(R.id.fecha)
        textView.text = java.text.DateFormat.getDateInstance().format(Date(lugar.fecha))
    }

}