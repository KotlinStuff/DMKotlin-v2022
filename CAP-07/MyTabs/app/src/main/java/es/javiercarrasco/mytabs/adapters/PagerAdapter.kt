package es.javiercarrasco.mytabs.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class PagerAdapter(
    fragmentManager: FragmentManager, lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private val mFragmentList = ArrayList<Fragment>()
    private val mFragmentTitleList = ArrayList<String>()

    // Devuelve el tamaño de lista de fragments.
    override fun getItemCount(): Int {
        return mFragmentList.size
    }

    // Se utiliza para devolver el frament en la posición indicada.
    override fun createFragment(position: Int): Fragment {
        return mFragmentList[position]
    }

    // Añade un fragment a la lista.
    fun addFragment(fragment: Fragment, title: String) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }

    // Devuelve el título de la pestaña.
    fun getPageTitle(position: Int): CharSequence {
        return mFragmentTitleList[position]
    }
}