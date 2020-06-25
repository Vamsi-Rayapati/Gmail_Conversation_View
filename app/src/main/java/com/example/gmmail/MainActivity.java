package com.example.gmmail;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static Intent intent;
    public static Context ctxt;
    public static SharedPreferences sharedPrefs;
    public static List<Udata> udataList=new ArrayList<>();
    public static DataBaseHelper mydb;
    public static ImageView fab;
    public static  BottomNavigationView navView;
    public  static FragmentManager manager;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener()
    {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item)
        {
            Fragment selectedFragment=null;
            switch (item.getItemId())
            {
                case R.id.chat:
                    selectedFragment=new ChatView();
                    break;
                case R.id.home:
                    selectedFragment=new Home(item.getTitle().toString().toUpperCase());
                    break;
                case R.id.feedback:
                    selectedFragment=new FeedBack();
                    break;
            }
            manager.beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctxt=this;
        sharedPrefs = this.getSharedPreferences("GMmaild", 0);
        if(sharedPrefs.contains("gmid"))
        {
            setContentView(R.layout.activity_main);
            fab=(ImageView) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fm = getSupportFragmentManager();
                    Label fragmentl = new Label();
                    fragmentl.show(fm, "fragment_label");
                }
            });
            ((FloatingActionButton)findViewById(R.id.sendmail)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent send=new Intent(ctxt,NewMail.class);
                    startActivity(send);
                }
            });

            navView = findViewById(R.id.nav_view);
            navView.setElevation(0);
            navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
            navView.getMenu().findItem(R.id.home).setChecked(true);
            manager=getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.fragment_container,new Home("INBOX")).commit();
            AsyncTask<Void,String,Void> loadchat=new AsyncTask<Void,String, Void>() {

                @Override
                protected Void doInBackground(Void... voids) {

                    mydb=new DataBaseHelper(ctxt);
                    Cursor res=mydb.getContacts(ctxt);
                    while(res.moveToNext())
                    {
                        Cursor res2=mydb.getName(ctxt,res.getString(0));
                        res2.moveToNext();
                        udataList.add(new Udata(res2.getString(0),res2.getString(1),res.getString(0)));
                    }
                    return null;
                }
            };
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) // Above Api Level 13
            {
                loadchat.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            else
            {
                loadchat.execute();
            }
        }
        else {
            intent = new Intent(this, Welcome.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            overridePendingTransition(0,0);
        }
    }

}
