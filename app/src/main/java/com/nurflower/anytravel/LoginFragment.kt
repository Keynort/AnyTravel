package com.nurflower.anytravel

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.view.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var mAuth : FirebaseAuth?= null


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
        val view = inflater.inflate(R.layout.fragment_login, container, false)
       // val firebaseApp = Firebase.app("first")
        mAuth = FirebaseAuth.getInstance()


        // Inflate the layout for this fragment
        view.createAccountTv.setOnClickListener {
            view.findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
        }



        view.forgotPassword.setOnClickListener {
            view.findNavController().navigate(R.id.action_loginFragment_to_recoverFragment)
        }

        view.loginBtn.setOnClickListener {
            var allow = true
            val email = view.emailAddress.text.toString()
            val password = view.password.text.toString()
            if (email.isEmpty()) {
                view.emailAddress.error = "Введите email"
                allow = false
            }
            if (password.isEmpty()) {
                view.password.error = "Введите пароль"
                allow = false
            }
            if(allow) {
                activity?.let { context ->
                    // get id}
                    mAuth?.signInWithEmailAndPassword(
                        view.emailAddress.text.toString(),
                        view.password.text.toString()
                    )?.addOnCompleteListener { task->
                        if (task.isSuccessful) {
                            App.writeSharedPreferences(context,"login", email)
                            startActivity(Intent(activity, MainActivity::class.java))
                        } else {
                            Toast.makeText(
                                activity,
                                "Вы указали не правильный пароль или email, попробуйте еще раз",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                }
            }
//            val intent = Intent(activity, MenuActivity::class.java)
//            startActivity(intent)
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
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}