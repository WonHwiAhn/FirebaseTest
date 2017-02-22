package com.example.ahn.firebaseauthentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Ahn on 2017-02-01.
 */

        public class DashBoard extends AppCompatActivity {

            private TextView txtWelcome;
            private EditText input_new_password;
            private Button btnChangePass, btnLogout;
            private RelativeLayout activity_dashboard;

            private FirebaseAuth auth;

/**수정한 부분**/
            private Button add_room;
            private EditText room_name;
            private String name, intro;

            private ListView listView;
            private ArrayAdapter<String> arrayAdapter;
            private ArrayList<String> list_of_rooms = new ArrayList<>();
            private DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("chatroom");
/*******************************************************/

            protected void onCreate(Bundle savedInstanceState){
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_dash_board);

                getWindow().getAttributes().width = WindowManager.LayoutParams.MATCH_PARENT;

                getWindow().getAttributes().height = WindowManager.LayoutParams.MATCH_PARENT;

                txtWelcome = (TextView) findViewById(R.id.dashboard_welcome);
                //input_new_password = (EditText) findViewById(R.id.dashboard_new_password);
                //btnChangePass = (Button) findViewById(R.id.dashboard_btn_change_pass);
                btnLogout = (Button) findViewById(R.id.dashboard_btn_logout);
                activity_dashboard = (RelativeLayout) findViewById(R.id.activity_dash_board);

                //btnChangePass.setOnClickListener(listener);
                btnLogout.setOnClickListener(listener);

                auth = FirebaseAuth.getInstance();


                /****************************수정한 부분*********************************/
                add_room = (Button) findViewById(R.id.btn01);
                room_name = (EditText) findViewById(R.id.EText01);
                listView = (ListView) findViewById(R.id.LView01);
                arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list_of_rooms);

        listView.setAdapter(arrayAdapter);

        //request_user_name();

        name = auth.getCurrentUser().getEmail();

        add_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put(room_name.getText().toString(), "");
                root.updateChildren(map);
            }
        });

        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Set<String> set = new HashSet<String>();
                Iterator i = dataSnapshot.getChildren().iterator();

                while(i.hasNext()){
                    set.add(((DataSnapshot)i.next()).getKey());
                }
                list_of_rooms.clear();
                list_of_rooms.addAll(set);

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), Chat_Room.class);
                intent.putExtra("room_name", ((TextView)view).getText().toString());
                intent.putExtra("user_name", name);
                intent.putExtra("intro", name+"님이 입장하셨습니다.");
                startActivity(intent);
            }
        });
                /**************************************************************************************/

        if(auth.getCurrentUser() != null)
            txtWelcome.setText("Welcome , " + auth.getCurrentUser().getEmail());


    }


    Button.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //if(view.getId() == R.id.dashboard_btn_change_pass){
               // changePassword(input_new_password.getText().toString());
           // }else if(view.getId() == R.id.dashboard_btn_logout){
            if(view.getId() == R.id.dashboard_btn_logout){
                logoutUser();
            }
        }
    };

    /* 수정시작

    private void request_user_name(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter name: ");

        final EditText input_field = new EditText(this);

        builder.setView(input_field);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                name = input_field.getText().toString();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                request_user_name();
            }
        });

        builder.show();
    }
     */
    private void logoutUser(){
        auth.signOut();
        if(auth.getCurrentUser() == null){
            startActivity(new Intent(DashBoard.this, MainActivity.class));
        }
    }
/*
    private void changePassword(String newPassword){
        FirebaseUser user = auth.getCurrentUser();
        user.updatePassword(newPassword).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Snackbar snackbar = Snackbar.make(activity_dashboard, "Password changed", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }
        });
    }*/
}
