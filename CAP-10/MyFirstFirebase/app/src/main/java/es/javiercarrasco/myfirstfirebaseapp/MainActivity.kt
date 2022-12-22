package es.javiercarrasco.myfirstfirebaseapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import es.javiercarrasco.myfirstfirebaseapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    data class User(var name: String? = "", var surname: String? = "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Se crea la referencia al punto raíz de la BD.
        val database = FirebaseDatabase
            .getInstance("https://my-first-firebase-projec-9fc7f-default-rtdb.europe-west1.firebasedatabase.app/")
            .reference

        // Se obtiene la referencia al nombre del gato.
        val dbfGato = database.child("mascotas")
            .child("gato")
        // .child("nombre")

        // Se crea un listener para que sean notificados los cambios en el nombre.
        dbfGato.addValueEventListener(object : ValueEventListener {
            // Se ejecuta cuando se obtiene el valor.
            override fun onDataChange(snapshot: DataSnapshot) {
                // binding.tvNombre.text = getString(R.string.txt_nombre, snapshot.value)
                binding.tvNombre.text =
                    getString(R.string.txt_nombre, snapshot.child("nombre").value)
                binding.tvRaza.text = getString(R.string.txt_raza, snapshot.child("raza").value)

            }

            // Se llama cuando se cancela la lectura, o se produce un error.
            override fun onCancelled(error: DatabaseError) {
                Log.e("onCancelled", "Error!", error.toException())
            }
        })

        // Añadir usuarios a Firebase.
        binding.btnAddUsers.setOnClickListener {
            val users: MutableList<User> = ArrayList()

            users.add(User("Javier", "Carrasco"))
            users.add(User("Nacho", "Cabanes"))
            users.add(User("Patricia", "Aracil"))
            users.add(User("Juan", "Palomo"))
            users.add(User("Raquel", "Sánchez"))

            database.child("usuarios").setValue(users)
        }

        // Actualizar usuario.
        binding.btnUpdateUser.setOnClickListener {
            val userUpdate = HashMap<String, Any>()
            userUpdate["0"] = User("Javi", "Hernández")

            database.child("usuarios").updateChildren(userUpdate)
        }

        // Eliminar usuario.
        binding.btnDelUser.setOnClickListener {
            database.child("usuarios")
                .child("0")
                .removeValue()
        }

        // Se obtiene la referencia a los usuarios.
        val dbfUsers = database.child("usuarios")

        dbfUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users: MutableList<User> = ArrayList()

                binding.tvUsersList.text = null

                for (userSnapshot in snapshot.children) {
                    users.add(userSnapshot.getValue(User::class.java)!!)

                    binding.tvUsersList.append(
                        "${userSnapshot.getValue(User::class.java)!!.name} " +
                                "${userSnapshot.getValue(User::class.java)!!.surname}\n"
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("onCancelled", "Error!", error.toException())
            }
        })
    }
}