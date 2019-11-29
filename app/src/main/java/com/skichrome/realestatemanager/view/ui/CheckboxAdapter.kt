package com.skichrome.realestatemanager.view.ui

import android.content.Context
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.skichrome.realestatemanager.model.database.Poi

class CheckboxAdapter(val context: Context?, private var items: MutableList<Checkboxes> = mutableListOf()) :
    RecyclerView.Adapter<CheckboxAdapter.CheckBoxesViewHolder>()
{
    // =======================================
    //                  Fields
    // =======================================

    private val checkboxList: MutableList<CheckBox> = mutableListOf()

    // =======================================
    //           Superclass Methods
    // =======================================

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckBoxesViewHolder
    {
        val cb = CheckBox(context)
        checkboxList.add(cb)
        return CheckBoxesViewHolder(cb)
    }

    override fun onBindViewHolder(holder: CheckBoxesViewHolder, position: Int) =
        holder.setText(items[position].checkboxName, items[position].isChecked, items[position].isClickable)

    override fun getItemCount(): Int = items.size

    // =======================================
    //                 Methods
    // =======================================

    fun getSelectedId(): List<Int>
    {
        val idList = mutableListOf<Int>()

        checkboxList.forEachIndexed { index, item ->
            if (item.isChecked)
                idList.add(index + 1)
        }
        return idList
    }

    fun replaceCheckboxData(newPoiList: List<Poi>)
    {
        val newItemList = mutableListOf<Checkboxes>()
        newPoiList.forEach {
            newItemList.add(Checkboxes(it.name, false))
        }
        items = newItemList
        notifyDataSetChanged()
    }

    fun setCheckboxesChecked(checkedList: List<Int>)
    {
        checkedList.forEach { items[it - 1].isChecked = true }
        items.forEach { it.isClickable = true }
        notifyDataSetChanged()
    }

    fun setCheckboxesCheckedAndDeleteUnchecked(checkedList: List<Int>)
    {
        items.forEach {
            it.isClickable = false
            it.isChecked = false
        }
        checkedList.forEach { items[it - 1].isChecked = true }

        notifyDataSetChanged()
    }

    class CheckBoxesViewHolder(private val checkBox: CheckBox) : RecyclerView.ViewHolder(checkBox)
    {
        fun setText(displayText: String, isChecked: Boolean = false, isClickable: Boolean)
        {
            checkBox.text = displayText
            checkBox.isChecked = isChecked
            checkBox.isClickable = isClickable
        }
    }

    data class Checkboxes(val checkboxName: String, var isChecked: Boolean, var isClickable: Boolean = true)
}