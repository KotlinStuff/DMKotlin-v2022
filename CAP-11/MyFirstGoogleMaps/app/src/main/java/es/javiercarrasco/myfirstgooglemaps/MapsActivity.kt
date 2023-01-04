package es.javiercarrasco.myfirstgooglemaps

import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import es.javiercarrasco.myfirstgooglemaps.databinding.ActivityMapsBinding
import java.io.IOException

class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener,
    GoogleMap.OnMarkerClickListener {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private var lastMarker: Marker? = null

    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            configMap()
        } else {
            Log.d("PERMISOS", "Explicación de la necesidad del permiso.")
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.title = getString(R.string.app_name)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Se habilitan los botones del zoom.
        mMap.uiSettings.isZoomControlsEnabled = true
        // Se habilita la brújula, solo aparecerá cuando se gire el mapa.
        mMap.uiSettings.isCompassEnabled = true
        // Se habilita el botón para centrar la ubicación actual (por defecto es true).
        mMap.uiSettings.isMyLocationButtonEnabled = true

        mMap.setOnMyLocationButtonClickListener(this)
        mMap.setOnMyLocationClickListener(this)
        mMap.setOnMapLongClickListener {
            Log.d("onMapLongClickListener", it.toString())
            placeMarkerOnMap(it)
            mMap.animateCamera(CameraUpdateFactory.newLatLng(it))
        }
        mMap.setOnMarkerClickListener(this)

        configMap()

        binding.button.setOnClickListener {
            // Se selecciona el tipo de mapa.
            when (mMap.mapType) {
                GoogleMap.MAP_TYPE_NORMAL -> {
                    mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                    binding.button.text = "Híbrido"
                }
                GoogleMap.MAP_TYPE_HYBRID -> {
                    mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                    binding.button.text = "Satélite"
                }
                GoogleMap.MAP_TYPE_SATELLITE -> {
                    mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                    binding.button.text = "Terreno"
                }
                GoogleMap.MAP_TYPE_TERRAIN -> {
                    mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                    binding.button.text = "Normal"
                }
            }
        }
    }

    // Comprueba el estado del permiso.
    private fun isPermissionGranted() = ContextCompat.checkSelfPermission(
        this,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    // Comprueba el permiso de ubicación y recoloca el mapa según la ubicación.
    @Suppress("MissingPermission")
    private fun configMap() {
        when {
            (isPermissionGranted()) -> {
                // Se añade la marca en la ubicación real.
                mMap.isMyLocationEnabled = true

                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        lastLocation = location
                        val currentLatLng = LatLng(location.latitude, location.longitude)
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
                    }
                }
            }
            else -> {
                requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        // Se establece el tipo de mapa.
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL // Por defecto.
        binding.button.text = "Normal"
    }

    override fun onMyLocationButtonClick(): Boolean {
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    lastLocation.latitude,
                    lastLocation.longitude
                ), 14f
            )
        )
        Log.d("onMyLocationButtonClick", "Click sobre el botón ubicación!!")
        return true
    }

    override fun onMyLocationClick(p0: Location) {
        Log.d("onMyLocationClick", "Mi ubicación pulsada [${p0.latitude}, ${p0.longitude}]")
    }

    /*
     * MARCADORES
     */


    // Método para añadir la marca en la localización indicada.
    private fun placeMarkerOnMap(location: LatLng) {
        // Se crea un objeto MarkerOptions para configurar la marca.
        val markerOptions = MarkerOptions().position(location)
        // Título de la marca.
        markerOptions.title(getAddress(location))

        // Cambia la marca de color.
        markerOptions.icon(
            BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_VIOLET
            )
        )

        // Se elimina la última marca.
        if (lastMarker != null) {
            lastMarker!!.remove()
        }

        // Se añade la marca al mapa y se guarda en lastMarker.
        lastMarker = mMap.addMarker(markerOptions)
    }

    // Se dispara cuando se hace click sobre una marca en el mapa, se implementa al heredar
    // de GoogleMap.OnMarkerClickListener.
    override fun onMarkerClick(p0: Marker): Boolean {
        p0.showInfoWindow()
        Log.d("onMarkerClick", "Click sobre una marca ${p0.id}")
        return true
    }

    // Método para obtener la dirección de una marca.
    private fun getAddress(latLng: LatLng): String {
        // Se instancian las variables necesarias.
        val geocoder = Geocoder(this)

        // Se pueden obtener más de una dirección de un mismo punto.
        val addresses: MutableList<Address>?
        val address: Address?
        var addressText = ""

        try {
            // Se obtiene la información del punto concreto.
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

            // Se comprueba que la dirección no sea nula o vacía.
            if ((null != addresses) && addresses.isNotEmpty()) {
                // Nos quedamos con la primera posición.
                address = addresses[0]

                // Se comprueba que la dirección tenga o no más de una línea.
                if (address.maxAddressLineIndex > 0) {
                    for (i in 0 until address.maxAddressLineIndex) {
                        addressText += if (i == 0) address.getAddressLine(i)
                        else "\n${address.getAddressLine(i)}"
                    }
                } else { // Acción más habitual.
                    addressText += "${address.thoroughfare}, ${address.subThoroughfare}\n"
                }
            }
        } catch (e: IOException) {
            e.localizedMessage?.let { Log.e("MapsActivity", it) }
        }
        return addressText
    }

}