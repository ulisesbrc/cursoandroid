package com.example.conbasededatos

import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.preference.PreferenceManager
import com.example.conbasededatos.modelo.GeoPunto
import com.example.conbasededatos.modelo.Lugar
import com.example.conbasededatos.modelo.TipoLugar
import com.example.conbasededatos.presentacion.Aplicacion

class LugaresBD(val contexto: Context?) :
    SQLiteOpenHelper(contexto, "lugares", null, 1) {
    override fun onCreate(bd: SQLiteDatabase) {
        bd.execSQL(
            "CREATE TABLE lugares (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nombre TEXT, " +
                    "direccion TEXT, " +
                    "longitud REAL, " +
                    "latitud REAL, " +
                    "tipo INTEGER, " +
                    "foto TEXT, " +
                    "telefono INTEGER, " +
                    "url TEXT, " +
                    "comentario TEXT, " +
                    "fecha BIGINT, " +
                    "valoracion REAL)"
        )
        bd.execSQL(
            ("INSERT INTO lugares VALUES (null, " +
                    "'Escuela Politécnica Superior de Gandía', " +
                    "'C/ Paranimf, 1 46730 Gandia (SPAIN)', -0.166093, 38.995656, " +
                    TipoLugar.EDUCACION.ordinal + ", '', 962849300, " +
                    "'http://www.epsg.upv.es', " +
                    "'Uno de los mejores lugares para formarse.', " +
                    System.currentTimeMillis() + ", 3.0)")
        )
        bd.execSQL(
            ("INSERT INTO lugares VALUES (null, 'Al de siempre', " +
                    "'P.Industrial Junto Molí Nou - 46722, Benifla (Valencia)', " +
                    " -0.190642, 38.925857, " + TipoLugar.BAR.ordinal + ", '', " +
                    "636472405, '', " + "'No te pierdas el arroz en calabaza.', " +
                    System.currentTimeMillis() + ", 3.0)")
        )
        bd.execSQL(
            ("INSERT INTO lugares VALUES (null, 'androidcurso.com', " +
                    "'ciberespacio', 0.0, 0.0," + TipoLugar.EDUCACION.ordinal + ", '', " +
                    "962849300, 'http://androidcurso.com', " +
                    "'Amplia tus conocimientos sobre Android.', " +
                    System.currentTimeMillis() + ", 5.0)")
        )
        bd.execSQL(
            ("INSERT INTO lugares VALUES (null,'Barranco del Infierno'," +
                    "'Vía Verde del río Serpis. Villalonga (Valencia)', -0.295058, " +
                    "38.867180, " + TipoLugar.NATURALEZA.ordinal + ", '', 0, " +
                    "'http://sosegaos.blogspot.com.es/2009/02/lorcha-villalonga-via-verde-del-" +
                    "rio.html', 'Espectacular ruta para bici o andar', " +
                    System.currentTimeMillis() + ", 4.0)")
        )
        bd.execSQL(
            ("INSERT INTO lugares VALUES (null, 'La Vital', " +
                    "'Avda. La Vital,0 46701 Gandia (Valencia)',-0.1720092,38.9705949," +
                    TipoLugar.COMPRAS.ordinal + ", '', 962881070, " +
                    "'http://www.lavital.es', 'El típico centro comercial', " +
                    System.currentTimeMillis() + ", 2.0)")
        )
    }

    override fun onUpgrade(
        db: SQLiteDatabase, oldVersion: Int,
        newVersion: Int
    ) {
    }

    fun extraeLugar(cursor: Cursor) = Lugar(
        nombre = cursor.getString(1),
        direccion = cursor.getString(2),
        posicion = GeoPunto(cursor.getDouble(3), cursor.getDouble(4)),
        tipoLugar = TipoLugar.values()[cursor.getInt(5)],
        foto = cursor.getString(6),
        telefono = cursor.getInt(7),
        url = cursor.getString(8),
        comentarios = cursor.getString(9),
        fecha = cursor.getLong(10),
        valoracion = cursor.getFloat(11)
    )

    fun extraeCursor(contexto: Context?): Cursor {
        val pref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(contexto)
        val cantidad = pref.getString("fragmentos", "10")
        val can: Int = cantidad!!.toInt()
        var consulta = when (pref.getString("orden", "0")) {
            "0" -> "SELECT * FROM lugares "
            "1" -> "SELECT * FROM lugares ORDER BY valoracion DESC"
            else -> {
                val lon = Aplicacion.posicionActual.longitud
                val lat = Aplicacion.posicionActual.latitud
                "SELECT * FROM lugares ORDER BY " +
                        "(($lon - longitud)*($lon - longitud) + " +
                        "($lat - latitud )*($lat - latitud ))"
            }
        }
        //consulta += " LIMIT ${pref.getString("maximo", "12")}"
        //return readableDatabase.rawQuery(consulta, null)
        return readableDatabase.rawQuery(consulta + " LIMIT $can",null)
    }
    fun elemento(id: Int): Lugar {
        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM lugares WHERE _id = $id", null)
        try {
            if (cursor.moveToNext())
                return extraeLugar(cursor)
            else
                throw SQLException("Error al acceder al elemento _id = $id")
        } catch (e:Exception) {
            throw e
        } finally {
            cursor?.close()
        }
    }
    fun actualiza(id:Int, lugar:Lugar) = with(lugar) {
        writableDatabase.execSQL("UPDATE lugares SET " +
                "nombre = '$nombre', direccion = '$direccion', " +
                "longitud = ${posicion.longitud}, latitud = ${posicion.latitud}, " +
                "tipo = ${tipoLugar.ordinal}, foto ='$foto', telefono =$telefono, "+
                "url = '$url', comentario = '$comentarios', fecha = $fecha, " +
                "valoracion = $valoracion  WHERE _id = $id")
    }
    fun nuevo():Int {
        var _id = -1
        val lugar = Lugar(
            nombre = "",
            direccion = "",
            posicion = GeoPunto.SIN_POSICION,
            tipoLugar = TipoLugar.OTROS,
            foto = "",
            telefono = 0,
            url = "",
            comentarios = "",
            fecha = 0,
            valoracion = 0F)
        writableDatabase.execSQL("INSERT INTO lugares (nombre, direccion, " +
                "longitud, latitud, tipo, foto, telefono, url, comentario, " +
                "fecha, valoracion) VALUES ('', '', ${lugar.posicion.longitud}, " +
                "${lugar.posicion.latitud}, ${lugar.tipoLugar.ordinal}, '', 0, " +
                "'', '', ${lugar.fecha},0 )")
        val c = readableDatabase.rawQuery((
                "SELECT _id FROM lugares WHERE fecha = " + lugar.fecha)+" ORDER BY _id DESC LIMIT 1", null)
        if (c.moveToNext()) _id = c.getInt(0)
        c.close()
        return _id
    }
    fun borrar(id: Int) {
        writableDatabase.execSQL("DELETE FROM lugares WHERE _id = $id")
    }
}
