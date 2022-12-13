package es.javiercarrasco.mysqlite.activities

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.cursoradapter.widget.SimpleCursorAdapter
import com.google.android.material.snackbar.Snackbar
import es.javiercarrasco.mysqlite.MyDBOpenHelper
import es.javiercarrasco.mysqlite.R
import es.javiercarrasco.mysqlite.databinding.ActivitySpinnerBinding

class SpinnerActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySpinnerBinding

    private val amigosDBHelper = MyDBOpenHelper(this, null)
    private lateinit var db: SQLiteDatabase
    private lateinit var cursor: Cursor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpinnerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(R.string.bt_ver_spinner)

        // Se instancia la BD en modo lectura y se crea la SELECT.
        db = amigosDBHelper.readableDatabase
        cursor = db.rawQuery("SELECT * FROM ${MyDBOpenHelper.TABLA_AMIGOS};", null)

    // Código utilizado para el AutoCompleteTextView.
        val datos = ArrayList<String>()

        cursor.moveToFirst()
        do {
            datos.add(cursor.getString(1))
            cursor.moveToNext()
        }while (!cursor.isAfterLast) // Se convierte el Cursor en un ArrayList.

        ArrayAdapter(this, android.R.layout.simple_list_item_1, datos).also {
            binding.autoTextView.setAdapter(it)
        }

    // Código utilizado para el Spinner.
        // Se crea el adaptador mediante SimpleCursorAdapter.
        val adapter = SimpleCursorAdapter(
            this,
            android.R.layout.simple_list_item_2,
            cursor,
            arrayOf(cursor.columnNames[0], cursor.columnNames[1]),
            intArrayOf(android.R.id.text1, android.R.id.text2),
            SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        )

        // Se carga el adaptador en el Spinner.
        binding.spinner.adapter = adapter

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                pos: Int,
                id: Long
            ) {
                val cursorPos = binding.spinner.getItemAtPosition(pos) as Cursor
                Snackbar.make(binding.root, cursorPos.getString(1),Snackbar.LENGTH_SHORT).show()

                Log.d(
                    "Spinner",
                    "${cursorPos.getString(0)} - ${cursorPos.getString(1)}"
                )
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    override fun onDestroy() {
        cursor.close()
        db.close()
        super.onDestroy()
    }
}