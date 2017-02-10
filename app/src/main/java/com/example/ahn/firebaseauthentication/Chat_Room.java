package com.example.ahn.firebaseauthentication;

/**
 * Created by Ahn on 2017-02-09.
 */
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.example.ahn.firebaseauthentication.R.id.TView01;

/**
 * Created by Ahn on 2017-02-08.
 */

public class Chat_Room extends AppCompatActivity {
    private Button btn_send_msg;
    private EditText input_msg;
    private TextView chat_conversation;
    private ScrollView SView01;

    private String user_name, room_name, temp_key;
    private DatabaseReference root;
    private boolean flag=true;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_room);

        btn_send_msg = (Button) findViewById(R.id.btn01);
        input_msg = (EditText) findViewById(R.id.EText01);
        chat_conversation = (TextView) findViewById(TView01);
        SView01 = (ScrollView) findViewById(R.id.SView01);

        user_name = getIntent().getExtras().get("user_name").toString();
        room_name = getIntent().getExtras().get("room_name").toString();
        setTitle(" Room - " + room_name);


        root = FirebaseDatabase.getInstance().getReference().child(room_name);
        
        // 스크롤 아래 포커스 주는 법1
        SView01.post(new Runnable() {
            @Override
            public void run() {
                SView01.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

        /*SView01.post(new Runnable(){
            public void run() {
                SView01.scrollTo(0, 200);
            }
        });*/

        btn_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> map = new HashMap<String, Object>();
                temp_key = root.push().getKey();
                root.updateChildren(map);

                DatabaseReference message_root = root.child(temp_key);

                Map<String, Object> map2 = new HashMap<String, Object>();
                map2.put("name", user_name);
                map2.put("msg", input_msg.getText().toString());
                message_root.updateChildren(map2);
            }
        });

        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                append_chat_conversation(dataSnapshot);
                if(flag){
                    chat_conversation.append(getIntent().getExtras().get("intro").toString()+"\n");
                    flag=false;
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                append_chat_conversation(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                append_chat_conversation(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                append_chat_conversation(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                getIntent().getExtras().get("user_name").toString();
            }
        });
    }

    private String chat_msg, chat_user_name;

    private void append_chat_conversation(DataSnapshot dataSnapshot){
        Iterator i = dataSnapshot.getChildren().iterator();

        while(i.hasNext()){
            chat_msg = (String) ((DataSnapshot)i.next()).getValue();
            chat_user_name = (String) ((DataSnapshot)i.next()).getValue();

            if(getIntent().getExtras().get("user_name").toString().equals(chat_user_name))
                chat_conversation.append(chat_user_name + " (나) : " + chat_msg + " \n");
            else
                chat_conversation.append(chat_user_name + " : " +chat_msg+" \n" );


        }
    }
}