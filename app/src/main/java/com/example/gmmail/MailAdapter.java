package com.example.gmmail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public  class MailAdapter extends RecyclerView.Adapter<MailAdapter.MailViewHolder>
{
    private Context mctxt;
    private List<Mail> mdatalist;
    private custommailClick cmclick2;

    public MailAdapter(Context context,List<Mail> list,custommailClick click)
    {
        this.mctxt=context;
        this.mdatalist=list;
        this.cmclick2=click;
    }
    @NonNull
    @Override
    public MailAdapter.MailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(mctxt);
        View view=inflater.inflate(R.layout.mail,null);
        MailViewHolder holder1=new MailViewHolder(view,cmclick2);
        return holder1;
    }

    @Override
    public void onBindViewHolder(@NonNull MailAdapter.MailViewHolder holder, int position) {

        Mail mail=mdatalist.get(position);
        holder.name.setText(mail.name2);
        holder.snippet.setText(mail.snippet2);
        holder.subject.setText(mail.subject2);
        String fname=mail.name2.substring(0,1);
        try {
            int resID = mctxt.getResources().getIdentifier(fname.toLowerCase(), "drawable", mctxt.getPackageName());
            holder.icon.setImageResource(resID);
        }
        catch (Exception exc)
        {
            int resID = mctxt.getResources().getIdentifier("user", "drawable", mctxt.getPackageName());
            holder.icon.setImageResource(resID);
        }
    }

    @Override
    public int getItemCount() {
        return mdatalist.size();
    }

    public class MailViewHolder extends  RecyclerView.ViewHolder implements  View.OnClickListener {
        TextView name;
        TextView subject;
        TextView snippet;
        ImageView icon;
        custommailClick click;

        public MailViewHolder(@NonNull View itemView,custommailClick click) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            subject=itemView.findViewById(R.id.subject);
            snippet=itemView.findViewById(R.id.snippet);
            icon=itemView.findViewById(R.id.icon);
            this.click=click;
            name.setOnClickListener(this);
            subject.setOnClickListener(this);
            snippet.setOnClickListener(this);
            icon.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            click.oncustommailclick(getAdapterPosition());
        }
    }
    public  interface  custommailClick{
        void oncustommailclick(int position);
    }
}