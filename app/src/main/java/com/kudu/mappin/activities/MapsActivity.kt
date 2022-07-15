package com.kudu.mappin.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
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
import com.google.android.gms.maps.model.*
import com.kudu.mappin.R
import com.kudu.mappin.databinding.ActivityMapsBinding
import net.postgis.jdbc.geometry.Point
import java.io.IOException
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener,
    GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraMoveStartedListener,
    GoogleMap.OnCameraIdleListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnMapClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private lateinit var toggle: ActionBarDrawerToggle
    private var currentLatitude = 0.0
    private var currentLongitude = 0.0
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var pointerLatitude = 0.0
    private var pointerLongitude = 0.0

    private var points: List<LatLng>? = null
    private var markers: List<Marker>? = null
    private var markerNumber = 1

    private var polygon: Polygon? = null
    private var latLngList: ArrayList<LatLng> = ArrayList()
    private var markerList: ArrayList<Marker> = ArrayList()
    private lateinit var markerOptions: MarkerOptions
    private lateinit var marker: Marker
    private var polygonOptions: PolygonOptions = PolygonOptions()

    private lateinit var point: Point


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
        //points and polygons
/*        points = ArrayList()
        markers = ArrayList<Marker>()*/
        askPermissionForLocation()

        //line and poly buttons visibility
        binding.linearAddPolyButtons.visibility = View.GONE
        binding.btnAddPoint.visibility = View.GONE

        //pointer and data visibility
        binding.ivPointer.visibility = View.GONE
        binding.tvXCoordinate.visibility = View.GONE
        binding.tvYCoordinate.visibility = View.GONE
        binding.tvLength.visibility = View.GONE
        binding.tvArea.visibility = View.GONE

        //radio buttons
        // TODO: radio buttons implementation for editing and viewing map
        binding.radioGroup.setOnCheckedChangeListener { _, _ ->
            if (binding.btnMaps.isChecked) {

            } else if (binding.btnEditMaps.isChecked) {

            }
        }

        //sidenav
        binding.navBarView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_online_point -> {
                    Toast.makeText(this,
                        "Online Point clicked",
                        Toast.LENGTH_SHORT).show()

                    binding.btnAddPoint.visibility = View.VISIBLE
                    binding.linearAddPolyButtons.visibility = View.GONE
                    binding.ivPointer.visibility = View.VISIBLE
                    binding.tvXCoordinate.visibility = View.VISIBLE
                    binding.tvYCoordinate.visibility = View.VISIBLE
                    binding.btnAddPoint.setOnClickListener {
                        startActivity(Intent(this, EditPointActivity::class.java))
                        Toast.makeText(this, "Add Point clicked", Toast.LENGTH_SHORT).show()
                    }
                    mMap.setOnMapClickListener { latlng -> // Clears the previously touched position
                        mMap.clear()
                        // Animating to the touched position
//                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))

                        val location = LatLng(latlng.latitude, latlng.longitude)
                        mMap.addMarker(MarkerOptions().position(location))
                    }
                }
                R.id.nav_online_polygon -> {
                    Toast.makeText(this,
                        "Online Polygon clicked",
                        Toast.LENGTH_SHORT).show()

                    binding.linearAddPolyButtons.visibility = View.VISIBLE
                    binding.btnAddPoint.visibility = View.GONE
                    binding.ivPointer.visibility = View.VISIBLE
                    binding.tvXCoordinate.visibility = View.VISIBLE
                    binding.tvYCoordinate.visibility = View.VISIBLE

                    mMap.setOnMapClickListener(this@MapsActivity)

                    binding.btnConfirmPoly.setOnClickListener {
                        startActivity(Intent(this, EditPolyActivity::class.java))
                        Toast.makeText(this, "Add Poly clicked", Toast.LENGTH_SHORT).show()
                    }

                    binding.btnRemovePoly.setOnClickListener {
                        if (latLngList.size <= 1) {
                            latLngList.clear()
                            mMap.clear()
                        } else {
                            latLngList.removeLast()
                            drawPolygon()
                        }
                        Toast.makeText(this, "Remove Poly clicked", Toast.LENGTH_SHORT).show()
                    }

                    binding.btnCancelPoly.setOnClickListener {

                        //dialog
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("Delete Polygon ?")
                        builder.setMessage("Are you sure you want to delete polygon ?")
                        builder.setIcon(R.drawable.ic_alert)

                        //performing positive action
                        builder.setPositiveButton("Yes") { dialogInterface, _ ->
                            removePolygon()
                            dialogInterface.dismiss()
                        }
                        //performing negative action
                        builder.setNegativeButton("No") { dialogInterface, _ ->
                            dialogInterface.dismiss()
                        }

                        //creating AlertDialog
                        val alertDialog: AlertDialog = builder.create()
                        //setting properties
                        alertDialog.setCancelable(false)
                        alertDialog.show()
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

        mMap.setOnCameraMoveListener(this)
        mMap.setOnCameraMoveStartedListener(this)
        mMap.setOnCameraIdleListener(this)

        mMap.setOnMarkerDragListener(this)
//        mMap.setOnMapClickListener(this)

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

    //pointer
    override fun onLocationChanged(location: Location) {
        val geocoder = Geocoder(this, Locale.getDefault())
        var addresses: List<Address>? = null
        try {
            addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        setAddress(addresses!![0])
    }

    @SuppressLint("SetTextI18n")
    private fun setAddress(addresses: Address) {
        if (addresses != null) {
            if (addresses.getAddressLine(0) != null) {
                binding.tvXCoordinate.text = "X: ${addresses.latitude}"
                binding.tvYCoordinate.text = "Y: ${addresses.longitude}"
//                binding.tvYCoordinate.text = addresses.longitude.toString()
            }
        }
    }

    override fun onCameraMove() {
    }

    override fun onCameraMoveStarted(p0: Int) {
    }

    override fun onCameraIdle() {
        var addresses: List<Address>? = null
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            addresses = geocoder.getFromLocation(mMap.cameraPosition.target.latitude,
                mMap.cameraPosition.target.longitude,
                1)

            setAddress(addresses!![0])

        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onMarkerDrag(p0: Marker) {
        TODO("Not yet implemented")
    }

    override fun onMarkerDragEnd(p0: Marker) {
        TODO("Not yet implemented")
    }

    override fun onMarkerDragStart(p0: Marker) {
        TODO("Not yet implemented")
    }

    // draw polygon
    override fun onMapClick(latLng: LatLng) {
        /*markerOptions = MarkerOptions().position(latLng)
        marker = mMap.addMarker(markerOptions)!!

        latLngList.add(latLng)
        markerList.add(marker)

        //draw polygon
        if (polygon != null) {
            polygon?.remove()
        }

        polygonOptions = PolygonOptions().addAll(latLngList)
            .clickable(true)
        polygon = mMap.addPolygon(polygonOptions)

        polygon!!.fillColor = resources.getColor(R.color.logo_color2)
        polygon!!.strokeColor = Color.BLUE*/
        latLngList.add(latLng)
        drawPolygon()

    }

    private fun removePolygon() {
        /*  if (polygon != null) {
              polygon!!.remove()
          }

          for (marker in markerList) {
              marker.remove()
          }
          latLngList.clear()
          markerList.clear()*/

        latLngList.clear()
        mMap.clear()
    }

    private fun drawPolygon() {
        mMap.clear()
        val polygon = PolygonOptions()
        latLngList.forEach {
            mMap.addMarker(MarkerOptions().position(it))
            polygon.add(it)
        }
        polygon.fillColor(resources.getColor(R.color.logo_color1))
        polygon.strokeWidth(6F)
        polygon.strokeColor(resources.getColor(R.color.logo_color2))
        mMap.addPolygon(polygon)
    }


}