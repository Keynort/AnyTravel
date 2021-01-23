package com.nurflower.anytravel

import App
import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.bottom_sheet.view.*
import kotlinx.android.synthetic.main.fragment_add.*
import kotlinx.android.synthetic.main.fragment_add.view.*
import kotlinx.android.synthetic.main.fragment_registration.view.*
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val INTENT_PERMISSION_REQUEST_CAMERA_FOR_PHOTO = 100
private const val INTENT_TAKE_PHOTO_RESULT = 150
private const val INTENT_CHOOSE_PHOTO_FROM_GALLERY = 170


/**
 * A simple [Fragment] subclass.
 * Use the [AddFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var imageFilePath: String? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var myRef: DatabaseReference = database.reference
    private var  actualProImagePath:String?=null
    private var layoutView:View?=null
    private var storageReference:StorageReference? = null



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
        layoutView = inflater.inflate(R.layout.fragment_add, container, false)
        storageReference = FirebaseStorage.getInstance().reference
        bottomSheetBehavior = BottomSheetBehavior.from<View>(layoutView!!.bottom_sheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(view: View, i: Int) {}
            override fun onSlide(view: View, v: Float) {}
        })


        layoutView?.addPhoto?.setOnClickListener {
            choosePhoto()
        }
        layoutView?.addTour?.setOnClickListener {
            val tourName = layoutView?.tourName?.text.toString()
            val data = layoutView?.tourData?.text.toString()
            val price = layoutView?.tourPrice?.text.toString()
            val numberOfPeople = layoutView?.numberOfPeople?.text.toString()
            val description = layoutView?.tourDescription?.text.toString()
            val phoneNumber = layoutView?.phoneNumber4?.text.toString()
            var allow = true

            activity?.let { it1 -> App.writeSharedPreferences(it1, "isLogged", "true") }

            if (tourName.length < 2 || tourName.length > 15) {
                layoutView?.tourName?.error =
                    "Название тура должно быть не меньше 2 символов или больше 15 символов"
                allow = false
            }
            if (data.length < 4 || data.length > 15) {
                layoutView?.tourData?.error =
                    "Дата должна быть не меньше 4 символов или больше 15 символов"
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
            if(phoneNumber.isDigitsOnly() && phoneNumber.length!=11){
                layoutView?.phoneNumber?.error="Введите существующий номер телефона с 8"
                allow=false
            }
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

            if (allow) {

                val tour = Tours()
                tour.tourName = tourName
                tour.dateAndTime = data
                tour.price = price.toLong()
                tour.numbersOfPeople = numberOfPeople.toInt()
                tour.phone = phoneNumber
                tour.description = description
                tour.regPeople = 0


                if (actualProImagePath != null){
                    val file = File(actualProImagePath)
                    val uri = Uri.fromFile(file)
                    val reference = storageReference!!.child(file.name);

                    reference.putFile(uri).addOnSuccessListener {

                        tour.imageId = file.name

                        myRef.child("users").child(mAuth.currentUser!!.uid).child("companyName").addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {
                            }

                            override fun onDataChange(snapshot: DataSnapshot) {

                                tour.companyName = snapshot.getValue(String::class.java)

                                val key = myRef.push().key!!
                                myRef.child("tours").child(key).setValue(tour)
                                myRef.child("users").child(mAuth.uid!!).child("tours").child(key)
                                    .setValue(tour)
                                try {
                                    activity?.let {
                                        layoutView?.findNavController()?.navigate(R.id.action_addFragment_to_listFragment)
                                    }
                                }catch (e:Exception){
                                }
                            }
                        })
                    }
                }else{
                    val toast :Toast=Toast.makeText(context,"Добавьте фото к туру",Toast.LENGTH_SHORT)
                    val view = toast.view
                    view?.setBackgroundResource(R.drawable.button_border)
                    view?.findViewById<View>(android.R.id.message) as TextView
                    toast.show()
                }

            }
        }
        layoutView!!.takePhotoLl.setOnClickListener {
            requestCamera(1);

            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        layoutView!!.chooseFromGalleryLl.setOnClickListener {
            requestCamera(2);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        return layoutView
    }


    private fun choosePhoto() {

        activity?.let { context ->

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {

                requestCameraPermission(INTENT_PERMISSION_REQUEST_CAMERA_FOR_PHOTO);

            } else {
                if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

        }
    }



    private fun requestCameraPermission(request: Int) {
        activity?.let {
            if (request == INTENT_PERMISSION_REQUEST_CAMERA_FOR_PHOTO) {
                ActivityCompat.requestPermissions(
                    it, arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    request
                )
            }
        }
    }



    private fun requestCamera(type: Int) {
        activity?.let { context ->
            val pictureIntent: Intent
            if (type == 1) {
                pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (pictureIntent.resolveActivity(context.packageManager) != null) {
                    //Create a file to store the image
                    var photoFile: File? = null
                    try {
                        photoFile = createImageFile()
                    } catch (ex: IOException) {
                        Log.d("addFragment", ex.toString())
                        // Error occurred while creating the File
                    }
                    if (photoFile != null) {
                        val photoURI: Uri = FileProvider.getUriForFile(context, "com.nurflower.anytravel.provider", photoFile)
                        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(pictureIntent, INTENT_TAKE_PHOTO_RESULT)
                    }
                }
            } else {
                pictureIntent = Intent(Intent.ACTION_PICK)
                pictureIntent.type = "image/*"
                startActivityForResult(
                    pictureIntent,
                    INTENT_CHOOSE_PHOTO_FROM_GALLERY
                )
            }
        }
    }




    private fun createImageFile(): File? {
        var image: File? = null
        activity?.let { context ->
            val imageFileName: String = App.generateUniCode("p")
            val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
            )
            imageFilePath = image?.absolutePath
        }
        return image
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == INTENT_TAKE_PHOTO_RESULT) {
            if (resultCode == RESULT_OK) {
                actualProImagePath = imageFilePath
                Glide.with(this).load(imageFilePath)
                    .thumbnail(0.1f).into(layoutView?.photoIv!!)
                layoutView?.photoIv?.visibility=View.VISIBLE
            }
        }

        if (requestCode == INTENT_CHOOSE_PHOTO_FROM_GALLERY) {
            if (resultCode == RESULT_OK) {
                try {
                    activity?.let { context ->
                        val imageUri: Uri? = data?.data
                        val imageStream: InputStream? = context.contentResolver.openInputStream(
                            imageUri!!
                        )
                        val selectedImage = BitmapFactory.decodeStream(imageStream)
                        actualProImagePath = getPathFromURI(imageUri)
                        Glide.with(this).asBitmap().load(selectedImage).thumbnail(0.1f)
                            .into(layoutView?.photoIv!!)
                        layoutView?.photoIv?.visibility=View.VISIBLE
                    }
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun getPathFromURI(contentUri: Uri?): String? {
        var res: String? = null
        activity?.let { context ->
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            val cursor: Cursor? = context.contentResolver.query(contentUri!!, proj, null, null, null)
            cursor?.let {
                if (cursor.moveToFirst()) {
                    val columnIndex: Int =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    res = cursor.getString(columnIndex)
                }
            }
            cursor?.close()
        }
        return res
    }




    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
