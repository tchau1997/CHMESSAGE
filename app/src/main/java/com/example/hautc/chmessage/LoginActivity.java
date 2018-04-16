package com.example.hautc.chmessage;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText edt_User, edt_Password;
    TextView tv_SignUp;
    Button btn_login;

    final static int DRAWABLE_RIGHT = 2;
    boolean isShow = false;
    final static  int MAX_LENGTH = 6 ;

    FirebaseAuth firebaseAuth;
    //FirebaseDatabase database;
    DatabaseReference root;
    DatabaseReference currentUser;

    //final static String currentUserChildName = "/currentUser/";
    Dialog loading_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //setContentView(R.layout.friend_layout);

        onInit();
        LoginAndSignUp();
    }

    void onInit() {
        // Mapping control
        edt_User = (EditText) findViewById(R.id.edt_UserName_dlg);
        edt_Password = (EditText) findViewById(R.id.edt_Password_dlg);
        tv_SignUp = (TextView) findViewById(R.id.tv_SignUp);
        btn_login = (Button) findViewById(R.id.btn_Login);

        // Make text underline
        tv_SignUp.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        // Init firebase
        firebaseAuth = FirebaseAuth.getInstance();

        // Show passwrod
        edt_Password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    if(event.getRawX() >= (edt_Password.getRight()-edt_Password.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())){
                        if(!isShow){
                            edt_Password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            isShow = true;
                            edt_Password.setSelection(edt_Password.length());
                        }
                        else {
                            edt_Password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            isShow = false;
                            edt_Password.setSelection(edt_Password.length());
                        }
                    }
                }
                return false;
            }
        });

        // firebase Init
        root = FirebaseDatabase.getInstance().getReference();
        //currentUser = root.child(currentUserChildName);
    }

    void LoginAndSignUp(){
        // Login state
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get userEmail
                final String username = edt_User.getText().toString();
                if(username.isEmpty()){
                    edt_User.setError("Username can't be empty");
                    return;
                }

                String password = edt_Password.getText().toString();
                if(password.isEmpty()){
                    edt_Password.setError("Password can't be empty");
                    return;
                }

                startLoading();

                // Get password
                firebaseAuth.signInWithEmailAndPassword(username, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // Login success
                                if(task.isSuccessful()){
                                    Toast.makeText(LoginActivity.this, "Welcome, "+username, Toast.LENGTH_SHORT).show();
                                    // Call inbox intent here

                                    // set online status
                                    toDatabaseCurrentUser(new UserLogin(username.replace('.',',')));

                                    loading_dialog.dismiss();

                                    Intent intent = new Intent(LoginActivity.this, FriendActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                // Login fail
                                else {
                                    Toast.makeText(LoginActivity.this, "Sorry, Wrong password or account doesn't exist please sign up", Toast.LENGTH_SHORT).show();
                                    loading_dialog.dismiss();
                                }
                            }
                        });
            }
        });

        // Sign up
        tv_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create Sign Up dialog
                final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                LayoutInflater inflater = LoginActivity.this.getLayoutInflater();

                builder.setView(inflater.inflate(R.layout.dialog_sigup, null));

                // Create sign up button
                builder.setPositiveButton("Register", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Dialog dialog1 = (Dialog) dialog;

                        final EditText uName, passWord;
                        uName = (EditText) dialog1.findViewById(R.id.edt_UserName_dlg);
                        passWord = (EditText) dialog1.findViewById(R.id.edt_Password_dlg);

                        // If username is null return
                        if(uName.getText().toString().isEmpty()){
                            uName.setError("Please input something");
                            return;
                        }

                        // If password <= MAX_LENGTH return
                        if (passWord.getText().toString().length() < MAX_LENGTH){
                            passWord.setError("Password must be 6 character or more");
                            return;
                        }

                        startLoading();
                        firebaseAuth.createUserWithEmailAndPassword(uName.getText().toString(), passWord.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(LoginActivity.this, "Register success", Toast.LENGTH_SHORT).show();
                                            // Call intent to Inbox
                                            String userName = uName.getText().toString();
                                            userName = userName.replace('.',',');
                                            toDatabaseCurrentUser(new UserLogin(userName));

                                            loading_dialog.dismiss();

                                            Intent intent = new Intent(LoginActivity.this, FriendActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                        else {
                                            Toast.makeText(LoginActivity.this, "Account already exits", Toast.LENGTH_SHORT).show();
                                            loading_dialog.dismiss();
                                        }
                                    }
                                });

                        dialog1.dismiss();
                    }
                });

                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    boolean checkInternet(){
        return true;
    }

    // Set online status to true
    private void toDatabaseCurrentUser(UserLogin newUser){
        Map<String, Object> map = newUser.toMap(DatabasePath.currentUserPath);
        root.updateChildren(map);
    }

    void startLoading(){
        loading_dialog = new Dialog(this);
        loading_dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        LinearLayout contentView = (LinearLayout) ((Activity) this)
                .getLayoutInflater().inflate(R.layout.loading_layout, null);
        loading_dialog.setContentView(contentView);

        ImageView image = (ImageView) contentView.findViewById(R.id.loading_image);
        image.setBackgroundResource(R.drawable.loading_animation);
        final AnimationDrawable animationDrawable = (AnimationDrawable) image.getBackground(); // gắn vào animationDrawable

        loading_dialog.setCancelable(false);

        loading_dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                animationDrawable.start(); // bắt đầu chạy animation
            }
        });
        loading_dialog.show();
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        loading_dialog.dismiss();
//    }
}
