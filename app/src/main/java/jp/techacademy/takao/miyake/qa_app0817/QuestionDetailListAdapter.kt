package jp.techacademy.takao.miyake.qa_app0817

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.util.Log
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

import com.google.firebase.auth.AuthResult

import java.util.HashMap


class QuestionDetailListAdapter(context: Context, private val mQustion: Question) : BaseAdapter() {
    companion object {
        private val TYPE_QUESTION = 0
        private val TYPE_ANSWER = 1
    }
    private var mGenre: Int = 0
    private var mLayoutInflater: LayoutInflater? = null

    private var fuserid: Int = 0
    private var fuseridQuestionid : Int = 0
    private var mQuestion: Question? = null

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDataBaseReference: DatabaseReference

    private lateinit var answerUid: String


    private lateinit var mAnswerRef: DatabaseReference

    private var dataSnapshot: DataSnapshot? = null

    init {
        mLayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
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

        if (getItemViewType(position) == TYPE_QUESTION) {
            if (convertView == null) {
                convertView = mLayoutInflater!!.inflate(R.layout.list_question_detail, parent, false)!!
            }
            val body = mQustion.body
            val name = mQustion.name

            val questionUid =mQustion.questionUid


            val userIdentification = FirebaseAuth.getInstance().currentUser!!.uid

 //           var mAnswer: Answer? = null

            var answer_name : String? = null

            //Firebaseからの読み込み用を設定
            val dataBaseReference = FirebaseDatabase.getInstance().reference


            //Firebaseへの書き込み用を設定
            answer_name = userIdentification

            val favorableUidAnswerRef = dataBaseReference.child(FavoritePATH).child(answer_name)

            val bodyTextView = convertView.findViewById<View>(R.id.bodyTextView) as TextView
            bodyTextView.text = body

            val nameTextView = convertView.findViewById<View>(R.id.nameTextView) as TextView
            nameTextView.text = name

            val bytes = mQustion.imageBytes
            if (bytes.isNotEmpty()) {
                val image = BitmapFactory.decodeByteArray(bytes, 0, bytes.size).copy(Bitmap.Config.ARGB_8888, true)
                val imageView = convertView.findViewById<View>(R.id.imageView) as ImageView
                imageView.setImageBitmap(image)
            }
            val data = HashMap<String, String>()

            val favorableIndicator = convertView.findViewById<View>(R.id.favorable_button) as Button //val favorableValue : String= qestion.favorable


            //Firebaseから読み込んだユーザごとのお気に入り情報を読み込む
            favorableIndicator.text = favorableUidAnswerRef.toString()

            //Firebaseから読みこもうとしてもデータがない（新規）の場合のIf文を作成する。
            if( favorableIndicator.text == "お気に入りちゃう" ){
                favorableIndicator.text="お気に入り"
            }else{
                favorableIndicator.text="超お気に入り"

            }


            favorableIndicator.setOnClickListener {

                Log.d("ANDROID","お気に入りボタンをクリックされた")


                if (favorableIndicator.text=="お気に入り") {
                    favorableIndicator.text="超お気に入り"


                        data["fuserquest"] = "お気に入りだぜ"
                        //Log.d("ANDROID","149 answername = " + answer_name)


                        data["fuserquestUid"] = questionUid
                        data["fuser"] = answer_name

                    Log.d("ANDROID","147 questionUid = " + favorableUidAnswerRef.toString())
                    Log.d("ANDROID","148 answer_name = " + answer_name)
                    favorableUidAnswerRef.push().setValue(data)


                } else {
                    favorableIndicator.text="お気に入り"

                    data["fuserquest"] = "お気に入りちゃう"


                        data["fuserquestUid"] = questionUid
                        data["fuser"] = answer_name

                    Log.d("ANDROID","161 questionUid = " + favorableUidAnswerRef.toString())
                    Log.d("ANDROID","162 answer_name = " + answer_name)
                    favorableUidAnswerRef.push().setValue(data)

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
}