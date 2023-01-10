package es.javiercarrasco.lectorxml

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import es.javiercarrasco.lectorxml.databinding.ActivityMainBinding
import org.xml.sax.Attributes
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler
import java.io.IOException
import javax.xml.parsers.ParserConfigurationException
import javax.xml.parsers.SAXParserFactory

/**
 * Created by Javier Carrasco on 9/1/23.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        escribirPizzas(leerPizzas())
    }

    // Método encargado de mostrar en el TextView el contenido de la MutableList.
    private fun escribirPizzas(pizzas: MutableList<Pizza>) {
        binding.textView.text = null
        pizzas.forEach {
            binding.textView.append("Pizza: ${it.nombre} | Precio: ${it.precio}€\n")

            binding.textView.append("Ingredientes:\n")
            it.ingredientes.forEach {
                binding.textView.append("- $it\n")
            }

            binding.textView.append("\n")
        }
    }

    // Devuelve en una MutableList el contenido del fichero XML.
    private fun leerPizzas(): MutableList<Pizza> {
        val pizzas: MutableList<Pizza> = ArrayList()

        try {
            val saxParserFactory = SAXParserFactory.newInstance()
            val saxParser = saxParserFactory.newSAXParser()
            val manejadorEventos = object : DefaultHandler() {
                var etiquetaActual = ""
                var contenido = ""
                lateinit var pizza: Pizza

                // Método que se llama al encontrar inicio de etiqueta: '<'.
                override fun startElement(
                    uri: String?,
                    localName: String?,
                    qName: String?,
                    attributes: Attributes?
                ) {
                    // Si el tag es "pizza", empieza una nueva y se guarda su nombre y precio.
                    if (qName != null) {
                        etiquetaActual = qName
                        if (etiquetaActual == "pizza") {
                            Log.d("XML-Pizza: ", attributes!!.getValue("nombre"))
                            pizza = Pizza(
                                attributes.getValue("nombre"),
                                attributes.getValue("precio").toInt()
                            )
                            pizzas.add(pizza) // Se añade a la lista.
                        }
                    }
                }

                // Obtiene el dato entre '>' y '<'.
                override fun characters(ch: CharArray?, start: Int, length: Int) {
                    contenido = String(ch!!, start, length)
                }

                // Se llama al encontrar un fin de etiqueta: '>'.
                override fun endElement(uri: String?, localName: String?, qName: String?) {
                    if (etiquetaActual != "") {
                        Log.d("XML-Ingrediente", contenido)
                        pizza.ingredientes.add(contenido)
                        etiquetaActual = ""
                    }
                }
            }

            // Cuerpo de la función: trata de analizar el fichero deseado
            // Llamará a startElement(), endElement() y character()
            saxParser.parse(resources.openRawResource(R.raw.pizzas), manejadorEventos);
        } catch (e: ParserConfigurationException) {
            e.printStackTrace();
        } catch (e: IOException) {
            e.printStackTrace();
        } catch (e: SAXException) {
            e.printStackTrace();
        }
        return pizzas
    }
}