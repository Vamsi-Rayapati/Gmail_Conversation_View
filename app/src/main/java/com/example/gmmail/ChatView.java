package com.example.gmmail;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import static com.example.gmmail.MainActivity.ctxt;
import static com.example.gmmail.MainActivity.udataList;



public class ChatView extends Fragment implements UdataAdapter.customClick{

    RecyclerView recyclerView;
    public static UdataAdapter udataAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_chat_view, container, false);
        recyclerView=(RecyclerView)view.findViewById(R.id.recycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ctxt));
        udataAdapter=new UdataAdapter(ctxt,udataList,this);
        recyclerView.setAdapter(udataAdapter);
        return view;

    }
    public void oncustomclick(int position) {
        Udata data=udataList.get(position);
        Intent intent=new Intent(ctxt,Conversation.class);
        intent.putExtra("contactId",data.getEmail());
        intent.putExtra("contactName",data.getName());
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

}
