package es.javiercarrasco.mysqlite

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import es.javiercarrasco.mysqlite.activities.ListViewActivity
import es.javiercarrasco.mysqlite.activities.RecyclerViewActivity
import es.javiercarrasco.mysqlite.activities.SpinnerActivity
import es.javiercarrasco.mysqlite.databinding.ActivityMainBinding
import es.javiercarrasco.mysqlite.databinding.DialogoBinding

const val UPDATE = "update"
const val DELETE = "delete"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var amigosDBHelper: MyDBOpenHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Se instancia el objeto MyDBOpenHelper.
        amigosDBHelper = MyDBOpenHelper(this, null)

        with(binding) {
            // Botón INSERTAR.
            btnInsertar.setOnClickListener {
                if (etNombre.text!!.isNotBlank() && etApes.text!!.isNotBlank()) {
                    // Se inserta en la tabla.
                    amigosDBHelper.addAmigo(
                        etNombre.text.toString().trim(),
                        etApes.text.toString().trim()
                    )

                    // Se limpian los EditText después de la inserción.
                    etNombre.text!!.clear()
                    etApes.text!!.clear()
                } else {
                    myToast("Los campos no pueden estar vacíos.")
                }
            }

            // Botón ACTUALIZAR.
            btnActualizar.setOnClickListener {
                if (etNombre.text!!.isNotBlank()) {
                    // Se lanza el dialogo para solicitar el id del registro,
                    // además, se indica el tipo de operación.
                    solicitaIdentificador(UPDATE)
                } else {
                    myToast("El campo nombre no debe estar vacío.")
                }
            }

            // Botón ELIMINAR.
            btnEliminar.setOnClickListener {
                // Se lanza el dialogo para solicitar el id del registro,
                // además, se indica el tipo de operación.
                solicitaIdentificador(DELETE)
            }

            // Botón CONSULTAR.
            btnConsultar.setOnClickListener {
                tvResult.text = ""

                // Se instancia la BD en modo lectura y se crea la SELECT.
                val db: SQLiteDatabase = amigosDBHelper.readableDatabase
                val cursor: Cursor = db.rawQuery(
                    "SELECT * FROM ${MyDBOpenHelper.TABLA_AMIGOS};",
                    null
                )

                // Se comprueba que al menos exista un registro.
                if (cursor.moveToFirst()) {
                    do {
                        tvResult.append(cursor.getInt(0).toString() + " - ")
                        tvResult.append(cursor.getString(1).toString() + " ")
                        tvResult.append(cursor.getString(2).toString() + "\n")
                    } while (cursor.moveToNext())
                } else {
                    myToast("No existen datos a mostrar.")
                }
                cursor.close()
                db.close()
            }

            // Botón VER EN LISTVIEW.
            btnVerListview.setOnClickListener {
                val myIntent = Intent(this@MainActivity, ListViewActivity::class.java)
                startActivity(myIntent)
            }

            // Botón VER EN SPINNER.
            btnVerSpinner.setOnClickListener {
                val myIntent = Intent(this@MainActivity, SpinnerActivity::class.java)
                startActivity(myIntent)
            }

            // Botón VER EN RECYCLERVIEW.
            btnVerRecyclerView.setOnClickListener {
                val myIntent = Intent(this@MainActivity, RecyclerViewActivity::class.java)
                startActivity(myIntent)
            }
        }
    }

    /**
     * Método encargado de mostrar un cuadro de diálogo para pedir el identificador al
     * usuario y realizar la acción correspondiente según la acción requerida.
     */
    fun solicitaIdentificador(accion: String) {
        // Se infla la vista para el diálogo.
        val dialogBinding = DialogoBinding.inflate(layoutInflater)

        // Se crea el builder.
        val builder = AlertDialog.Builder(this).setView(dialogBinding.root)

        builder.apply {
            setPositiveButton(android.R.string.ok) { dialog, _ ->
                if (!dialogBinding.etIdentificador.text.isNullOrBlank()) {
                    val identificador = dialogBinding.etIdentificador.text.toString()

                    when (accion) { // Se realiza la acción.
                        UPDATE -> {
                            amigosDBHelper.updateAmigo(
                                identificador.toInt(),
                                binding.etNombre.text.toString()
                            )

                            // Se limpian los EditText después de la inserción.
                            binding.etNombre.text!!.clear()
                            binding.etApes.text!!.clear()
                        }
                        DELETE -> {
                            myToast(
                                "Eliminado/s " +
                                        "${amigosDBHelper.delAmigo(identificador.toInt())} " +
                                        "registro/s"
                            )
                        }
                    }
                }
            }
            setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
        }.show()
    }

    fun myToast(mensaje: String) {
        Toast.makeText(
            this@MainActivity,
            mensaje,
            Toast.LENGTH_SHORT
        ).show()
    }
}