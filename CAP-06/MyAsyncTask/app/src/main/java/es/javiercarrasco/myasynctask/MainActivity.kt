package es.javiercarrasco.myasynctask

import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import es.javiercarrasco.myasynctask.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Se deshabilitan los botones cancelar hasta que se activen
        // las tareas.
        binding.btnCancelUno.isEnabled = false
        binding.btnCancelDos.isEnabled = false

        // Variables para crear las tareas asíncronas.
        lateinit var myAsyncTask1: MyAsyncTask
        lateinit var myAsyncTask2: MyAsyncTask

        binding.btnTaskUno.setOnClickListener {
            myAsyncTask1 = MyAsyncTask(
                this,
                binding.progressBarUno,
                binding.btnTaskUno,
                binding.btnCancelUno
            )
            // Se lanza la tarea.
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
                myAsyncTask1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 100, 20)
            else myAsyncTask1.execute(100, 20)
        }

        binding.btnCancelUno.setOnClickListener {
            myAsyncTask1.cancel(true)
        }

        binding.btnTaskDos.setOnClickListener {
            myAsyncTask2 = MyAsyncTask(
                this,
                binding.progressBarDos,
                binding.btnTaskDos,
                binding.btnCancelDos
            )
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
                myAsyncTask2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 200, 15)
            else myAsyncTask2.execute(200, 15)
        }

        binding.btnCancelDos.setOnClickListener {
            myAsyncTask2.cancel(true)
        }
    }

    // Clase privada anidada para crear tareas asíncronas.
    private inner class MyAsyncTask(
        val contexto: Context,
        val progressBar: ProgressBar,
        val buttonStart: Button,
        val buttonCancel: Button
    ) : AsyncTask<Int, Int, Int>() {

        /**
         * Acciones antes de iniciar la tarea, se utiliza
         * para inicializar o configurar la tarea.
         */
        override fun onPreExecute() {
            // super.onPreExecute()
            Log.d(buttonStart.text.toString(), "${buttonStart.text}, iniciada!!")
            progressBar.progress = 0
            buttonStart.isEnabled = false
            buttonCancel.isEnabled = true
        }

        /**
         * Este método realiza la tarea a ejecutar,
         * su implementación es obligatoria.
         * vararg -> array con los parámetros indicados.
         */
        override fun doInBackground(vararg p0: Int?): Int {
            if (p0.size == 2) {
                var contador = 0
                while (contador < p0[0]!!) {
                    try {
                        contador++
                        Thread.sleep(p0[1]!!.toLong())
                    } catch (e: Exception) {
                        Log.println(
                            Log.WARN,
                            "doInBackground",
                            e.message.toString()
                        )
                    }
                    // Se comprueba si la tarea ha sido cancelada.
                    if (!isCancelled)
                        publishProgress(
                            (((contador + 1) * 100 / p0[0]!!).toFloat()).toInt()
                        )
                    else break
                }
                return 1
            } else return -1
        }

        /**
         * Método que se ejecutará al cancelarse la tarea.
         */
        override fun onCancelled(result: Int?) {
            //super.onCancelled(result)

            progressBar.progress = 0
            Log.d(buttonStart.text.toString(), "${buttonStart.text}, cancelada!!")
            Toast.makeText(contexto, "${buttonStart.text}, cancelada!!", Toast.LENGTH_SHORT).show()

            buttonStart.isEnabled = true
            buttonCancel.isEnabled = false
        }

        /**
         * Permite mostrar información al usuario, se ejecuta
         * cuando se utiliza el método publishProgress() desde el
         * método doInBackground().
         */
        override fun onProgressUpdate(vararg values: Int?) {
            //super.onProgressUpdate(values[0])
            progressBar.progress = values[0]!!
        }

        /**
         * Se ejecuta al finalizar el método doInBackground()
         * y se le pasa el resultado obtenido.
         */
        override fun onPostExecute(result: Int?) {
            //super.onPostExecute(result)
            if (result == 1) {
                progressBar.progress = 100
                Toast.makeText(contexto, buttonStart.text, Toast.LENGTH_SHORT).show()
            }

            buttonStart.isEnabled = true
            buttonCancel.isEnabled = false
        }
    }
}