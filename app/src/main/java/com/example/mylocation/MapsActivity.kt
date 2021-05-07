package com.example.mylocation

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnSuccessListener
import java.io.IOException

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val LOCATION_PERMISSION_REQUEST = 1
    lateinit var searchView: SearchView
    lateinit var mapFrag: SupportMapFragment
    lateinit var fusedLocationProvider: FusedLocationProviderClient
    val Tag: String="com.example.mylocation"
    val GEOFENCE_RADIUS: Float= 200F


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        //Logcat lat and long print
        // janapuri - buuton - geofence


        var search=findViewById<SearchView>(R.id.location)


        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                var location: String = search.query.toString()
                lateinit var addressList: List<Address>

                if (location == null || location == "") {
                    Toast.makeText(applicationContext, "provide location", Toast.LENGTH_SHORT).show()
                } else {
                    val geoCoder = Geocoder(this@MapsActivity)
                    try {
                        addressList = geoCoder.getFromLocationName(location, 1)

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    mMap.clear()
                    val address: Address = addressList.get(0)
                    val city: String = address.locality
                    val state: String= address.adminArea
                    val country: String= address.countryName
                    val knownName: String= address.featureName

                    Log.e("com.example.mylocation","City: "+city + " State: " + state +" Country: "+country+ " Known Name: "+ knownName)


                    var latLng: LatLng = LatLng(address.latitude, address.longitude)

                    mMap.addMarker(MarkerOptions().position(latLng).title("Lat: ${address.latitude}, " + "Lng: ${address.longitude}"))
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))



                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        fusedLocationProvider= LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProvider.lastLocation.addOnSuccessListener(object : OnSuccessListener<Location> {
            override fun onSuccess(location: Location) {
                if (location != null) {
                    var lat: Double = location.latitude
                    var long: Double = location.longitude
                }
                var latLng: LatLng = LatLng(location.latitude, location.longitude)


                mMap.addMarker(MarkerOptions().position(latLng).title("Lat: ${location.latitude}, " + "Lng: ${location.longitude}"))
            }
        })
        getLocationAccess()
    }


    private fun getLocationAccess() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        }
        else
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST)
    }


    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                mMap.isMyLocationEnabled = true
            }
            else {
                Toast.makeText(this, "User has not granted location access permission", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }


    fun addCirle(latLng: LatLng, radius: Float){
        val circleOptions: CircleOptions?=null
        circleOptions?.center(latLng)
        circleOptions?.radius(radius.toDouble())
        circleOptions?.strokeColor(Color.argb(255, 255, 0, 0))
        circleOptions?.fillColor(Color.argb(64, 255, 0, 0))
        circleOptions?.strokeWidth(4F)
        mMap.addCircle(circleOptions)

    }

}