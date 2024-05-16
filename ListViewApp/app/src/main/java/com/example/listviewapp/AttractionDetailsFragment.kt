package com.example.listviewapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AttractionDetailsFragment : Fragment() {

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_CAMERA_PERMISSION = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val orientation = resources.configuration.orientation
        val layoutResId = if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            R.layout.fragment_attraction_details_land
        } else {
            R.layout.fragment_attraction_details
        }
        return inflater.inflate(layoutResId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val attraction = arguments?.getParcelable<Attraction>("attraction")

        if (attraction != null) {
            val titleTextView: TextView = view.findViewById(R.id.title_text_view)
            val descriptionTextView: TextView = view.findViewById(R.id.description_text_view)
            val imageView: ImageView = view.findViewById(R.id.image_view)
            val stagesRecyclerView: RecyclerView = view.findViewById(R.id.stages_recycler_view)
            val fab: FloatingActionButton = view.findViewById(R.id.fab)

            titleTextView.text = attraction.name
            descriptionTextView.text = attraction.description
            imageView.setImageResource(attraction.imageResId)

            stagesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            stagesRecyclerView.adapter = StagesAdapter(attraction.stages)

            fab.setOnClickListener {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA),
                        REQUEST_CAMERA_PERMISSION)
                } else {
                    launchCamera()
                }
            }
        }
    }

    private fun launchCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } else {
            Toast.makeText(requireContext(), "Camera not available", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    launchCamera()
                } else {
                    Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            // Handle the captured photo here (e.g., display it in an ImageView or save it)
            Toast.makeText(requireContext(), "Photo captured successfully", Toast.LENGTH_SHORT).show()
        }
    }
}
