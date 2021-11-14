package mx.tecnm.tepic.u3_p3

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteException

class Notas(l:String, h:String, f: String, d:String,p:Context) {
    var id = 0
    var titulo = l
    var hora = h
    var fecha = f
    var contenido = d
    var puntero = p
    val nombre = "NOTAS"


    fun insertar():Boolean {
        try {
            var base = BaseDatos(puntero!!, nombre, null, 1)
            var insertar = base.writableDatabase
            var datos = ContentValues()

            datos.put("TITULO", titulo)
            datos.put("HORA", hora)
            datos.put("FECHA", fecha)
            datos.put("CONTENIDO", contenido)

            var res = insertar.insert("NOTAS", "IDNOTAS", datos)

            if (res.toInt() == -1) {
                return false
            }
        }catch (e: SQLiteException) {
            return false
        }
        return true
    }

    fun recuperarDatos():ArrayList<Notas>{
        var data = ArrayList<Notas>()
        try{
            var bd = BaseDatos(puntero!!,nombre,null,1 )
            var select = bd.readableDatabase
            var columnas = arrayOf("*")

            var cursor  = select.query("NOTAS", columnas, null, null, null, null, null)
            if(cursor.moveToFirst()){
                do{
                    var temp = Notas(cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),puntero)
                    temp.id = cursor.getInt(0)
                    data.add(temp)
                }while (cursor.moveToNext())
            }else{
            }
        }catch (e:SQLiteException){
        }
        return data
    }

    fun consultaID(id:String): Notas{
        var registro = Notas("","","","",puntero)

        try {
            var bd = BaseDatos(puntero!!, nombre, null, 1)
            var select = bd.readableDatabase
            var busca = arrayOf("*")
            var buscaID = arrayOf(id)

            var res = select.query("NOTAS", busca, "IDNOTA =?",buscaID, null, null, null)
            if(res.moveToFirst()){
                registro.id = id.toInt()
                registro.titulo = res.getString(1)
                registro.hora = res.getString(2)
                registro.fecha = res.getString(3)
                registro.contenido = res.getString(4)
            }
        }catch (e:SQLiteException){
            e.message.toString()
        }
        return registro
    }

    fun eliminar(id:String):Boolean{
        try{
            var base = BaseDatos(puntero!!, nombre,null,1)
            var eliminar = base.writableDatabase
            var eliminarID = arrayOf(id)

            var res = eliminar.delete("NOTAS","IDNOTA = ?",eliminarID)
            if(res == 0){
                return false
            }
        }catch (e:SQLiteException){
            return false
        }
        return true
    }

    fun actualizar():Boolean{
        try{
            var base = BaseDatos(puntero!!, nombre,null,1)
            var actualizar = base.writableDatabase
            var datos = ContentValues()
            var actualizarID = arrayOf(id.toString())

            datos.put("TITULO", titulo)
            datos.put("HORA", hora)
            datos.put("FECHA", fecha)
            datos.put("CONTENIDO", contenido)

            var res = actualizar.update("NOTAS",datos,"IDNOTA = ?", actualizarID)
            if(res == 0){
                return false
            }
        }catch (e:SQLiteException){
            return false
        }
        return true
    }
}