package com.example.ahn.firebaseauthentication;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Ahn on 2017-02-15.
 */

public class ChatAdapter extends BaseAdapter {
    private final String TAG = "";
    private Context context;
    private int layout;
    private ArrayList<ListviewItem> data;
    private LayoutInflater inflater;
    private int position;
    private String me;

    LinearLayout layoutStatus;
    TextView user, content;

    public ChatAdapter(Context context, int layout, ArrayList<ListviewItem> data, String me){
        this.context = context;
        this.layout = layout;
        this.data = data;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.me = me;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i).getChatContent();
    }

    @Override
    public long getItemId(int i) {
        return this.position = i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null){
                view = inflater.inflate(this.layout, viewGroup, false);
        }
        layoutStatus = (LinearLayout) view.findViewById(R.id.relative);
        user = (TextView) view.findViewById(R.id.TView01);
        content = (TextView) view.findViewById(R.id.TView02);

        /*
           현재 사용자의 채팅은 우측에
           상대의 채팅은 좌측에 띄우기 위해 조건문을 설정
        */
        if(this.me.equals(data.get(i).getName())) {  //현재 로그인한 사용자와 메세지 입력자의 아이디가 동일할 때

            layoutStatus.setGravity(Gravity.END);
            user.setBackground(null);
            user.setGravity(Gravity.BOTTOM);
            content.setBackgroundResource(R.drawable.me);

            user.setText(data.get(i).getName());
            content.setText(data.get(i).getChatContent());

            user.setTextColor(Color.parseColor("#2E2EFE"));
            content.setTextColor(Color.BLACK);
        }else{
            layoutStatus.setGravity(Gravity.START);

            content.setBackground(null);
            user.setGravity(Gravity.BOTTOM);
            user.setBackgroundResource(R.drawable.me1);

            user.setText(data.get(i).getChatContent());
            content.setText(data.get(i).getName());

            content.setTextColor(Color.parseColor("#2E2EFE"));
            user.setTextColor(Color.BLACK);
        }
        return view;
    }
}
