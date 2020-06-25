package com.example.gmmail;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static com.example.gmmail.MainActivity.ctxt;

public class Conversation extends AppCompatActivity implements ChatAdapter.conversationclick {
    RecyclerView recyclerView;
    DataBaseHelper mydb2;
    List<Chat> chatList;
    ChatAdapter chatAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        Intent intent=getIntent();
        chatList=new ArrayList<>();
        mydb2=new DataBaseHelper(this);
        Cursor res=mydb2.getChat(this,intent.getExtras().getString("contactId"));
        TextView uname=(TextView)findViewById(R.id.username);
        uname.setText(intent.getExtras().getString("contactName"));
        while(res.moveToNext())
        {
            chatList.add(new Chat(res.getString(0),res.getString(1),res.getString(2),res.getString(3),res.getString(4),uname.getText().toString()));
        }
        recyclerView=(RecyclerView)findViewById(R.id.convrecycle);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layout=new LinearLayoutManager(this);
        layout.setReverseLayout(true);
        recyclerView.setLayoutManager(layout);
        chatAdapter=new ChatAdapter(this,chatList,this);
        recyclerView.setAdapter(chatAdapter);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    @Override
    public void onconversationclick(int position) {
        Chat chat=chatList.get(position);
        Intent intent=new Intent(ctxt,ReadMaill.class);
        intent.putExtra("mid",chat.mid);
        intent.putExtra("name",chat.name);
        intent.putExtra("time",chat.time);
        intent.putExtra("subject",chat.subject);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }
    public  void goback(View view)
    {
        onBackPressed();
    }
}
