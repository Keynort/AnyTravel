package com.nurflower.anytravel

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.text.isDigitsOnly
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.activity_add.addPhoto
import kotlinx.android.synthetic.main.activity_add.numberOfPeople
import kotlinx.android.synthetic.main.activity_add.phoneNumber4
import kotlinx.android.synthetic.main.activity_add.photoIv
import kotlinx.android.synthetic.main.activity_add.tourData
import kotlinx.android.synthetic.main.activity_add.tourDescription
import kotlinx.android.synthetic.main.activity_add.tourName
import kotlinx.android.synthetic.main.activity_add.tourPrice
import kotlinx.android.synthetic.main.bottom_sheet.*
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream


private const val INTENT_PERMISSION_REQUEST_CAMERA_FOR_PHOTO = 100
private const val INTENT_TAKE_PHOTO_RESULT = 150
private const val INTENT_CHOOSE_PHOTO_FROM_GALLERY = 170



class AddActivity : AppCompatActivity() {
    private var imageFilePath: String? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var myRef: DatabaseReference = database.reference
    private var  actualProImagePath:String?=null
    private var storageReference:StorageReference? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)


        val tourId = intent?.getStringExtra("tourId")!!
        storageReference = FirebaseStorage.getInstance().reference
        bottomSheetBehavior = BottomSheetBehavior.from<View>(bottom_sheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(view: View, i: Int) {}
            override fun onSlide(view: View, v: Float) {}
        })

        addPhoto?.setOnClickListener {
            choosePhoto()
        }
        myRef.child("tours").child(tourId).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val mTour = snapshot.getValue(Tours::class.java)
                tourName.setText(mTour?.tourName)
                tourData.setText(mTour?.dateAndTime)
                tourPrice.setText(mTour?.price.toString())
                numberOfPeople.setText(mTour?.numbersOfPeople.toString())
                phoneNumber4.setText(mTour?.phone)
                tourDescription.setText(mTour?.description)
//                photoIv.setImageResource(mTour?.imageId.toString().toInt())
            }

        })




        saveTour.setOnClickListener {
            val mtourName = tourName.text.toString()
            val data = tourData.text.toString()
            val price = tourPrice.text.toString()
            val mnumberOfPeople = numberOfPeople.text.toString()
            val description = tourDescription.text.toString()
            val phoneNumber = phoneNumber4.text.toString()
            var allow = true
            if (mtourName.length < 2 || mtourName.length > 15) {
                tourName.error = "Название тура должно быть не меньше 2 символов или больше 15 символов"
                allow = false
            }
            if (data.length < 4 || data.length > 15) {
                tourData.error =
                    "Дата должна быть не меньше 2 символов или больше 15 символов"
                allow = false
            }
            if (price.length < 2 || price.length > 12) {
                tourPrice.error =
                    "Цена не должна быть меньше 2 символов или больше 12 символов"
                allow = false
            }
            if (mnumberOfPeople.length < 2 || mnumberOfPeople.length > 5) {
                numberOfPeople.error = "Количество людей не может быть меньше 2 "
                allow = false
            }
            if (description.length > 2500) {
                tourDescription.error = "Описание не можеть быть больше 2500 символов"
                allow = false
            }
            if(phoneNumber.isDigitsOnly() && phoneNumber.length!=11){
                phoneNumber4.error = "Введите существующий номер телефона "
                allow=false
            }
            if (allow) {
                if(actualProImagePath!=null) {
                    val file = File(actualProImagePath)
                    val uri = Uri.fromFile(file)
                    val reference = storageReference!!.child(file.name);

                    val tour = Tours()


                    reference.putFile(uri).addOnSuccessListener {
                        tour.tourName = mtourName
                        tour.dateAndTime = data
                        tour.regPeople = 0
                        tour.numbersOfPeople = mnumberOfPeople.toInt()
                        tour.phone = phoneNumber
                        tour.description = description
                        tour.imageId = file.name
                        tour.price = price.toLong()

                        myRef.child("users").child(mAuth.currentUser!!.uid).child("companyName")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(error: DatabaseError) {


                                }

                                override fun onDataChange(snapshot: DataSnapshot) {
                                    tour.companyName = snapshot.getValue(String::class.java)
                                    myRef.child("tours").child(tourId).setValue(tour)
                                    myRef.child("users").child(mAuth.uid!!).child("tours")
                                        .child(tourId).setValue(tour)

                                }
                            })
                    }
                }else{
                    val toast : Toast = Toast.makeText(this,"Добавьте фото к туру", Toast.LENGTH_SHORT)
                    val view = toast.view
                    view?.setBackgroundResource(R.drawable.button_border)
                    view?.findViewById<View>(android.R.id.message) as TextView
                    toast.show()
                }
            }
            if(actualProImagePath!=null) {
                finish()
            }
        }
        takePhotoLl.setOnClickListener {
            requestCamera(1);

            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        chooseFromGalleryLl.setOnClickListener {
            requestCamera(2);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        backButton2.setOnClickListener{
            finish()
        }
    }


    private fun choosePhoto() {

       let { context ->

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
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
        let {
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
        let { context ->
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
        let { context ->
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
            if (resultCode == Activity.RESULT_OK) {
                actualProImagePath = imageFilePath
                Glide.with(this).load(imageFilePath)
                    .thumbnail(0.1f).into(photoIv!!)
                photoIv.visibility=View.VISIBLE
            }
        }

        if (requestCode == INTENT_CHOOSE_PHOTO_FROM_GALLERY) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    let { context ->
                        val imageUri: Uri? = data?.data
                        val imageStream: InputStream? = context.contentResolver.openInputStream(
                            imageUri!!
                        )
                        val selectedImage = BitmapFactory.decodeStream(imageStream)
                        actualProImagePath = getPathFromURI(imageUri)
                        Glide.with(this).asBitmap().load(selectedImage).thumbnail(0.1f)
                            .into(photoIv!!)
                        photoIv.visibility=View.VISIBLE
                    }
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun getPathFromURI(contentUri: Uri?): String? {
        var res: String? = null
        let { context ->
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
}