package com.example.gmmail;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static com.example.gmmail.MainActivity.ctxt;

public class Home extends Fragment implements MailAdapter.custommailClick {
    RecyclerView recyclerView;
    public static MailAdapter mdataAdapter;
    public static List<Mail> mailList;
    public static String Label;
    DataBaseHelper mydb;
    public Home(String lbl)
    {
        Label=lbl;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mailList=new ArrayList<>();
        mydb=new DataBaseHelper(ctxt);
        Cursor res=mydb.getMails(Label);
        while(res.moveToNext())
        {
            mailList.add(new Mail(res.getString(0),res.getString(1),res.getString(2),res.getString(3),res.getString(4),res.getString(5)));
        }

        View view= inflater.inflate(R.layout.fragment_home, container, false);        recyclerView=(RecyclerView)view.findViewById(R.id.recycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ctxt));
        mdataAdapter=new MailAdapter(ctxt,mailList,this);
        recyclerView.setAdapter(mdataAdapter);
        return view;
    }

    @Override
    public void oncustommailclick(int position) {
        Mail mail=mailList.get(position);
        Intent intent=new Intent(ctxt,ReadMaill.class);
        intent.putExtra("mid",mail.mid2);
        intent.putExtra("name",mail.name2);
        intent.putExtra("time",mail.time2);
        intent.putExtra("subject",mail.subject2);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }
}
