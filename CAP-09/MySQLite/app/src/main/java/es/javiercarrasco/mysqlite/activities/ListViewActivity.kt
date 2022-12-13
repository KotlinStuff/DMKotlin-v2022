package es.javiercarrasco.mysqlite.activities

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cursoradapter.widget.CursorAdapter
import es.javiercarrasco.mysqlite.MyDBOpenHelper
import es.javiercarrasco.mysqlite.R
import es.javiercarrasco.mysqlite.databinding.ActivityListViewBinding
import es.javiercarrasco.mysqlite.databinding.ItemListViewBinding

class ListViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListViewBinding

    private val amigosDBHelper = MyDBOpenHelper(this, null)
    private lateinit var db: SQLiteDatabase
    private lateinit var cursor: Cursor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(R.string.bt_ver_listview)

        // Se instancia la BD en modo lectura y se crea la SELECT.
        db = amigosDBHelper.readableDatabase
        cursor = db.rawQuery("SELECT * FROM ${MyDBOpenHelper.TABLA_AMIGOS};", null)

        // Se crea el CursorAdapter.
        val myCursorAdapter = MyListCursorAdapter(this, cursor)

        // Se carga los datos en el ListView.
        binding.myListview.adapter = myCursorAdapter
    }

    inner class MyListCursorAdapter(context: Context, cursor: Cursor) :
        CursorAdapter(context, cursor, FLAG_REGISTER_CONTENT_OBSERVER) {

        // "Infla" cada uno de los elementos de la lista.
        override fun newView(
            context: Context?,
            cursor: Cursor?,
            parent: ViewGroup?
        ): View = ItemListViewBinding.inflate(layoutInflater, parent, false).root

        // Rellena el ListView.
        override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
            val bindingItems = ItemListViewBinding.bind(view!!)
            with(bindingItems) {
                tvItemNombre.text = cursor!!.getString(1)
                tvItemApes.text = cursor.getString(2)

                view.setOnClickListener {
                    Toast.makeText(
                        this@ListViewActivity,
                        "${tvItemNombre.text} ${tvItemApes.text}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onDestroy() {
        cursor.close()
        db.close()

        super.onDestroy()
    }
}