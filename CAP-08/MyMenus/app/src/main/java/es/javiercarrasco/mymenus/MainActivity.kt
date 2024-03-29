package es.javiercarrasco.mymenus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import es.javiercarrasco.mymenus.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val personas =
        arrayOf(
            "Javier", "Pedro", "Nacho", "Patricia",
            "Miguel", "Susana", "Raquel", "Antonio", "Andrea",
            "Nicolás", "Juan José", "José Antonio", "Daniela",
            "María", "Verónica", "Natalia"
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Se crea el adapter para la lista de nombres.
        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            android.R.layout.simple_expandable_list_item_1,
            personas.sortedArray()
        )

        with(binding) {
            // Se cargan los datos en la lista.
            myListView.adapter = arrayAdapter

            // Se registra el uso de un menú contextual al ListView.
            registerForContextMenu(myListView)

            // Acción sobre el elemento de la lista pulsado.
            myListView.setOnItemClickListener { parent, view, position, id ->
                myToast(
                    "Pulsado $id - " +
                            "${myListView.getItemAtPosition(position)}"
                )
            }
        }
    }

    // Se "infla" el menú contextual con el resource. Se ejecuta tras el registro.
    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        val inflater = menuInflater
        inflater.inflate(R.menu.context_menu, menu)
    }

    // Se comprueba la opción de menú seleccionada y sobre que ítem se ha ejecutado.
    override fun onContextItemSelected(item: MenuItem): Boolean {
        // Se obtiene el nombre de la persona, con
        // ApaterView.AdapterContextMenuInfo se obtiene la posición
        // sobre la que se ha hecho clic.
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val posicion = info.position
        val nombre = personas.sortedArray()[posicion]
        return when (item.itemId) {
            R.id.option01 -> {
                myToast("Opción 1: $nombre")
                true
            }
            R.id.option02 -> {
                myToast("Opción 2: $nombre")
                true
            }
            else -> false
        }
    }

    // Se infla el menú para mostrarlo en la barra de aplicación.
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflate = menuInflater
        inflate.inflate(R.menu.demo_menu, menu)
        return true
    }

    // Método encargado de gestionar las opciones pulsadas del menú.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.op01 -> {
                Log.d("MENU", "${getString(R.string.op1)} seleccionada")
                myToast("${getString(R.string.op1)} seleccionada")
                true
            }
            // La opción op02 no haría falta, ya que abre el submenú.
            R.id.op021 -> {
                Log.d("MENU", "${getString(R.string.op21)} seleccionada")
                myToast("${getString(R.string.op21)} seleccionada")
                true
            }
            R.id.op022 -> {
                Log.d("MENU", "${getString(R.string.op22)} seleccionada")
                myToast("${getString(R.string.op22)} seleccionada")
                true
            }
            R.id.op03 -> {
                Log.d("MENU", "${getString(R.string.op3)} seleccionada")
                myToast("${getString(R.string.op3)} seleccionada")
                true
            }
            R.id.op04 -> {
                Log.d("MENU", "${getString(R.string.op4)} seleccionada")
                myToast("${getString(R.string.op4)} seleccionada")
                true
            }
            else -> false
        }
    }

    private fun myToast(mensaje: String) {
        Toast.makeText(
            this,
            mensaje,
            Toast.LENGTH_SHORT
        ).show()
    }
}