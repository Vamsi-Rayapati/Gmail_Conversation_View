package com.example.gmmail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.gmmail.NewMail.arrayOfFiles;

public class FileAdapter extends ArrayAdapter<MFile> {
    public FileAdapter(Context context, ArrayList<MFile> files) {
        super(context, 0, files);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        MFile file = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.selectedfile, parent, false);
        }
        // Lookup view for data population
        TextView fileName = (TextView) convertView.findViewById(R.id.filename);
        ImageView close=(ImageView)convertView.findViewById(R.id.imageView2);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayOfFiles.remove(position);
                notifyDataSetChanged();
            }
        });
        fileName.setText(file.fileName);
        return convertView;
    }

}
