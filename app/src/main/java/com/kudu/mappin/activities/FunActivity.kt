package com.kudu.mappin.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolygonOptions
import com.kudu.mappin.R
import com.kudu.mappin.databinding.ActivityFunBinding

class FunActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private lateinit var binding: ActivityFunBinding
    private lateinit var mMap: GoogleMap

    private lateinit var clear: Button

    //    private var polygon: Polygon? = null
    private var polygon: PolygonOptions? = null
    private var latLngList = ArrayList<LatLng>()
    private var markerList: ArrayList<Marker> = ArrayList()
    private lateinit var markerOptions: MarkerOptions
    private lateinit var marker: Marker
    private var polygonOptions: PolygonOptions = PolygonOptions()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFunBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fun) as SupportMapFragment
        mapFragment.getMapAsync(this@FunActivity)
        askPermissionForLocation()

        clear = binding.btnClearPolygon

        clear.setOnClickListener {
            /*      if (polygon != null) {
                      polygon!!.remove()
                  }

                  for (marker in markerList) {
                      marker.remove()
                  }
                  latLngList.clear()
                  markerList.clear()*/

            if (latLngList.size <= 1) {
                latLngList.clear()
                mMap.clear()
            } else {
                latLngList.removeLast()
                drawPolygon()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
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
        mMap.setMinZoomPreference(20F)

        mMap.setOnMapClickListener(this)

    }

    private fun askPermissionForLocation() {
        askPermission(Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION) {
            //all permissions already granted or just granted


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
        polygon!!.strokeColor = Color.BLUE

*/
        latLngList.add(latLng)
        drawPolygon()

    }

    private fun drawPolygon() {
        mMap.clear()
        val polygon = PolygonOptions()
        latLngList.forEach {
            mMap.addMarker(MarkerOptions().position(it))
            polygon.add(it)
        }
        /*polygon.fillColor(R.color.logo_color1)
        polygon.strokeColor(Color.WHITE)*/
        polygon.fillColor(resources.getColor(R.color.logo_color1))
        polygon.strokeColor(resources.getColor(R.color.logo_color2))
        mMap.addPolygon(polygon)
    }



}