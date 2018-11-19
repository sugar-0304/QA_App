package jp.techacademy.sugaru.takano.qa_app;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class FavoriteActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private Toolbar mToolbar;
    private int mGenre = 0;

    private DatabaseReference mDatabaseReference;
    private DatabaseReference mGenreRef;
    private ListView mListView;

    private FirebaseUser user;
    private DatabaseReference mFavGenreRef;
    private ArrayList<Favorite> mFavoriteArrayList;
    private  FavoriteListAdapter mFavoriteAdapter;


    private ChildEventListener favGenreEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            HashMap map = (HashMap) dataSnapshot.getValue();
            String title = (String) map.get("title");
            String body = (String) map.get("body");
            String name = (String) map.get("name");
            String imageString = (String) map.get("image");
            byte[] bytes;
            if (imageString != null) {
                bytes = Base64.decode(imageString, Base64.DEFAULT);
            } else {
                bytes = new byte[0];
            }
            String answerSize = (String)map.get("answerSize");

            Favorite favorite = new Favorite(title,body,name,bytes,answerSize);
            mFavoriteArrayList.add(favorite);
            mFavoriteAdapter.notifyDataSetChanged();
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
        setContentView(R.layout.activity_favorite);
        mToolbar = (Toolbar) findViewById(R.id.fav_toolbar);
        setSupportActionBar(mToolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fav_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        // ナビゲーションドロワーの設定
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.fav_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, mToolbar, R.string.app_name, R.string.app_name);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.fav_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Firebase
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        // ListViewの準備
        mListView = (ListView) findViewById(R.id.fav_listView);

        mFavoriteAdapter = new FavoriteListAdapter(this);
        mFavoriteArrayList = new ArrayList<Favorite>();
        mFavoriteAdapter.notifyDataSetChanged();


    }

    @Override
    protected void onResume(){
        super.onResume();

        //1:趣味を既定の選択とする
        if (mGenre == 0){
            NavigationView navigationView = (NavigationView)findViewById(R.id.fav_nav_view);
            onNavigationItemSelected(navigationView.getMenu().getItem(0));
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_favorite, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.fav_action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.fav_nav_hobby) {
            mToolbar.setTitle("趣味");
            mGenre = 1;
        } else if (id == R.id.fav_nav_life) {
            mToolbar.setTitle("生活");
            mGenre = 2;
        } else if (id == R.id.fav_nav_health) {
            mToolbar.setTitle("健康");
            mGenre = 3;
        } else if (id == R.id.fav_nav_compter) {
            mToolbar.setTitle("コンピューター");
            mGenre = 4;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.fav_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        mFavoriteArrayList.clear();
        mFavoriteAdapter.setFavoriteArrayList(mFavoriteArrayList);
        mListView.setAdapter(mFavoriteAdapter);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (mFavGenreRef != null){
            mFavGenreRef.removeEventListener(favGenreEventListener);
        }
        mFavGenreRef = mDatabaseReference.child(Const.UsersPATH).child(user.getUid()).child(Const.FavoritePATH).child(String.valueOf(mGenre));
        mFavGenreRef.addChildEventListener(favGenreEventListener);

        return true;
    }

}
