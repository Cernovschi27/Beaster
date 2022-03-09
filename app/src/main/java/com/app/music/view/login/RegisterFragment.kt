package com.app.music.view.login

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.TextView
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.dhaval2404.imagepicker.ImagePicker
import com.app.music.R
import com.app.music.databinding.RegisterFragmentBinding
import com.app.music.domain.User
import com.app.music.validators.UIValidator
import com.app.music.viewModel.login.LoginViewModel
import java.io.File

enum class Mode {
    EDITABLE, READ_ONLY, DEFAULT
}

private const val maxImageSize = 1024
private const val maxWidth = 250
private const val maxHeight = 250


class RegisterFragment : Fragment() {

    companion object {
        const val PERMISSION_REQUEST_CODE_CAMERA = 1
        const val PERMISSION_REQUEST_CODE_WRITE_EXTERNAL = 2
        const val PERMISSION_REQUEST_CODE_READ_EXTERNAL = 3
    }

    private lateinit var binding: RegisterFragmentBinding

    private lateinit var viewModel: LoginViewModel
    private val validator = UIValidator()

    private lateinit var inputViews: List<TextView>

    private var fileUri: Uri? = null
    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                RESULT_OK -> {
                    //Image Uri will not be null for RESULT_OK
                    fileUri = data?.data!!

                    binding.profileImage.setImageURI(fileUri)
                    Log.d(LOG_TAG, "image set")
                    Log.d(LOG_TAG, fileUri.toString())
                    //viewModel.saveProfileImage(fileUri!!, requireContext())
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT)
                        .show()
                }
                else -> {
                    Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.register_fragment, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()

        initializeByMode()
        //display currently edited input view above keyboard
        requireActivity().window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
                    or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        )

        checkOrRequestExternalStoragePermission()
    }

    private fun initializeByMode() {
        viewModel.mode.observe(viewLifecycleOwner, {
            when (it!!) {
                Mode.DEFAULT -> {
                    clearData()
                }
                Mode.READ_ONLY -> {
                    makeReadOnly()
                }
                Mode.EDITABLE -> {
                    binding.registerButton.text = getString(R.string.edit_info_save_text)
                }
            }
            Log.d(LOG_TAG, "mode $it")
        })
        viewModel.user.observe(viewLifecycleOwner, {
            fillData(it)
            Log.d(LOG_TAG, "USER $it")
        })


    }

    /**
     * clears all input fields
     */
    private fun clearData() {
        Log.d(LOG_TAG, " clear ")
        inputViews.forEach { view -> view.text = getString(R.string.empty) }
    }

    /**
     * disables all user interactions
     */
    private fun makeReadOnly() {
        inputViews.forEach { view -> view.isEnabled = false }
        binding.registerButton.isVisible = false
        binding.textInputLayoutConfirmPassword.visibility = INVISIBLE
        binding.textInputLayoutPassword.visibility = INVISIBLE
        binding.textInputLayoutFirstName.helperText = getString(R.string.blankspace)
        binding.textInputLayoutLastName.helperText = getString(R.string.blankspace)
        binding.cancelButton.setText(R.string.back_button)
        binding.profileImage.isEnabled = false
        binding.ccp.setCcpClickable(false)
    }

    /**
     * fills the views with the given user's data
     * @param user an User object
     */
    private fun fillData(user: User) {
        binding.editTextFirstName.setText(user.firstName)
        binding.editTextLastName.setText(user.lastName)
        binding.editTextPhoneNumber.setText(user.phoneNumber)
        binding.editTextEmail.setText(user.email)
        binding.ccp.setCountryForNameCode(user.country)
        if (user.getImageURI() != Uri.EMPTY)
            binding.profileImage.setImageURI(user.getImageURI())

    }

    /**
     * wrapper to check the password validity
     * @param text a CharSequence containing the password to be validated
     */
    private fun checkPassword(text: CharSequence): Boolean {
        return validator.checkPassword(text, getString(R.string.allowed_password_special_chars))
    }

    /**
     * wrapper to check if the password and its confirmation match
     * @param text - a CharSequence containing the password to be compared against the other password entered
     */
    private fun checkPasswordsMatch(text: CharSequence): Boolean {
        return validator.checkPasswordsMatch(
            text.toString(),
            binding.editTextPassword.text.toString()
        )
    }

    private fun initialize() {
        viewModel = ViewModelProvider(requireActivity()).get(LoginViewModel::class.java)

        binding.profileImage.setOnClickListener {
            checkOrRequestCameraPermission()
        }

        binding.editTextFirstName.doOnTextChanged { text, _, _, _ ->
            viewModel.updateStroke(
                text,
                binding.textInputLayoutFirstName,
                validator::checkFirstName
            )
        }

        binding.editTextLastName.doOnTextChanged { text, _, _, _ ->
            viewModel.updateStroke(text, binding.textInputLayoutLastName, validator::checkLastName)
        }

        binding.editTextPhoneNumber.doOnTextChanged { text, _, _, _ ->
            viewModel.updateStroke(
                text,
                binding.textInputLayoutPhoneNumber,
                validator::checkPhoneNumber
            )
        }

        binding.editTextEmail.doOnTextChanged { text, _, _, _ ->
            viewModel.updateStroke(text!!, binding.textInputLayoutEmail, validator::checkEmail)
        }

        binding.editTextPassword.doOnTextChanged { text, _, _, _ ->
            viewModel.updateStroke(text!!, binding.textInputLayoutPassword, ::checkPassword)
        }

        binding.editTextConfirmPassword.doOnTextChanged { text, _, _, _ ->
            viewModel.updateStroke(
                text!!,
                binding.textInputLayoutConfirmPassword,
                ::checkPasswordsMatch
            )
        }
        binding.registerButton.setOnClickListener {
            onRegister()
        }
        binding.cancelButton.setOnClickListener {
            goToLoginScreen()
        }

        inputViews = listOf(
            binding.editTextEmail,
            binding.editTextLastName,
            binding.editTextFirstName,
            binding.editTextPhoneNumber,
            binding.editTextPassword,
            binding.editTextConfirmPassword
        )
    }

    private fun onRegister() {
        if (checkRequiredData()) {
            val country: String = binding.ccp.selectedCountryNameCode
            val phoneNumber: String = binding.editTextPhoneNumber.text.toString()
            if (phoneNumber.isNotEmpty() && !validator.checkPhoneNumber(phoneNumber)) {
                Toast.makeText(requireContext(), "Phone number incorrect", Toast.LENGTH_SHORT)
                    .show()
                return
            }
            var profilePhoto: Uri = Uri.EMPTY
            if (fileUri != null)
                profilePhoto = fileUri!!
            val firstName: String = binding.editTextFirstName.text.toString()
            val lastName: String = binding.editTextLastName.text.toString()
            val email: String = binding.editTextEmail.text.toString()
            val password: String = binding.editTextPassword.text.toString()
            viewModel.register(
                firstName,
                lastName,
                email,
                password,
                country,
                phoneNumber,
                profilePhoto
            )
            goToLoginScreen()
        } else {
            Toast.makeText(
                requireContext(),
                "Please insert missing data correctly",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * checks if the required data is completed correctly
     * @return true if the data is correct, false if the data is not valid
     */
    private fun checkRequiredData(): Boolean {
        val firstName: String = binding.editTextFirstName.text.toString()
        val lastName: String = binding.editTextLastName.text.toString()
        val email: String = binding.editTextEmail.text.toString()
        val password: String = binding.editTextPassword.text.toString()
        val confirmPassword: String = binding.editTextConfirmPassword.text.toString()

        var isValid = true
        if (!validator.checkFirstName(firstName)) {
            isValid = false
            viewModel.highlightWrong(binding.textInputLayoutFirstName)
        }
        if (!validator.checkLastName(lastName)) {
            isValid = false
            viewModel.highlightWrong(binding.textInputLayoutLastName)
        }
        if (!validator.checkEmail(email)) {
            isValid = false
            viewModel.highlightWrong(binding.textInputLayoutEmail)
        }
        //changing the password is not required in the editable mode
        if (viewModel.mode.value == Mode.EDITABLE && password.isEmpty())
            return isValid
        if (!checkPassword(password)) {
            isValid = false
            viewModel.highlightWrong(binding.textInputLayoutPassword)
        }
        if (!validator.checkPasswordsMatch(password, confirmPassword)) {
            isValid = false
            viewModel.highlightWrong(binding.textInputLayoutPassword)
            viewModel.highlightWrong(binding.textInputLayoutConfirmPassword)
        }

        return isValid

    }

    private fun goToLoginScreen() {

        //remove LoginFragment from backstack
        requireActivity().onBackPressed()

    }

    private fun displayAppChooser() {
        ImagePicker.with(this)
            //.compress(maxImageSize)         //Final image size will be less than 1 MB(Optional)
            // .maxResultSize(maxWidth, maxHeight)  //Final image resolution will be less than 1080 x 1080(Optional)
            //save image to pictures
            .saveDir(
                File(
                    requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!,
                    "ImagePicker"
                )
            )
            .createIntent { intent ->
                startForProfileImageResult.launch(intent)
            }
    }

    //handle permissions
    private fun checkOrRequestCameraPermission() {
        //check if the camera permission was accepted
        val result = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)

        if (result == PackageManager.PERMISSION_GRANTED) {
            chooseImage()
        } else {
            Toast.makeText(requireContext(), "Camera permission required", Toast.LENGTH_SHORT)
                .show()
            requestForSpecificPermission(Manifest.permission.CAMERA, PERMISSION_REQUEST_CODE_CAMERA)
        }

    }

    private fun checkOrRequestExternalStoragePermission() {
        //check if the storage permission was accepted
        val writeResult = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val readResult = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        if (writeResult != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(
                requireContext(),
                "Write external storage permission required",
                Toast.LENGTH_SHORT
            )
                .show()
            requestForSpecificPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                PERMISSION_REQUEST_CODE_WRITE_EXTERNAL
            )
        }
        if (readResult != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(
                requireContext(),
                "Read external storage permission required",
                Toast.LENGTH_SHORT
            )
                .show()
            requestForSpecificPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                PERMISSION_REQUEST_CODE_READ_EXTERNAL
            )
        }

    }

    private fun requestForSpecificPermission(sPermission: String, requestCode: Int) {
        requestPermissions(
            arrayOf(
                sPermission
            ),
            requestCode
        )

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(LOG_TAG, "req: $permissions ### $grantResults ")
        when (requestCode) {
            PERMISSION_REQUEST_CODE_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (permissions[0] == Manifest.permission.CAMERA)
                        chooseImage()
                }
            }
        }

    }

    /**
     * Opens the image picker dialog
     */
    private fun chooseImage() {
        displayAppChooser()
        Log.d(LOG_TAG, "Photo chooser opened")
    }

}