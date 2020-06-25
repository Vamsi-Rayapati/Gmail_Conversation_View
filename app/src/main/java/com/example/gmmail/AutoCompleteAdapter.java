package com.example.gmmail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.example.gmmail.MainActivity.ctxt;

public class AutoCompleteAdapter extends ArrayAdapter<Udata> {
    private  List<Udata> udataList;

    public AutoCompleteAdapter(Context context, List<Udata> udataList) {
        super(context, 0, udataList);
        this.udataList=new ArrayList<>(udataList);
    }
    @NonNull
    @Override
    public Filter getFilter() {
        return udataFilter;
    }

    private Filter udataFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<Udata> suggestions = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(udataList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Udata item : udataList) {
                    if (item.getEmail().toLowerCase().contains(filterPattern)) {
                        suggestions.add(item);
                    }
                }
            }
            results.values = suggestions;
            results.count = suggestions.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List) results.values);
            notifyDataSetChanged();
        }
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((Udata) resultValue).getName();
        }
    };
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.home_data, parent, false
            );
        }
        TextView textViewName = convertView.findViewById(R.id.udname);
        ImageView imageViewFlag = convertView.findViewById(R.id.icon);
        TextView Email =convertView.findViewById(R.id.snippet);
        Udata udata = getItem(position);
        if (udata != null) {
            String fname=udata.getName().substring(0,1);
            int resID;
            try {
                resID = ctxt.getResources().getIdentifier(fname.toLowerCase(), "drawable", ctxt.getPackageName());
            }
            catch (Exception exc)
            {
                resID = ctxt.getResources().getIdentifier("user", "drawable", ctxt.getPackageName());
            }
            textViewName.setText(udata.getName());
            imageViewFlag.setImageResource(resID);
            Email.setText(udata.getEmail());
        }
        return convertView;
    }


}
