package com.nurflower.anytravel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_recover.*
import kotlinx.android.synthetic.main.fragment_recover.view.*
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RecoverFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecoverFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var mAuth: FirebaseAuth? = null


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
        mAuth = FirebaseAuth.getInstance();
        val view = inflater.inflate(R.layout.fragment_recover, container, false)
        view.recoverPassword.setOnClickListener {
            view.findNavController()
                .navigate(R.id.action_recoverFragment_to_userPasswordFragment)
        }
        view.backButton2.setOnClickListener {
            view.findNavController().navigate(R.id.action_recoverFragment_to_loginFragment)
        }
        // Inflate the layout for this fragment

                view.recoverPassword.setOnClickListener {
                    var allow = true
                    if(view?.emailAdress2?.text.toString().isEmpty()) {
                        emailAdress2.error = "Введите email "
                        allow=false
                    }
                    if(allow) {
                    mAuth?.sendPasswordResetEmail(view?.emailAdress2?.text.toString())
                        ?.addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(
                                    activity,
                                    "Ссылка для восстановления пароля была отправлена на почту ",
                                    Toast.LENGTH_SHORT
                                ).show()
                                view.recoverPassword.findNavController()
                                    .navigate(R.id.action_recoverFragment_to_loginFragment)

                            } else {
                                Toast.makeText(
                                    activity,
                                    "Данного пользователя не существует ",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
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
         * @return A new instance of fragment RecoverFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RecoverFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}