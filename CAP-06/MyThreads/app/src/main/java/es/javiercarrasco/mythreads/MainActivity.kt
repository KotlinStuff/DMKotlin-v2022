package es.javiercarrasco.mythreads

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import es.javiercarrasco.mythreads.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            btnHiloUno.setOnClickListener {
                createThread(btnHiloUno, 500, 10, tvHiloUno)
            }
            btnHiloDos.setOnClickListener {
                createThread(btnHiloDos, 100, 20, tvHiloDos)
            }
            btnHiloTres.setOnClickListener {
                createThread(btnHiloTres, 200, 30, tvHiloTres)
            }
            btnHiloCuatro.setOnClickListener {
                createThread(btnHiloCuatro, 300, 40, tvHiloCuatro)
            }
        }
    }

    // Método encargado de crear hilos.
    private fun createThread(
        boton: Button, duracion: Long,
        vueltas: Int, visualizador: TextView
    ) {
        visualizador.text = "0"
        visualizador.setBackgroundColor(Color.TRANSPARENT)
        boton.isEnabled = false

        Thread(Runnable {
            // Se imita la realización de una tarea.
            var contador = 0
            while (contador < vueltas) {
                try {
                    contador++
                    Thread.sleep(duracion)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                // Permite actuar con elementos de la UI.
                visualizador.post {
                    visualizador.text = contador.toString()
                }
            }

            // Acciones que se realizarán al finalizar la tarea.
            runOnUiThread {
                visualizador.text = "FIN"
                visualizador.setBackgroundColor(Color.GREEN)
                boton.isEnabled = true
                Toast.makeText(this, boton.text, Toast.LENGTH_SHORT).show()
            }
        }).start()
    }
}