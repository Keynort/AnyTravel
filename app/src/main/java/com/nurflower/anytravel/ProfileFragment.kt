package com.nurflower.anytravel

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private var mAuth: FirebaseAuth?=null
    private var myRef: DatabaseReference? = null
    private var database: FirebaseDatabase? = null
    private var uid:String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        mAuth=FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        myRef = database!!.getReference("users")
        uid = mAuth?.uid!!

        myRef?.child(uid!!)?.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {


            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                view.phoneNumber2.setTextColor(Color.BLACK)
                view.userEmailAdress.setTextColor(Color.BLACK)
                view.profileCompanyName.setTextColor(Color.BLACK)
                view.userEmailAdress.setText(user?.email)
                view.phoneNumber2.setText(user?.phoneNumber)
                view.profileCompanyName.setText(user?.companyName)
            }

        })

        view.logIn2.setOnClickListener {
            if(view.logIn2.text.toString().toUpperCase() == "Сохранить".toUpperCase()){
                view.userEmailAdress.isEnabled = false
                view.phoneNumber2.isEnabled=false
                view.profileCompanyName.isEnabled=false
                view.logIn2.text = "Редактировать профиль"

//                myRef!!.child(uid!!).child("email").setValue(view.userEmailAdress.text.toString())
                myRef!!.child(uid!!).child("phoneNumber").setValue(view.phoneNumber2.text.toString())
            }else{
                view.userEmailAdress.isEnabled = false
                view.phoneNumber2.isEnabled=true
                view.profileCompanyName.isEnabled=false
                view.logIn2.text = "Сохранить"
            }
        }


        view.logOut2.setOnClickListener {
            mAuth?.signOut()
            activity?.let {it
                App.writeSharedPreferences(it, "isLoged", "false")
                it.finish()
                startActivity(Intent(it, LoginActivity::class.java))
            }
        }
        return view
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}






