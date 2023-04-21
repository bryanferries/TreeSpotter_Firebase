package com.example.treespotter_firebase

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import com.google.android.material.floatingactionbutton.FloatingActionButton

private const val TAG = "TREE_MAP_FRAGMENT"

class TreeMapFragment : Fragment() {

    private lateinit var addTreeButton: FloatingActionButton

    private var locationPermissionGranted = false

    private var moveMapToUsersLocation = false

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
        //draw blue dot at user's localtion
        //show no location message if location permission not granted
        //or device does not have location enabled.
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

        //todo request user's permission to accesss device location

        //todo disable add tree button until location is available

        //todo draw existing trees on map

        return mainView
    }

    companion object {
        @JvmStatic
        fun newInstance() = TreeMapFragment()
    }
}