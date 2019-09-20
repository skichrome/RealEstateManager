package com.skichrome.realestatemanager.view

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.skichrome.realestatemanager.R
import com.skichrome.realestatemanager.databinding.FragmentAddRealtyBinding
import com.skichrome.realestatemanager.model.database.MediaReference
import com.skichrome.realestatemanager.model.database.Realty
import com.skichrome.realestatemanager.utils.REQUEST_IMAGE_CAPTURE
import com.skichrome.realestatemanager.utils.StorageUtils
import com.skichrome.realestatemanager.view.base.BaseFragment
import com.skichrome.realestatemanager.viewmodel.RealtyViewModel
import kotlinx.android.synthetic.main.fragment_add_realty.*
import java.io.IOException
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*

class AddRealtyFragment : BaseFragment<FragmentAddRealtyBinding, RealtyViewModel>(),
    DatePickerDialogFragment.DatePickerListener,
    RealtyPhotoAdapter.OnClickPictureListener
{
    // =================================
    //              Fields
    // =================================

    private val photoAdapter = RealtyPhotoAdapter(list = mutableListOf(null), callback = WeakReference(this))
    private val materialEditTextViewList = arrayListOf<TextInputLayout>()
    private val date = SimpleDateFormat.getDateInstance()
    private lateinit var spinnerArray: Array<String>
    private lateinit var realtyCreationDate: Calendar
    private lateinit var imageAddedSrc: String
    private lateinit var imageAddedTypeFromSpinner: String
    private var realtyType: Int = -1
    private var realtyStatus: Boolean = false
    private var realtySoldDate: Calendar? = null
    private var canRegisterARealty = false

    // =================================
    //        Superclass Methods
    // =================================

    override fun getFragmentLayout(): Int = R.layout.fragment_add_realty
    override fun getViewModelClass(): Class<RealtyViewModel> = RealtyViewModel::class.java

    override fun configureFragment()
    {
        populateViewList()
        configureRecyclerView()
        configureViewModel()
        configureDateInput()
        addRealtyFragSubmitBtn.setOnClickListener {
            canRegisterARealty = true
            getDateInputData()
            getMaterialInputTextData()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
            photoAdapter.addPictureToAdapter(MediaReference(reference = imageAddedSrc, shortDesc = imageAddedTypeFromSpinner))
        super.onActivityResult(requestCode, resultCode, data)
    }

    // =================================
    //              Methods
    // =================================

    // --------  Configuration  ---------

    private fun populateViewList()
    {
        materialEditTextViewList.add(addRealtyFragInputAddressTextLayout)
        materialEditTextViewList.add(addRealtyFragInputAgentTextLayout)
        materialEditTextViewList.add(addRealtyFragCityInputLayout)
        materialEditTextViewList.add(addRealtyFragInputNameTextLayout)
        materialEditTextViewList.add(addRealtyFragPostCodeLayout)
        materialEditTextViewList.add(addRealtyFragInputPriceTextLayout)
        materialEditTextViewList.add(addRealtyFragInputRoomNumberTextLayout)
        materialEditTextViewList.add(addRealtyFragInputSurfaceTextLayout)
    }

    private fun configureRecyclerView()
    {
        binding.addRealtyFragRecyclerViewAddPhoto.adapter = photoAdapter
    }

    private fun configureViewModel()
    {
        viewModel.insertLoading.observe(this, Observer {
            if (!it)
                findNavController().navigateUp()
        })
    }

    private fun configureDateInput()
    {
        spinnerArray = resources.getStringArray(R.array.add_realty_frag_hint_status_spinner)

        realtyCreationDate = Calendar.getInstance()
        val displayDate = date.format(realtyCreationDate.time)

        addRealtyFragDateCreatedEditText.setText(displayDate)
        addRealtyFragDateAddedBtn.setOnClickListener { showDatePicker(0) }
        addRealtyFragDateSoldBtn.setOnClickListener { showDatePicker(1) }
        addRealtyFragStatusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, p3: Long)
            {
                realtyStatus = parent?.getItemAtPosition(position) == spinnerArray[1]
                addRealtyFragDateSoldBtn.isEnabled = realtyStatus
                addRealtyFragSoldDateEditText.isEnabled = realtyStatus
                addRealtyFragSoldDateEditText.text = null
                addRealtyFragSoldDateTextViewLayout.error = null
            }

            override fun onNothingSelected(p0: AdapterView<*>?) = Unit
        }
        addRealtyFragTypeInputSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
        {
            override fun onNothingSelected(p0: AdapterView<*>?) = Unit

            override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, p3: Long)
            {
                realtyType = position
            }
        }
    }

    // --------  UI  ---------

    private fun showDatePicker(tag: Int)
    {
        val dialogFragment = DatePickerDialogFragment(WeakReference(this), tag, realtyCreationDate)
        dialogFragment.show(fragmentManager!!, "DateDialog")
    }

    private fun launchCamera()
    {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(activity!!.packageManager)?.also {
                val photoFile = try
                {
                    if (StorageUtils.isExternalStorageWritable())
                    {
                        context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                            ?.let { StorageUtils.createOrGetImageFile(it, "test") }
                    } else null

                } catch (e: IOException)
                {
                    null
                }

                photoFile?.also {
                    imageAddedSrc = it.absolutePath

                    val uri = FileProvider.getUriForFile(
                        context!!.applicationContext,
                        getString(R.string.content_provider_authority),
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    private fun getMaterialInputTextData()
    {
        for (layout in materialEditTextViewList)
        {
            val textInput = layout.findViewWithTag<TextInputEditText>(getString(R.string.add_realty_fragment_edit_text_input_tag))
            val str: String? = textInput?.text?.toString()

            if (textInput != null && str == null || str == "")
            {
                textInput.error = getString(R.string.add_realty_frag_error_input_field)
                canRegisterARealty = false
            }
        }

        if (canRegisterARealty)
        {
            val realtyToBeAdded = Realty(
                status = realtyStatus,
                surface = addRealtyFragSurfaceInput.text.toString().toFloat(),
                roomNumber = addRealtyFragRoomNumberInput.text.toString().toInt(),
                fullDescription = addRealtyFragDescriptionInput.text.toString(),
                dateSell = if (realtySoldDate == null) null else Date(realtySoldDate!!.time.time),
                dateAdded = Date(realtyCreationDate.time.time),
                agent = addRealtyFragAgentInput.text.toString(),
                city = addRealtyFragCityInput.text.toString(),
                postCode = addRealtyFragPostCodeInput.text.toString().toInt(),
                address = addRealtyFragAddressInput.text.toString(),
                price = addRealtyFragPriceInput.text.toString().toFloat(),
                realtyTypeId = realtyType
            )
            viewModel.insertRealty(realtyToBeAdded, photoAdapter.getAllPicturesReferences())
        }
    }

    private fun getDateInputData()
    {
        val dateCreated = addRealtyFragDateCreatedEditText?.text?.toString()
        val dateSold = addRealtyFragSoldDateEditText?.text?.toString()

        // Check if a creation date is entered
        addRealtyFragInputDateCreatedTextLayout.error =
            if (dateCreated == null || dateCreated == "")
            {
                canRegisterARealty = false
                getString(R.string.add_realty_frag_error_input_field)
            } else null

        // Check if a sold date is entered, only if sinner is on "sold" value
        if ("${addRealtyFragStatusSpinner.selectedItem}" == spinnerArray[1])
        {
            addRealtyFragSoldDateTextViewLayout.error =
                if (dateSold == null || dateSold == "")
                {
                    canRegisterARealty = false
                    getString(R.string.add_realty_frag_error_input_field)
                } else null
        }
    }

    // =================================
    //            Callbacks
    // =================================

    override fun onDateSet(calendar: Calendar, tag: Int)
    {
        val time = date.format(calendar.time)
        when (tag)
        {
            0 ->
            {
                addRealtyFragDateCreatedEditText.setText(time)
                realtyCreationDate = calendar
            }
            1 ->
            {
                addRealtyFragSoldDateEditText.setText(time)
                addRealtyFragSoldDateTextViewLayout.error = null
                realtySoldDate = calendar
            }
        }
    }

    override fun onClickAddPicture()
    {
        val alertDialog = AlertDialog.Builder(context!!).apply {

            val alertInflater = View.inflate(context, R.layout.alert_dialog_spinner, null)
            setView(alertInflater)

            setTitle(getString(R.string.select_picture_type_alert_title))
                .setMessage(getString(R.string.select_picture_type_alert_message))
                .setPositiveButton(getString(R.string.select_picture_type_alert_confirm)) { _, _ -> launchCamera() }
                .setNegativeButton(getString(R.string.select_picture_type_alert_cancel)) { dialog, _ -> dialog.dismiss() }
                .create()

            alertInflater.findViewById<Spinner>(R.id.alertDialogSpinner).onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener
                {
                    override fun onNothingSelected(p0: AdapterView<*>?) = Unit

                    override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, p3: Long)
                    {
                        imageAddedTypeFromSpinner = parent?.getItemAtPosition(position).toString()
                    }
                }
        }
        alertDialog.show()
    }

    override fun onLongClickPicture(position: Int)
    {
        AlertDialog.Builder(context!!).apply {
            setTitle(getString(R.string.delete_picture_alert_title))
                .setMessage(getString(R.string.delete_picture_alert_message))
                .setPositiveButton(getString(R.string.delete_picture_alert_confirm)) { _, _ -> photoAdapter.removePictureFromAdapter(position) }
                .setNegativeButton(getString(R.string.delete_picture_alert_cancel)) { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }
}