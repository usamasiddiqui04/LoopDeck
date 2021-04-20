package com.example.loopdeck.ui.collection

import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.loopdeck.R
import com.example.loopdeck.ui.collection.publish.PublishFragment
import com.example.loopdeck.ui.collection.recents.RecentsFragment
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_recents.*
import kotlinx.android.synthetic.main.activity_recents.drawer_layout
import kotlinx.android.synthetic.main.activity_recents.nav_view
import kotlinx.android.synthetic.main.fragment_recents.*

class CollectionActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var titles = arrayOf("Recent", "Published")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recents)

        setSupportActionBar(toolbar)

        view_pager.adapter = ViewPagerFragmentAdapter(this);

        TabLayoutMediator(
            tab_layout, view_pager
        ) { tab: TabLayout.Tab, position: Int ->
            tab.text = titles[position]
        }.attach()

        val toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout!!.addDrawerListener(toggle)

        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        drawer_layout!!.setViewScale(GravityCompat.START, 0.9f)
        drawer_layout!!.setRadius(GravityCompat.START, 35f)
        drawer_layout!!.setViewElevation(GravityCompat.START, 20f)
    }

    private class ViewPagerFragmentAdapter(@NonNull fragmentActivity: FragmentActivity?) :
        FragmentStateAdapter(fragmentActivity!!) {
        @NonNull
        override fun createFragment(position: Int): Fragment {
            when (position) {
                0 -> return RecentsFragment()
                1 -> return PublishFragment()
            }
            return RecentsFragment()
        }

        override fun getItemCount(): Int {
            return 2
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawer_layout!!.closeDrawer(GravityCompat.START)
        return true
    }
}