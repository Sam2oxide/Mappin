package com.kudu.mappin.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.kudu.mappin.R
import com.kudu.mappin.databinding.ActivityMapsBinding
import java.io.IOException


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private lateinit var toggle: ActionBarDrawerToggle
    private var currentLatitude = 0.0
    private var currentLongitude = 0.0
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

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
        askPermissionForLocation()

        //online point functionality
        binding.linearAddPolyButtons.visibility = View.GONE
        /*binding.btnAddPoly.setOnClickListener {
            startActivity(Intent(this, EditPolyActivity::class.java))
            Toast.makeText(this, "Add Poly clicked", Toast.LENGTH_SHORT).show()
        }*/

        //online point
        binding.btnAddPoint.visibility = View.GONE
        /*binding.btnAddPoint.setOnClickListener {
            startActivity(Intent(this, EditPointActivity::class.java))
            Toast.makeText(this, "Add Point clicked", Toast.LENGTH_SHORT).show()
        }*/

        //radio buttons
        // TODO: radio buttons implementation for editing and viewing map

        //sidenav
        binding.navBarView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_online_point -> {
                    Toast.makeText(this,
                        "Online Point clicked",
                        Toast.LENGTH_SHORT).show()

                    binding.btnAddPoint.visibility = View.VISIBLE
                    binding.linearAddPolyButtons.visibility = View.GONE
                    binding.btnAddPoint.setOnClickListener {
                        startActivity(Intent(this, EditPointActivity::class.java))
                        Toast.makeText(this, "Add Point clicked", Toast.LENGTH_SHORT).show()
                    }
                }
                R.id.nav_online_polygon -> {
                    Toast.makeText(this,
                        "Online Polygon clicked",
                        Toast.LENGTH_SHORT).show()

                    binding.linearAddPolyButtons.visibility = View.VISIBLE
                    binding.btnAddPoint.visibility = View.GONE
                    binding.btnAddPoly.setOnClickListener {
                        startActivity(Intent(this, EditPolyActivity::class.java))
                        Toast.makeText(this, "Add Poly clicked", Toast.LENGTH_SHORT).show()
                    }
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


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }

    //menu
    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.maps_nav_menu, menu)

        val searchView = menu?.findItem(R.id.search_view)?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            //            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextSubmit(query: String?): Boolean {
                val location = searchView.query.toString()
                var addressList: List<Address>? = null
                if (location != null || location == "") {
                    val geocoder = Geocoder(this@MapsActivity)
                    try {
                        addressList = geocoder.getFromLocationName(location, 1)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    val address: Address = addressList!![0]
                    val latLng = LatLng(address.latitude, address.longitude)

                    mMap.addMarker(MarkerOptions().position(latLng).title(location))
                    moveCamera(latLng, 20F)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
//                return true
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        googleMap.mapType = MAP_TYPE_SATELLITE
        askPermissionForLocation()

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mMap.isMyLocationEnabled = true // mMap!!
        mMap.uiSettings.isZoomControlsEnabled = true
    }

    private fun askPermissionForLocation() {
        askPermission(Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION) {
            //all permissions already granted or just granted

            getCurrentLocation()

        }.onDeclined { e ->
            if (e.hasDenied()) {
                //the list of denied permissions
                repeat(e.denied.size) {
                }
                AlertDialog.Builder(this)
                    .setMessage("Please accept our permissions")
                    .setPositiveButton("yes") { _, _ ->
                        e.askAgain()
                    } //ask again
                    .setNegativeButton("no") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }

            if (e.hasForeverDenied()) {
                //the list of forever denied permissions, user has check 'never ask again'
                repeat(e.foreverDenied.size) {
                }
                e.goToSettings()
            }
        }
    }

    private fun getCurrentLocation() {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this@MapsActivity)

        try {
            @SuppressLint("MissingPermission")
            val location = fusedLocationProviderClient.lastLocation

            location.addOnCompleteListener { loc ->
                if (loc.isSuccessful) {
                    val currentLocation = loc.result
                    if (currentLocation != null) {
                        moveCamera(LatLng(currentLocation.latitude, currentLocation.longitude), 20F)

                        currentLatitude = currentLocation.latitude
                        currentLongitude = currentLocation.longitude
                    }
                } else {
                    askPermissionForLocation()
                }
            }
        } catch (se: Exception) {
            Log.e("TAG", "Security Exception")
        }
    }

    private fun moveCamera(latLng: LatLng, zoom: Float) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
    }
}