package jp.techacademy.takao.miyake.qa_app0817

import android.content.Intent
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

import com.google.firebase.database.*

import java.util.HashMap
import android.content.Context


class QuestionDetailListAdapter(context: Context, private val mQustion: Question) : BaseAdapter() {

    companion object {
        private val TYPE_QUESTION = 0
        private val TYPE_ANSWER = 1
    }

    private var mLayoutInflater: LayoutInflater? = null
    private lateinit var mAuth: FirebaseAuth

    init {
        mLayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
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

        var fuserquestRef: String? = null



        if (getItemViewType(position) == TYPE_QUESTION) {
            if (convertView == null) {
                convertView =
                    mLayoutInflater!!.inflate(R.layout.list_question_detail, parent, false)!!
            }
            val body = mQustion.body
            val name = mQustion.name

            val questionUid = mQustion.questionUid


            val user = FirebaseAuth.getInstance().currentUser

            if (user == null) {

               val context = parent.context
                val intent = Intent(context, LoginActivity::class.java)
                context.startActivity(intent)
                return convertView

            }

            val userIdentification = FirebaseAuth.getInstance().currentUser!!.uid
            var longinUser: String? = null

            //Firebaseからの読み込み用を設定
            val dataBaseReference = FirebaseDatabase.getInstance().reference

            //Firebaseへの書き込み用を設定
            longinUser = userIdentification

            val favorableUidAnswerRef =
                dataBaseReference.child(FavoritePATH).child(longinUser).child(questionUid)

            //中身が入っているかどうかをif文で判断して名称を入れる。
            val favorableIndicator = convertView.findViewById<View>(R.id.favorable_button) as Button

            favorableUidAnswerRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data = snapshot.value as Map<*, *>?
                    //val data = HashMap<String, String>()

                    if(data?.get("fuserquest") != null){
                        fuserquestRef = data["fuserquest"].toString()
                    } else if(data?.get("fuserquest")==null){
                        fuserquestRef = "お気に入り"
                        //data["fuserquest"]="お気に入り"
                    }

                    favorableIndicator.text = fuserquestRef
                }

                override fun onCancelled(firebaseError: DatabaseError) {}
            })

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

            favorableIndicator.setOnClickListener {

                Log.d("ANDROID", "クリックされた")
                Log.d("ANDROID","147 fuserquestRef = " + fuserquestRef)

                if (fuserquestRef == "お気に入り") {
                    favorableIndicator.text = "超お気に入り"
                    fuserquestRef = "超お気に入り"
                    data["fuserquest"] = "超お気に入り"
                    favorableUidAnswerRef.setValue(data)

                } else if (fuserquestRef == "超お気に入り") {
                    favorableIndicator.text = "お気に入り"
                    fuserquestRef = "お気に入り"
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

}







