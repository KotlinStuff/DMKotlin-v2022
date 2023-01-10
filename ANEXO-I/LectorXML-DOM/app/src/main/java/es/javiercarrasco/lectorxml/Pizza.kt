package es.javiercarrasco.lectorxml

/**
 * Created by Javier Carrasco on 9/1/23.
 */
data class Pizza(val nom: String, val p: Int) {
    var nombre: String = nom
    var precio: Int = p
    var ingredientes: MutableList<String> = ArrayList()
}