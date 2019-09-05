package com.skichrome.realestatemanager.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.skichrome.realestatemanager.R
import com.skichrome.realestatemanager.model.database.Realty
import com.skichrome.realestatemanager.viewmodel.Injection
import com.skichrome.realestatemanager.viewmodel.RealtyViewModel
import kotlinx.android.synthetic.main.fragment_add_realty.*
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*

class AddRealtyFragment : Fragment(), DatePickerDialogFragment.DatePickerListener
{
    private val materialEditTextViewList = arrayListOf<TextInputLayout>()
    private lateinit var spinnerArray: Array<String>
    private val date = SimpleDateFormat.getDateInstance()
    private var realtyStatus: Boolean = false
    private lateinit var realtyCreationDate: Calendar
    private var realtySoldDate: Calendar? = null
    private var canRegisterARealty = false

    private lateinit var viewModel: RealtyViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        return inflater.inflate(R.layout.fragment_add_realty, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        spinnerArray = resources.getStringArray(R.array.add_realty_frag_hint_status_spinner)
        populateViewList()
        configureViewModel()
        configureDateInput()
        addRealtyFragSubmitBtn.setOnClickListener {
            canRegisterARealty = true
            getDateInputData()
            getMaterialInputTextData()
        }
    }

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
        materialEditTextViewList.add(addRealtyFragInputTypeTextLayout)
    }

    private fun configureViewModel()
    {
        val vmFactory = Injection.provideViewModelFactory(context!!)
        viewModel = ViewModelProviders.of(this, vmFactory).get(RealtyViewModel::class.java)

        viewModel.insertLoading.observe(this, Observer {
            if (!it)
            {
                findNavController().navigateUp()
            }
        })
    }

    private fun configureDateInput()
    {
        realtyCreationDate = Calendar.getInstance()
        val displayDate = date.format(realtyCreationDate.time)

        addRealtyFragDateCreatedEditText.setText(displayDate)
        addRealtyFragDateAddedBtn.setOnClickListener { showDatePicker(0) }
        addRealtyFragDateSoldBtn.setOnClickListener { showDatePicker(1) }
        addRealtyFragStatusSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener
            {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    p3: Long
                )
                {
                    realtyStatus = parent?.getItemAtPosition(position) == spinnerArray[1]
                    addRealtyFragDateSoldBtn.isEnabled = realtyStatus
                    addRealtyFragSoldDateEditText.isEnabled = realtyStatus
                    addRealtyFragSoldDateEditText.text = null
                    addRealtyFragSoldDateTextViewLayout.error = null
                }

                override fun onNothingSelected(p0: AdapterView<*>?) = Unit
            }
    }

    private fun showDatePicker(tag: Int)
    {
        val dialogFragment = DatePickerDialogFragment(WeakReference(this), tag, realtyCreationDate)
        dialogFragment.show(fragmentManager!!, "DateDialog")
    }

    private fun getMaterialInputTextData()
    {
        for (layout in materialEditTextViewList)
        {
            val textInput =
                layout.findViewWithTag<TextInputEditText>(getString(R.string.add_realty_fragment_edit_text_input_tag))
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
                price = addRealtyFragPriceInput.text.toString().toFloat()
            )
            Log.e("getInput", realtyToBeAdded.toString())
            viewModel.insertRealty(realtyToBeAdded)
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
}