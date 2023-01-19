package es.javiercarrasco.myfirstmapbox

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.Style
import com.mapbox.maps.ViewAnnotationAnchor
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.extension.style.layers.properties.generated.SymbolPlacement
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.attribution.attribution
import com.mapbox.maps.plugin.compass.compass
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.gestures.addOnMapLongClickListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.logo.logo
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import es.javiercarrasco.myfirstmapbox.databinding.ActivityMainBinding
import es.javiercarrasco.myfirstmapbox.databinding.TituloMarcaBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var permissionsManager: PermissionsManager

    private fun moveCam(punto: Point) = cameraOptions {
        zoom(12.0)
        pitch(10.0) // Inclinación.
        bearing(45.0) // Rotación.
        center(punto)
    }

    private val ANIM_CAM = MapAnimationOptions.mapAnimationOptions { duration(1200) }
    private val RESET_CAM = cameraOptions {
        zoom(9.0)
        pitch(0.0) // Inclinación.
        bearing(0.0) // Rotación.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onMapReady()

        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            activarLocalizacion()
        } else {
            permissionsManager = PermissionsManager(permissionsListener)
            permissionsManager.requestLocationPermissions(this)
        }
    }

    private fun activarLocalizacion() {
        binding.mapView.location.enabled = true

        // Personalización básica del puck.
        binding.mapView.location.updateSettings {
            enabled = true
            pulsingEnabled = true
            pulsingColor = Color.CYAN
        }

        // Se pasa la ubicación del usuario a la cámara, activando un tracker.
        binding.mapView.location.addOnIndicatorPositionChangedListener(
            onIndicatorPositionChangedListener
        )
        // Este listener activa la rotación.
//        binding.mapView.location.addOnIndicatorBearingChangedListener(
//            onIndicatorBearingChangedListener
//        )
    }

    // Obtiene las coordenadas de la localización del usuario.
    private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener {
        binding.mapView.getMapboxMap().setCamera(
            CameraOptions.Builder().bearing(it).build()
        )
    }

    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        with(binding.mapView) {
            getMapboxMap().setCamera(CameraOptions.Builder().center(it).build())
            gestures.focalPoint = this
                .getMapboxMap()
                .pixelForCoordinate(it)
        }
    }

    private fun onMapReady() {
        // Mapa configurado y estilo cargado.
        binding.mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS).apply {
            // Ya se puede añadir información o hacer otros ajustes.
            binding.mapView.logo.enabled = false // Desactiva el logo.
            binding.mapView.attribution.enabled = false // Desactiva la atribución.
            // Mantiene la brújula siempre activada.
            binding.mapView.compass.fadeWhenFacingNorth = false
        }

        binding.floatingActionButton.setOnClickListener {
            binding.mapView.annotations.cleanup()
            binding.mapView.viewAnnotationManager.removeAllViewAnnotations()
        }


        binding.mapView.getMapboxMap().addOnMapClickListener { point ->
//            val coordenadas = binding.mapView.getMapboxMap().project(point, 20.0)
//            Toast.makeText(
//                this@MainActivity,
//                "onClick\nLAT: ${point.latitude()}, LON: ${point.longitude()}\n$coordenadas",
//                Toast.LENGTH_SHORT
//            ).show()

            // Se elimina el tracking de la cámara sobre la posición del usuario para evitar
            // que vuelva tras pulsar otro punto.
            binding.mapView.location.removeOnIndicatorPositionChangedListener(
                onIndicatorPositionChangedListener
            )

            binding.mapView.getMapboxMap().flyTo(moveCam(point), ANIM_CAM)

            true
        }

        binding.mapView.getMapboxMap().addOnMapLongClickListener { point ->
            addAnnotation(point, "Anotación\nLAT: ${point.latitude()}, LON: ${point.longitude()}")
            true
        }

//        binding.mapView.getMapboxMap().addOnCameraChangeListener {
//            Log.d("CameraChange", "La cámara se mueve.")
//        }

        binding.mapView.compass.addCompassClickListener {
            activarLocalizacion()
            binding.mapView.getMapboxMap().flyTo(RESET_CAM, ANIM_CAM)
            binding.mapView.annotations.cleanup()
        }

    }

    // MARCADORES
    private fun addAnnotation(point: Point, titulo: String) {
        // Se crea una instancia de la API Annotation y se obtiene PointAnnotationManager.
        val annotationApi = binding.mapView.annotations
        val pointAnnotationManager = annotationApi.createPointAnnotationManager()

        // Se configuran las opciones de la anotación.
        val pointAnnotationOptions = PointAnnotationOptions()
            // Se indica el punto de la anotación.
            .withPoint(point)
            // Se especifica la imagen asociada a la anotación.
            .withIconImage(
                BitmapFactory.decodeResource(
                    this.resources,
                    R.drawable.red_marker
                )
            )
            .withIconAnchor(IconAnchor.BOTTOM)

        // Se ajusta el tamaño de la marca.
        pointAnnotationOptions.iconSize = 0.5

        // Se añade el resultado al mapa.
        val pointAnnotation = pointAnnotationManager.create(pointAnnotationOptions)

        // Se prepara el título para la anotación.
        val viewAnnotationManager = binding.mapView.viewAnnotationManager
        val viewAnnotation = viewAnnotationManager.addViewAnnotation(
            resId = R.layout.titulo_marca,
            options = viewAnnotationOptions {
                geometry(point)
                //associatedFeatureId(pointAnnotation.featureIdentifier)
                anchor(ViewAnnotationAnchor.BOTTOM)
                offsetY(
                    ((pointAnnotation.iconImageBitmap?.height!! * pointAnnotation.iconSize!!) + 10)
                        .toInt()
                )
            }
        )
        TituloMarcaBinding.bind(viewAnnotation).textViewTitulo.text = titulo

        viewAnnotation.setOnClickListener {
            binding.mapView.viewAnnotationManager.removeViewAnnotation(it)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    var permissionsListener: PermissionsListener = object : PermissionsListener {
        override fun onExplanationNeeded(permissionsToExplain: List<String>) {
            Toast.makeText(
                applicationContext,
                getText(R.string.explicacion_permiso),
                Toast.LENGTH_LONG
            ).show()
        }

        override fun onPermissionResult(granted: Boolean) {
            if (granted) {
                activarLocalizacion()
            } else {
                Toast.makeText(
                    applicationContext,
                    getText(R.string.permiso_no_concedido),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    /**
     * CICLO DE VIDA
     */
    override fun onStart() {
        super.onStart()
        binding.mapView.onStart().apply {
            Log.d("onStart", "Start Mapbox!!")
        }
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.location.removeOnIndicatorBearingChangedListener(
            onIndicatorBearingChangedListener
        )
        binding.mapView.location.removeOnIndicatorPositionChangedListener(
            onIndicatorPositionChangedListener
        )
        binding.mapView.onStop().apply {
            Log.d("onStop", "Stop Mapbox!!")
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory().apply {
            Log.d("onLowMemory", "LowMemory Mapbox!")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy().apply {
            Log.d("onDestroy", "Destroy Mapbox!")
        }
    }
}