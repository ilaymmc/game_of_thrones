package ru.skillbranch.gameofthrones.ui.houses

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import ru.skillbranch.gameofthrones.data.local.entities.HouseType
import ru.skillbranch.gameofthrones.ui.houses.house.HouseFragment

class HousesPagerAdapter(fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment =
        HouseFragment.newInstance(HouseType.values()[position].title)

    override fun getPageTitle(position: Int): CharSequence? =
        HouseType.values()[position].title

    override fun getCount(): Int =
        HouseType.values().size

}