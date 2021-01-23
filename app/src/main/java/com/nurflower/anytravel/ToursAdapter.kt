package com.nurflower.anytravel

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.tour_item.view.*
import kotlinx.android.synthetic.main.tour_item.view.tourData
import kotlinx.android.synthetic.main.tour_item.view.tourName
import kotlinx.android.synthetic.main.tour_item.view.tourPrice
import kotlinx.android.synthetic.main.tour_item.view.tourDescription


class ToursAdapter(val context: Context ,private val listener: (Tours) -> Unit):RecyclerView.Adapter<ToursAdapter.ViewHolder>(){


    private var tours : ArrayList<Tours>? = null
    private var storageReference: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://anytravel-ef9c8.appspot.com")


    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()

    private var tourRef : DatabaseReference =database.getReference("tours")
    private var userRef : DatabaseReference =database.getReference("users")
    private var mAuth : FirebaseAuth = FirebaseAuth.getInstance()


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bind(item: Tours, listener : (Tours) -> Unit) = with(itemView) {
            companyName.text = item.companyName
            tourPrice.text = "Цена : " + " "+item.price
            tourDescription.text = "О туре : " + " "+item.description
            tourData.text = "Дата : " + " "+item.dateAndTime
            tourName.text = "Название тура : "+" "+item.tourName
            maxNumberOfPeople.text = "Макс кол-во людей : "+" "+item.numbersOfPeople
            phoneNumber.text="Номер телефона : " +" "+item.phone


            storageReference.child(item.imageId.toString()).downloadUrl.addOnSuccessListener {
                Glide.with(this).load(it)
                    .thumbnail(0.1f).into(imageView)
            }.addOnCanceledListener {
            }

            editTour.setOnClickListener {
                val intent = Intent(context,AddActivity:: class.java)
                intent.putExtra("tourId", item.tourId)
                context.startActivity(intent)
            }
            aboutTour.setOnClickListener {
                val intent = Intent(context,TourInformationActivity:: class.java)
                intent.putExtra("tourId", item.tourId)
                context.startActivity(intent)
            }
            removeTour.setOnClickListener {
                showDialog(item)
            }
        }
    }
    private fun showDialog(tour: Tours){
        val dialog = AlertDialog.Builder(context)
        dialog.setCancelable(false)
        dialog.setMessage("Вы действительно хотите удалить этот тур?")
        dialog.setPositiveButton("Да") { _, _ ->
            if (tours!=null){
                tourRef.child(tour.tourId!!).removeValue()
                userRef.child(mAuth.currentUser?.uid!!).child("tours").child(tour.tourId!!).removeValue()
                notifyDataSetChanged()
            }
        }
        dialog.setNegativeButton("Нет") { _, _ ->
        }
        val alert : AlertDialog = dialog.create()
        alert.show()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tour_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tours?.size?:0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        tours?.get(position)?.let {
            holder.bind(it,listener)
        }
    }
    fun setTours(tourList : ArrayList<Tours>){
        tours = tourList
        notifyDataSetChanged()
    }
}