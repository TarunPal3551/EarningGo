package com.avilaksh.earningo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.applovin.adview.AppLovinInterstitialAd;
import com.applovin.adview.AppLovinInterstitialAdDialog;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdClickListener;
import com.applovin.sdk.AppLovinAdDisplayListener;
import com.applovin.sdk.AppLovinAdLoadListener;
import com.applovin.sdk.AppLovinAdSize;
import com.applovin.sdk.AppLovinAdVideoPlaybackListener;
import com.applovin.sdk.AppLovinSdk;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pollfish.constants.Position;
import com.pollfish.interfaces.PollfishSurveyCompletedListener;
import com.pollfish.interfaces.PollfishSurveyNotAvailableListener;
import com.pollfish.interfaces.PollfishSurveyReceivedListener;
import com.pollfish.interfaces.PollfishUserNotEligibleListener;
import com.pollfish.main.PollFish;
import com.pollfish.main.PollFish.ParamsBuilder;

import java.util.Date;
import java.util.Random;

public class Play_and_Earn_Activity extends AppCompatActivity {
    Button spin_now;
    ImageView spin_image_animation;
    TextView textView_spin_count;
    int degree = 0, degree_old = 0;
    Random r;
    AlertDialog.Builder builder;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference reference;
    Dialog epicDialog;
    TextView earn_points;
    Button spin_again, startServey;
    ImageView close_popup;
    String today_date;
    int[] count_spin = {0};
    int[] total_coin_earned = {0};
    int[] today_coin_earned = {0};
    int[] today_clicked = {0};
    private AppLovinAd loadedAd;
    int daily_spin_count = 0;
    int survey_count_add_for_spin = 0;
    public static final float FACTOR = 15f;
    CheckBox policycheckbox;
    TextView pollfishpolicy;
    Button submitpolicy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playandearn);
        AppLovinSdk.initializeSdk(Play_and_Earn_Activity.this);
        mAuth = FirebaseAuth.getInstance();
        spin_now = (Button) findViewById(R.id.button_spin_now);
        spin_image_animation = (ImageView) findViewById(R.id.wheel);
        textView_spin_count = (TextView) findViewById(R.id.textViewspinno_);
        builder = new AlertDialog.Builder(Play_and_Earn_Activity.this);
        epicDialog = new Dialog(this);
        r = new Random();
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();


        final String currentDate = android.text.format.DateFormat.format("dd/MM/yyyy", new Date((new Date()).getTime())).toString();
        Toast.makeText(Play_and_Earn_Activity.this, "" + currentDate, Toast.LENGTH_SHORT).show();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    today_date = dataSnapshot.child("User_Data").
                            child(mAuth.getCurrentUser().getUid())
                            .child("Last_Login_Date").getValue().toString();
                    if(currentDate.equals(today_date)) {
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (mAuth.getCurrentUser() != null) {
                                    if (dataSnapshot.child("User_Data").child(mAuth.getCurrentUser().getUid()).hasChild("Spin_Count")) {
                                        count_spin[0] = Integer.valueOf(dataSnapshot.child("User_Data").
                                                child(mAuth.getCurrentUser().getUid()).child("Spin_Count").getValue().toString());
                                        textView_spin_count.setText(String.valueOf(count_spin[0]));
                                    } else {
                                        count_spin[0] = 0;
                                        reference.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("Spin_Count").setValue(count_spin[0]);
                                        textView_spin_count.setText(String.valueOf(count_spin[0]));

                                    }
                                    if (dataSnapshot.child("User_Data").child(mAuth.getCurrentUser().getUid()).hasChild("today_clicks")) {
                                        today_clicked[0] = Integer.valueOf(dataSnapshot.child("User_Data").
                                                child(mAuth.getCurrentUser().getUid()).child("today_clicks").getValue().toString());
                                    } else {
                                        today_clicked[0] = 0;
                                        reference.child("User_Data").
                                                child(mAuth.getCurrentUser().getUid()).child("today_clicks").setValue(today_clicked[0]);

                                    }
                                    if (dataSnapshot.child("User_Data").child(mAuth.getCurrentUser().getUid()).hasChild("today_earned_coins")) {
                                        today_coin_earned[0] = Integer.valueOf(dataSnapshot.child("User_Data").
                                                child(mAuth.getCurrentUser().getUid()).child("today_earned_coins").getValue().toString());
                                    } else {
                                        today_coin_earned[0] = 0;
                                        reference.child("User_Data").
                                                child(mAuth.getCurrentUser().getUid()).child("today_earned_coins").setValue(today_coin_earned[0]);

                                    }
                                    if (dataSnapshot.child("Update offer data").hasChild("daily_spin_count")) {
                                        daily_spin_count = Integer.valueOf(dataSnapshot.child("Update offer data").child("daily_spin_count").getValue().toString());

                                    } else {
                                        daily_spin_count = 5;
                                        reference.child("Update offer data").child("daily_spin_count").setValue(daily_spin_count);
                                    }
                                    if (dataSnapshot.child("Update offer data").hasChild("survey_count_add")) {
                                        survey_count_add_for_spin = Integer.valueOf(dataSnapshot.child("Update offer data").child("survey_count_add").getValue().toString());

                                    } else {
                                        survey_count_add_for_spin = 5;
                                        reference.child("Update offer data").child("survey_count_add").setValue(survey_count_add_for_spin);
                                    }


                                }
                            }


                            @Override
                            public void onCancelled(DatabaseError databaseError) {


                            }
                        });


                    } else {
                        count_spin[0] = 0;
                        reference.child("User_Data").
                                child(mAuth.getCurrentUser().getUid()).child("Spin_Count").setValue(count_spin[0]);
                        today_clicked[0] = 0;
                        reference.child("User_Data").
                                child(mAuth.getCurrentUser().getUid()).child("today_clicks").setValue(today_clicked[0]);
                        today_coin_earned[0] = 0;
                        reference.child("User_Data").
                                child(mAuth.getCurrentUser().getUid()).child("today_earned_coins").setValue(today_coin_earned[0]);
                        textView_spin_count.setText(String.valueOf(count_spin[0]));
                        if (dataSnapshot.child("Update offer data").hasChild("daily_spin_count")) {
                            daily_spin_count = Integer.valueOf(dataSnapshot.child("Update offer data").child("daily_spin_count").getValue().toString());

                        } else {
                            daily_spin_count = 5;
                            reference.child("Update offer data").child("daily_spin_count").setValue(daily_spin_count);
                        }
                        if (dataSnapshot.child("Update offer data").hasChild("survey_count_spin")) {
                            survey_count_add_for_spin = Integer.valueOf(dataSnapshot.child("Update offer data").child("survey_count_spin").getValue().toString());

                        } else {
                            survey_count_add_for_spin = 5;
                            reference.child("Update offer data").child("survey_count_spin").setValue(survey_count_add_for_spin);
                        }


                    }


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mAuth.getCurrentUser() != null) {
                    if (dataSnapshot.child("User_Data").child(mAuth.getCurrentUser().getUid()).hasChild("total_Coins")) {
                        total_coin_earned[0] = Integer.valueOf(dataSnapshot.child("User_Data").
                                child(mAuth.getCurrentUser().getUid()).child("total_Coins").getValue().toString());
                    } else {
                        total_coin_earned[0] = 0;
                        reference.child("User_Data").
                                child(mAuth.getCurrentUser().getUid()).child("total_Coins").setValue(total_coin_earned[0]);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        AppLovinSdk.getInstance(Play_and_Earn_Activity.this).getAdService().loadNextAd(AppLovinAdSize.INTERSTITIAL, new AppLovinAdLoadListener() {
            @Override
            public void adReceived(AppLovinAd ad) {
                loadedAd = ad;
            }

            @Override
            public void failedToReceiveAd(int errorCode) {
                // Look at AppLovinErrorCodes.java for list of error codes.
            }
        });
        AppLovinInterstitialAdDialog interstitialAd = AppLovinInterstitialAd.create(AppLovinSdk.getInstance(Play_and_Earn_Activity.this), Play_and_Earn_Activity.this);

        interstitialAd.setAdDisplayListener(new AppLovinAdDisplayListener() {
            @Override
            public void adDisplayed(AppLovinAd appLovinAd) {

            }

            @Override
            public void adHidden(AppLovinAd appLovinAd) {

            }
        });
        interstitialAd.setAdClickListener(new AppLovinAdClickListener() {
            @Override
            public void adClicked(AppLovinAd appLovinAd) {

            }
        });
        interstitialAd.setAdVideoPlaybackListener(new AppLovinAdVideoPlaybackListener() {
            @Override
            public void videoPlaybackBegan(AppLovinAd appLovinAd) {

            }

            @Override
            public void videoPlaybackEnded(AppLovinAd appLovinAd, double v, boolean b) {

            }
        });
        interstitialAd.showAndRender(loadedAd);


        final ParamsBuilder paramsBuilder = new ParamsBuilder("7a0df0f4-14bb-4a3d-bff6-ead8962a05a0")
                .indicatorPosition(Position.BOTTOM_LEFT)
                .requestUUID(mAuth.getCurrentUser().getUid())
                .userLayout((ViewGroup) getWindow().getDecorView())
                .releaseMode(true)
                .customMode(true)
                .build();
        paramsBuilder.pollfishSurveyReceivedListener(new PollfishSurveyReceivedListener() {
            @Override
            public void onPollfishSurveyReceived(boolean b, int i) {
                Toast.makeText(Play_and_Earn_Activity.this, "Wait Survey is loaded", Toast.LENGTH_SHORT).show();

            }
        }).pollfishSurveyCompletedListener(new PollfishSurveyCompletedListener() {
            @Override
            public void onPollfishSurveyCompleted(boolean b, int i) {
                Toast.makeText(Play_and_Earn_Activity.this, "You Won" + "" + survey_count_add_for_spin + "" + "Spin", Toast.LENGTH_SHORT).show();
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        count_spin[0] = Integer.valueOf(dataSnapshot.child("User_Data").
                                child(mAuth.getCurrentUser().getUid()).child("Spin_Count").getValue().toString());


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {


                    }
                });
                count_spin[0] = count_spin[0] - survey_count_add_for_spin;
                textView_spin_count.setText(String.valueOf(count_spin[0]));
                reference.child("User_Data").
                        child(mAuth.getCurrentUser().getUid()).child("Spin_Count").setValue(count_spin[0]);


            }
        }).pollfishSurveyNotAvailableListener(new PollfishSurveyNotAvailableListener() {
            @Override
            public void onPollfishSurveyNotAvailable() {
                Toast.makeText(Play_and_Earn_Activity.this, "Survey not available", Toast.LENGTH_SHORT).show();
                builder.setTitle("Survey Not Recived");
                builder.setMessage("Currently Survey not available! Try Again.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        }).pollfishUserNotEligibleListener(new PollfishUserNotEligibleListener() {
            @Override
            public void onUserNotEligible() {
                Toast.makeText(Play_and_Earn_Activity.this, "Survey not Eligible", Toast.LENGTH_SHORT).show();

            }
        }).build();
        // textView_spin_count.setText(count_spin[0]);


        spin_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final int[] points = new int[1];

                    if (count_spin[0] < daily_spin_count) {


                        degree_old = degree % 360;
                        degree = r.nextInt(3600) + 720;
                        RotateAnimation rotate = new RotateAnimation(degree_old, degree, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
                        rotate.setDuration(3000);
                        rotate.setFillAfter(true);
                        rotate.setInterpolator(new DecelerateInterpolator());
                        rotate.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                                spin_now.setEnabled(false);


//                        pm.bcount((Integer.parseInt(pm.getCount()) + 1) + "");
//                        pm.pdate(dateString);

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                final String text = currentposition(360 - (degree % 360));
                                epicDialog.setContentView(R.layout.popupforearnmoney);
                                close_popup = (ImageView) epicDialog.findViewById(R.id.close_popup);
                                earn_points = (TextView) epicDialog.findViewById(R.id.earnpoints);
                                spin_again = (Button) epicDialog.findViewById(R.id.spin_agin_button);
                                points[0] = Integer.parseInt(text);

                                close_popup.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        AppLovinSdk.getInstance(Play_and_Earn_Activity.this).getAdService().loadNextAd(AppLovinAdSize.INTERSTITIAL, new AppLovinAdLoadListener() {
                                            @Override
                                            public void adReceived(AppLovinAd ad) {
                                                loadedAd = ad;
                                            }

                                            @Override
                                            public void failedToReceiveAd(int errorCode) {
                                                // Look at AppLovinErrorCodes.java for list of error codes.
                                            }
                                        });
                                        AppLovinInterstitialAdDialog interstitialAd = AppLovinInterstitialAd.create(AppLovinSdk.getInstance(Play_and_Earn_Activity.this), Play_and_Earn_Activity.this);

                                        interstitialAd.setAdDisplayListener(new AppLovinAdDisplayListener() {
                                            @Override
                                            public void adDisplayed(AppLovinAd appLovinAd) {

                                            }

                                            @Override
                                            public void adHidden(AppLovinAd appLovinAd) {

                                            }
                                        });
                                        interstitialAd.setAdClickListener(new AppLovinAdClickListener() {
                                            @Override
                                            public void adClicked(AppLovinAd appLovinAd) {

                                            }
                                        });
                                        interstitialAd.setAdVideoPlaybackListener(new AppLovinAdVideoPlaybackListener() {
                                            @Override
                                            public void videoPlaybackBegan(AppLovinAd appLovinAd) {

                                            }

                                            @Override
                                            public void videoPlaybackEnded(AppLovinAd appLovinAd, double v, boolean b) {

                                            }
                                        });
                                        interstitialAd.showAndRender(loadedAd);
                                        epicDialog.dismiss();

                                    }
                                });

                                earn_points.setText("+" + points[0] + " " + "Points");

                                //textView_spin_count.setText(String.valueOf(count_spin[0]));

                                spin_again.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        epicDialog.dismiss();
                                        AppLovinSdk.getInstance(Play_and_Earn_Activity.this).getAdService().loadNextAd(AppLovinAdSize.INTERSTITIAL, new AppLovinAdLoadListener() {
                                            @Override
                                            public void adReceived(AppLovinAd ad) {
                                                loadedAd = ad;
                                            }

                                            @Override
                                            public void failedToReceiveAd(int errorCode) {
                                                // Look at AppLovinErrorCodes.java for list of error codes.
                                            }
                                        });
                                        AppLovinInterstitialAdDialog interstitialAd = AppLovinInterstitialAd.create(AppLovinSdk.getInstance(Play_and_Earn_Activity.this), Play_and_Earn_Activity.this);

                                        interstitialAd.setAdDisplayListener(new AppLovinAdDisplayListener() {
                                            @Override
                                            public void adDisplayed(AppLovinAd appLovinAd) {

                                            }

                                            @Override
                                            public void adHidden(AppLovinAd appLovinAd) {

                                            }
                                        });
                                        interstitialAd.setAdClickListener(new AppLovinAdClickListener() {
                                            @Override
                                            public void adClicked(AppLovinAd appLovinAd) {

                                            }
                                        });
                                        interstitialAd.setAdVideoPlaybackListener(new AppLovinAdVideoPlaybackListener() {
                                            @Override
                                            public void videoPlaybackBegan(AppLovinAd appLovinAd) {

                                            }

                                            @Override
                                            public void videoPlaybackEnded(AppLovinAd appLovinAd, double v, boolean b) {

                                            }
                                        });
                                        interstitialAd.showAndRender(loadedAd);
                                    }
                                });
                                epicDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                epicDialog.show();
                                spin_now.setEnabled(true);


//                            builder.setMessage("Congratulations!!!"
//                                    + "You have won " + text + " points ");
//                            builder.setPositiveButton("OK",
//                                    new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            int points=Integer.parseInt(text);
//                                            textView_spin_count.setText(String.valueOf(count_spin[0]));
//                                            reference.addValueEventListener(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(DataSnapshot dataSnapshot) {
//
//                                                }
//
//                                                @Override
//                                                public void onCancelled(DatabaseError databaseError) {
//
//                                                }
//                                            });
//                                            reference.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("Total_Credits").setValue(points);
//
//                                        }
//                                    });
//                            builder.setCancelable(false);
//                            AlertDialog alertDialog = builder.create();
//                            alertDialog.show();
//                            spin_now.setEnabled(true);
                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        total_coin_earned[0] = Integer.valueOf(dataSnapshot.child("User_Data").
                                                child(mAuth.getCurrentUser().getUid()).child("total_Coins").getValue().toString());
                                        count_spin[0] = Integer.valueOf(dataSnapshot.child("User_Data").
                                                child(mAuth.getCurrentUser().getUid()).child("Spin_Count").getValue().toString());
                                        today_clicked[0] = Integer.valueOf(dataSnapshot.child("User_Data").
                                                child(mAuth.getCurrentUser().getUid()).child("today_clicks").getValue().toString());
                                        today_coin_earned[0] = Integer.valueOf(dataSnapshot.child("User_Data").
                                                child(mAuth.getCurrentUser().getUid()).child("today_earned_coins").getValue().toString());


                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {


                                    }
                                });
                                count_spin[0] = count_spin[0] + 1;
                                today_clicked[0] = today_clicked[0] + 1;
                                today_coin_earned[0] = today_coin_earned[0] + points[0];
                                reference.child("User_Data").
                                        child(mAuth.getCurrentUser().getUid()).child("today_clicks").setValue(today_clicked[0]);
                                reference.child("User_Data").
                                        child(mAuth.getCurrentUser().getUid()).child("today_earned_coins").setValue(today_coin_earned[0]);


                                textView_spin_count.setText(String.valueOf(count_spin[0]));
                                reference.child("User_Data").
                                        child(mAuth.getCurrentUser().getUid()).child("Spin_Count").setValue(count_spin[0]);
                                total_coin_earned[0] = total_coin_earned[0] + points[0];
                                reference.child("User_Data").
                                        child(mAuth.getCurrentUser().getUid()).child("total_Coins").setValue(total_coin_earned[0]);

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        spin_image_animation.startAnimation(rotate);

                    } else {
                        epicDialog.setContentView(R.layout.earn_spin_start_survey);
                        close_popup = (ImageView) epicDialog.findViewById(R.id.close_popup);
                        //watchpoint= (TextView) epicDialog.findViewById(R.id.earnpointswatch);
                        startServey = (Button) epicDialog.findViewById(R.id.takesurvey);
                        //watchpoint.setText(vedio_rewarded_point);


                        close_popup.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AppLovinSdk.getInstance(Play_and_Earn_Activity.this).getAdService().loadNextAd(AppLovinAdSize.INTERSTITIAL, new AppLovinAdLoadListener() {
                                    @Override
                                    public void adReceived(AppLovinAd ad) {
                                        loadedAd = ad;
                                    }

                                    @Override
                                    public void failedToReceiveAd(int errorCode) {
                                        // Look at AppLovinErrorCodes.java for list of error codes.
                                    }
                                });
                                AppLovinInterstitialAdDialog interstitialAd = AppLovinInterstitialAd.create(AppLovinSdk.getInstance(Play_and_Earn_Activity.this), Play_and_Earn_Activity.this);

                                interstitialAd.setAdDisplayListener(new AppLovinAdDisplayListener() {
                                    @Override
                                    public void adDisplayed(AppLovinAd appLovinAd) {

                                    }

                                    @Override
                                    public void adHidden(AppLovinAd appLovinAd) {

                                    }
                                });
                                interstitialAd.setAdClickListener(new AppLovinAdClickListener() {
                                    @Override
                                    public void adClicked(AppLovinAd appLovinAd) {

                                    }
                                });
                                interstitialAd.setAdVideoPlaybackListener(new AppLovinAdVideoPlaybackListener() {
                                    @Override
                                    public void videoPlaybackBegan(AppLovinAd appLovinAd) {

                                    }

                                    @Override
                                    public void videoPlaybackEnded(AppLovinAd appLovinAd, double v, boolean b) {

                                    }
                                });
                                interstitialAd.showAndRender(loadedAd);
                                epicDialog.dismiss();

                            }
                        });
                        startServey.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                PollFish.initWith(Play_and_Earn_Activity.this, paramsBuilder);
                                PollFish.show();
                                AppLovinSdk.getInstance(Play_and_Earn_Activity.this).getAdService().loadNextAd(AppLovinAdSize.INTERSTITIAL, new AppLovinAdLoadListener() {
                                    @Override
                                    public void adReceived(AppLovinAd ad) {
                                        loadedAd = ad;
                                    }

                                    @Override
                                    public void failedToReceiveAd(int errorCode) {
                                        // Look at AppLovinErrorCodes.java for list of error codes.
                                    }
                                });
                                AppLovinInterstitialAdDialog interstitialAd = AppLovinInterstitialAd.create(AppLovinSdk.getInstance(Play_and_Earn_Activity.this), Play_and_Earn_Activity.this);

                                interstitialAd.setAdDisplayListener(new AppLovinAdDisplayListener() {
                                    @Override
                                    public void adDisplayed(AppLovinAd appLovinAd) {

                                    }

                                    @Override
                                    public void adHidden(AppLovinAd appLovinAd) {

                                    }
                                });
                                interstitialAd.setAdClickListener(new AppLovinAdClickListener() {
                                    @Override
                                    public void adClicked(AppLovinAd appLovinAd) {

                                    }
                                });
                                interstitialAd.setAdVideoPlaybackListener(new AppLovinAdVideoPlaybackListener() {
                                    @Override
                                    public void videoPlaybackBegan(AppLovinAd appLovinAd) {

                                    }

                                    @Override
                                    public void videoPlaybackEnded(AppLovinAd appLovinAd, double v, boolean b) {

                                    }
                                });
                                interstitialAd.showAndRender(loadedAd);
                                epicDialog.dismiss();
                            }
                        });
                        epicDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        epicDialog.show();
                        PollFish.initWith(Play_and_Earn_Activity.this, paramsBuilder);
                        PollFish.show();

                    }


                }

        });
    }

    @Override
    public void onResume() {
        super.onResume();

        //PollFish.initWith(this, new ParamsBuilder("480c37dd-01e2-4e9f-8d47-c34a3928ddb9").build());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //PollFish.initWith(this, new ParamsBuilder("480c37dd-01e2-4e9f-8d47-c34a3928ddb9").build());


    }

    public String currentposition(int degree) {
        String text = "";
        if (degree >= (FACTOR * 1) && degree < (FACTOR * 3)) {
            text = "2";
        }
        if (degree >= (FACTOR * 3) && degree < (FACTOR * 5)) {
            text = "3";
        }
        if (degree >= (FACTOR * 5) && degree < (FACTOR * 7)) {
            text = "4";
        }
        if (degree >= (FACTOR * 7) && degree < (FACTOR * 9)) {
            text = "5";
        }
        if (degree >= (FACTOR * 9) && degree < (FACTOR * 11)) {
            text = "6";
        }
        if (degree >= (FACTOR * 11) && degree < (FACTOR * 13)) {
            text = "7";
        }
        if (degree >= (FACTOR * 13) && degree < (FACTOR * 15)) {
            text = "8";
        }
        if (degree >= (FACTOR * 15) && degree < (FACTOR * 17)) {
            text = "9";
        }
        if (degree >= (FACTOR * 17) && degree < (FACTOR * 19)) {
            text = "10";
        }
        if (degree >= (FACTOR * 19) && degree < (FACTOR * 21)) {
            text = "15";
        }
        if (degree >= (FACTOR * 21) && degree < (FACTOR * 23)) {
            text = "0";
        }
        if (degree >= (FACTOR * 23) && degree < (FACTOR * 25)) {
            text = "0";
        }
        if ((degree >= (FACTOR * 25) && degree < 360) || (degree >= 0 && degree < (FACTOR * 1))) {
            text = "1";
        }


        return text;
    }

}
