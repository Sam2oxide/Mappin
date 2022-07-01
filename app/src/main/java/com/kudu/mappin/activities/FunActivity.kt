package com.kudu.mappin.activities

import android.Manifest
import android.content.pm.PackageManager
import android.location.LocationListener
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.kudu.mappin.R
import com.kudu.mappin.databinding.ActivityFunBinding

class FunActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener {
    private lateinit var binding: ActivityFunBinding
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFunBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this@FunActivity)
        askPermissionForLocation()
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

        mMap.setOnCameraMoveListener(this)
        mMap.setOnCameraMoveStartedListener(this)
        mMap.setOnCameraIdleListener(this)
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
}