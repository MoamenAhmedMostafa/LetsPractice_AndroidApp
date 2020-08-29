package com.moamen.letspractice

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.moamen.icpcassuit.Connection_Network
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.chat_from.view.*
import kotlinx.android.synthetic.main.chat_to.view.*
import kotlinx.android.synthetic.main.fragment_chat.*
import java.text.SimpleDateFormat

/**
 * A simple [Fragment] subclass.
 */
class Chat : Fragment() {
    var myAuth= FirebaseAuth.getInstance()
    lateinit var v:View
    var cur_user:User?=null
    val userDataBase= FirebaseFirestore.getInstance().collection("Users");
    val adapter_group_chat= GroupAdapter<ViewHolder>()
    lateinit var rec_chat: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v= inflater.inflate(R.layout.fragment_chat, container, false)
        return v
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        rec_chat =v.findViewById(R.id.chatRecyclerView)
        rec_chat.setHasFixedSize(true)
        rec_chat.adapter=adapter_group_chat
        if(myAuth.currentUser!=null)userDataBase.document(myAuth.currentUser!!.uid.toString()).get().addOnSuccessListener {
            cur_user = it.toObject(User::class.java)!!
        }
        ListenToMessage()
        chat_send_message.setOnClickListener{
            val e= Connection_Network(v.context)
            if(e.Isconnected()) {
                if (myAuth.uid != null) {
                    if(cur_user?.score!!.toInt() >= 100) {
                        if (chat_message.text.toString().trim().isNotEmpty()) {
                            sendMessage(chat_message.text.toString().trim())
                        }
                    }
                    else {
                        Toast.makeText(v.context, "Your score less than 100", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(v.context, "You're a guest", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                Toast.makeText(v.context,"No internet connection",Toast.LENGTH_SHORT).show()
            }
        }
        val helper:ItemTouchHelper = ItemTouchHelper(object:ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                TODO("Not yet implemented")
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if(myAuth.uid!=null) {
                    val p=viewHolder.adapterPosition
                    if(adapter_group_chat.getItem(p).layout==R.layout.chat_from) {
                        val msg_id = viewHolder.itemView.msg_id.text.toString()
                        FirebaseFirestore.getInstance().collection("GroupChat").document(msg_id)
                            .delete().addOnCompleteListener {
                                if (it.isSuccessful) {
                                    adapter_group_chat.notifyDataSetChanged()
                                }
                            }
                    }
                    else if(cur_user!=null && (cur_user?.gmail=="moamen.fci@gmail.com"||cur_user?.gmail=="admin@gmail.com")){
                        val msg_id = viewHolder.itemView.msg_id2.text.toString()
                        FirebaseFirestore.getInstance().collection("GroupChat").document(msg_id)
                            .delete().addOnCompleteListener {
                                if (it.isSuccessful) {
                                    adapter_group_chat.notifyDataSetChanged()
                                }
                            }
                    }
                }
            }
        })
        if(myAuth.uid!=null)helper.attachToRecyclerView(rec_chat)
    }
    private fun sendMessage(ms:String) {
        val msg_id=FirebaseFirestore.getInstance().collection("GroupChat").document().id
        val Mesg=Message(ms,myAuth.uid.toString(), Timestamp.now(),msg_id)
        wait_send_chat.visibility=View.VISIBLE
        chat_message.setText("")
        val DataBase= FirebaseFirestore.getInstance().collection("GroupChat")
            .document(msg_id).set(Mesg).addOnCompleteListener{
                if(it.isSuccessful){
                    wait_send_chat.visibility=View.GONE
                    ListenToMessage()
                }
                else {
                    Toast.makeText(v.context,it.exception!!.message, Toast.LENGTH_LONG).show()
                    wait_send_chat.visibility=View.GONE
                }
            }
    }
    private fun ListenToMessage() {
        val DataBase= FirebaseFirestore.getInstance().collection("GroupChat")
            .orderBy("time", Query.Direction.DESCENDING).addSnapshotListener{
                    q,fire_x->
                adapter_group_chat.clear()
                q!!.documents.forEach{m->
                    val mesg=  m.toObject(Message::class.java)
                    if (myAuth.uid==null || mesg!!.userid != myAuth.uid.toString())
                        adapter_group_chat.add(ChatItemTo(mesg!!))
                    else
                        adapter_group_chat.add(ChatItemFrom(mesg))
                }
            }
    }

    public class ChatItemTo(val Current_Message:Message): Item<ViewHolder>() {
        override fun getLayout(): Int {
            return R.layout.chat_to
        }
        override fun bind(viewHolder: ViewHolder, position: Int) {
            FirebaseFirestore.getInstance().collection("Users")
                .document(Current_Message.userid).get().addOnSuccessListener {
                   val u = it.toObject(User::class.java)
                    if (u != null) {
                        viewHolder.itemView.To_message.text = Current_Message.mesg+'\n'+'\n'+
                                SimpleDateFormat("HH:mm").format(Current_Message.time.toDate()).toString()
                        viewHolder.itemView.To_name.text = u!!.name
                        viewHolder.itemView.msg_id2.text = Current_Message.id
                        val storge = FirebaseStorage.getInstance()
                        if (u!!.img!!.isNotEmpty()) {
                            Glide.with(viewHolder.itemView.context)
                                .load(storge.getReference(u!!.img))
                                .into(viewHolder.itemView.To_img)
                        } else {
                            Glide.with(viewHolder.itemView.context)
                                .load(storge.getReference("/ProfileImages/fb.jpeg"))
                                .into(viewHolder.itemView.To_img)
                        }
                    }
                }
        }
    }
    public class ChatItemFrom(val Current_Message: Message): Item<ViewHolder>() {
        override fun getLayout(): Int {
            return R.layout.chat_from
        }
        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.From_message.text=Current_Message.mesg+'\n'+'\n'+
                    SimpleDateFormat("HH:mm").format(Current_Message.time.toDate()).toString()
            viewHolder.itemView.msg_id.text=Current_Message.id
        }
    }

}
