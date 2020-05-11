package com.example.permisosenmarshmallow

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
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

class MainActivity : AppCompatActivity() {
    val SOLICITUD_PERMISO_WRITE_CALL_LOG = 0
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
