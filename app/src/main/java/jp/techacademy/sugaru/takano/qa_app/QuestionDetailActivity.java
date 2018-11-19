package jp.techacademy.sugaru.takano.qa_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class QuestionDetailActivity extends AppCompatActivity {

    private ListView mListView;
    private Question mQuestion;
    private QuestionDetailListAdapter mAdapter;

    private DatabaseReference mAnswerRef;

    //-------------------------
    private DatabaseReference mFavQuestionRef;
    private boolean mfavoriteFlag = false;//falseはお気に入りされていない、trueはお気に入りされている

    private Favorite mFavorite;
    FavoriteListAdapter favAdapter;

    private ImageView favorite;



    /*public void addFavorite(String title, String body, String name, String uid, String questionUid, int genre, byte[] bytes, ArrayList<Answer> answers){
        Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();

        mFavorite = new Favorite();

        RealmResults<Favorite> favoriteRealmResults = realm.where(Favorite.class).findAll();

        int identifier;
        if (favoriteRealmResults.max("id") != null){
            identifier = favoriteRealmResults.max("id").intValue() + 1;
        }else {
            identifier = 0;
        }
        mFavorite.setId(identifier);
        mFavorite.setTitle(title);
        mFavorite.setBody(body);
        mFavorite.setName(name);
        mFavorite.setUid(uid);
        mFavorite.setQuestionUid(questionUid);
        mFavorite.setGenre(genre);
        mFavorite.setImageBytes(bytes);
        mFavorite.setAnswers(answers);

        realm.copyToRealmOrUpdate(mFavorite);
        realm.commitTransaction();

        realm.close();
    }*/



    private ChildEventListener mEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            HashMap map = (HashMap) dataSnapshot.getValue();

            String answerUid = dataSnapshot.getKey();

            for(Answer answer : mQuestion.getAnswers()) {
                // 同じAnswerUidのものが存在しているときは何もしない
                if (answerUid.equals(answer.getAnswerUid())) {
                    return;
                }
            }

            String body = (String) map.get("body");
            String name = (String) map.get("name");
            String uid = (String) map.get("uid");

            Answer answer = new Answer(body, name, uid, answerUid);
            mQuestion.getAnswers().add(answer);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };



    //--------------------------------------------------------------
    private ChildEventListener favQsEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            favorite.setImageResource(R.drawable.favorite_null);
            mfavoriteFlag = true;

            //String title, String body, String name, String uid, String questionUid, int genre, byte[] bytes, ArrayList<Answer> answers
            /*String title = mQuestion.getTitle();
            String body = mQuestion.getBody();
            String name = mQuestion.getName();
            String uid = mQuestion.getUid();
            String questionUid = mQuestion.getQuestionUid();
            int genre = mQuestion.getGenre();
            byte[] bytes = mQuestion.getImageBytes();
            ArrayList<Answer> answers = mQuestion.getAnswers();

            addFavorite(title,body,name,uid,questionUid,genre,bytes,answers);
            favAdapter.notifyDataSetChanged();*/

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);


        // 渡ってきたQuestionのオブジェクトを保持する
        Bundle extras = getIntent().getExtras();
        mQuestion = (Question) extras.get("question");

        setTitle(mQuestion.getTitle());

        // ListViewの準備
        mListView = (ListView) findViewById(R.id.listView);
        mAdapter = new QuestionDetailListAdapter(this, mQuestion);
        mListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ログイン済みのユーザーを取得する
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user == null) {
                    // ログインしていなければログイン画面に遷移させる
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                } else {
                    // Questionを渡して回答作成画面を起動する
                    Intent intent = new Intent(getApplicationContext(), AnswerSendActivity.class);
                    intent.putExtra("question", mQuestion);
                    startActivity(intent);
                }
            }
        });

        //-----------------------------------------------------
        DatabaseReference dataBaseReference = FirebaseDatabase.getInstance().getReference();
        favorite = (ImageView)findViewById(R.id.favoriteButton);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            favorite.setVisibility(View.GONE);
        }else {
            mFavQuestionRef = dataBaseReference.child(Const.UsersPATH).child(user.getUid()).child(Const.FavoritePATH).child(String.valueOf(mQuestion.getGenre())).child(mQuestion.getQuestionUid());
            mFavQuestionRef.addChildEventListener(favQsEventListener);
            favorite.setVisibility(View.VISIBLE);
        }

        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mfavoriteFlag) {
                    Map<String, String> data = new HashMap<String, String>();
                    data.put("title",mQuestion.getTitle());
                    data.put("body",mQuestion.getBody());
                    data.put("name",mQuestion.getName());
                    byte[] bytes = mQuestion.getImageBytes();
                    String bitmapString = Base64.encodeToString(bytes, Base64.DEFAULT);
                    data.put("image",bitmapString);
                    data.put("answerSize",String.valueOf(mQuestion.getAnswers().size()));
                    mFavQuestionRef.setValue(data);
                    favorite.setImageResource(R.drawable.favorite_null);
                    mfavoriteFlag = true;
                }else {
                    mFavQuestionRef.removeValue();
                    favorite.setImageResource(R.drawable.favorite);
                    mfavoriteFlag = false;
                }
            }
        });



        //DatabaseReference dataBaseReference = FirebaseDatabase.getInstance().getReference();
        mAnswerRef = dataBaseReference.child(Const.ContentsPATH).child(String.valueOf(mQuestion.getGenre())).child(mQuestion.getQuestionUid()).child(Const.AnswersPATH);
        mAnswerRef.addChildEventListener(mEventListener);

        //---------------------------------------


    }
}
