package com.moamen.letspractice

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.RestrictionsManager
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.moamen.icpcassuit.Connection_Network
import kotlinx.android.synthetic.main.fragment_profile.*
import java.io.ByteArrayOutputStream
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class Profile : Fragment() {
    private val STORAGE_PERMISSION_CODE = 1
    var myAuth= FirebaseAuth.getInstance()
    val userDataBase= FirebaseFirestore.getInstance().collection("Users");
    val storge= FirebaseStorage.getInstance()
    lateinit var v:View
    var selected_imageByte:ByteArray?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v= inflater.inflate(R.layout.fragment_profile, container, false)
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var u:User?=null
        userDataBase.document(myAuth.currentUser!!.uid.toString()).get().addOnSuccessListener {
            u = it.toObject(User::class.java)!!
            if (u != null) {
                Profile_Email.text = u?.gmail
                Profile_name.setText(u?.name)
                Profile_phone.setText(u?.phone)
                Profile_Score.text=u?.score.toString()
                Profile_num_of_quiz.text=u?.n_of_quiz.toString()
                if (u?.img!!.isNotEmpty()) {
                    Glide.with(v.context)
                        .load(storge.getReference(u!!.img))
                        .into(Profile_image)
                } else {
                    Glide.with(v.context)
                        .load(storge.getReference("/ProfileImages/fb.jpeg"))
                        .into(Profile_image)
                }
            }
        }
        Profile_image.setOnClickListener{


            if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(activity!!, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
            } else {
                val myImageIntent= Intent().apply {
                    type="image/*"
                    action=Intent.ACTION_GET_CONTENT
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpg","image/jpeg","image/png"))
                }
                startActivityForResult(Intent.createChooser(myImageIntent,"Select image"),2)
            }
        }

        Profile_change.setOnClickListener {
            if (Profile_name.text.isEmpty()) Profile_name.setError("Not allow")
            if (Profile_phone.text.isEmpty()) Profile_phone.setError("Not allow")
            val e = Connection_Network(v.context)
            if (e.Isconnected()) {
                if (!(Profile_name.text.isEmpty()) && !(Profile_phone.text.isEmpty())) {
                    profile_wait.visibility = View.VISIBLE
                    u?.name = Profile_name.text.toString()
                    u?.phone = Profile_phone.text.toString()
                    if (selected_imageByte != null) {
                        val currentUserStorageRef = storge.reference.child(
                            "ProfileImages/${u!!.gmail}/${UUID.nameUUIDFromBytes(selected_imageByte)}"
                        )
                        currentUserStorageRef.putBytes(selected_imageByte!!).addOnCompleteListener {
                            if (it.isSuccessful) {
                                val user_update = mutableMapOf<String, Any>()
                                user_update["name"] = u!!.name
                                user_update["img"] = currentUserStorageRef.path
                                user_update["phone"] = u!!.phone
                                userDataBase.document(myAuth.currentUser!!.uid.toString())
                                    .update(user_update).addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            Toast.makeText(v.context, "Saved..", Toast.LENGTH_LONG)
                                                .show()
                                            selected_imageByte = null
                                            profile_wait.visibility = View.GONE

                                        } else {
                                            profile_wait.visibility = View.GONE

                                            Toast.makeText(
                                                v.context,
                                                it.exception!!.message,
                                                Toast.LENGTH_LONG
                                            ).show()

                                        }
                                    }
                            } else {
                                profile_wait.visibility = View.GONE

                                Toast.makeText(v.context, it.exception!!.message, Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                    } else {
                        val user_update = mutableMapOf<String, Any>()
                        user_update["name"] = u!!.name
                        user_update["phone"] = u!!.phone
                        userDataBase.document(myAuth.currentUser!!.uid.toString())
                            .update(user_update).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Toast.makeText(v.context, "Saved..", Toast.LENGTH_LONG)
                                        .show()
                                    profile_wait.visibility = View.GONE

                                } else {
                                    Toast.makeText(
                                        v.context,
                                        it.exception!!.message,
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                    profile_wait.visibility = View.GONE

                                }
                            }
                    }
                }
            }
            else {
                Toast.makeText(v.context,"No internet connection",Toast.LENGTH_SHORT).show()
            }
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==2 && resultCode== Activity.RESULT_OK && data!=null && data.data!=null){
            Profile_image.setImageURI(data.data)
            val selected_image_path=data.data
            val selected_imageBmp=
                MediaStore.Images.Media.getBitmap(v.context.contentResolver,selected_image_path)
            val outputstream= ByteArrayOutputStream()
            selected_imageBmp.compress(Bitmap.CompressFormat.JPEG,30,outputstream)
            selected_imageByte=outputstream.toByteArray()
        }
    }

}
