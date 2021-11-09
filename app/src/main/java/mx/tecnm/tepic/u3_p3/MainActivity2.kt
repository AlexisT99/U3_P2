package mx.tecnm.tepic.u3_p3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import mx.tecnm.tepic.u3_p3.databinding.ActivityMain2Binding
import mx.tecnm.tepic.u3_p3.databinding.ActivityMainBinding

class MainActivity2 : AppCompatActivity() {
    var id = ""
    private lateinit var b: ActivityMain2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(b.root)

        var extras = intent.extras

        b.editText1.setText(extras!!.getString("lugar"))
        b.editText2.setText(extras!!.getString("hora"))
        b.editText3.setText(extras!!.getString("fecha"))
       b. editText4.setText(extras!!.getString("descripcion"))

        id = extras.getString("id").toString()

        b.btnActualizar.setOnClickListener {
            var actualizacion = evento(b.editText1.text.toString(), b.editText2.text.toString(), b.editText3.text.toString(),b.editText4.text.toString(),this)
            actualizacion.id = id.toInt()

            if(actualizacion.actualizar()) {
                Toast.makeText(this,"Actualización con exito", Toast.LENGTH_LONG)
                    .show()
                var ventana =
                    finish()
            } else {
                AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("No se pudo actualizar")
                    .setPositiveButton("OK"){d,i->}
                    .show()
            }
            finish()
        }
        b.btnRegresar.setOnClickListener {
            finish()
        }
    }
}