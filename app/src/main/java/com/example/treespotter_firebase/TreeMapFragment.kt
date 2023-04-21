package com.example.treespotter_firebase

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

private const val TAG = "TREE_MAP_FRAGMENT"

class TreeMapFragment : Fragment() {

    private lateinit var addTreeButton: FloatingActionButton

    private var locationPermissionGranted = false

    private var movedMapToUsersLocation = false

    private var fusedLocationProvider: FusedLocationProviderClient? = null

    private var map: GoogleMap? = null

    private val treeMarkers = mutableListOf<Marker>()

    private var treeList = listOf<Tree>()

    private val treeViewModel: TreeViewModel by lazy {
        ViewModelProvider(requireActivity()).get(TreeViewModel::class.java)
    }

    private val mapReadyCallback = OnMapReadyCallback { googleMap ->

        Log.d(TAG, "Google map ready")
        map = googleMap
        updateMap()
    }

    private fun updateMap() {

        //todo draw markers

        if (locationPermissionGranted) {
            if (!movedMapToUsersLocation) {
                moveMapToUserLocation()
            }
        }
    }


    private fun setAddTreeButtonEnabled(isEnabled: Boolean) {
        addTreeButton.isClickable = isEnabled
        addTreeButton.isEnabled = isEnabled

        if (isEnabled) {
            addTreeButton.backgroundTintList = AppCompatResources.getColorStateList(requireActivity(),
                android.R.color.holo_green_light)
        } else {
            addTreeButton.backgroundTintList = AppCompatResources.getColorStateList(requireActivity(),
                android.R.color.darker_gray)
        }
    }


    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
    }

    private fun requestLocationPermission() {
        //has user already granted perm??
        if (ContextCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationPermissionGranted = true
            Log.d(TAG, "permission already granted")
            updateMap()
            fusedLocationProvider = LocationServices.getFusedLocationProviderClient(requireActivity())

        } else {
            //need to ask for perm
            val requestLocationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                if (granted) {
                Log.d(TAG, "User granted permission")
                setAddTreeButtonEnabled(true)
                    locationPermissionGranted = true
                fusedLocationProvider = LocationServices.getFusedLocationProviderClient(requireActivity())
            } else {
                Log.d(TAG, "user granted no permission")
                setAddTreeButtonEnabled(false)
                    locationPermissionGranted = false
                showSnackbar(getString(R.string.give_permission))
            }

        }

            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    @SuppressLint("MissingPermission")
    private fun moveMapToUserLocation() {

        if (map == null) {
            return
        }

        if (locationPermissionGranted) {
            map?.isMyLocationEnabled = true
            map?.uiSettings?.isMyLocationButtonEnabled = true

            fusedLocationProvider?.lastLocation?.addOnCompleteListener { getLocationTask ->
                val location = getLocationTask.result
                if (location != null) {
                    Log.d(TAG, "User's location $location")
                    val center = LatLng(location.latitude, location.longitude)
                    val zoomLevel = 8f
                    map?.moveCamera(CameraUpdateFactory.newLatLngZoom(center, zoomLevel))
                    movedMapToUsersLocation = true
                } else {
                    showSnackbar(getString(R.string.no_location))
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainView = inflater.inflate(R.layout.fragment_tree_map, container, false)

        addTreeButton = mainView.findViewById(R.id.add_tree)
        addTreeButton.setOnClickListener {
            //todo addd tree at user's location
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment
        mapFragment?.getMapAsync(mapReadyCallback)

        // disable add tree button until location is available
        setAddTreeButtonEnabled(false)

        // request user's permission to accesss device location
        requestLocationPermission()

        //todo draw existing trees on map

        return mainView
    }

    companion object {
        @JvmStatic
        fun newInstance() = TreeMapFragment()
    }
}