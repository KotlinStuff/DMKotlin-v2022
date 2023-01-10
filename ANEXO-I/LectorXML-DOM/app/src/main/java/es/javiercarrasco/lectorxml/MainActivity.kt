package es.javiercarrasco.lectorxml

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import es.javiercarrasco.lectorxml.databinding.ActivityMainBinding
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.IOException
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

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
            binding.textView.append("Pizza: ${it.nombre} | Precio: ${it.precio}€\nIngredientes:\n")
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
            val factory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
            val builder: DocumentBuilder = factory.newDocumentBuilder()
            val doc: Document = builder.parse(resources.openRawResource(R.raw.pizzas))
            val raiz: Element = doc.getDocumentElement()
            val items: NodeList = raiz.getElementsByTagName("pizza")


            for (i in 0..items.length - 1) { // Recorre todos los elementos principales.
                val nodoPizza: Node = items.item(i)
                // Se crea la pizza utilizando las propiedades de tag "pizza".
                val pizza = Pizza(
                    nodoPizza.attributes.getNamedItem("nombre").nodeValue,
                    nodoPizza.attributes.getNamedItem("precio").nodeValue.toInt()
                )

                for (j in 0..nodoPizza.childNodes.length - 1) { // Recorre los hijos (ingredientes).
                    val nodoActual = nodoPizza.childNodes.item(j)
                    // Comprueba si es un elemento de tipo Node.
                    if (nodoActual.nodeType == Node.ELEMENT_NODE) {
                        if (nodoActual.nodeName.equals("ingrediente")) {
                            Log.d("XML-ingrediente", nodoActual.childNodes.item(0).nodeValue)
                            pizza.ingredientes.add(nodoActual.childNodes.item(0).nodeValue)
                        }
                    }
                }
                pizzas.add(pizza)
            }
        } catch (e: ParserConfigurationException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return pizzas
    }
}