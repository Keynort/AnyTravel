package com.nurflower.anytravel

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.about_tour_item.view.*

class UsersAdapter(val context: Context, private val listener: (BookedUsers) -> Unit): RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    private var users : ArrayList<BookedUsers>? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(item: BookedUsers, listener: (BookedUsers) -> Unit) =
            with(itemView) {
                userInitials.text = "Ф.И.О : " + " " + item.name
                userPhoneNumber.text = "Номер телефона : " + " " + item.phone
                reservationNumber.text = "Номер брони : " + " " + item.bookedId
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.about_tour_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return users?.size?:0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        users?.get(position)?.let {
            holder.bind(it,listener)
        }
    }

    fun setUsers(bookedUsers: ArrayList<BookedUsers>){
        users = bookedUsers
        notifyDataSetChanged()
    }
}