package com.example.localizacion

import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), LocationListener {

    val TIEMPO_MIN = (10 * 1000).toLong() // 10 segundos
    val DISTANCIA_MIN = 5.0F // 5 metros
    val A = arrayOf("n/d", "preciso", "impreciso")
    val P = arrayOf("n/d", "bajo", "medio", "alto")
    val E = arrayOf("fuera de servicio", "temporalmente no disponible",
            "disponible")
    lateinit var manejador: LocationManager
    lateinit var proveedor: String

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
    }
    override fun onPause() {
        super.onPause()
        manejador.removeUpdates(this)
    }

    // Métodos de la interfaz LocationListener
    override fun onLocationChanged(location: Location) {
        log("Nueva localización: ")
        muestraLocaliz(location)
    }

    override fun onProviderDisabled(proveedor: String) {
        log("Proveedor deshabilitado: $proveedor\n")
    }

    override fun onProviderEnabled(proveedor: String) {
        log("Proveedor habilitado: $proveedor\n")
    }

    override fun onStatusChanged(proveedor: String, estado: Int,
                                 extras: Bundle) {
        log("Cambia estado proveedor: $proveedor, estado="+
                " ${E[Math.max(0, estado)]}, extras= $extras\n")
    }

    // Métodos para mostrar información
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
}