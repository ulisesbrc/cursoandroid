package com.example.conbasededatos

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.conbasededatos.presentacion.Aplicacion

class CasosUsoLocalizacion(val actividad: Activity,
                           val codigoPermiso: Int) : LocationListener {
    val TAG = "MisLugares"
    val manejadorLoc = actividad.getSystemService(
            AppCompatActivity.LOCATION_SERVICE) as LocationManager
    var mejorLoc: Location? = null
    var posicionActual = Aplicacion.posicionActual
    val lugares by lazy { Aplicacion.lugares(actividad) }
    //var lugares = LugaresBD(this)
    val adaptador by lazy { Aplicacion.adaptador(lugares,actividad) }
    //val adaptador = Aplicacion.adaptador
    val DOS_MINUTOS:Long = (2 * 60 * 1000)
    init {
        ultimaLocalizazion()
    }
    fun hayPermisoLocalizacion() = (ActivityCompat.checkSelfPermission(
            actividad, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED)
    @SuppressLint("MissingPermission")
    fun ultimaLocalizazion() {
        if (hayPermisoLocalizacion()) {
            if (manejadorLoc.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                actualizaMejorLocaliz( manejadorLoc.getLastKnownLocation(
                        LocationManager.GPS_PROVIDER))
            }
            if (manejadorLoc.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                actualizaMejorLocaliz( manejadorLoc.getLastKnownLocation(
                        LocationManager.NETWORK_PROVIDER))
            }
        } else {
            solicitarPermiso(Manifest.permission.ACCESS_FINE_LOCATION,
                    "Sin el permiso localización no puedo mostrar la distancia" +
                            " a los lugares.", codigoPermiso, actividad)
        }
    }


    fun permisoConcedido() {
        ultimaLocalizazion()
        activarProveedores()
        adaptador.notifyDataSetChanged()
    }

    private fun activarProveedores() {
        if (hayPermisoLocalizacion()) {
            if (manejadorLoc.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                manejadorLoc.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,20 * 1000, 5F, this )
            }
            if (manejadorLoc.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                manejadorLoc.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, 10 * 1000, 10F, this )
            }
        } else {
            solicitarPermiso(Manifest.permission.ACCESS_FINE_LOCATION,
                    "Sin el permiso localización no puedo mostrar la distancia" +
                            " a los lugares.", codigoPermiso, actividad )
        }
    }
    fun solicitarPermiso(permiso: String, justificacion: String,
                         requestCode: Int, actividad: Activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(actividad,
                        permiso)) {
            AlertDialog.Builder(actividad)
                    .setTitle("Solicitud de permiso")
                    .setMessage(justificacion)
                    .setPositiveButton("Ok", DialogInterface.OnClickListener {
                        dialog, whichButton -> ActivityCompat.requestPermissions(
                            actividad, arrayOf(permiso), requestCode )
                    }).show()
        } else {
            ActivityCompat.requestPermissions(
                    actividad,arrayOf(permiso), requestCode)
        }
    }
    override fun onLocationChanged(location: Location) {
        Log.d(TAG, "Nueva localización: $location")
        actualizaMejorLocaliz(location)
        adaptador.notifyDataSetChanged()
    }
    override fun onProviderDisabled(proveedor: String) {
        Log.d(TAG, "Se deshabilita: $proveedor")
        activarProveedores()
    }
    override fun onProviderEnabled(proveedor: String) {
        Log.d(TAG, "Se habilita: $proveedor")
        activarProveedores()
    }
    override fun onStatusChanged(proveedor:String, estado:Int, extras: Bundle){
        Log.d(TAG, "Cambia estado: $proveedor")
        activarProveedores()
    }
    private fun actualizaMejorLocaliz(loc: Location?) {
        if (loc != null && (mejorLoc == null
                        || loc.accuracy < 2 * mejorLoc!!.getAccuracy()
                        || loc.time - mejorLoc!!.getTime() > DOS_MINUTOS)) {
            Log.d(TAG, "Nueva mejor localización")
            mejorLoc = loc
            //(application as Aplicacion).posicionActual.latitud = loc.latitude
            //((application as Aplicacion).posicionActual.longitud = loc.longitude
            Aplicacion.posicionActual.latitud = loc.latitude
            Aplicacion.posicionActual.longitud = loc.longitude
        }
    }
    fun activar() {
        if (hayPermisoLocalizacion()) activarProveedores()
    }

    fun desactivar() {
        if (hayPermisoLocalizacion()) manejadorLoc.removeUpdates(this)
    }
}