package com.example.gmmail;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import android.widget.Space;
import androidx.fragment.app.FragmentManager;

import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

import static com.example.gmmail.MainActivity.ctxt;
import static com.example.gmmail.MainActivity.manager;
import static com.example.gmmail.MainActivity.navView;

public class Label extends DialogFragment implements View.OnClickListener {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogStyle);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_label, container);
        ((CardView) view.findViewById(R.id.space1)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // When button is clicked, call up to owning activity.
                dismiss();
            }
        });
        ((Button)view.findViewById(R.id.inbox)).setOnClickListener(this);
        ((Button)view.findViewById(R.id.unread)).setOnClickListener(this);
        ((Button)view.findViewById(R.id.sent)).setOnClickListener(this);

        ((Button)view.findViewById(R.id.spam)).setOnClickListener(this);
        ((Button)view.findViewById(R.id.draft)).setOnClickListener(this);
        ((Button)view.findViewById(R.id.allmails)).setOnClickListener(this);

        ((Button)view.findViewById(R.id.important)).setOnClickListener(this);
        ((Button)view.findViewById(R.id.starred)).setOnClickListener(this);
        ((Button)view.findViewById(R.id.trash)).setOnClickListener(this);

        return  view;
    }
    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.dimAmount=0.0f;
        window.setAttributes(lp);
        window.setGravity(Gravity.BOTTOM);
        window.getAttributes().windowAnimations = R.style.DialogStyleAnim;

    }


    @Override
    public void onClick(View v) {
        navView.getMenu().findItem(R.id.home).setChecked(true);
        Button selected=(Button)v;
        String Label=selected.getText().toString();
        dismiss();
        Label=Label.substring(0,1).toUpperCase()+Label.substring(1,Label.length()).toLowerCase();
        navView.getMenu().findItem(R.id.home).setTitle(Label);
        manager.beginTransaction().replace(R.id.fragment_container,new Home(Label.toUpperCase())).commit();
    }
}
