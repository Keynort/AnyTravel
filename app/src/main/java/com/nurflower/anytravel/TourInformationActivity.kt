package com.nurflower.anytravel

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_tour_information.*

class TourInformationActivity : AppCompatActivity() {

    private var adapter:UsersAdapter ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tour_information)


        adapter = UsersAdapter(this) {
        }
        aboutTour.adapter=adapter
        aboutTour.setHasFixedSize(true)

        val mapList = ArrayList<BookedUsers>()

        val toursDatabase = FirebaseDatabase.getInstance()
        toursDatabase.getReference("tours").child(intent.getStringExtra("tourId").toString()).child("regData").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    snapshot.children.forEach {
                        val user = it.getValue(BookedUsers::class.java)
                        mapList.add(user!!)
                    }
                    regPeople.text = " Кол-во туристов : " + " " +mapList.size.toString()
                    adapter?.setUsers(mapList)
                }else{
                    regPeople.text = " Кол-во туристов : 0 "
                }
            }
        })

    }
}