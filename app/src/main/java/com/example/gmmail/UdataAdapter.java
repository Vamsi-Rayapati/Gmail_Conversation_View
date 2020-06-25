package com.example.gmmail;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class UdataAdapter extends RecyclerView.Adapter<UdataAdapter.UDataViewHolder> {
    private Context uctxt;
    private List<Udata> udatalist;
    private customClick ccclick2;
    public  UdataAdapter(Context ctxt,List<Udata> list,customClick ccclick2)
    {
        this.uctxt=ctxt;
        this.udatalist=list;
        this.ccclick2=ccclick2;
    }
    @NonNull
    @Override
    public UDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(uctxt);
        View view=inflater.inflate(R.layout.home_data,null);
        UDataViewHolder holder1=new UDataViewHolder(view,ccclick2);
        return holder1;
    }

    @Override
    public void onBindViewHolder(@NonNull UDataViewHolder holder, int position) {
        Udata udata=udatalist.get(position);
        holder.uname.setText(udata.getName());
        holder.usnippet.setText(udata.getSnippet());
        holder.umail.setText(udata.getEmail());
        String fname=udata.getName().substring(0,1);
        try {
            int resID = uctxt.getResources().getIdentifier(fname.toLowerCase(), "drawable", uctxt.getPackageName());
            holder.uimage.setImageResource(resID);
        }
        catch (Exception exc)
        {
            int resID = uctxt.getResources().getIdentifier("user", "drawable", uctxt.getPackageName());
            holder.uimage.setImageResource(resID);
        }


    }
    @Override
    public int getItemCount() {
        return udatalist.size();
    }

    class UDataViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView uname;
        TextView usnippet;
        TextView umail;
        customClick cclick;
        ImageView uimage;

        public UDataViewHolder(@NonNull View itemView,customClick cclick) {
            super(itemView);
            uname=itemView.findViewById(R.id.udname);
            usnippet=itemView.findViewById(R.id.snippet);
            umail=itemView.findViewById(R.id.mailid);
            uimage=itemView.findViewById(R.id.icon);
            this.cclick=cclick;
            umail.setOnClickListener(this);
            uname.setOnClickListener(this);
            usnippet.setOnClickListener(this);
            uimage.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            cclick.oncustomclick(getAdapterPosition());
        }
    }
    public  interface  customClick{
        void oncustomclick(int position);
    }

}
