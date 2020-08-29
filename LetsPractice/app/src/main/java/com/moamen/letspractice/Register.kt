package com.moamen.letspractice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.moamen.icpcassuit.Connection_Network
import kotlinx.android.synthetic.main.activity_register.*

class Register : AppCompatActivity() {
    var myAuth= FirebaseAuth.getInstance()
    val userDataBase= FirebaseFirestore.getInstance().collection("Users");
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        back_login.setOnClickListener{
            finish()
            overridePendingTransition(R.anim.slid_in_left,R.anim.slid_out_right)
        }
        Register_btn.setOnClickListener {
            val name = R_name.text.trim().toString()
            val email = R_email.text.trim().toString()
            val phone = R_phone.text.trim().toString()
            val pass = R_pass.text.trim().toString()
            val pass2 = R_confirm_pass.text.trim().toString()
            if (name.isEmpty()) R_name.setError("Not allow empty");
            if (email.isEmpty()) R_email.setError("Not allow empty");
            if (phone.isEmpty()) R_phone.setError("Not allow empty");
            if (pass.isEmpty()) R_pass.setError("Not allow empty");
            if (pass2.isEmpty()) R_confirm_pass.setError("Not allow empty");
            if (pass != pass2) {
                R_pass.setError("Not identical");
                R_confirm_pass.setError("Not identical");
            }
            val e = Connection_Network(this)
            if (e.Isconnected()) {
                if (name.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty() && pass.isNotEmpty() && pass2.isNotEmpty() && pass == pass2) {
                    register_wait.visibility = View.VISIBLE
                    myAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {
                            var u: User = User()
                            u.gmail = email
                            u.name = name
                            u.phone = phone
                            u.id = myAuth.currentUser!!.uid.toString()
                            u.img = ""
                            u.n_of_quiz = 0
                            u.score = 0
                            userDataBase.document(myAuth.currentUser!!.uid.toString()).set(u)
                            register_wait.visibility = View.GONE
                            var i = Intent(this, MainPage::class.java)
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(i)
                            Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            register_wait.visibility = View.GONE
                            Toast.makeText(this, it.exception!!.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            else {
                Toast.makeText(this,"No internet connection",Toast.LENGTH_SHORT).show()
            }
        }
    }
}
