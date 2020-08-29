package com.moamen.letspractice

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.graphics.green
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_all_users.*
import kotlinx.android.synthetic.main.users_view.view.*

/**
 * A simple [Fragment] subclass.
 */
class AllUsers : Fragment() {
    var myAuth= FirebaseAuth.getInstance()
    lateinit var v:View
    companion object {
        var c:Int = 0
    }
    val adapter_users= GroupAdapter<ViewHolder>()
    lateinit var rec_users: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v= inflater.inflate(R.layout.fragment_all_users, container, false)
        return v
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        rec_users =v.findViewById(R.id.recycler_all_users)
        rec_users.setHasFixedSize(true)
        rec_users.layoutManager= LinearLayoutManager(this.activity, LinearLayout.VERTICAL as Int,false)
        rec_users.adapter=adapter_users
        load_users();
        all_users_refresh.setOnRefreshListener {
                load_users();
            Handler().postDelayed(object:Runnable {
                override fun run() {
                    all_users_refresh.isRefreshing=false
                }
            },900)
        }
       /* search_text.addTextChangedListener(this)
        search_btn.setOnClickListener{
            if(!search_text.text.trim().isEmpty()){
                load_users_search()
            }
        }*/
    }
    fun load_users(){
        adapter_users.clear()
        var id:Int=1
        val userDataBase= FirebaseFirestore.getInstance().collection("Users").orderBy("score", Query.Direction.DESCENDING).
            limit(15)
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
                viewHolder.itemView.users_n_of_quiz.text ="Number of practices: "+ user?.n_of_quiz.toString()
                viewHolder.itemView.rank.text=id.toString()
                if(user.rnk-id<0) {
                    Glide.with(viewHolder.itemView.context)
                        .load(storge.getReference("/mainhome/downarrow.png"))
                        .into(viewHolder.itemView.img_stat)
                    viewHolder.itemView.txt_stat.text="-"+(id-user.rnk).toString()
                    viewHolder.itemView.txt_stat.setTextColor(Color.parseColor("#F30909"))
                }
                else if(user.rnk-id>0){
                    Glide.with(viewHolder.itemView.context)
                        .load(storge.getReference("/mainhome/uparrow.png"))
                        .into(viewHolder.itemView.img_stat)
                    viewHolder.itemView.txt_stat.text="+"+(user.rnk-id).toString()
                    viewHolder.itemView.txt_stat.setTextColor(Color.parseColor("#29FA04"))

                }
                else {
                    Glide.with(viewHolder.itemView.context)
                        .load(storge.getReference("/mainhome/empty.png"))
                        .into(viewHolder.itemView.img_stat)
                    viewHolder.itemView.txt_stat.text=""
                }

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
}
