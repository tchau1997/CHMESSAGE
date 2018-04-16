package com.example.hautc.chmessage;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FriendActivity extends AppCompatActivity {

    FloatingActionMenu actionMenu;
    FloatingActionButton fab_AddContact;
    FloatingActionButton fab_newMessage;

    ListView ContactList;

    DatabaseReference friendData;
    DatabaseReference availableData;
    DatabaseReference chatData;


    FirebaseListAdapter<Friend> friendAdapter;

    String userEmail;

    static final String defaultMessage = "You haven't started any conversation yet";

    static final int ADDFRIEND_REQUESTCODE = 100;
    static final int ADDFRIEND_RESULTCODE = 101;

    Intent BroadastIntent;

    final static String broadcastName = "com.example.hautc.test";

    MessageReceiver messageReceiver;
    IntentFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        init();
        addFriend();
        toChat();
    }

    private void init() {
        getSupportActionBar().setTitle("Inbox");

        // Map menu
        //actionMenu = (FloatingActionMenu) findViewById(R.id.fam_contact);
        fab_AddContact = (FloatingActionButton) findViewById(R.id.fab_addContact);
        //fab_newMessage = (FloatingActionButton) findViewById(R.id.fab_newMessage);

        // Contact List
        ContactList = (ListView) findViewById(R.id.lv_contact);

        // Get current login Email
        userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        userEmail = userEmail.replace('.',',');

        // Get  user friend list
        friendData = FirebaseDatabase.getInstance().getReference().child(DatabasePath.friendPath).child(userEmail);

        friendAdapter = new FirebaseListAdapter<Friend>(this, Friend.class, R.layout.contact_layout, friendData) {
            @Override
            protected void populateView(View v, Friend model, int position) {
                TextView friendEmail = (TextView) v.findViewById(R.id.inbox_contactName);
                TextView date = (TextView) v.findViewById(R.id.inbox_Date);
                TextView message = (TextView) v.findViewById(R.id.inbox_newestMessage);

                friendEmail.setText(model.getName().replace(',','.'));
                date.setText(model.getLastestMessageDate());
                message.setText(model.getLastestMessage());
            }
        };

        // Init list view with real-time data
        ContactList.setAdapter(friendAdapter);

        // get Availiable user list
        availableData = FirebaseDatabase.getInstance().getReference().child(DatabasePath.currentUserPath);

        // get chat data
        chatData = FirebaseDatabase.getInstance().getReference().child(DatabasePath.chatPath).child(userEmail);

        // Map menu
        registerForContextMenu(ContactList);

        // Broadcast Receiver init
//        BroadastIntent = new Intent(FriendActivity.this,MessageReceiver.class);
//
//        BroadastIntent.setAction(broadcastName);
//        BroadastIntent.putExtra("user_email",userEmail);
//        sendBroadcast(BroadastIntent);
//        messageReceiver = new MessageReceiver();
//        IntentFilter filter = new IntentFilter(broadcastName);
//        registerReceiver(messageReceiver, filter);
    }

    void addFriend(){
        fab_AddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dialog or Activity?
                Intent intent1 = new Intent(FriendActivity.this, AddFriendActivity.class);
                startActivityForResult(intent1, ADDFRIEND_REQUESTCODE);
            }
        });
    }

//    void newMessage(){
//        fab_newMessage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//    }

    void CheckAndPush(final String Email){
        availableData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, String> data = (HashMap<String, String>) dataSnapshot.getValue();

                Set<String> keys = data.keySet();

                for(String key : keys) {
                    // check if friend email is valid
                    if (Email.replace('.',',').compareTo(data.get(key)) == 0){
                        //Add data to Database
                        addFriendtoDatabase(Email);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void addFriendtoDatabase(String email){
        DateFormat df = new SimpleDateFormat("dd/MM/yy");
        Date currentTime = new Date();

        Friend tmp = new Friend(email, defaultMessage, df.format(currentTime));
        Map<String, Object> child = new HashMap<>();

        child.put(email.replace('.',','), tmp.toMap());

        friendData.updateChildren(child);
    }

    void toChat(){
        ContactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Friend tmp = friendAdapter.getItem(position);

                chatData.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // get data from Database
                       HashMap<String, String> data = (HashMap<String, String>) dataSnapshot.getValue();

                        // check if data is null, create, then call chat
                        if(data == null) {
                            String key = chatData.push().getKey();
                            newChat(tmp.getName(), key);
                            Intent intent = new Intent(FriendActivity.this, ChatActivity.class);
                            intent.putExtra("key", key);
                            intent.putExtra("desEmail", tmp.getName());
                            startActivity(intent);
                            return;
                        }

                        // get keyset
                        Set<String> keySet = data.keySet();

                        // get value and compare if exist call chatActivity, else create one then call
                        for( String key : keySet) {
                            if(tmp.getName().compareTo(data.get(key)) == 0){
                                Intent intent = new Intent(FriendActivity.this, ChatActivity.class);
                                intent.putExtra("key", key);
                                intent.putExtra("desEmail", tmp.getName());
                                startActivity(intent);
                                return;
                            }
                        }

                        // Get unique key and push to database then call chatActivity
                        String key = chatData.push().getKey();
                        newChat(tmp.getName(), key);
                        Intent intent = new Intent(FriendActivity.this, ChatActivity.class);
                        intent.putExtra("key", key);
                        intent.putExtra("desEmail", tmp.getName());
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    void newChat(String FriendEmail, String key){
        //Get friend chat map
        DatabaseReference friendchatData = FirebaseDatabase.getInstance().getReference().child(DatabasePath.chatPath).child(FriendEmail);

        // Create map
        HashMap<String, Object> chatMap = new HashMap<>();
        HashMap<String, Object> FriendchatMap = new HashMap<>();

        // Create Map to store chat info
        chatMap.put(key, FriendEmail);
        FriendchatMap.put(key,userEmail);

        // push chat info into Database
        chatData.updateChildren(chatMap);
        friendchatData.updateChildren(FriendchatMap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater(); // gọi menuinflater để load layout
        menuInflater.inflate(R.menu.inbox_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.inbox_menu_signout:
            {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(FriendActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            }

            case R.id.inbox_menu_info:
            {
                AlertDialog.Builder builder=new AlertDialog.Builder(FriendActivity.this); // bien de xay
                builder.setTitle("CHMessage"); // tua
                builder.setMessage("Made by Tran Cong Hau"); // thong tin
                builder.setCancelable(true); // ko the nao bam ra ngoai dc
                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() { // nut no tuong tac voi man hinh chinh
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // thoat khoi activity
                    }
                });
                AlertDialog alertDialog=builder.create(); // bien de show
                alertDialog.show(); // show dialod
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ADDFRIEND_REQUESTCODE){
            if(resultCode == ADDFRIEND_RESULTCODE){
                String testEmail = data.getStringExtra("friendEmail");
                CheckAndPush(testEmail);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseAuth.getInstance().signOut();
        //disableReiver();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.inbox_item_menu,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId())
        {
            case R.id.inbox_item_menu_delete:
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(FriendActivity.this);
                builder.setTitle("Do you want to delete?");

                builder.setCancelable(false);

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Friend tmp = friendAdapter.getItem(menuInfo.position);

                        friendData.child(tmp.getName()).removeValue();
                    }
                });

                builder.setNeutralButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = builder.create();

                dialog.show();
                // lay child cua Node listItem bang cach drNode.child(key)


                break;
            }
        }

        return super.onContextItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //enableReceiver();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //disableReiver();
        //enableReceiver();
    }


    void enableReceiver(){
        PackageManager pm  = FriendActivity.this.getPackageManager();
        ComponentName componentName = new ComponentName(FriendActivity.this, MessageReceiver.class);
        pm.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    void disableReiver(){
        PackageManager pm  = FriendActivity.this.getPackageManager();
        ComponentName componentName = new ComponentName(FriendActivity.this, MessageReceiver.class);
        pm.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
}
