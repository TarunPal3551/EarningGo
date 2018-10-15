package com.avilaksh.earningo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class Notification_Activity extends AppCompatActivity {
    private static final String TAG = "Notification_Activity";
    RecyclerView notificationRecylerView;
    NotificationRecylerViewAdapter notificationRecylerViewAdapter;
    List<NotificationItem>notificationItemslist=new ArrayList<>();
    ImageView backbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_);
        notificationRecylerView = (RecyclerView) findViewById(R.id.notificationrecylerview);
        backbutton=(ImageView)findViewById(R.id.backbuttonnotification);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent=new Intent(Notification_Activity.this,Home_Main_Activity.class);
//                startActivity(intent);
                Notification_Activity.super.onBackPressed();
            }
        });
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);



                Log.d(TAG, "onCreate: "+key);


                //txt.append(key + ": " + value + "\n\n");
            }
            Object title=bundle.get("title");
            Object body=bundle.get("body");
            NotificationItem notificationItem=new NotificationItem(title.toString(),body.toString(),"notificationicon");
            notificationItemslist.add(notificationItem);


        }
//       Intent intent=getIntent();
//       Bundle bundle=intent.getExtras();
//
//           if (bundle!=null) {
//
//               if (bundle.getString("messagebody") != null) {
//                   NotificationItem notificationItem = new NotificationItem();
//                   notificationItem.setBody(bundle.getString("messagebody"));
//                   notificationItem.setTittle(bundle.getString("title"));
//                   recylerView(notificationItem);
//
//
//               } else {
//                   Toast.makeText(this, "No message service", Toast.LENGTH_LONG).show();
//               }
//           }
//       else{
//           Toast.makeText(this,"No message",Toast.LENGTH_LONG).show();
//
//           }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        notificationRecylerView.setLayoutManager(layoutManager);
        notificationRecylerViewAdapter = new NotificationRecylerViewAdapter(notificationItemslist, Notification_Activity.this);
        notificationRecylerView.setAdapter(notificationRecylerViewAdapter);
        notificationRecylerViewAdapter.notifyDataSetChanged();


    }
//    public void setNotificationdata(){
//        NotificationItem notificationItem = new NotificationItem("Offer of the day","Earn Extra vedio bonus","notificationicon");
//        notificationItemslist.add(notificationItem);
//        notificationItem = new NotificationItem("Spin Offer","Earn Extra Spin","notificationicon");
//        notificationItemslist.add(notificationItem);
//        notificationRecylerViewAdapter.notifyDataSetChanged();
//
//    }




}
