package dev.rranndt.storay.presentation.main.addstory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import dev.rranndt.storay.R
import dev.rranndt.storay.core.domain.model.AddStoryRequest
import dev.rranndt.storay.databinding.ActivityAddStoryBinding
import dev.rranndt.storay.presentation.main.MainActivity
import dev.rranndt.storay.util.Constant
import dev.rranndt.storay.util.Constant.PERMISSIONS
import dev.rranndt.storay.util.Helper
import dev.rranndt.storay.util.Helper.alert
import dev.rranndt.storay.util.Helper.hideKeyboard
import dev.rranndt.storay.util.Helper.negativeButton
import dev.rranndt.storay.util.Helper.showShortSnackBar
import dev.rranndt.storay.util.Result
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private val viewModel: AddStoryViewModel by viewModels()

    private lateinit var currentPhotoPath: String
    private var getFile: File? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latLng: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setSupportActionBar(binding.toolbarAddStory.toolbar)
        setContentView(binding.root)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.text_add_story_title)
        }

        setupView()
        subscribeToAddStoryEvent()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun setupView() {
        binding.apply {
            ivPreviewPhoto.setOnClickListener {
                this@AddStoryActivity.alert {
                    setTitle(getString(R.string.title_add_photo))
                    setItems(R.array.add_photos) { _, item ->
                        if (item == 0) {
                            launcherPermission.launch(PERMISSIONS.first())
                        } else if (item == 1) {
                            startGallery()
                        }
                    }
                    negativeButton { it.dismiss() }
                }
            }
            btnSend.setOnClickListener {
                if (getFile != null) {
                    getFile?.let {
                        viewModel.onEvent(
                            AddStoryEvent.AddStory(
                                AddStoryRequest(
                                    reduceFileImage(it),
                                    edtDescription.text.toString(),
                                    latLng
                                )
                            )
                        )
                    }
                } else {
                    binding.root.showShortSnackBar(getString(R.string.pick_your_image_first))
                }
                hideKeyboard()
            }
            checkBox?.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) getLatestLocation()
            }
        }
    }

    private fun subscribeToAddStoryEvent() = lifecycleScope.launch {
        viewModel.addStory.collect { result ->
            when (result.addStory) {
                is Result.Success -> {
                    showLoading(false)
                    startActivity(Intent(this@AddStoryActivity, MainActivity::class.java))
                }

                is Result.Error -> {
                    showLoading(false)
                    binding.root.showShortSnackBar(result.addStory.message)
                }

                is Result.Loading -> {
                    showLoading(true)
                }

                else -> {}
            }
        }
    }

    private fun startGallery() {
        val intent = Intent().apply {
            action = Intent.ACTION_GET_CONTENT
            type = Constant.IMAGE
        }
        val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
        launcherIntentGallery.launch(chooser)
    }

    private fun startCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(this.packageManager)

        Helper.createTempFile(this).also {
            val photoUri: Uri = FileProvider.getUriForFile(
                this,
                this.packageName,
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = Helper.uriToFile(uri, this)
                getFile = myFile
                binding.ivPreviewPhoto.setImageURI(uri)
            }
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            myFile.let { file ->
                Helper.rotateFile(file)
                getFile = file
                binding.ivPreviewPhoto.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private fun reduceFileImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > Constant.MAXIMAL_SIZE)
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            if (isLoading) progressBar.isVisible = true
            else progressBar.isInvisible = true
        }
    }

    private val launcherPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted and !isPermissionsGranted()) {
            binding.root.showShortSnackBar(getString(R.string.cant_access_permission_camera))
        } else {
            startCamera()
        }
    }

    private fun isPermissionsGranted() = PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun getLatestLocation() {
        if (checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
            and checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    latLng = LatLng(location.latitude, location.longitude)
                } else {
                    binding.root.showShortSnackBar(getString(R.string.location_not_found))
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
    }

    private fun checkPermission(permission: String) =
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> getLatestLocation()
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> getLatestLocation()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

}