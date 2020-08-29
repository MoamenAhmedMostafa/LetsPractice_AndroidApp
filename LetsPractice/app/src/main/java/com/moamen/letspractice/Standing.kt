package com.moamen.letspractice

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.users_view.view.*

class Standing : AppCompatActivity() {
    var myAuth= FirebaseAuth.getInstance()
    val adapter_users= GroupAdapter<ViewHolder>()
    lateinit var rec_standing: RecyclerView
    lateinit var mAdView : AdView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_standing)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
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
        rec_standing =findViewById(R.id.rec_standing)
        rec_standing.setHasFixedSize(true)
        rec_standing.layoutManager= LinearLayoutManager(this, LinearLayout.VERTICAL as Int,false)
        rec_standing.adapter=adapter_users
        load_users();
    }
    fun load_users(){
        adapter_users.clear()
        var id:Int=1
        val userDataBase= FirebaseFirestore.getInstance().collection("CompetitionUser").orderBy("score", Query.Direction.DESCENDING)
        userDataBase.get().addOnSuccessListener {result->
            for(u in result){
                val cur_user=  u.toObject(User::class.java)
                adapter_users.add(UsersItem(cur_user,id))
                id++
            }
        }
    }

    public class UsersItem(val user: User,var id:Int): Item<ViewHolder>() {
        override fun getLayout(): Int {
            return R.layout.users_view
        }
        override fun bind(viewHolder: ViewHolder, position: Int) {
            val storge= FirebaseStorage.getInstance()
            if(user!=null) {
                viewHolder.itemView.users_Name.text = user?.name
                viewHolder.itemView.users_Score.text ="Score: "+ user?.score.toString()
                viewHolder.itemView.users_n_of_quiz.visibility= View.INVISIBLE
                viewHolder.itemView.rank.text=id.toString()
                viewHolder.itemView.img_stat.visibility= View.INVISIBLE
                viewHolder.itemView.txt_stat.visibility= View.INVISIBLE


                if(user.img!!.isNotEmpty()){
                    Glide.with(viewHolder.itemView.context)
                        .load(storge.getReference(user?.img!!.toString()))
                        .into(viewHolder.itemView.users_image)
                }
                else {
                    Glide.with(viewHolder.itemView.context)
                        .load(storge.getReference("/ProfileImages/fb.jpeg"))
                        .into(viewHolder.itemView.users_image)
                }
            }
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
