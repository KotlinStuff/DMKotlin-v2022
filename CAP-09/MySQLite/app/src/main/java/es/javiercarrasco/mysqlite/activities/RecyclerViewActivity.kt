package es.javiercarrasco.mysqlite.activities

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import es.javiercarrasco.mysqlite.MyDBOpenHelper
import es.javiercarrasco.mysqlite.R
import es.javiercarrasco.mysqlite.adapters.MyRecyclerAdapter
import es.javiercarrasco.mysqlite.databinding.ActivityRecyclerViewBinding

class RecyclerViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecyclerViewBinding

    private val amigosDBHelper = MyDBOpenHelper(this, null)
    private lateinit var db: SQLiteDatabase
    private lateinit var cursor: Cursor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecyclerViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(R.string.bt_ver_recyclerview)

        // Se instancia la BD en modo lectura y se crea la SELECT.
        db = amigosDBHelper.readableDatabase
        cursor = db.rawQuery(
            "SELECT * FROM ${MyDBOpenHelper.TABLA_AMIGOS};",
            null
        )

        // Se crea el adaptador con el resultado del cursor.
        val myRecyclerViewAdapter = MyRecyclerAdapter(cursor)

        // Montamos el RecyclerView.
        binding.myRecycler.setHasFixedSize(true)
        binding.myRecycler.layoutManager = LinearLayoutManager(this)
        binding.myRecycler.adapter = myRecyclerViewAdapter

    }

    // Se cierran las conexiones al terminar la activity.
    override fun onDestroy() {
        cursor.close()
        db.close()
        super.onDestroy()
    }
}