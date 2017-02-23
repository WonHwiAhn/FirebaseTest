package com.example.ahn.firebaseauthentication;

/**
 * Created by Ahn on 2017-02-09.
 */

import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Iterator;

public class Chat_Room extends AppCompatActivity {
    private String chat_msg, chat_user_name, testData;  //사용자메세지, 메세지 입력한 ID, 필요없는값
    private Button btn_send_msg;
    private EditText input_msg;
    private ListView LView01;

    private ClipboardManager clipboard;  //클립보드 복사에 활용

    private String user_name, room_name, temp_key;
    private DatabaseReference root;  //디비에 활용하는 변수

    private String[] myId;  //사용자 아이디가 메일이기 때문에 @앞에꺼만 넣기 위한 변수

    ChatAdapter adapter1;  //리스트뷰에 들어갈 어댑터
    ArrayList<ListviewItem> data = new ArrayList<>();  //리스트뷰에 다양한 아이템을 넣기 위한 arraylist

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_room);

        clipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);  //클릭보드 사용

        myId = getIntent().getExtras().get("user_name").toString().split("@"); //@ 앞에 아이디만 삽입
        user_name = getIntent().getExtras().get("user_name").toString(); //이메일 통째로 삽입
        room_name = getIntent().getExtras().get("room_name").toString();  //현재 방이름 삽입

        root = FirebaseDatabase.getInstance().getReference().child("chatroom").child(room_name); //디비 루트 설정 (채팅 방 밑 노드)

        setTitle(" Room - " + room_name);

        btn_send_msg = (Button) findViewById(R.id.btn01);
        input_msg = (EditText) findViewById(R.id.EText01);
        LView01 = (ListView) findViewById(R.id.LView01);

        /****************채팅 전송버튼 이벤트관리하는 부분************************/
        btn_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {  //채팅말쓰기 버튼, 디비에 데이터 삽입
                ChatData chatData = new ChatData(user_name, input_msg.getText().toString(), "testData");  // 유저 이름과 메세지로 chatData 만들기
                root.push().setValue(chatData);  // 기본 database 하위 message라는 child에 chatData를 list로 만들기
                input_msg.setText(""); //현재 채팅치는 곳 초기화
            }
        });
        /*************************************************************************/

        /****************디비에 값이 추가 됬을때 관리하는 부분********************/
        root.addChildEventListener(new ChildEventListener() {  //디비에 무언가가 추가됬을경우에
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                append_chat_conversation(dataSnapshot);
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
        /**********************************************************************/

        /***********************리스트뷰에서 아이템을 길게 눌렀을 때 이벤트 관리****************************/
        LView01.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                Toast.makeText(getApplicationContext(), adapter1.getItem(i).toString(), Toast.LENGTH_LONG).show();

                /**********아이템을 길게 눌렀을 때 팝업뜨게 설정******************/
                new AlertDialog.Builder(Chat_Room.this)
                        .setItems(R.array.clipboard_items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                switch(which){
                                    case 0:
                                        String string = adapter1.getItem(i).toString(); //클릭한 아이템의 컨텐츠 내용
                                        if(string != null)
                                            clipboard.setText(string); //클릭보드에 복사할 내용 삽입
                                        break;

                                    case 1:
                                        clipboard.getText(); //클립보드에 복사된 내용 가져오기
                                        break;
                                }
                            }
                        }).show();
                /****************************************************************/
                return false;
            }
        });

        adapter1 = new ChatAdapter(this, R.layout.chat_custom, data, myId[0]); //채팅 데이터 넣는곳 (나)일경우
        LView01.setAdapter(adapter1);
    }
    /******************************************************************************************************/

    public void append_chat_conversation(DataSnapshot dataSnapshot) {  //DataSnapshot 데이터 값 다루는 곳
        Iterator i = dataSnapshot.getChildren().iterator();

        while(i.hasNext()){
            chat_msg = (String) ((DataSnapshot)i.next()).getValue();  //사용자가 입력한 채팅메세지
            testData = (String) ((DataSnapshot)i.next()).getValue();  //그냥 데이터 추가해보려고 넣어놓은 값
            chat_user_name = (String) ((DataSnapshot)i.next()).getValue();  //사용자 아이디

            String[] chat = new String[100];

            /******** 입력하는 메세지가 너무 길 경우 잘라주는 곳 *********/
            int count = 0;
            for(int j=0; j<chat_msg.length(); j+=15){ //15글자를 넘을 경우 문장을 잘라줌
                if(chat_msg.length()>j+15) {
                    chat[count] = chat_msg.substring(j, j + 15); //15단위로 배열에 넣어줌
                    chat[count] += "\n";
                    count++;
                }else if(count >= 1){ //15씩 짤리기 때문에 18글자인 경우 3글자가 버려지는 현상을 방지
                    chat[count] = chat_msg.substring(j, chat_msg.length());
                    chat_msg = ""; //메세지 초기화. 밑에서 다시 합쳐주기 때문에 여기서 초기화해준다.
                }
            }
            /*************************************************************/

            /******** 위에서 메세지 자른 것을 다시 합치는 부분 ***********/
            int count1 = 0;
            while(chat[count1] != null){
                chat_msg += chat[count1];  //15글자씩 나눴던 배열을 다시 합침
                count1++;
            }
            /*************************************************************/

            /*********현재 유저의 id값 생성하는 곳**********/
            String[] userId = chat_user_name.split("@");
            /***********************************************/

            ListviewItem listViewitem = new ListviewItem(userId[0], chat_msg); //리스트뷰에 삽입할 아이템 생성
            data.add(listViewitem); //생성한 아이템을 data에 삽입
            adapter1.notifyDataSetChanged(); //adapter1이 변했는지 안변했는지 체크 해줌
            LView01.setSelection(adapter1.getCount() - 1);  //글을 쓸 때마다 리스트 뷰의 가장 하단으로 포커스를 맞춰줌
        }
    }
}
