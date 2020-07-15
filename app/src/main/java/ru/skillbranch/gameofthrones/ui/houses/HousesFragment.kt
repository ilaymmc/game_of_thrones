package ru.skillbranch.gameofthrones.ui.houses

import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.annotation.ColorInt
import androidx.core.animation.doOnEnd
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_houses.*
import ru.skillbranch.gameofthrones.ui.MainActivity
import ru.skillbranch.gameofthrones.R
import kotlin.math.hypot
import kotlin.math.max

class HousesFragment : Fragment() {

    private lateinit var colors: Array<Int>
    private lateinit var housesPagerAdapter: HousesPagerAdapter

    @ColorInt
    private var currentColor: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        housesPagerAdapter = HousesPagerAdapter(childFragmentManager)
        colors = requireContext().run {
            arrayOf(
                getColor(R.color.stark_primary),
                getColor(R.color.lannister_primary),
                getColor(R.color.targaryen_primary),
                getColor(R.color.baratheon_primary),
                getColor(R.color.greyjoy_primary),
                getColor(R.color.martel_primary),
                getColor(R.color.tyrel_primary)
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
        with(menu.findItem(R.id.search)?.actionView as SearchView) {
            queryHint = "Search character"
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_houses, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).setSupportActionBar(toolbar)

        if (currentColor != -1)
            appbar.setBackgroundColor(currentColor)

        view_pager.adapter = housesPagerAdapter
        with(tabs) {
            setupWithViewPager(view_pager)
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabUnselected(tab: TabLayout.Tab?) { }
                override fun onTabReselected(tab: TabLayout.Tab?) { }
                override fun onTabSelected(tab: TabLayout.Tab) {
                    val position = tab.position

                    if ((appbar.background as ColorDrawable).color != colors[position]) {
                        val rect = Rect()
                        val tabView = tab.view as View

                        tabView.getGlobalVisibleRect(rect)
                        animateAppbarReval(position, rect.centerX(), rect.centerY())
                    }
                }

            })
        }
    }

    private fun animateAppbarReval(position: Int, centerX: Int, centerY: Int) {
        val endRadius = max(
            hypot(centerX.toDouble(), centerY.toDouble()),
            hypot((appbar.width - centerX).toDouble(), centerY.toDouble())
        )

        with(reveal_view) {
            visibility = View.VISIBLE
            setBackgroundColor(colors[position])
        }

        ViewAnimationUtils.createCircularReveal(
            reveal_view,
            centerX,
            centerY,
            0f,
            endRadius.toFloat()
        ).apply {
            doOnEnd {
                appbar?.setBackgroundColor(colors[position])
                reveal_view?.visibility = View.INVISIBLE
            }
            start()
        }
        currentColor = colors[position]
    }


}