package com.example.permisosenmarshmallow

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.CallLog
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.fragment_first.*

class MainActivity : AppCompatActivity() {
    val SOLICITUD_PERMISO_WRITE_CALL_LOG = 0
    val SOLICITUD_PERMISO_GPS_LOG = 1
    lateinit var manejador: LocationManager
    lateinit var proveedor: String
    val TIEMPO_MIN = (10 * 1000).toLong() // 10 segundos
    val DISTANCIA_MIN = 5.0F // 5 metros
    val A = arrayOf("n/d", "preciso", "impreciso")
    val P = arrayOf("n/d", "bajo", "medio", "alto")
    val E = arrayOf("fuera de servicio", "temporalmente no disponible",
        "disponible")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            borrarLlamada();
            /*view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()*/
        }
        gps.setOnClickListener {
            mostrargps();
            /*view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()*/
        }

    }
    fun borrarLlamada() {
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission
                        .WRITE_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
            getContentResolver().delete(CallLog.Calls.CONTENT_URI,
                    "number='555555555'", null);
            Snackbar.make(vista_principal, "Llamadas borradas del registro.",
                    Snackbar.LENGTH_SHORT).show();
        } else {
            solicitarPermiso(Manifest.permission.WRITE_CALL_LOG, "Sin el permiso"+
                    " administrar llamadas no puedo borrar llamadas del registro.",
                    SOLICITUD_PERMISO_WRITE_CALL_LOG, this);
        }
    }
    fun mostrargps() {
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission
                .ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //hacer
            manejador = getSystemService(Context.LOCATION_SERVICE) as
                    LocationManager
            log("Proveedores de localización: \n ")
            muestraProveedores()
            val criterio = Criteria().apply {
                isCostAllowed = false
                isAltitudeRequired = false
                accuracy = Criteria.ACCURACY_FINE
            }
            proveedor = manejador.getBestProvider(criterio, true)
            log("Mejor proveedor: $proveedor\n")
            log("Comenzamos con la última localización conocida:")
            muestraLocaliz(manejador.getLastKnownLocation(proveedor))

            Snackbar.make(vista_principal, "Datos del gps.",
                Snackbar.LENGTH_SHORT).show();
        } else {
            solicitarPermisogps(Manifest.permission.ACCESS_FINE_LOCATION, "Sin el permiso"+
                    " de la ubicación no puedo accecer al gps.",
                SOLICITUD_PERMISO_GPS_LOG, this);
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
    fun solicitarPermisogps(permiso: String, justificacion: String,
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
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == SOLICITUD_PERMISO_WRITE_CALL_LOG) {
            if (grantResults.size == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                borrarLlamada()
            } else {
                Toast.makeText(this, "Sin el permiso, no puedo realizar la " +
                        "acción", Toast.LENGTH_SHORT).show()
            }
        }
        if (requestCode == SOLICITUD_PERMISO_GPS_LOG) {
            if (grantResults.size == 1 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mostrargps()
            } else {
                Toast.makeText(this, "Sin el permiso, no puedo ver los datos " +
                        "del gps", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun log(cadena: String) = salida.append(cadena + "\n")

    private fun muestraLocaliz(localizacion: Location?) {
        if (localizacion == null)
            log("Localización desconocida\n")
        else
            log(localizacion!!.toString() + "\n")
    }

    private fun muestraProveedores() {
        log("Proveedores de localización: \n ")
        val proveedores = manejador.getAllProviders()
        for (proveedor in proveedores) {
            muestraProveedor(proveedor)
        }
    }

    private fun muestraProveedor(proveedor: String) {
        with(   manejador.getProvider(proveedor)) {
            log("LocationProvider[ " + "getName= $name, isProviderEnabled" +
                    "=${manejador.isProviderEnabled(proveedor)}, " +
                    "getAccuracy=${A[Math.max(0, accuracy)]}, " +
                    "getPowerRequirement=${P[Math.max(0, powerRequirement)]}, " +
                    "hasMonetaryCost=${hasMonetaryCost()}, " +
                    "requiresCell=${requiresCell()}, " +
                    "requiresNetwork=${requiresNetwork()}, " +
                    "requiresSatellite=${requiresSatellite()}, " +
                    "supportsAltitude=${supportsAltitude()}, " +
                    "supportsBearing=${supportsBearing()}, " +
                    "supportsSpeed=${supportsSpeed()} ]\n")
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
