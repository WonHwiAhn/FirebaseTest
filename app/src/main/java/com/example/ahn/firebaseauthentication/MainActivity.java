package com.example.ahn.firebaseauthentication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    Button btnLogin;
    EditText input_email, input_password;
    TextView btnSignup, btnForgotPass;

    RelativeLayout activity_main;

    private FirebaseAuth auth;

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
                Toast.makeText(getApplicationContext(), "asdasd", Toast.LENGTH_LONG).show();
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
                            startActivity(new Intent(MainActivity.this, DashBoard.class));
                        }
                    }
                });
    }

}
