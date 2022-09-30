package es.javiercarrasco.mytabs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import es.javiercarrasco.mytabs.databinding.FragmentAnimalsBinding

class AnimalsFragment : Fragment() {
    private lateinit var binding: FragmentAnimalsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("Animales", "onCreateView")
        binding = FragmentAnimalsBinding.inflate(layoutInflater)
        return binding.root
    }
}