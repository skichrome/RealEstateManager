package com.skichrome.realestatemanager.view.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.skichrome.realestatemanager.R
import com.skichrome.realestatemanager.databinding.ActivitySignInRecyclerViewItemBinding
import com.skichrome.realestatemanager.model.database.Agent
import java.lang.ref.WeakReference

class LoginUsersAdapter(private var agents: List<Agent>, private val callback: WeakReference<LoginSelectedListener>) :
    RecyclerView.Adapter<LoginUsersAdapter.LoginUsersViewHolder>()
{
    // =======================================
    //                  Fields
    // =======================================

    private lateinit var binding: ActivitySignInRecyclerViewItemBinding

    // =======================================
    //           Superclass Methods
    // =======================================

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoginUsersViewHolder
    {
        val inflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_sign_in_recycler_view_item, parent, false)
        return LoginUsersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LoginUsersViewHolder, position: Int) = holder.bind(agents[position], callback)

    override fun getItemCount(): Int = agents.size

    // =======================================
    //                 Methods
    // =======================================

    fun replaceAgentList(newList: List<Agent>)
    {
        agents = newList
        notifyDataSetChanged()
    }

    class LoginUsersViewHolder(private val binding: ActivitySignInRecyclerViewItemBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(agent: Agent, callback: WeakReference<LoginSelectedListener>)
        {
            binding.name = agent.name
            binding.root.setOnClickListener { callback.get()?.onUserSelected(agent) }
            binding.executePendingBindings()
        }
    }

    interface LoginSelectedListener
    {
        fun onUserSelected(user: Agent)
    }
}