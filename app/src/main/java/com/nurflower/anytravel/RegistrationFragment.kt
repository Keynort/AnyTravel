package com.nurflower.anytravel

import App
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_registration.view.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RegistrationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegistrationFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var mAuth: FirebaseAuth? = null
    private var myRef: DatabaseReference? = null
    private var database: FirebaseDatabase? = null

    private var layoutView: View? =null


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
        // Inflate the layout for this fragment
         layoutView =  inflater.inflate(R.layout.fragment_registration, container, false)
        mAuth=FirebaseAuth.getInstance()
        database= FirebaseDatabase.getInstance()
        myRef = database!!.getReference("users")

        layoutView?.successfulReg?.setOnClickListener {
            val companyName = layoutView?.companyName?.text.toString()
            val phoneNumber = layoutView?.phoneNumber?.text.toString()
            val email= layoutView?.emailAdress?.text.toString()
            val password = layoutView?.password?.text.toString()
            val repeatPassword = layoutView?.repeatPassword?.text.toString()






            activity?.let { it1 -> App.writeSharedPreferences(it1, "isLogged", "true") }

            var allow =true
            if(companyName.length <2 || companyName.length>15){
                layoutView?.companyName?.error="Название компании должно быть не меньше 2 символов или больше 15 символов"
                allow=false
            }
            if(companyName.isEmpty()){
                layoutView?.companyName?.error="Введите имя компании"
                allow=false
            }
            if(email.isEmpty()){
                layoutView?.emailAdress?.error="Введите email"
                allow=false
            }
            if(password.length<6){
                layoutView?.password?.error="Пароль должен содержать как минимум 6 символов"
                allow=false
            }
            if(password.isEmpty()){
                layoutView?.password?.error="Введите пароль"
                allow=false
            }
            if(repeatPassword.isEmpty()){
                layoutView?.repeatPassword?.error="Подтвердите пароль"
                allow=false
            }
            if(phoneNumber.isEmpty()){
                layoutView?.phoneNumber?.error="Введите номер телефона"
                allow=false
            }
            if(repeatPassword != password){
                layoutView?.repeatPassword?.error = "Пароли не совпадают попробуйте еще раз "
                allow = false
            }
            if(phoneNumber.isDigitsOnly() && phoneNumber.length!=11){
                layoutView?.phoneNumber?.error="Введите существующий номер телефона с 8"
                allow=false
            }
                if(allow){
                    val user = User()
                    user.companyName = companyName
                    user.phoneNumber=phoneNumber
                    user.email=email
                    checkCompanyName(email, password, user)
                }
        }

        layoutView?.backButton?.setOnClickListener {
            layoutView?.findNavController()?.navigate(R.id.action_registrationFragment_to_loginFragment)
        }
        return layoutView
    }

    private fun checkCompanyName(email: String, password: String, user: User) {
        myRef?.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var allow = true
                snapshot.children.forEach {
                    val mUser = it.getValue(User::class.java)
                    if (mUser?.companyName == user.companyName){
                        allow  = false
                    }
                }
                if (allow){
                    createAccount(email, password, user)
                }else{
                    layoutView?.companyName?.error="Данное имя компании уже существует"
                }
            }
        })
    }


    private fun createAccount(email: String, password: String, user: User){

        mAuth?.createUserWithEmailAndPassword(email,password)?.addOnCompleteListener {
            if(it.isSuccessful) {
                Toast.makeText(activity, "Вы успешно зарегестрировались", Toast.LENGTH_SHORT).show()
                it.result?.user?.uid?.let { uid ->
                    myRef?.child(uid)?.setValue(user)
                    layoutView?.findNavController()?.navigate(R.id.action_registrationFragment_to_loginFragment)
                }
            }
        }?.addOnFailureListener {
            Toast.makeText(activity,"Введите существующий email ",Toast.LENGTH_SHORT).show()
        }
    }







    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RegistrationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegistrationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}