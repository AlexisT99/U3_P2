package mx.tecnm.tepic.u3_p3

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BaseDatos(
    context: Context?,
    name: String,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int)
    : SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE AGENDA(IDAGENDA INTEGER PRIMARY KEY AUTOINCREMENT,LUGAR VARCHAR(200),HORA VARCHAR(7),"+
                "FECHA DATE, DESCRIPCION VARCHAR(200))")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}