package com.example.hautc.chmessage;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import static android.content.Context.NOTIFICATION_SERVICE;

public class MessageReceiver extends BroadcastReceiver {

    DatabaseReference InboxData;

    DatabaseReference chatData;

    Context tmp;

    ArrayList<String> keys = new ArrayList<>();
    ArrayList<String> names = new ArrayList<>();

    int index = -1;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        String userEmail = intent.getStringExtra("user_email");

        InboxData = FirebaseDatabase.getInstance()
                .getReference().child(DatabasePath.friendPath)
                .child(userEmail);

        chatData = FirebaseDatabase.getInstance()
                .getReference().child(DatabasePath.chatPath)
                .child(userEmail);

        Log.i("Broadcast", "Broadcast");
        tmp = context;
        chatData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (keys.size() != 0 && names.size() != 0){
                    keys.clear();
                    names.clear();
                }
                // get data from Database
                HashMap<String, String> data = (HashMap<String, String>) dataSnapshot.getValue();

                // get keyset
                Set<String> keySet = data.keySet();

                // get value and compare if exist call chatActivity, else create one then call
                for( String key : keySet) {
                    keys.add(key);
                    names.add(data.get(key));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        InboxData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Friend info = dataSnapshot.getValue(Friend.class);

                NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(tmp);

                nBuilder.setSmallIcon(R.drawable.logo3);
                nBuilder.setContentTitle(info.getName());
                nBuilder.setContentText(info.getLastestMessage());
                nBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));

                Intent intent = new Intent(tmp,ChatActivity.class);


                intent.putExtra("key", findKey(info.getName()));
                intent.putExtra("desEmail", info.getName());

                PendingIntent pendingIntent = PendingIntent.getActivity(tmp,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

                nBuilder.setContentIntent(pendingIntent);

                nBuilder.setAutoCancel(true);

                NotificationManager notificationManager = (NotificationManager) tmp.getSystemService(NOTIFICATION_SERVICE);

                notificationManager.notify(index,nBuilder.build());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    String findKey(String name){
        for(int i = 0; i < names.size(); i++){
            if(name.compareTo(names.get(i)) == 0){
                index = i;
                break;
            }
        }
        return keys.get(index);
    }
}
