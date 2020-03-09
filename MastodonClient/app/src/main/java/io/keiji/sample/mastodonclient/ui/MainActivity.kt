package io.keiji.sample.mastodonclient.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.keiji.sample.mastodonclient.R
import io.keiji.sample.mastodonclient.ui.toot_list.TootListFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            val fragment = TootListFragment()
            supportFragmentManager.beginTransaction()
                .add(
                    R.id.fragment_container,
                    fragment,
                    TootListFragment.TAG
                )
                .commit()
        }
    }
}
