package com.kudu.mappin.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kudu.mappin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //local import button
        binding.btnLocalFile.setOnClickListener {
            startActivity(Intent(this, ImportActivity::class.java))
            Toast.makeText(this, "import local clicked", Toast.LENGTH_SHORT).show()
        }

        binding.btnCloudFile.setOnClickListener {
//            startActivity(Intent(this, MapsActivity::class.java))
            startActivity(Intent(this, MapActivity::class.java))
            Toast.makeText(this, "Cloud import clicked", Toast.LENGTH_SHORT).show()
        }
    }
}