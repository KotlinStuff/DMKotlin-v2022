package es.javiercarrasco.mysqlite.adapters

import android.database.Cursor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import es.javiercarrasco.mysqlite.databinding.ItemRecyclerViewBinding

/**
 * Created by Javier Carrasco on 16/11/22.
 * App: My SQLite
 */
class MyRecyclerAdapter(private var cursor: Cursor) :
    RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder>() {

    // Se "infla" la vista de los items.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d("RECYCLERVIEW", "onCreateViewHolder")
        return ViewHolder(
            ItemRecyclerViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ).root
        )
    }

    // Se completan los datos de cada vista mediante ViewHolder.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Importante para recorrer el cursor.
        cursor.moveToPosition(position)
        Log.d("RECYCLERVIEW", "onBindViewHolder")

        // Se asignan los valores a los elementos de la UI.
        holder.id.text = cursor.getString(0)
        holder.nombre.text = cursor.getString(1)
        holder.apes.text = cursor.getString(2)
    }

    override fun getItemCount(): Int {
        return cursor.count
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Se crean las referencias de la UI.
        val id: TextView
        val nombre: TextView
        val apes: TextView

        init {
            // Se enlazan los elementos de la UI mediante ViewBinding.
            val bindingItemsRV = ItemRecyclerViewBinding.bind(itemView)
            this.id = bindingItemsRV.tvIdentificador
            this.nombre = bindingItemsRV.tvNombre
            this.apes = bindingItemsRV.tvApes

            itemView.setOnClickListener {
                Toast.makeText(
                    bindingItemsRV.cvItem.context,
                    "${this.id.text}-${this.nombre.text} ${this.apes.text}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}