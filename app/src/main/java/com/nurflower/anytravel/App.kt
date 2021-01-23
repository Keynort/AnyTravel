import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.preference.PreferenceManager
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import kotlinx.android.synthetic.main.fragment_add.view.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import java.util.*

class App: Application() {


    override fun onCreate() {
        super.onCreate()



        val options2 = FirebaseOptions.Builder()
            .setApplicationId("1:1022762460227:android:641ffde1567a8e5a7c60f6")
            .setApiKey("AIzaSyAPsHBJbrTHVQxs3L_VDsWKFlAuxwFeQXs")
            .setDatabaseUrl("https://myapp-69087.firebaseio.com/")
            .build()

        val app = Firebase.initialize(applicationContext, options2, "secondary")
        FirebaseDatabase.getInstance(app)

    }

















    fun checkTour():Boolean{
        var layoutView: View?=null
        val tourName = layoutView?.tourName?.text.toString()
        val data = layoutView?.tourData?.text.toString()
        val price = layoutView?.tourPrice?.text.toString()
        val numberOfPeople = layoutView?.numberOfPeople?.text.toString()
        val description = layoutView?.tourDescription?.text.toString()
        val phoneNumber = layoutView?.phoneNumber4?.text.toString()


        var allow = true
        if(tourName.isEmpty()){
            layoutView?.tourName?.error="Введите название тура"
            allow=false
        }
        if(data.isEmpty()){
            layoutView?.tourData?.error="Введие дату для тура"
            allow=false
        }
        if(price.isEmpty()){
            layoutView?.tourPrice?.error="Введите цену для тура "
            allow=false
        }
        if(numberOfPeople.isEmpty()){
            layoutView?.numberOfPeople?.error="Введите кол-во людей"
            allow=false
        }
        if(description.isEmpty()){
            layoutView?.tourDescription?.error="Введите описание к туру"
            allow=false
        }
        if(phoneNumber.isEmpty()){
            layoutView?.phoneNumber4?.error="Введите номер телефона"
            allow=false
        }
        if (tourName.length < 2 || tourName.length > 15) {
            layoutView?.tourName?.error =
                "Название тура должно быть не меньше 2 символов или больше 15 символов"
            allow = false
        }
        if (data.length < 4 || data.length > 15) {
            layoutView?.tourData?.error =
                "Дата должна быть не меньше 2 символов или больше 15 символов"
            allow = false
        }
        if (price.length < 2 || price.length > 12) {
            layoutView?.tourPrice?.error =
                "Цена не должна быть меньше 2 символов или больше 12 символов"
            allow = false
        }
        if (numberOfPeople.length < 2 || numberOfPeople.length > 5) {
            layoutView?.numberOfPeople?.error = "Количество людей не может быть меньше 2 "
            allow = false
        }
        if (description.length > 2500) {
            layoutView?.tourDescription?.error = "Описание не можеть быть больше 2500 символов"
            allow = false
        }
        if ((phoneNumber.length == 12 && phoneNumber[0] == '+') || (phoneNumber.length == 11 && phoneNumber[0] == '8')) {
            allow = true
        } else {
            layoutView?.phoneNumber4?.error = "Введите существующий номер телефона "
        }
        return allow
    }

    fun checkLgn():Boolean{
        var view:View?=null
        var allow = true
        val email = view?.emailAddress?.text.toString()
        val password = view?.password?.text.toString()
        if (email.isEmpty()) {
            view?.emailAddress?.error = "Введите email"
            allow = false
        }
        if (password.isEmpty()) {
            view?.password?.error = "Введите пароль"
            allow = false
        }
        return allow
    }



    companion object {


        @SuppressLint("ApplySharedPref")
         fun writeSharedPreferences(context: Context, key: String, value: String) {

            val preferences = PreferenceManager.getDefaultSharedPreferences((context))
            val editor = preferences.edit()
            editor.putString(key, value)
            editor.commit()
        }

        fun readSharedPreferences(context: Context, key: String): String? {
            val preferences = PreferenceManager.getDefaultSharedPreferences((context))
            return preferences.getString(key, "Null")
        }

         fun generateUniCode(type:String):String{
            val numberRandom :Int= Random().nextInt()

            return type.plus(numberRandom).plus(System.currentTimeMillis())
        }

         fun isValidEmail(email: CharSequence): Boolean {
            return if(TextUtils.isEmpty(email)){
                false
            } else{
                Patterns.EMAIL_ADDRESS.matcher(email).matches()
            }
        }
    }
}
