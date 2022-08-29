package es.javiercarrasco.myimageviewer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import es.javiercarrasco.myimageviewer.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    // Contendrá las URLs de las imágenes a descargar.
    private val urlImages: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        readURLs()
    }

    override fun onStart() {
        super.onStart()
        var tarea: Job? = null

        binding.button.setOnClickListener {
            if (binding.button.text == getString(R.string.btn_imageDownloader)) {
                // Se comprueba el estado de la conexión.
                if (checkConnection()) {
                    // Se comprueba la existencia de ImageViews, si existen se eliminan.
                    if (binding.myLinearLayout.childCount > 3) {
                        binding.myLinearLayout.removeViews(
                            3,
                            (binding.myLinearLayout.childCount) - 3
                        )
                    }
                    tarea = downloadImages()
                } else Snackbar.make(it, "Sin conexión", Snackbar.LENGTH_LONG).show()

            } else {
                tarea?.let {
                    tarea?.cancel()
                    binding.button.text = getString(R.string.btn_imageDownloader)
                    binding.tvInfo.text = getString(R.string.txt_descargaCancelada)
                }
            }
        }
    }

    // Método encargado de comprobar la conexión.
    private fun checkConnection(): Boolean {
        var estado = false

        // Se comprueba el estado de la conexión.
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetwork
        val actNetwork = cm.getNetworkCapabilities(networkInfo)

        if (actNetwork != null)
            estado = when {
                actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }

        return estado
    }

    private fun readURLs() = CoroutineScope(Dispatchers.Main).launch {
        // Preparación
        binding.progressBarURLs.isVisible = true

        withContext(Dispatchers.IO) {
            readFile()
            // Retiene la ejecución para ver la UI.
            delay(2000)
        }

        // Fin de la lectura del fichero
        Log.i("FILE", urlImages.size.toString())
        binding.button.isEnabled = urlImages.size > 0
        binding.progressBarURLs.isVisible = false
    }

    // Lee el fichero RAW con las URLs de las imágenes.
    private fun readFile() {
        val entrada = InputStreamReader(resources.openRawResource(R.raw.urlimages))
        lateinit var br: BufferedReader

        try {
            br = BufferedReader(entrada)
            var linea = br.readLine()

            while (!linea.isNullOrEmpty()) {
                urlImages.add(linea)
                Log.i("FILE", linea)
                linea = br.readLine()
            }
        } catch (e: IOException) {
            Log.e("ERROR IO", e.message.toString())
        } finally {
            br.close()
            entrada.close()
        }
    }

    private fun downloadImages() = CoroutineScope(Dispatchers.Main).launch {
        binding.button.text = getString(android.R.string.cancel)
        binding.tvInfo.text = getString(R.string.txt_descargando)

        binding.progressBar.progress = 0

        val images = ArrayList<Bitmap>()

        urlImages.forEach {
            Log.d("URLs", it)

            // Tarea asíncronta, se descargan las imágenes y se almacenan en un
            // ArrayList<Bitmatp>().
            withContext(Dispatchers.IO) {
                try {
                    val inputStream = URL(it).openStream()
                    images.add(BitmapFactory.decodeStream(inputStream))
                    inputStream.close()
                } catch (e: Exception) {
                    Log.e("DOWNLOAD", e.message.toString())
                }
            }
            binding.progressBar.progress = (images.size * 100) / urlImages.size
        }

        // Fin de la tarea.
        // Se añaden las imágenes a la vista una vez descargadas.
        images.forEach {
            addImage(it)
        }

        binding.button.text = getString(R.string.btn_imageDownloader)

        binding.tvInfo.text = getString(
            R.string.txt_descargaCompleta,
            images.size
        )
    }

    // Método encargado de "inflar" los ImageView.
    private fun addImage(image: Bitmap) {
        val img = ImageView(this)

        // Se carga la imagen en el ImageView mediante Glide y se ajusta el tamaño.
        Glide.with(this)
            .load(image)
            .override(binding.myLinearLayout.width - 100)
            .into(img)
        img.setPadding(0, 0, 0, 10)

        // Se infla el LinearLayout con una imagen nueva.
        binding.myLinearLayout.addView(img)
    }
}