package com.moamen.letspractice

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.news.view.*
import kotlinx.android.synthetic.main.practice_view.view.*

/**
 * A simple [Fragment] subclass.
 */
class Home : Fragment() {

    lateinit var v:View
    val adapter_news= GroupAdapter<ViewHolder>()
    lateinit var rec_new: RecyclerView

    val adapter_programe_lang= GroupAdapter<ViewHolder>()
    lateinit var rec_programe_lang: RecyclerView

    val adapter_alltyps= GroupAdapter<ViewHolder>()
    lateinit var rec_all_type: RecyclerView

    val adapter_subject= GroupAdapter<ViewHolder>()
    lateinit var rec_subject: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v= inflater.inflate(R.layout.fragment_home, container, false)
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //setting of helpful links
        rec_new =v.findViewById(R.id.rec_news)
        rec_new.layoutManager=LinearLayoutManager(v.context,LinearLayoutManager.HORIZONTAL,true)
        rec_new.setHasFixedSize(true)
        rec_new.adapter=adapter_news


        //setting of programe_lang

        rec_programe_lang =v.findViewById(R.id.rec_programe_lang)
        rec_programe_lang.layoutManager=LinearLayoutManager(v.context,LinearLayoutManager.HORIZONTAL,false)
        rec_programe_lang.setHasFixedSize(true)
        rec_programe_lang.adapter=adapter_programe_lang

        // setting of General subjects
        rec_all_type =v.findViewById(R.id.rec_all_type)
        rec_all_type.layoutManager=LinearLayoutManager(v.context,LinearLayoutManager.HORIZONTAL,false)
        rec_all_type.setHasFixedSize(true)
        rec_all_type.adapter=adapter_alltyps


        // setting of core subjects

        rec_subject =v.findViewById(R.id.rec_subject)
        rec_subject.layoutManager=LinearLayoutManager(v.context,LinearLayoutManager.HORIZONTAL,false)
        rec_subject.setHasFixedSize(true)
        rec_subject.adapter=adapter_subject

        // when do refresh
        home_refresh.setOnRefreshListener {

            // display them for database
            load("GeneralSubjects")
            load("ProgrammingSubjects")
            load("CoreSubjects")
            FirebaseFirestore.getInstance().collection("HelpfulLinks").addSnapshotListener { q, fire_x ->
                adapter_news.clear()
                q!!.documents.forEach { P ->
                    if (P != null) {
                        val p = P.toObject(DataAds::class.java)
                        if(p!=null)
                            adapter_news.add(NewsItem(p!!))
                    }
                }
            }
            Handler().postDelayed(object:Runnable {
                override fun run() {
                    home_refresh.isRefreshing=false
                }
            },900)
        }
        // load for first time
        load("GeneralSubjects")
        load("ProgrammingSubjects")
        load("CoreSubjects")

        // load Helpful Links
        adapter_news.clear()
        FirebaseFirestore.getInstance().collection("HelpfulLinks").addSnapshotListener { q, fire_x ->
            q!!.documents.forEach { P ->
                if (P != null) {
                    val p = P.toObject(DataAds::class.java)
                    if(p!=null)
                    adapter_news.add(NewsItem(p!!))
                }
            }
        }
       /* val thread=Thread{
            var cnt:Int=1
            var left_or_right:Boolean=true
            while(true){
                Thread.sleep(2000)
                rec_new.smoothScrollToPosition(cnt)
                if(left_or_right){
                    if(cnt<adapter_news.getItemCount()-1)
                        cnt=cnt+1
                    else{
                        left_or_right=false
                        cnt = cnt - 1
                    }
                }
                else {
                    if(cnt>=1)
                        cnt=cnt-1
                    else {
                        left_or_right=true
                        cnt = cnt + 1
                    }
                }
            }
        }
        thread.start()*/


    }
    fun load(name:String){
        if(name=="GeneralSubjects")   adapter_alltyps.clear()
        else if(name=="ProgrammingSubjects")   adapter_programe_lang.clear()
        else if(name=="CoreSubjects")   adapter_subject.clear()
        FirebaseFirestore.getInstance().collection(name).addSnapshotListener { q, fire_x ->
            q!!.documents.forEach { P ->
                if (P != null) {
                    val p = P.toObject(TypePractice::class.java)
                    if(p!=null) {
                        //Toast.makeText(v.context,"H",Toast.LENGTH_SHORT).show()
                       if (name == "GeneralSubjects") adapter_alltyps.add(PracticeItem(p!!))
                       else if (name == "ProgrammingSubjects") adapter_programe_lang.add( PracticeItem(p!!) )
                       else if (name == "CoreSubjects") adapter_subject.add(PracticeItem(p!!))
                    }
                }
            }
        }
    }
    public class NewsItem(val news: DataAds): Item<ViewHolder>() {
        override fun getLayout(): Int {
            return R.layout.news
        }
        override fun bind(viewHolder: ViewHolder, position: Int) {
            val storge= FirebaseStorage.getInstance()
            if(news!=null) {
                 if(news.img!!.isNotEmpty()){
                    Glide.with(viewHolder.itemView.context)
                        .load(storge.getReference(news?.img!!.toString()))
                        .into(viewHolder.itemView.news_img)
                }
                else {
                     viewHolder.itemView.news_img.setImageResource(R.drawable.logo)
                 }
            }
            viewHolder.itemView.news_img.setOnClickListener{
                val openURL = Intent(Intent.ACTION_VIEW)
                openURL.data= Uri.parse(news?.uri)
                viewHolder.itemView.context.startActivity(openURL)
            }
        }
    }


    public class PracticeItem(val type: TypePractice): Item<ViewHolder>() {
        override fun getLayout(): Int {
            return R.layout.practice_view
        }
        override fun bind(viewHolder: ViewHolder, position: Int) {
            val storge= FirebaseStorage.getInstance()
            if(type!=null) {
                if(type.img!!.isNotEmpty()){
                    Glide.with(viewHolder.itemView.context)
                        .load(storge.getReference(type?.img!!.toString()))
                        .into(viewHolder.itemView.practice_img)
                }
                else {
                    viewHolder.itemView.practice_img.setImageResource(R.drawable.logo)
                }
            }
            viewHolder.itemView.practice_img.setOnClickListener{
                var i = Intent(viewHolder.itemView.context,PracticeLevel::class.java)
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                i.putExtra("type",type?.name)
                viewHolder.itemView.context.startActivity(i)

            }
        }
    }
}
