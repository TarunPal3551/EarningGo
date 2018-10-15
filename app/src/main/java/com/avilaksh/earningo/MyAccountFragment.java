package com.avilaksh.earningo;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MyAccountFragment extends Fragment {
    public static String FACEBOOK_URL = "https://www.facebook.com/earningo/";
    public static String FACEBOOK_PAGE_ID = "1674203159354538";
    TextView mobileNumber;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    RelativeLayout rateapp, likefbpage, invitefriend;
    ImageView helpbuttonpayment, getHelpbuttonpayment;
    AlertDialog.Builder builder;
    Button redeem_points,add_Mobilenumber;
    int redeem_Point_for_user = 0;
    int total_coins_of_user = 0;
    int total_coins_redeem=0;
    String status;
    TextView username,useremail;
    ImageView imageViewprofile;
    RelativeLayout relPhone_number;
    Dialog epicDialog;
    EditText mobile_number;
    ImageView close_popup_addmobile;
    Button submit;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_my_account,
                container, false);
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        mAuth = FirebaseAuth.getInstance();
        builder = new AlertDialog.Builder(getContext());
        mobileNumber = (TextView) view.findViewById(R.id.mobile_number_text_virw);
        relPhone_number=(RelativeLayout)view.findViewById(R.id.rel1);
        rateapp = (RelativeLayout) view.findViewById(R.id.rating_layout);
        likefbpage = (RelativeLayout) view.findViewById(R.id.like_layout);
        invitefriend = (RelativeLayout) view.findViewById(R.id.sharelayout);
        helpbuttonpayment = (ImageView) view.findViewById(R.id.requsetpaymentpopup);
        getHelpbuttonpayment = (ImageView) view.findViewById(R.id.paymentrelatedissuehelp);
        redeem_points = (Button) view.findViewById(R.id.redeem_points);
        username=(TextView)view.findViewById(R.id.username);
        useremail=(TextView)view.findViewById(R.id.useremail);
        imageViewprofile=(ImageView)view.findViewById(R.id.imageViewprofileimage);
        relPhone_number.setVisibility(View.GONE);
        add_Mobilenumber=(Button)view.findViewById(R.id.buttonaddpaytm);
        epicDialog=new Dialog(getContext());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mAuth.getCurrentUser() != null) {

                    if (dataSnapshot.child("User_Data").child(mAuth.getCurrentUser().getUid()).hasChild("User_Mobile_Number")) {
                        String mobile_number = dataSnapshot.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("User_Mobile_Number").getValue().toString();
                        mobileNumber.setText(mobile_number);
                        relPhone_number.setVisibility(View.VISIBLE);
                        add_Mobilenumber.setText("Edit your Paytm Number");
                       add_Mobilenumber.setEnabled(false);
                    }
                    if (dataSnapshot.child("Updte offer data").hasChild("Redeem_Points")) {
                        redeem_Point_for_user = Integer.valueOf(dataSnapshot.child("Update offer data").child("Redeem_Points").getValue().toString());

                    } else {
                        redeem_Point_for_user = 50000;
                        databaseReference.child("Update offer data").child("Redeem_Points").setValue(redeem_Point_for_user);
                    }

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        add_Mobilenumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                epicDialog.setContentView(R.layout.popuforaddmobilenumber);
                mobile_number=(EditText)epicDialog.findViewById(R.id.editTextnumber);
                submit=(Button)epicDialog.findViewById(R.id.buttonsubmit);
                close_popup_addmobile=(ImageView)epicDialog.findViewById(R.id.close_popup_add_mobile);
                close_popup_addmobile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        epicDialog.dismiss();
                    }
                });
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String mobilenumber=mobile_number.getText().toString();
                        if (mobilenumber.isEmpty() || mobilenumber.equals("")){
                            Toast.makeText(getContext(),"Enter you Registerd PayTm Mobile Number",Toast.LENGTH_LONG).show();
                            epicDialog.dismiss();

                        }
                        else{
                           databaseReference.child("User_Data").
                                    child(mAuth.getCurrentUser().getUid()).child("User_Mobile_Number").setValue(mobilenumber);
                           relPhone_number.setVisibility(View.VISIBLE);
                           mobileNumber.setText(mobilenumber);
                           databaseReference.addValueEventListener(new ValueEventListener() {
                               @Override
                               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                   if (dataSnapshot.child("User_Data").child(mAuth.getCurrentUser().getUid()).hasChild("User_Mobile_Number")) {
                                       mobileNumber.setText(dataSnapshot.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("User_Mobile_Number").getValue().toString());
                                       epicDialog.dismiss();
                                       Toast.makeText(getContext(), "Your Paytm number is added! Earn Coin and Redeem your credits", Toast.LENGTH_LONG).show();
                                       add_Mobilenumber.setText("Edit your Paytm Number");
                                       add_Mobilenumber.setEnabled(false);
                                   }
                               }

                               @Override
                               public void onCancelled(@NonNull DatabaseError databaseError) {
                                   epicDialog.dismiss();


                               }
                           });
                        }


                    }
                });
                epicDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                epicDialog.show();






            }
        });

        if (mAuth.getCurrentUser()!=null){
            username.setText(String.valueOf(mAuth.getCurrentUser().getDisplayName()));
           useremail.setText(String.valueOf(mAuth.getCurrentUser().getEmail()));
            Picasso.get().load(mAuth.getCurrentUser().getPhotoUrl())
                    .into(imageViewprofile);

        }

        if (mAuth.getCurrentUser() != null) {

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("Update offer data").hasChild("Redeem_Points")) {
                        redeem_Point_for_user = Integer.valueOf(dataSnapshot.child("Update offer data").child("Redeem_Points").getValue().toString());
                        if (dataSnapshot.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("total_Coins").getValue() != null) {
                            total_coins_of_user = Integer.valueOf(dataSnapshot.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("total_Coins").getValue().toString());

                        }

                    } else {
                        if (dataSnapshot.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("total_Coins").getValue()!=null){
                            total_coins_of_user = Integer.valueOf(dataSnapshot.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("total_Coins").getValue().toString());

                        }
                        else {
                            total_coins_of_user=0;

                        }

                        redeem_Point_for_user = 50000;
                        databaseReference.child("Update offer data").child("Redeem_Points").setValue(redeem_Point_for_user);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        redeem_points.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (total_coins_of_user >= redeem_Point_for_user) {
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child("Redeem_Requests").child(mAuth.getCurrentUser().getUid()).hasChild("total_coins_redeem")){
                                total_coins_redeem= Integer.parseInt(dataSnapshot.child("Redeem_Requests").child(mAuth.getCurrentUser().getUid()).child("total_coins_redeem").getValue().toString());

                            }
                            else {
                                total_coins_redeem=50000;
                                databaseReference.child("Redeem_Requests").child(mAuth.getCurrentUser().getUid()).child("total_coins_redeem").setValue(total_coins_redeem);

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    total_coins_of_user = total_coins_of_user - redeem_Point_for_user;
                    total_coins_redeem=total_coins_redeem+redeem_Point_for_user;
                    databaseReference.child("Redeem_Requests").child(mAuth.getCurrentUser().getUid()).child("total_coins_redeem").setValue(total_coins_redeem);
                    databaseReference.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("total_Coins").setValue(total_coins_of_user);
                    builder.setTitle("REQUEST SUCCESSFUL");
                    builder.setMessage("Your request has been successfylly send. A/c will be credited on or after 5 days.");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    databaseReference.child("Redeem_Requests").child(mAuth.getCurrentUser().getUid()).child("User_Mobile_Number").setValue(mAuth.getCurrentUser().getPhoneNumber());
                    databaseReference.child("Redeem_Requests").child(mAuth.getCurrentUser().getUid()).child("Redeem_Request_status").setValue("Pending");


                    //FirebaseMessaging.getInstance().subscribeToTopic("RedeemRequest");


                } else {
                    //FirebaseMessaging.getInstance().subscribeToTopic("RedeemRequest");
                    builder.setTitle("REQUEST FAILED");
                    builder.setMessage("Your total earning is less than" + " " +"+"+ redeem_Point_for_user+" "+"points"+"."+"Earn more coins to redeem.");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });


                }
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }


        });
        getHelpbuttonpayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setTitle("Information");
                builder.setMessage("After successful redeem users will get the payout on or after 5 days from te date of request.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });
        helpbuttonpayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setTitle("Information");
                builder.setMessage("Users can redeem their coins only after reaching the milestone +50,000 coins.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });



        rateapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getContext().getPackageName())));
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://goo.gl/P51ZNt")));
                }
            }
        });
        invitefriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "Hey! Earn FREE PayTm cash daily by watching video ads. Download EarninGo app now & start earning from today!\n" +"https://goo.gl/P51ZNt");
                startActivity(Intent.createChooser(intent, "Share Via"));


            }
        });
        likefbpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
//                String facebookUrl = facebook();
//                facebookIntent.setData(Uri.parse(facebookUrl));
//
//                startActivity(facebookIntent);
                facebook();

                // facebook();

            }
        });


        return view;

    }



    public String getFacebookPageURL(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                return "fb://page/" + FACEBOOK_PAGE_ID;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return FACEBOOK_URL; //normal web url
        }
    }

    public void facebook() {
        final String urlFb = "fb://page/" + FACEBOOK_PAGE_ID;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(urlFb));

        // If Facebook application is installed, use that else launch a browser
        final PackageManager packageManager = getContext().getPackageManager();
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() == 0) {
            final String urlBrowser = "https://www.facebook.com/pages/" + FACEBOOK_PAGE_ID;
            intent.setData(Uri.parse(urlBrowser));
        }
        startActivity(intent);
    }


}
