package es.javiercarrasco.mytabs

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import es.javiercarrasco.mytabs.adapters.DepthPageTransformer
import es.javiercarrasco.mytabs.adapters.PagerAdapter
import es.javiercarrasco.mytabs.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewPager2 = binding.viewPager2

        // Se crea el adapter.
        val adapter = PagerAdapter(supportFragmentManager, lifecycle)

        // Se añaden los fragments y los títulos de pestañas.
        adapter.addFragment(PersonsFragment(), "Personas")
        adapter.addFragment(FruitsFragment(), "Frutas")
        adapter.addFragment(AnimalsFragment(), "Animales")

        // Se asocia el adapter al ViewPager2.
        viewPager2.adapter = adapter

        // Efectos para el ViewPager2.
        viewPager2.setPageTransformer(DepthPageTransformer())

        // Carga de las pestañas en el TabLayout.
        TabLayoutMediator(binding.tabLayout, viewPager2) { tab, position ->
            tab.text = adapter.getPageTitle(position)
        }.attach()
    }
}