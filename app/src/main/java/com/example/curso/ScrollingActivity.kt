package com.example.curso

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.curso.casos_uso.CasosUsoActividades
import com.example.curso.casos_uso.CasosUsoLugar
import com.example.curso.modelo.Lugar
import com.example.curso.presentacion.Aplicacion
import kotlinx.android.synthetic.main.botones.*
import kotlinx.android.synthetic.main.content_main.*
import java.lang.Integer.parseInt


class ScrollingActivity : AppCompatActivity() {
    val SOLICITUD_PERMISO_LOCALIZACION = 1
    val usoLocalizacion by lazy {
        CasosUsoLocalizacion(this, SOLICITUD_PERMISO_LOCALIZACION) }
    val actividad by lazy { CasosUsoActividades(this) }
    val lugares by lazy { Aplicacion.lugares }
    val usoLugar by lazy { CasosUsoLugar(this, lugares) }
    val adaptador by lazy { Aplicacion.adaptador }
    lateinit var lugar: Lugar

    //var mp: MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
       // mp = MediaPlayer.create(this, R.raw.audio)
       // mp?.start()
       /*setContentView(R.layout.content_main)

        recycler_view.apply {
            setHasFixedSize(true)
           // layoutManager = GridLayoutManager(this@ScrollingActivity,2)
           layoutManager = LinearLayoutManager(this@ScrollingActivity)
            adapter = adaptador
        }
        adaptador.onClick  =  {
            val pos = recycler_view.getChildAdapterPosition(it)
            usoLugar.mostrar(pos)
        }
        Toast.makeText(this, "Método onCreate", Toast.LENGTH_SHORT).show();

*/
        button03.setOnClickListener{
            //lanzarAcercaDe()
            actividad.lanzarAcerdaDe()
        }
        button04.setOnClickListener{
            finish()
        }

       /* setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

         */
    }
    override fun onResume() {
        super.onResume()
        usoLocalizacion.activar()
    }

    override fun onPause() {
        super.onPause()
        usoLocalizacion.desactivar()
    }
    override fun onStart() {
        super.onStart()
        Toast.makeText(this, "onStart", Toast.LENGTH_SHORT).show()
    }
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray ) {
        if (requestCode == SOLICITUD_PERMISO_LOCALIZACION
                && grantResults.size == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            usoLocalizacion.permisoConcedido()

    }
    /*override fun onResume() {
        super.onResume()
        Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show()
    }

    override fun onPause() {
        Toast.makeText(this, "onPause", Toast.LENGTH_SHORT).show()
        super.onPause()
        //mp?.pause();
    }
     */

    override fun onStop() {
        Toast.makeText(this, "onStop", Toast.LENGTH_SHORT).show()
        super.onStop()
    }

    override fun onRestart() {
        super.onRestart()
        Toast.makeText(this, "onRestart", Toast.LENGTH_SHORT).show()
       // mp?.start();
    }

    override fun onDestroy() {
        Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show()
        super.onDestroy()
    }
    /*override fun onSaveInstanceState(estadoGuardado: Bundle) {
        super.onSaveInstanceState(estadoGuardado)
        if (mp != null) {
            val pos = mp?.getCurrentPosition()
            val aux = pos.toString()
            val auxs = aux.toInt()
            estadoGuardado.putInt("posicion", auxs)
        }
    }

    override fun onRestoreInstanceState(estadoGuardado: Bundle?) {
        super.onRestoreInstanceState(estadoGuardado)
        if (estadoGuardado != null && mp != null) {
           // val pos = estadoGuardado.getInt("posicion")
           // mp?.seekTo(pos)
        }
    }
*/

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            //R.id.action_settings -> true
            R.id.acercaDe -> {
                lanzarAcercaDe()
                true
            }
            R.id.action_settings -> {
                lanzarPreferencias()
                true
            }
            R.id.menu_buscar -> {
                lanzarVistaLugar()
                true;
            }
            R.id.menu_mapa -> {
                startActivity(Intent(this, MapaActivity::class.java))
                true;
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    fun lanzarAcercaDe(view: View? = null) {
        val i = Intent(this, AcercaDeActivity::class.java)
        startActivity(i)
    }
    fun lanzarPreferencias(view: View? = null) {
        val i = Intent(this, PreferenciasActivity::class.java)
        startActivity(i)
    }
    fun salir(view: View?) {
        finish()
    }
    fun mostrarPreferencias(view: View?) {
        val pref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val s = ("musica: " + pref.getBoolean("musica", true)
                + ", graficos: " + pref.getString("graficos", "?")
                + ", Número de Fragmentos: " + pref.getString("fragmentos", ""))
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }
    fun lanzarVistaLugar(view: View? = null) {
        val entrada = EditText(this)
        entrada.setText("0")
        AlertDialog.Builder(this)
            .setTitle("Selección de lugar")
            .setMessage("indica su id:")
            .setView(entrada)
            .setPositiveButton("Ok")  { dialog, whichButton ->
               val id = parseInt(entrada.getText().toString())
                usoLugar.mostrar(id);
              // Toast.makeText(this id, Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }


}
