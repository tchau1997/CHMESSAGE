package com.example.hautc.chmessage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

public class AddFriendActivity extends AppCompatActivity {

    EditText inputEmail;
    ActionBar actionBar;

    static final int ADDFRIEND_RESULTCODE = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        inputEmail = (EditText) findViewById(R.id.add_friend_fnameinput);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Add your friend");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_check_black_24dp);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent = new Intent(this, FriendActivity.class);
        intent.putExtra("friendEmail", inputEmail.getText().toString().replace('.',','));
        setResult(ADDFRIEND_RESULTCODE ,intent);
        finish();
    }
}
