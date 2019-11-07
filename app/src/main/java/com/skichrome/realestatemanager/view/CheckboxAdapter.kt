package com.skichrome.realestatemanager.view

import android.content.Context
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.skichrome.realestatemanager.model.database.Poi

class CheckboxAdapter(val context: Context, private var items: List<Poi>) : RecyclerView.Adapter<CheckboxAdapter.CheckBoxesViewHolder>()
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

    override fun onBindViewHolder(holder: CheckBoxesViewHolder, position: Int) = holder.setText(items[position].name)

    override fun getItemCount(): Int = items.size

    // =======================================
    //                 Methods
    // =======================================

    fun getSelectedId(): List<Int>
    {
        val idList = mutableListOf<Int>()

        checkboxList.forEachIndexed { index, item ->
            if (item.isChecked)
                idList.add(index)
        }
        return idList
    }

    fun replaceCheckboxData(newItems: List<Poi>)
    {
        items = newItems
        notifyDataSetChanged()
    }

    class CheckBoxesViewHolder(private val checkBox: CheckBox) : RecyclerView.ViewHolder(checkBox)
    {
        fun setText(displayText: String)
        {
            checkBox.text = displayText
        }
    }
}