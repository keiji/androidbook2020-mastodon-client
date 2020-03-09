package io.keiji.sample.mastodonclient.ui.toot_edit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.keiji.sample.mastodonclient.R

class TootEditActivity : AppCompatActivity() {

    companion object {
        val TAG = TootEditActivity::class.java.simpleName

        fun newIntent(context: Context): Intent {
            return Intent(context, TootEditActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toot_edit)

        if (savedInstanceState == null) {
            val fragment = TootEditFragment.newInstance()
            supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment, TootEditFragment.TAG)
                    .commit()
        }
    }

}