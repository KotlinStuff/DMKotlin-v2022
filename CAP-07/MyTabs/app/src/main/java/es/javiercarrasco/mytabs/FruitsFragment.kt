package es.javiercarrasco.mytabs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import es.javiercarrasco.mytabs.databinding.FragmentFruitsBinding

class FruitsFragment : Fragment() {
    private lateinit var binding: FragmentFruitsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("Frutas", "onCreateView")
        binding = FragmentFruitsBinding.inflate(layoutInflater)
        return binding.root
    }
}