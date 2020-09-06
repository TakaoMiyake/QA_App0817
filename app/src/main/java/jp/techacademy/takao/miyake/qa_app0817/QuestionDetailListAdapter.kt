package jp.techacademy.takao.miyake.qa_app0817

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.util.Log
import android.widget.Button
import com.google.firebase.FirebaseError
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.database.*

import java.util.HashMap
import android.content.Context as ContentContext


class QuestionDetailListAdapter(context: ContentContext, private val mQustion: Question) : BaseAdapter() {
    private lateinit var mQuestion: Question
    private lateinit var mAdapter: QuestionDetailListAdapter
    private lateinit var mFavorite: Favorite

    companion object {
        private val TYPE_QUESTION = 0
        private val TYPE_ANSWER = 1
    }

    private var mLayoutInflater: LayoutInflater? = null
    private lateinit var mAuth: FirebaseAuth

    init {
        mLayoutInflater =
            context.getSystemService(ContentContext.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return 1 + mQustion.answers.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            TYPE_QUESTION
        } else {
            TYPE_ANSWER
        }
    }

    override fun getViewTypeCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Any {
        return mQustion
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        // FirebaseAuthのオブジェクトを取得する
        mAuth = FirebaseAuth.getInstance()
        val context = parent.context
        val intent = Intent(context, LoginActivity::class.java)

        if (getItemViewType(position) == TYPE_QUESTION) {
            if (convertView == null) {
                convertView =
                    mLayoutInflater!!.inflate(R.layout.list_question_detail, parent, false)!!
            }
            val body = mQustion.body
            val name = mQustion.name

            val questionUid = mQustion.questionUid
            val fuser = mFavorite.favorable

            val user = FirebaseAuth.getInstance().currentUser


            if (user == null) {

//                val context = parent.context
 //               val intent = Intent(context, LoginActivity::class.java)
                context.startActivity(intent)
                return convertView


            }
            fun onResume(savedInstanceState: Bundle?, listView: Any) {
                //onResume(savedInstanceState)

                // 渡ってきたQuestionのオブジェクトを保持する

                val extras = intent.extras
                mQuestion = extras.get("question") as Question

               // var title = mQuestion.title

                // ListViewの準備
                mAdapter = QuestionDetailListAdapter(context, mQuestion)
              //  listView.adapter=mAdapter
              //  convertView.adapter = mAdapter
                mAdapter.notifyDataSetChanged()

            }


            val userIdentification = FirebaseAuth.getInstance().currentUser!!.uid
            var answer_name: String? = null

            //Firebaseからの読み込み用を設定
            val dataBaseReference = FirebaseDatabase.getInstance().reference

            //Firebaseへの書き込み用を設定
            answer_name = userIdentification

            Log.d("ANDROID", "94 answer_name= " + answer_name)

            //val favorableUidAnswerRef = dataBaseReference.child(FavoritePATH)
            val favorableUidAnswerRef =
                dataBaseReference.child(FavoritePATH).child(answer_name).child(questionUid)

            //val fuser = mAuth.currentUser
            val favorableUidAnswerRefFuser = dataBaseReference.child(answer_name).child(questionUid).child(fuser)

//            favorableUidAnswerRefFuser.addListenerForSingleValueEvent(object :ValueEventListener{
//                override fun onCancelled(firebaseError:  DatabaseError) {
//                }
//
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val data = snapshot.value as Map<*,*>?
//                   getName(data!!["fuser"] as String)
//                }
//            })

//            //https://www.firebase.google.com/docs/database/android/read-and-write?hl=jaより
//            val favorableUidAnswerRefFuser = object : ValueEventListener{
//                override fun onDataChange(p0: DataSnapshot) {
//
//                    val post = dataSnapshot .getValue<Post>()
//                }
//                override fun onCancelled(databaseError: DatabaseError){
//                    Log.w(TAG, "loadpost:oncancelled", databaseError.toException())
//                }
//            }
//            postReference.addValueEventListener(favorableUidAnswerRefFuser)


            val bodyTextView = convertView.findViewById<View>(R.id.bodyTextView) as TextView
            bodyTextView.text = body

            val nameTextView = convertView.findViewById<View>(R.id.nameTextView) as TextView
            nameTextView.text = name

            val bytes = mQustion.imageBytes
            if (bytes.isNotEmpty()) {
                val image = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    .copy(Bitmap.Config.ARGB_8888, true)
                val imageView = convertView.findViewById<View>(R.id.imageView) as ImageView
                imageView.setImageBitmap(image)
            }
            val data = HashMap<String, String>()

            val favorableIndicator = convertView.findViewById<View>(R.id.favorable_button) as Button


            //Firebaseから読み込んだユーザごとのお気に入り情報を読み込む
            favorableIndicator.text = favorableUidAnswerRef.toString()
          //  favorableIndicator.text = favorableUidAnswerRefFuser.toString()

            //Firebaseから読みこもうとしてもデータがない（新規）の場合のIf文を作成する。
            if (favorableIndicator.text =="") {
                favorableIndicator.text = "お気に入り"
            }

            Log.d("ANDROID", "favorableIndicator= " + favorableIndicator.text.toString())


            favorableIndicator.setOnClickListener {

                Log.d("ANDROID", "クリックされた")

                if (favorableIndicator.text == "お気に入り") {
                    favorableIndicator.text = "超お気に入り"
                    data["fuserquest"] = "超お気に入り"
                    favorableUidAnswerRef.setValue(data)

                } else {
                    favorableIndicator.text = "お気に入り"
                    data["fuserquest"] = "お気に入り"
                    favorableUidAnswerRef.setValue(data)
                }
            }

        } else {
            if (convertView == null) {
                convertView = mLayoutInflater!!.inflate(R.layout.list_answer, parent, false)!!
            }

            val answer = mQustion.answers[position - 1]
            val body = answer.body
            val name = answer.name

            val bodyTextView = convertView.findViewById<View>(R.id.bodyTextView) as TextView
            bodyTextView.text = body

            val nameTextView = convertView.findViewById<View>(R.id.nameTextView) as TextView
            nameTextView.text = name
        }

            return convertView

    }
//    fun getName(name: String) {
//        // Preferenceに保存する
//        val sp = PreferenceManager.getDefaultSharedPreferences(this)
//        val editor = sp.edit()
//        editor.getString("fuser",fuser)
//        editor.commit()
//    }


}




