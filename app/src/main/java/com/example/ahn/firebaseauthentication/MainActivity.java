package com.example.ahn.firebaseauthentication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    Button btnLogin;
    EditText input_email, input_password;
    TextView btnSignup, btnForgotPass;

    RelativeLayout activity_main;
    String user = "user";

    private FirebaseAuth auth;

    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("user");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().getAttributes().width = WindowManager.LayoutParams.MATCH_PARENT;

        getWindow().getAttributes().height = WindowManager.LayoutParams.MATCH_PARENT;

        //View
        btnLogin = (Button) findViewById(R.id.login_btn_login);
        input_email = (EditText) findViewById(R.id.login_email);
        input_password = (EditText) findViewById(R.id.login_password);
        btnSignup = (TextView) findViewById(R.id.login_btn_signup);
        btnForgotPass = (TextView) findViewById(R.id.login_btn_forgot_password);
        activity_main = (RelativeLayout) findViewById(R.id.activity_main);

        btnSignup.setOnClickListener(listener);
        btnForgotPass.setOnClickListener(listener);
        btnLogin.setOnClickListener(listener);

        input_password.post(new Runnable() {
            @Override
            public void run() {
                input_password.setFocusableInTouchMode(true);
                input_password.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(input_email, 0);
            }
        });

        //init Firebase Auth
        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null)
            startActivity(new Intent(MainActivity.this, DashBoard.class));
    }

    Button.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view){
            if(view.getId() == R.id.login_btn_forgot_password){
                startActivity(new Intent(MainActivity.this, ForgotPassword.class));
                finish();
            }else if(view.getId() == R.id.login_btn_signup){
                startActivity(new Intent(MainActivity.this, SignUp.class));
                finish();
            }else if(view.getId() == R.id.login_btn_login){
                loginUser(input_email.getText().toString(), input_password.getText().toString());
            }
        }
    };

    /*public void onClick(View view){
        if(view.getId() == R.id.login_btn_forgot_password){
            startActivity(new Intent(MainActivity.this, ForgotPassword.class));
            finish();
        }else if(view.getId() == R.id.login_btn_signup){
            startActivity(new Intent(MainActivity.this, SignUp.class));
            finish();
        }else if(view.getId() == R.id.login_btn_login){
            loginUser(input_email.getText().toString(), input_password.getText().toString());
        }
    }*/

    private void loginUser(String email, final String password){
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            if(password.length() < 6){
                                Snackbar snackBar = Snackbar.make(activity_main, "Password length must be over 6", Snackbar.LENGTH_SHORT);
                                snackBar.show();
                            }
                        }else{
                            Map<String, Object> user = new HashMap<String, Object>();
                            if(user.containsValue(input_email.getText().toString())){
                                //아이디 중복 체크
                                Snackbar snackBar = Snackbar.make(activity_main, "아이디가 이미 로그인되있습니다.", Snackbar.LENGTH_SHORT);
                                snackBar.show();
                            }else {
                                user.put(GetDevicesUUID(getApplicationContext()), input_email.getText().toString());
                                //user.remove(GetDevicesUUID(getApplicationContext()));  삭제할 때 맵을 이용해서 삭제
                                root.updateChildren(user);
                                Snackbar snackBar = Snackbar.make(activity_main, "로그인 성공!", Snackbar.LENGTH_SHORT);
                                snackBar.show();
                                startActivity(new Intent(MainActivity.this, DashBoard.class));
                            }
                        }
                    }
                });
    }
    private String GetDevicesUUID(Context mContext){
        final TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();
        return deviceId;
    }
}
