package com.kudu.mappin.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kudu.mappin.R
import com.kudu.mappin.databinding.ActivityImportBinding

class ImportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbarImportActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_white)
        }
        binding.toolbarImportActivity.setNavigationOnClickListener { onBackPressed() }
    }

    //menu
    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.import_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //menu items
    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_import_from_folder -> {
                Toast.makeText(this, "Folder Import", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.action_import_from_zip -> {
                Toast.makeText(this, "Zip Import", Toast.LENGTH_SHORT).show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}