package dev.rranndt.storay.presentation.main.maps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import dev.rranndt.storay.R
import dev.rranndt.storay.databinding.FragmentMapsBinding
import dev.rranndt.storay.presentation.base.BaseFragment
import dev.rranndt.storay.util.Helper.showShortSnackBar
import dev.rranndt.storay.util.Result
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MapsFragment : BaseFragment<FragmentMapsBinding, MapsViewModel>(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val boundsBuilder = LatLngBounds.Builder()

    override val viewModel: MapsViewModel by viewModels()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentMapsBinding = FragmentMapsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        viewModel.onEvent(MapsEvent.GetStoriesWithLocation)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireActivity(), R.raw.map_style))
        displayMarker()
    }

    private fun displayMarker() = lifecycleScope.launch {
        viewModel.getStoriesWithLocation.collect { result ->
            when (result.getStoriesWithLocation) {
                is Result.Success -> {
                    result.getStoriesWithLocation.data?.forEach {
                        val position = LatLng(it.lat, it.lon)
                        mMap.addMarker(MarkerOptions().position(position).title(it.name))
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 10f))
                        boundsBuilder.include(position)
                    }
                }

                is Result.Error -> {
                    binding?.root?.showShortSnackBar(result.getStoriesWithLocation.message)
                }

                is Result.Loading -> {}

                else -> {}
            }
        }
    }
}