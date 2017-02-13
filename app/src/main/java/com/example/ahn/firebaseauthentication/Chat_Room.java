package com.example.ahn.firebaseauthentication;

/**
 * Created by Ahn on 2017-02-09.
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Iterator;

import static com.example.ahn.firebaseauthentication.R.id.TView01;

public class Chat_Room extends AppCompatActivity {
    private Button btn_send_msg;
    private EditText input_msg;
    private TextView chat_conversation;
    private ScrollView SView01;

    private String user_name, room_name, temp_key;
    private DatabaseReference root;
    private boolean flag=true;

    ArrayAdapter adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_room);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);

        btn_send_msg = (Button) findViewById(R.id.btn01);
        input_msg = (EditText) findViewById(R.id.EText01);
        chat_conversation = (TextView) findViewById(TView01);
        SView01 = (ScrollView) findViewById(R.id.SView01);

        user_name = getIntent().getExtras().get("user_name").toString();
        room_name = getIntent().getExtras().get("room_name").toString();
        setTitle(" Room - " + room_name);


        root = FirebaseDatabase.getInstance().getReference().child(room_name);

        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference();

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
                //Map<String, Object> map = new HashMap<String, Object>();
                temp_key = root.push().getKey();
                //root.updateChildren(map);

                DatabaseReference message_root = root.child(temp_key);

                ChatData chatData = new ChatData(user_name, input_msg.getText().toString(), "testData");  // 유저 이름과 메세지로 chatData 만들기
                databaseReference.child(room_name).push().setValue(chatData);  // 기본 database 하위 message라는 child에 chatData를 list로 만들기
                input_msg.setText("");

                /*Map<String, Object> map2 = new HashMap<String, Object>();
                map2.put("name", user_name);
                map2.put("msg", input_msg.getText().toString());
                message_root.updateChildren(map2);*/
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

    private String chat_msg, chat_user_name, testData;

    private void append_chat_conversation(DataSnapshot dataSnapshot) {
        Iterator i = dataSnapshot.getChildren().iterator();

        while(i.hasNext()){
            chat_msg = (String) ((DataSnapshot)i.next()).getValue();
            testData = (String) ((DataSnapshot)i.next()).getValue();
            chat_user_name = (String) ((DataSnapshot)i.next()).getValue();

            if(getIntent().getExtras().get("user_name").toString().equals(chat_user_name)) {
                chat_conversation.append(chat_user_name + " (나) : " + chat_msg + "    " + testData + " \n");
                Toast.makeText(this, user_name, Toast.LENGTH_LONG).show();
            }
            else
                chat_conversation.append(chat_user_name + " : " +chat_msg+ "    " + testData +" \n" );
        }
    }
}