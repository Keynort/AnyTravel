package com.nurflower.anytravel


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_list.view.*

class ListFragment : Fragment() {

    private var adapter:ToursAdapter ?= null
    private var database:FirebaseDatabase = FirebaseDatabase.getInstance()
    private var myRef :DatabaseReference=database.getReference("users")
    private var mAuth : FirebaseAuth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        activity?.let {
            adapter = ToursAdapter(it){tour->
                Toast.makeText(it, tour.tourName, Toast.LENGTH_SHORT).show()
            }
        }

        view.toursRv.adapter = adapter
        view.toursRv.setHasFixedSize(true)

        myRef.child(mAuth.currentUser?.uid!!).child("tours").addValueEventListener(object :
        ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<Tours>()
                if(snapshot.exists()){
                    snapshot.children.forEach {
                        val tour : Tours? = it.getValue(Tours::class.java)
                        tour?.tourId = it.key
                        tour?.let {
                            list.add(tour)
                        }
                    }
                    if(list.size==0){
                        view?.toursRv?.visibility=View.GONE
                        view?.noItemLl?.visibility = View.VISIBLE
                    }else{
                        view?.toursRv?.visibility=View.VISIBLE
                        view?.noItemLl?.visibility = View.GONE
                    }
                    adapter?.setTours(list)
                }else{
                    view?.toursRv?.visibility=View.GONE
                    view?.noItemLl?.visibility = View.VISIBLE
                }
            }
        })
        return view
    }




    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ListFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ListFragment().apply {
            }
    }
}