package com.example.gmmail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private Context context;
    private List<Chat> chatList;
    private conversationclick click;

    public ChatAdapter(Context context, List<Chat> chatList,conversationclick click) {
        this.context = context;
        this.chatList = chatList;
        this.click=click;
    }


    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=view=inflater.inflate(R.layout.left,parent,false);
        if(viewType==1)
        {
            view=inflater.inflate(R.layout.right,parent,false);
        }

        ChatViewHolder holder1=new ChatViewHolder(view,click);
        return holder1;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {

        Chat chat=chatList.get(position);
        holder.subject.setText(chat.subject);
        holder.snippet.setText(chat.snippet);
        holder.time.setText(chat.time);

    }

    @Override
    public int getItemViewType(int position) {
        Chat chat=chatList.get(position);
        if(chat.pos.equals("0"))
        {
            return 0;
        }
        else
        {
            return 1;
        }


    }



    @Override
    public int getItemCount() {
        return chatList.size();
    }


    public class ChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView subject;
        TextView snippet;
        TextView time;
        conversationclick click2;
        public ChatViewHolder(@NonNull View itemView,conversationclick click) {
            super(itemView);
            subject=itemView.findViewById(R.id.subject);
            snippet=itemView.findViewById(R.id.snippet);
            time=itemView.findViewById(R.id.time);
            this.click2=click;
            itemView.setOnClickListener(this);
            subject.setOnClickListener(this);
            snippet.setOnClickListener(this);
            time.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            click2.onconversationclick(getAdapterPosition());
        }
    }
    public  interface  conversationclick{
        void onconversationclick(int position);
    }
}
