package es.javiercarrasco.myfragments2

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import es.javiercarrasco.myfragments2.databinding.FragmentNewBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_NUMFRAG = "param1"
//private const val ARG_COLORBACK = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewFragment : Fragment() {
    private lateinit var binding: FragmentNewBinding
    private var numFrag: Int? = null
    private var colorBack: Int? = null

    private val TAG = "FragmentNew"

    interface DatoDevuleto {
        fun datoActualizado(dato: String)
    }

    private var datoFragment: DatoDevuleto? = null

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach")
        super.onAttach(context)

        if (context is DatoDevuleto)
            datoFragment = context
        else throw RuntimeException(
            requireContext().toString() + " debe implementar DatoDevuelto."
        )
    }

    // Método encargado de pasar la información a la activity contenedora.
    fun actualizarDato() {
        if (!binding.etFragment.text.isEmpty())
            datoFragment?.datoActualizado(binding.etFragment.text.toString())
        else datoFragment?.datoActualizado("Sin información")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)

        // Si existen argumentos pasados en el Bundle desde la llamada, se asignan a las propiedades.
        // ARG_NUMFRAG y ARG_COLORBACK están declaradas en MainActivity.
        arguments?.let {
            numFrag = it.getInt(ARG_NUMFRAG)
            colorBack = it.getInt(ARG_COLORBACK)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentNewBinding.inflate(inflater)
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param nFrag Parameter 1.
         * @param cBack Parameter 2.
         * @return A new instance of fragment NewFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(nFrag: Int, cBack: Int) =
            NewFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_NUMFRAG, nFrag)
                    putInt(ARG_COLORBACK, cBack)
                }
            }
    }

    // Se modifican las propiedades en este método para asegurar que la activity
    // está creada y evitar así posibles fallos.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")
        binding.frameRoot.setBackgroundColor(colorBack!!)
        binding.tvFragment.text = "Fragment\n${numFrag}"

        binding.btnFragment.setOnClickListener {
            actualizarDato()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    // El resto de métodos sobrecargados son exactamente igual que los utilizados en el ejemplo MyFragments.
    override fun onStart() {
        Log.d(TAG, "onStart")
        super.onStart()
    }

    override fun onResume() {
        Log.d(TAG, "onResume")
        super.onResume()
    }

    override fun onPause() {
        Log.d(TAG, "onPause")
        super.onPause()
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView")
        super.onDestroyView()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()
    }

    override fun onDetach() {
        Log.d(TAG, "onDetach")
        super.onDetach()
        datoFragment = null
    }
}