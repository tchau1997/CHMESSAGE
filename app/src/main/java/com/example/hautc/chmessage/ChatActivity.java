package com.example.hautc.chmessage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {

    DatabaseReference messagePath;
    String Receiver;
    String key;
    String Sender;

    ListView messageDisplay;
    EditText chatMessage;
    ImageButton btn_send;

    FirebaseListAdapter<MessageInfo> infoFirebaseListAdapter;

    ActionBar actionBar;

    String lastMess;
    String lastDate;

    TextView dateTime, messageContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        init();
        sendMessage();
    }

    void init(){
        Intent intent = getIntent();
        key = intent.getStringExtra("key");
        Receiver = intent.getStringExtra("desEmail");
        Sender = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace('.',',');

        messagePath = FirebaseDatabase.getInstance().getReference().child(DatabasePath.messagePath).child(key);

        messageDisplay = (ListView) findViewById(R.id.chat_message_display_list);
        chatMessage = (EditText) findViewById(R.id.chat_message_input);
        btn_send = (ImageButton) findViewById(R.id.chat_message_send);

        infoFirebaseListAdapter = new FirebaseListAdapter<MessageInfo>(this, MessageInfo.class, R.layout.receiver_listview_layout, messagePath) {
            @Override
            public View getView(int position, View view, ViewGroup viewGroup) {
                if (view == null) {
                    view = LayoutInflater.from(mContext).inflate(mLayout, viewGroup, false);
                }

                MessageInfo model = getItem(position);

                if(model.getFrom().compareTo(Receiver) == 0){
                    view = LayoutInflater.from(mContext).inflate(R.layout.receiver_listview_layout, viewGroup, false);
                    dateTime = (TextView) view.findViewById(R.id.receiver_time);
                    messageContent = (TextView) view.findViewById(R.id.receiver_message_content);
                }else {
                    view = LayoutInflater.from(ChatActivity.this).inflate(R.layout.sender_listview_layout, null);
                    dateTime = (TextView) view.findViewById(R.id.sender_time);
                    messageContent = (TextView) view.findViewById(R.id.sender_message_content);
                }


                    // Call out to subclass to marshall this model into the provided view
                populateView(view, model, position);
                return view;
            }

            @Override
            protected void populateView(View v, MessageInfo model, int position) {
                // check if message is receive or send
//                if(model.getFrom().compareTo(Receiver) == 0){
//                    v = LayoutInflater.from(ChatActivity.this).inflate(R.layout.receiver_listview_layout, null);

                dateTime.setText(model.getTime());
                messageContent.setText(model.getMessage());

                messageDisplay.smoothScrollToPosition(infoFirebaseListAdapter.getCount() - 1);
//                }
//                else {
//                    v = LayoutInflater.from(ChatActivity.this).inflate(R.layout.sender_listview_layout, null);
//                    dateTime = (TextView) v.findViewById(R.id.sender_time);
//                    messageContent = (TextView) v.findViewById(R.id.sender_message_content);
//
//                    dateTime.setText(model.getTime());
//                    messageContent.setText(model.getMessage());
//
//                    lastDate = model.getTime();
//                    lastMess = model.getMessage();
//                }
            }
        };

        messageDisplay.setAdapter(infoFirebaseListAdapter);

        actionBar = getSupportActionBar();
        actionBar.setTitle(Receiver.replace(',','.'));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);

        chatMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(s.toString().trim().length() == 0){
                        btn_send.setEnabled(false);
                        btn_send.setImageResource(R.drawable.ic_send_gray_24dp);
                    }
                    else {
                        btn_send.setEnabled(true);
                        btn_send.setImageResource(R.drawable.ic_send_black_24dp);
                    }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    void sendMessage(){
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateFormat df = new SimpleDateFormat("dd/MM/yy");
                Date currentTime = new Date();

                String message = chatMessage.getText().toString();

                MessageInfo messageInfo = new MessageInfo(Sender, Receiver, message, df.format(currentTime));

                HashMap<String, Object> info = new HashMap<String, Object>();

                info.put(messagePath.push().getKey(), messageInfo.toMap());

                lastDate = messageInfo.getTime();
                lastMess = messageInfo.getMessage();

                messagePath.updateChildren(info);

                chatMessage.getText().clear();

                updateLastMessage();
            }
        });
    }

    void updateLastMessage(){
        DatabaseReference updateSelf, updateFriend;

        updateSelf = FirebaseDatabase.getInstance().getReference().child(DatabasePath.friendPath).child(Sender).child(Receiver);
        updateFriend = FirebaseDatabase.getInstance().getReference().child(DatabasePath.friendPath).child(Receiver).child(Sender);

        updateFriend.child("lastestMessage").setValue(lastMess);
        updateFriend.child("lastestMessageDate").setValue(lastDate);
        updateFriend.child("name").setValue(Sender);

        updateSelf.child("lastestMessage").setValue(lastMess);
        updateSelf.child("lastestMessageDate").setValue(lastDate);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        disableReiver();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        enableReceiver();
//    }
//
//    void enableReceiver(){
//        PackageManager pm  = this.getPackageManager();
//        ComponentName componentName = new ComponentName(this, MessageReceiver.class);
//        pm.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//                PackageManager.DONT_KILL_APP);
//    }
//
//    void disableReiver(){
//        PackageManager pm  = this.getPackageManager();
//        ComponentName componentName = new ComponentName(this, MessageReceiver.class);
//        pm.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//                PackageManager.DONT_KILL_APP);
//    }
}
