package com.example.gmmail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import static android.widget.ListPopupWindow.MATCH_PARENT;
import static android.widget.ListPopupWindow.WRAP_CONTENT;
import static com.example.gmmail.NewMail.arrayOfUsers;
import static com.example.gmmail.NewMail.flow;


public class UserAdapter extends ArrayAdapter<User> {

    public UserAdapter(Context context, ArrayList<User> users) {
        super(context, 0, users);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        User user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_selected_mail, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.name);
        TextView tvHome = (TextView) convertView.findViewById(R.id.mailid);
        ImageView imageView=(ImageView)convertView.findViewById(R.id.imageView2);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayOfUsers.remove(position);
                notifyDataSetChanged();
                if(arrayOfUsers.size()==3)
                {
                    flow.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT,WRAP_CONTENT));
                }

            }
        });
        // Populate the data into the template view using the data object
        tvName.setText(user.name);
        tvHome.setText(user.mailid);
        // Return the completed view to render on screen
        return convertView;
    }
}
