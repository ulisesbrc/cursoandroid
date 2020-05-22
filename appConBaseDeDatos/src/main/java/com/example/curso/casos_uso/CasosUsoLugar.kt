package com.example.curso.casos_uso

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.curso.EdicionLugarActivity
import com.example.curso.R
import com.example.curso.VistaLugarActivity
import com.example.curso.datos.LugaresLista
import com.example.curso.modelo.GeoPunto
import com.example.curso.modelo.Lugar
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.net.URL


class CasosUsoLugar(val actividad: Activity,
                    val lugares: LugaresLista
) {
    // OPERACIONES BÁSICAS
    fun mostrar(pos: Int) {
        val i = Intent(actividad, VistaLugarActivity::class.java)
        i.putExtra("pos", pos);
        actividad.startActivity(i);
    }
    fun editar(pos: Int,codidoSolicitud: Int) {
        //actividad.finish()
        val i = Intent(actividad, EdicionLugarActivity::class.java)
        i.putExtra("pos", pos);
        actividad.startActivityForResult(i, codidoSolicitud);
    }
    fun borrar(id: Int) {
        lugares.borrar(id)
        actividad.finish()
    }
    fun actualiza(id:Int, lugar: Lugar){
        lugares.actualiza(id, lugar)
        actividad.finish()
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
        actividad.startActivityForResult(i, RESULTADO_GALERIA)
    }
    fun ponerFoto(pos: Int, uri: String, imageView: ImageView) {
        val lugar = lugares.elemento(pos)
        lugar.foto = uri ?: ""
        visualizarFoto(lugar, imageView, uri)
    }

    fun visualizarFoto(lugar: Lugar, imageView: ImageView, uri: String) {
        if (!(lugar.foto == null || lugar.foto.isEmpty())) {
            //imageView.setImageURI(Uri.parse(uri))
           val resources : Resources = Resources.getSystem()
            imageView.setImageBitmap(
                decodeSampledBitmapFromResource(resources, R.id.foto, 100, 100)
            )
           // imageView.setImageBitmap(reduceBitmap(contexto, uri, 1024,   1024));
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
                    actividad, "com.example.curso.fileProvider", file )
            else Uri.fromFile(file)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriUltimaFoto)
            actividad.startActivityForResult(intent, codidoSolicitud)
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
    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }
    fun decodeSampledBitmapFromResource(
        res: Resources,
        resId: Int,
        reqWidth: Int,
        reqHeight: Int
    ): Bitmap {
        // First decode with inJustDecodeBounds=true to check dimensions
        return BitmapFactory.Options().run {
            inJustDecodeBounds = true
            BitmapFactory.decodeResource(res, resId, this)

            // Calculate inSampleSize
            inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)

            // Decode bitmap with inSampleSize set
            inJustDecodeBounds = false

            BitmapFactory.decodeResource(res, resId, this)
        }
    }
}