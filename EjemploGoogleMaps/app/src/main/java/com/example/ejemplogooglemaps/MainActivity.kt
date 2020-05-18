package com.example.ejemplogooglemaps

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : FragmentActivity(), OnMapReadyCallback,
    GoogleMap.OnMapClickListener {
    lateinit var mapa: GoogleMap
    val UPV = LatLng(39.481106, -0.340987)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapa)
                as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mapa = googleMap.apply {
            mapType = GoogleMap.MAP_TYPE_SATELLITE
            uiSettings.isZoomControlsEnabled = false
            moveCamera(CameraUpdateFactory.newLatLngZoom(UPV, 15f))
            addMarker(MarkerOptions().position(UPV).title("UPV")
                .snippet("Universidad Politécnica de Valencia")
                .icon(
                    BitmapDescriptorFactory.fromResource(
                    android.R.drawable.ic_menu_compass))
                .anchor(0.5f, 0.5f))
        }
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            mapa.isMyLocationEnabled = true
            mapa.uiSettings.isCompassEnabled = true
        }
        mapa.setOnMapClickListener(this)
    }

    fun moveCamera(view: View) {
        mapa.moveCamera(CameraUpdateFactory.newLatLng(UPV))
    }

    fun animateCamera(view: View) {
        mapa.animateCamera(CameraUpdateFactory.newLatLng(UPV))
    }

    fun addMarker(view: View) {
        mapa.addMarker(MarkerOptions().position(mapa.cameraPosition.target))
    }

    override fun onMapClick(puntoPulsado: LatLng) {
        mapa.addMarker(MarkerOptions().position(puntoPulsado)
            .icon(BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_YELLOW)))
    }
}