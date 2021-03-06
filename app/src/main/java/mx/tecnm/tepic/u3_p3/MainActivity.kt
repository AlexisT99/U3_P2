package mx.tecnm.tepic.u3_p3

import android.R
import android.content.Intent
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import mx.tecnm.tepic.u3_p3.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    var listaID = ArrayList<String>()
    var base = BaseDatos(this, "NOTAS", null, 1)
    //FIREBASE
    var baseRemota = FirebaseFirestore.getInstance()
    var datosRemotos = ArrayList<String>()
    private lateinit var b: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)
        b.btnInsertar.setOnClickListener {
            var Notas = Notas(
                b.editText1.text.toString(),
                b.editText2.text.toString(),
                b.editText3.text.toString(),
                b.editText4.text.toString(),
                this
            )

            if(Notas.insertar()) {
                mensaje("La información se ha guardado")
                b.editText1.setText("")
                b.editText2.setText("")
                b.editText3.setText("")
                b.editText4.setText("")
                cargaInformacion()
            } else {
                mensaje("Error al guardar")
            }
        }
        cargaInformacion()

        b.btnSincronizar.setOnClickListener {
                sincronizar()
        }
    }

    private fun cargaInformacion(){
        try {
            var c = Notas("","","","",this)
            var datos = c.recuperarDatos()

            var tamaño = datos.size-1
            var v = Array(datos.size,{""})

            listaID = ArrayList()
            (0..tamaño).forEach {
                var Notas = datos[it]
                var item = Notas.titulo+"\n"+Notas.hora+"\n"+Notas.fecha+"\n"+Notas.contenido
                v[it] = item
                listaID.add(Notas.id.toString())
            }

            b.lista.adapter = ArrayAdapter(this, R.layout.simple_list_item_1,v)

            b.lista.setOnItemClickListener{ _, _, i, _ ->
                mostrarAlertEliminarActualizar(i)
            }
        }catch (e: Exception){
            mensaje(e.message.toString())
        }
    }
    private fun mensaje(s: String) {
        AlertDialog.Builder(this).setTitle("Atención").setMessage(s)
            .setPositiveButton("OK"){
                    d,i-> d.dismiss()
            }
            .show()
    }

    private fun sincronizar() {
        datosRemotos.clear()
        baseRemota.collection("Notas")
            .addSnapshotListener {querySnapshot, firebaseFirestoreException ->
                if(firebaseFirestoreException !=null) {
                    mensaje("No se pudo conectar con la nube")
                    return@addSnapshotListener
                }

                var cadena= ""

                for(registro in querySnapshot!!)
                {
                    cadena=registro.id
                    datosRemotos.add(cadena)
                }

                try {
                    var c = base.readableDatabase
                    var res = c.query("NOTAS", arrayOf("*"),null,null,null,null,null)

                    if(res.moveToFirst())
                    {
                        do {
                            if(datosRemotos.contains(res.getString(0))) {
                                baseRemota.collection("Notas")
                                    .document(res!!.getString(0))
                                    .update("TITULO",res!!.getString(1),"HORA",res!!.getString(2), "FECHA",res!!.getString(3), "CONTENIDO", res!!.getString(4))
                                    .addOnFailureListener {
                                        AlertDialog.Builder(this)
                                            .setTitle("ERROR")
                                            .setMessage("El espejeo no funciono")
                                            .setPositiveButton("OK"){d,i->}
                                            .show()
                                    }
                            } else {
                                var datosInsertar = hashMapOf(
                                    "TITULO" to res!!.getString(1),
                                    "HORA" to res!!.getString(2),
                                    "FECHA" to res!!.getString(3),
                                    "CONTENIDO" to res!!.getString(4)
                                )

                                baseRemota.collection("Notas").document("${res!!.getString(0)}")
                                    .set(datosInsertar as Any)
                                    .addOnFailureListener{
                                        mensaje("No se pudo espejear")
                                    }
                            }
                        }while(res.moveToNext())
                    } else {
                        datosRemotos.add("No hay cambios que subir")
                    }
                    c.close()
                } catch (e: SQLiteException) {
                    mensaje("Algo ha fallado: " + e.message!!)
                }
                deleteRemoto()
            }
        mensaje("Sincronización con exito")
    }

    private fun deleteRemoto() {
        var eliminarRemoto= datosRemotos.subtract(listaID)
        if(eliminarRemoto.isEmpty()) {
        } else {
            eliminarRemoto.forEach(){
                baseRemota.collection("Notas")
                    .document(it)
                    .delete()
                    .addOnFailureListener{
                        mensaje("EL espejeo ha fallado")
                    }
            }
        }
    }

    private fun mostrarAlertEliminarActualizar(posicion: Int) {
        var idLista = listaID.get(posicion)

        AlertDialog.Builder(this)
            .setTitle("Atencion")
            .setMessage("¿Qué deseas hacer con este registro?")
            .setPositiveButton("Eliminar") {d,i-> eliminar(idLista)}
            .setNeutralButton("Cancelar") {d,i->}
            .setNegativeButton("Actualizar") {d,i-> llamarVentanaActualizar(idLista)}
            .show()
    }

    private fun eliminar(id:String) {
        var c = Notas("","","","",this)
        if(c.eliminar(id)) {
            mensaje("Se ha borrado correctamente la Nota")
            cargaInformacion()
        } else {
            mensaje("Ha ocurrido un error")
        }
    }

    private fun llamarVentanaActualizar(idLista: String) {
        var ventana = Intent(this,MainActivity2::class.java)
        var c = Notas("","","","",this)
        var l = c.consultaID(idLista).titulo
        var h = c.consultaID(idLista).hora
        var f = c.consultaID(idLista).fecha
        var d = c.consultaID(idLista).contenido

        ventana.putExtra("id",idLista)
        ventana.putExtra("lugar",l)
        ventana.putExtra("hora",h)
        ventana.putExtra("fecha",f)
        ventana.putExtra("descripcion",d)
        startActivity(ventana)
    }
    override fun onResume() {
        super.onResume()
        cargaInformacion()
    }
}