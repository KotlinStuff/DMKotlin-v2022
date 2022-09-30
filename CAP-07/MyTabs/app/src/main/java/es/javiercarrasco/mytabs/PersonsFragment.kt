package es.javiercarrasco.mytabs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import es.javiercarrasco.mytabs.databinding.FragmentPersonsBinding

class PersonsFragment : Fragment() {
    private lateinit var binding: FragmentPersonsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("Personas", "onCreateView")
        binding = FragmentPersonsBinding.inflate(layoutInflater)
        return binding.root
    }
}