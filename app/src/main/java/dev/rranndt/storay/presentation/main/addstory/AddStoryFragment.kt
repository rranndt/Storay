package dev.rranndt.storay.presentation.main.addstory

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.rranndt.storay.R
import dev.rranndt.storay.core.domain.model.AddStoryRequest
import dev.rranndt.storay.databinding.FragmentAddStoryBinding
import dev.rranndt.storay.presentation.base.BaseFragment
import dev.rranndt.storay.util.Constant.IMAGE
import dev.rranndt.storay.util.Helper.alert
import dev.rranndt.storay.util.Helper.createTempFile
import dev.rranndt.storay.util.Helper.hideKeyboard
import dev.rranndt.storay.util.Helper.negativeButton
import dev.rranndt.storay.util.Helper.rotateFile
import dev.rranndt.storay.util.Helper.showShortSnackBar
import dev.rranndt.storay.util.Helper.uriToFile
import dev.rranndt.storay.util.Result
import dev.rranndt.storay.util.permission.Permission
import dev.rranndt.storay.util.permission.PermissionManager
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class AddStoryFragment : BaseFragment<FragmentAddStoryBinding, AddStoryViewModel>() {

    private lateinit var currentPhotoPath: String
    private var getFile: File? = null

    private val permissionManager = PermissionManager.from(this)

    override val viewModel: AddStoryViewModel by viewModels()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentAddStoryBinding = FragmentAddStoryBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        subscribeToAddStoryEvent()
    }

    private fun setupView() {
        binding?.apply {
            ivPreviewPhoto.setOnClickListener {
                requireContext().alert {
                    setTitle(getString(R.string.title_add_photo))
                    setItems(R.array.add_photos) { _, item ->
                        if (item == 0) {
                            permissionManager
                                .request(Permission.Camera)
                                .rationale(getString(R.string.rationale_permission_camera))
                                .checkPermission { granted ->
                                    if (granted) {
                                        startCamera()
                                    } else {
                                        binding?.root?.showShortSnackBar(getString(R.string.cant_access_permission_camera))
                                    }
                                }
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
                                AddStoryRequest(reduceFileImage(it), edtDescription.text.toString())
                            )
                        )
                    }
                } else {
                    binding?.root?.showShortSnackBar(getString(R.string.pick_your_image_first))
                }
                hideKeyboard()
            }
        }
    }

    private fun subscribeToAddStoryEvent() = lifecycleScope.launch {
        viewModel.addStory.collect { result ->
            when (result.addStory) {
                is Result.Success -> {
                    showLoading(false)
                    findNavController().navigate(AddStoryFragmentDirections.actionAddStoryFragmentToHomeFragment())
                }

                is Result.Error -> {
                    showLoading(false)
                    binding?.root?.showShortSnackBar(result.addStory.message)
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
            action = ACTION_GET_CONTENT
            type = IMAGE
        }
        val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
        launcherIntentGallery.launch(chooser)
    }

    private fun startCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(requireContext().packageManager)

        createTempFile(requireContext()).also {
            val photoUri: Uri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().packageName,
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
                val myFile = uriToFile(uri, requireContext())
                getFile = myFile
                binding?.ivPreviewPhoto?.setImageURI(uri)
            }
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            myFile.let { file ->
                rotateFile(file)
                getFile = file
                binding?.ivPreviewPhoto?.setImageBitmap(BitmapFactory.decodeFile(file.path))
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
        } while (streamLength > MAXIMAL_SIZE)
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.apply {
            if (isLoading) progressBar.isVisible = true
            else progressBar.isInvisible = true
        }
    }

    companion object {
        private const val MAXIMAL_SIZE = 1000000
    }
}