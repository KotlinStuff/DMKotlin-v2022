package es.javiercarrasco.mycloudfirestore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import es.javiercarrasco.mycloudfirestore.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Se obtiene la instancia de la BD.
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()

        // Se obtiene la colección.
        val coleccion: CollectionReference = db.collection("profesores")

        // Obtener un documento concreto.
        val docRef: DocumentReference = coleccion.document("1")

        // Primer método de lectura.
//        docRef.get().apply {
//            // Obtiene información, se lanza sin llegar a terminar la conexión.
//            addOnSuccessListener {
//                Log.d("addOnSuccessListener", "Cached document data: ${it.data}")
//                val texto = "${it["modulo"]} - ${it["nombre"]} ${it["apellido"]}"
//                binding.textView.text = texto
//            }
//
//            // Fallo de lectura.
//            addOnFailureListener { e ->
//                Log.d("addOnFailureListener", "Fallo de lectura ", e)
//            }
//        }

        // Segundo método de lectura.
//        docRef.get().apply {
//            addOnCompleteListener {
//                if (it.isSuccessful) {
//                    // Documento encontrado en la caché offline.
//                    val doc = it.result
//
//                    Log.d("addOnCompleteListener", "Document data: ${doc?.data}")
//                    val texto = "${doc!!["modulo"]} - ${doc["nombre"]} ${doc["apellido"]}"
//                    binding.textView.text = texto
//                } else {
//                    Log.d("addOnCompleteListener", "Fallo de lectura ", it.exception)
//                }
//            }
//        }

        // Tercer método de lectura. Con escucha activa.
        // Escucha del documento, contrasta la caché con la base de datos.
//        docRef.addSnapshotListener { value, error ->
//            // Se comprueba si hay fallo.
//            if (error != null) {
//                Log.w("addSnapshotListener", "Escucha fallida!", error)
//                return@addSnapshotListener
//            }
//
//            if (value != null && value.exists()) {
//                Log.d("addSnapshotListener", "Información actual: ${value.data}")
//                val texto = "${value["modulo"]} - ${value["nombre"]} ${value["apellido"]}"
//                binding.textView.text = texto
//            } else Log.d("addSnapshotListener", "Información actual: null")
//        }

        // Obtener todos los documentos de una colección (sin escucha).
//        coleccion.get().apply {
//            addOnSuccessListener {
//                for (doc in it) {
//                    Log.d("DOC", "${doc.id} => ${doc.data}")
//                    binding.textView.append(
//                        "${doc!!["modulo"]} - ${doc["nombre"]} ${doc["apellido"]}\n"
//                    )
//                }
//            }
//
//            addOnFailureListener { exception ->
//                Log.d("DOC", "Error durante la recogida de documentos: ", exception)
//            }
//        }

        // Obtener todos los documentos de una colección (con escucha).
//        coleccion.addSnapshotListener { querySnapshot, firestoreException ->
//            if (firestoreException != null) {
//                Log.w("addSnapshotListener", "Escucha fallida!.", firestoreException)
//                return@addSnapshotListener
//            }
//
//            binding.textView.text = ""
//            for (doc in querySnapshot!!) {
//                Log.d("DOC", "${doc.id} => ${doc.data}")
//                binding.textView.append("${doc["modulo"]} - ${doc["nombre"]} ${doc["apellido"]}\n")
//            }
//        }

        // Obtiene todos los documentos de una colección filtrados (sin escucha).
        coleccion.whereEqualTo("modulo", "PMDM").get().apply {
            addOnSuccessListener {
                binding.textView.text = "Sin escucha\n"
                for (doc in it) {
                    Log.d("DOC", "${doc.id} => ${doc.data}")
                    binding.textView.append(
                        "${doc["modulo"]} - ${doc["nombre"]} ${doc["apellido"]}\n"
                    )
                }
            }
            addOnFailureListener { exception ->
                Log.d("DOC", "Error durante la recogida de documentos: ", exception)
            }
        }

        // Obtiene todos los documentos de una colección filtrados (con escucha).
        coleccion.whereEqualTo("modulo", "ED")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.w("addSnapshotListener", "Escucha fallida!.", firebaseFirestoreException)
                    return@addSnapshotListener
                }

                binding.textView2.text = "Con escucha\n"
                for (doc in querySnapshot!!) {
                    Log.d("DOC", "${doc.id} => ${doc.data}")
                    binding.textView2.append(
                        "${doc["modulo"]} - ${doc["nombre"]} ${doc["apellido"]}\n"
                    )
                }
            }

        // Añadir un documente nuevo a una colección.
//        binding.button.setOnClickListener {
//            // Se crea la estructura del documento.
//            val profe = hashMapOf(
//                "nombre" to "Miguel",
//                "apellido" to "López",
//                "modulo" to "ED"
//            )
//
//            // Se añade el documento sin indicar ID, dejando que Firebase genere el ID
//            // al añadir el documento. Para esta acción se recomienda add().
//            coleccion.document("7").set(profe)
//                // Respuesta si ha sido correcto.
//                .addOnSuccessListener {
//                    Log.d("DOC_SET", "Documento añadido!")
//                }
//
//                // Respuesta si se produce un fallo.
//                .addOnFailureListener { e ->
//                    Log.w("DOC_SET", "Error en la escritura", e)
//                }
//        }

        // Crear una nueva colección y añadir un nuevo documento.
//        binding.button.setOnClickListener {
//            // Se selecciona la coleción y, si no existe, se crea.
//            val colModulos = db.collection("modulos")
//
//            val moduloNuevo = hashMapOf(
//                "siglas" to "PMDM",
//                "nombre" to "Programación Multimedia y Dispositivos Móviles"
//            )
//
//            colModulos.add(moduloNuevo)
//                .addOnSuccessListener { Log.d("DOC_ADD", "Documento añadido, id: ${it.id}") }
//                .addOnFailureListener { e -> Log.w("DOC_ADD", "Error añadiendo el documento", e) }
//        }

        // Crear un documento nuevo obteniendo su referencia.
//        binding.button.setOnClickListener {
//            val refModuloNuevo = db.collection("modulos").document()
//
//            val modulo = hashMapOf(
//                "abreviatura" to "ED",
//                "nombre" to "Entornos de Desarrollo"
//            )
//            // Se añaden los datos.
//            refModuloNuevo.set(modulo)
//        }

        // Actualizar un documento.
//        binding.button.setOnClickListener {
//            // Se obtiene la referencia del documento a actualizar.
//            val refModuloUpd = db.collection("modulos").document("OKKpJwCJuFzzMSiL2LcC")
//
//            refModuloUpd
//                .update("abreviatura", "MÓVILES")
//                .addOnSuccessListener { Log.d("DOC_UPD", "Documento actualizado correctamente") }
//                .addOnFailureListener { e -> Log.w("DOC_UPD", "Error al actualizar el documento", e) }
//        }

        // Eliminar un documento.
//        binding.button.setOnClickListener {
//            // Se obtiene la referencia del documento a eliminar.
//            val refModuloDel = db.collection("modulos").document("KjJpFq1isfDUfzawZ3m4")
//
//            refModuloDel
//                .delete()
//                .addOnSuccessListener { Log.d("DOC_DEL", "Documento eliminado correctamente") }
//                .addOnFailureListener { e -> Log.w("DOC_DEL", "Error al eliminar el documento", e) }
//        }
    }
}