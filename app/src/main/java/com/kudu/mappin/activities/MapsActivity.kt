package com.kudu.mappin.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.kudu.mappin.R
import com.kudu.mappin.databinding.ActivityMapsBinding


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //toggle
        toggle = ActionBarDrawerToggle(this, binding.root, R.string.open, R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setBackgroundDrawable(AppCompatResources.getDrawable(this,
            R.drawable.app_gradient_color_bg))
        supportActionBar?.title = ""

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //sidenav
        binding.navBarView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_online_point -> {
                    Toast.makeText(this,
                        "Online Point clicked",
                        Toast.LENGTH_SHORT).show()
                }
                R.id.nav_online_polygon -> {
                    Toast.makeText(this,
                        "Online Polygon clicked",
                        Toast.LENGTH_SHORT).show()
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT)
                        .show()
                }
                R.id.nav_cloud_map -> {
                    Toast.makeText(this, "Cloud Maps clicked", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            true
        }

        //sideNav header view
        /*val headerView: View = binding.navBarView.getHeaderView(0)
        val btnSettings = headerView.findViewById<ImageView>(R.id.btn_settings)
        val btnCloud = headerView.findViewById<ImageView>(R.id.btn_cloud_nav)
        headerView.setOnClickListener {
            when (it.id) {
                R.id.btn_settings -> {
//                    startActivity(Intent(this, SettingsActivity::class.java))
                    Toast.makeText(baseContext, "Settings clicked", Toast.LENGTH_SHORT)
                        .show()
                }
                R.id.btn_cloud_nav -> {
                    Toast.makeText(baseContext, "Cloud Maps clicked", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }*/


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }

    //menu
    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.maps_nav_menu, menu)


        //TODO: implement search for locations
        val searchView = menu?.findItem(R.id.search_view)?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true

            override fun onQueryTextChange(newText: String?): Boolean {
                /* musicListSearch = ArrayList()
 //                Toast.makeText(this@MainActivity, newText.toString(), Toast.LENGTH_SHORT).show()
                 if (newText != null) {
                     val userInput = newText.lowercase()
                     for (song in musicListMA)
                         if (song.title.lowercase().contains(userInput))
                             musicListSearch.add(song)
                     search = true
                     musicAdapter.updateMusicList(searchList = musicListSearch)
                 }*/
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}