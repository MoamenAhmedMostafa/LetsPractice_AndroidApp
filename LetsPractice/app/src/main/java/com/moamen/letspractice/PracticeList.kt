package com.moamen.letspractice

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.moamen.icpcassuit.Connection_Network
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_practice_list.*
import kotlinx.android.synthetic.main.practice_list_view.view.*

class PracticeList : AppCompatActivity() {
    lateinit var mAdView : AdView
    val adapter_practice= GroupAdapter<ViewHolder>()
    lateinit var rec_practice: RecyclerView
    lateinit var type:String
    lateinit var level:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practice_list)
        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        mAdView.adListener = object: AdListener() {
            override fun onAdClosed() {
                mAdView.loadAd(adRequest)
            }
            override fun onAdClicked() {
                mAdView.loadAd(adRequest)
            }
            override fun onAdOpened() {
                mAdView.loadAd(adRequest)
            }
        }
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
         type=intent.getStringExtra("type")
         level =intent.getStringExtra("level")
        rec_practice =findViewById(R.id.rec_practice_list)
        rec_practice.setHasFixedSize(true)
        rec_practice.adapter=adapter_practice
        rec_practice.layoutManager= LinearLayoutManager(this, LinearLayout.VERTICAL as Int,false)
        load_practice()
        practices_refresh.setOnRefreshListener {
             load_practice()
            Handler().postDelayed(object:Runnable {
                override fun run() {
                    practices_refresh.isRefreshing=false
                }
            },900)
        }
    }
    fun load_practice() {
        adapter_practice.clear()
        FirebaseFirestore.getInstance().collection("Practice").document(type).collection(level)
            .orderBy("id", Query.Direction.ASCENDING).addSnapshotListener { q, fire_x ->
                q!!.documents.forEach { P ->
                    if(P!=null) {
                        val p = P.toObject(DataPractice::class.java)
                        adapter_practice.add(PracticeItem(p!!,type,level,P.id))
                    }
                }
            }
    }
    public class PracticeItem(val p:DataPractice,var type:String,var level:String,var ID:String): Item<ViewHolder>() {
        override fun getLayout(): Int {
            return R.layout.practice_list_view
        }
        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.btn_start_practice.text=p.id.toString()
            viewHolder.itemView.number_of_people.text=p.number_of_people.toString()
            viewHolder.itemView.btn_start_practice.setOnClickListener {
                val myAuth = FirebaseAuth.getInstance()
                val e = Connection_Network(viewHolder.itemView.context)
                if (e.Isconnected()) {
                    if (myAuth.currentUser != null || myAuth.currentUser == null && p.id==1) {
                        var i = Intent(viewHolder.itemView.context, StartPractice::class.java)
                        if (p.path_of_questions != "") {
                            FirebaseFirestore.getInstance().collection(p.path_of_questions)
                                .addSnapshotListener { q, fire_x ->
                                    var id = 0
                                    q!!.documents.forEach { P ->
                                        if (P != null) {
                                            val p = P.toObject(DataQuestion::class.java)
                                            load(id, p!!)
                                            id++
                                        }
                                    }
                                }
                        }
                        val builder = AlertDialog.Builder(viewHolder.itemView.context)
                        builder.setMessage("Are you ready?")
                        builder.setPositiveButton("YES") { dialog, which ->

                           if(myAuth.uid!=null) {
                               var id: Int = 1

                               var num: Int = 16;
                               val userDataBase =
                                   FirebaseFirestore.getInstance().collection("Users")
                                       .orderBy("score", Query.Direction.DESCENDING).limit(15)
                               userDataBase.get().addOnSuccessListener { result ->
                                   for (u in result) {
                                       val cur_user = u.toObject(User::class.java)
                                       if (cur_user.id == myAuth.uid.toString()) {
                                           num = id
                                       }
                                       id++
                                   }
                                   val user_update = mutableMapOf<String, Any>()
                                   user_update["rnk"] = num
                                   val u = FirebaseFirestore.getInstance().collection("Users");
                                   u.document(myAuth.currentUser!!.uid.toString())
                                       .update(user_update)
                               }
                           }

                            val time = object : CountDownTimer(5000, 1000) {
                                override fun onFinish() {
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    i.putExtra("name", ID)
                                    i.putExtra("time", (10*60*1000).toString())
                                    viewHolder.itemView.context.startActivity(i)

                                }

                                override fun onTick(millisUntilFinished: Long) {
                                }
                            }
                            update()
                            val progressionDialog = ProgressDialog(viewHolder.itemView.context)
                            progressionDialog.setMessage("Loading.....")
                            progressionDialog.setCancelable(false)
                            progressionDialog.show()
                            Handler().postDelayed({ progressionDialog.dismiss() }, 5000)
                            time.start()
                        }
                        builder.setNegativeButton("NO") { dialog, which ->
                        }
                        val dialog = builder.create()
                        dialog.show()
                    }
                    else {
                        Toast.makeText(
                            viewHolder.itemView.context,
                            "Only number 1 allow, Login to practice",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                     else {
                        Toast.makeText(
                            viewHolder.itemView.context,
                            "No internet connection",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
        fun update(){
            p.number_of_people++;
            val new_prac = mutableMapOf<String, Any>()
            new_prac["path_of_questions"] = p.path_of_questions
            new_prac["id"] = p.id
            new_prac["number_of_people"] = p.number_of_people
            FirebaseFirestore.getInstance().collection("Practice").document(type)
                .collection(level).document(ID).update(new_prac)
        }
        fun load(id:Int,data:DataQuestion){
            StartPractice.dataQuestions[id]= data
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                finish()
                overridePendingTransition(R.anim.slid_in_left,R.anim.slid_out_right)

                return  true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
