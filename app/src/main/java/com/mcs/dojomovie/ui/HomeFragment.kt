package com.mcs.dojomovie.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mcs.dojomovie.R
import com.mcs.dojomovie.adapter.FilmAdapter
import com.mcs.dojomovie.model.Film
import com.mcs.dojomovie.repository.FilmRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {
    private lateinit var filmAdapter: FilmAdapter
    private lateinit var rvFilms: RecyclerView
    private lateinit var filmRepository: FilmRepository

    private var myMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        filmRepository = FilmRepository(requireContext().applicationContext)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        rvFilms = view.findViewById(R.id.rv_film)

        setupMap()
        setupRecyclerView()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadAndDisplayFilms(forceRefresh = false)
    }

    private fun setupMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync { googleMap ->
            myMap = googleMap
            val dojoMovieLocation = LatLng(-6.2088, 106.8456)
            myMap?.addMarker(
                MarkerOptions().position(dojoMovieLocation).title("DoJo Movie")
            )
            myMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(dojoMovieLocation, 17f))
        }
    }

    private fun setupRecyclerView() {
        val itemClickListener = object : FilmAdapter.OnItemClickListener {
            override fun onItemClick(film: Film) {
                val intent = Intent(requireActivity(), FilmPage::class.java)
                intent.putExtra(FilmPage.EXTRA_FILM_ID, film.id)
                startActivity(intent)
            }
        }
        filmAdapter = FilmAdapter(ArrayList(), itemClickListener)
        rvFilms.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvFilms.adapter = filmAdapter
    }

    private fun loadAndDisplayFilms(forceRefresh: Boolean = false) {
        viewLifecycleOwner.lifecycleScope.launch {
            val filmsFromDb = filmRepository.getFilmsFromDb()

            if (filmsFromDb.isEmpty() || forceRefresh) {
                filmRepository.fetchFilmsFromNetwork { success, message ->
                    if (success) {
                        viewLifecycleOwner.lifecycleScope.launch {
                            val updatedFilms = filmRepository.getFilmsFromDb()
                            updateAdapterUI(updatedFilms)
                        }
                    } else {
                        viewLifecycleOwner.lifecycleScope.launch {
                            updateAdapterUI(filmsFromDb)
                        }
                    }
                }
            } else {
                updateAdapterUI(filmsFromDb)

                filmRepository.fetchFilmsFromNetwork { success, message ->
                    if (success) {
                        viewLifecycleOwner.lifecycleScope.launch {
                            val latestFilms = filmRepository.getFilmsFromDb()
                            updateAdapterUI(latestFilms)
                        }
                    }
                }
            }
        }
    }

    private suspend fun updateAdapterUI(films: List<Film>) {
        withContext(Dispatchers.Main) {
            filmAdapter.updateData(films)
        }
    }
}