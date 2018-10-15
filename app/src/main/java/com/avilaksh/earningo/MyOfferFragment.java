package com.avilaksh.earningo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.applovin.adview.AppLovinIncentivizedInterstitial;
import com.applovin.adview.AppLovinInterstitialAd;
import com.applovin.adview.AppLovinInterstitialAdDialog;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdClickListener;
import com.applovin.sdk.AppLovinAdDisplayListener;
import com.applovin.sdk.AppLovinAdLoadListener;
import com.applovin.sdk.AppLovinAdRewardListener;
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

import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.MODE_PRIVATE;

public class MyOfferFragment extends Fragment {
    RelativeLayout playandEarn, watch_earn, activity_bonus;
    TextView lastLoginDate;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth mAuth;


    private AppLovinAd loadedAd;
    private AppLovinIncentivizedInterstitial myIncent;
    int countVedio = 0;
    int todaycoins = 0;
    int today_Clicks = 0;
    int totalCoins = 0;
    CheckBox policycheckbox;
    String today_date;
    TextView pollfishpolicy;
    Button submitpolicy;
    TextView totalCoinstextview, totalclicktextView, today_Earn_TextView;

    Dialog epicDialog;
    TextView earn_pointsspin, watchpoint, dailypoint;
    Button spin_again, watchagin, okdaily;
    ImageView close_popup;
    int last_login_date;
    private static final String TAG = "MyOfferFragment";
    String loginDate;
    int countDailybonus = 0;
    AlertDialog.Builder builder;
    ImageView help;
    int vedio_rewarded_point = 0, daily_rewarde_points = 0;
    Button startServey;
    TextView dailybonusbody, watchandearndailybonus;
    int dailyvediocount = 0, survey_count_add_for_vedio = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_my_offer, container, false);
        AppLovinSdk.initializeSdk(getContext());
        builder = new AlertDialog.Builder(getContext());
        //myIncent = AppLovinIncentivizedInterstitial.create(getContext());
        playandEarn = (RelativeLayout) view.findViewById(R.id.play_earn_spin_layout);
        watch_earn = (RelativeLayout) view.findViewById(R.id.watch_and_earn);
        activity_bonus = (RelativeLayout) view.findViewById(R.id.activity_bonus);
        today_Earn_TextView = (TextView) view.findViewById(R.id.today_earn_text_view);
        totalclicktextView = (TextView) view.findViewById(R.id.today_click_text_view);
        totalCoinstextview = (TextView) view.findViewById(R.id.totalearn_textview);
        help = (ImageView) view.findViewById(R.id.imageViewsponsoredhelp);
        final ProgressBar pbHeaderProgress = (ProgressBar)view.findViewById(R.id.progressBar3);
        setProgress(5,pbHeaderProgress);


        dailybonusbody = (TextView) view.findViewById(R.id.dailybonusbody);
        watchandearndailybonus = (TextView) view.findViewById(R.id.watchandearnbody);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                builder.setTitle("Information");
                builder.setMessage("All the offers belongs to 3rd parties and for any kind of discrepancies we are not liable for any loss.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });


        // textView_spin_count =(TextView)view.findViewById(R.id.textViewspinno_);
        // builder = new AlertDialog.Builder(getContext());
        epicDialog = new Dialog(getContext());
        playandEarn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Play_and_Earn_Activity.class);
                startActivity(intent);
            }
        });
        mAuth = FirebaseAuth.getInstance();
        lastLoginDate = (TextView) view.findViewById(R.id.textViewlastlogindate);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {

                    if (dataSnapshot.child("User_Data").child(mAuth.getCurrentUser().getUid()).hasChild("Last_Login_Date")) {

                        loginDate = dataSnapshot.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("Last_Login_Date").getValue().toString();

                        if (loginDate.isEmpty() || loginDate.equals("") || loginDate.length() == 0) {
                            final String currentDate = android.text.format.DateFormat.format("dd/MM/yyyy", new Date((new Date()).getTime())).toString();
                            lastLoginDate.setText(currentDate);
                            //  last_login_date=Integer.parseInt(currentDateTimeString);

                            reference.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("Last_Login_Date").setValue(currentDate);
                        } else {
                            lastLoginDate.setText(loginDate);

//                        last_login_date=Integer.parseInt(loginDate);

                        }
                    } else {

                        final String currentDate = android.text.format.DateFormat.format("dd/MM/yyyy", new Date((new Date()).getTime())).toString();

                        lastLoginDate.setText(currentDate);
//                    last_login_date=Integer.parseInt(currentDateTimeString);
                        loginDate = currentDate;
                        reference.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("Last_Login_Date").setValue(currentDate);
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @SuppressWarnings("unchecked")
                    public void run() {
                        try {
                            if (mAuth.getCurrentUser() != null) {
                                updateAllActivity();


                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }

                    @Override
                    protected void finalize() throws Throwable {
                        super.finalize();
                    }
                });
            }
        };
        final String currentlyDate = android.text.format.DateFormat.format("dd/MM/yyyy", new Date((new Date()).getTime())).toString();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    today_date = dataSnapshot.child("User_Data").
                            child(mAuth.getCurrentUser().getUid())
                            .child("Last_Login_Date").getValue().toString();
                    if (currentlyDate.equals(today_date)){
                        Log.d(TAG, "onCreateView: inside date condition");
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (mAuth.getCurrentUser() != null) {
                                    if (dataSnapshot.child("User_Data").child(mAuth.getCurrentUser().getUid()).hasChild("today_vedio_count")) {
                                        int count = Integer.valueOf(dataSnapshot.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("today_vedio_count").getValue().toString());
                                        countVedio = count;

                                    } else {
                                        countVedio = 0;
                                        reference.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("today_vedio_count").setValue(countVedio);
                                    }
                                    if (dataSnapshot.child("User_Data").child(mAuth.getCurrentUser().getUid()).hasChild("today_earned_coins")) {
                                        int coins = Integer.valueOf(dataSnapshot.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("today_earned_coins").getValue().toString());
                                        todaycoins = coins;

                                    } else {
                                        todaycoins = 0;
                                        reference.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("today_earned_coins").setValue(todaycoins);
                                    }
                                    if (dataSnapshot.child("User_Data").child(mAuth.getCurrentUser().getUid()).hasChild("today_clicks")) {
                                        int clicks = Integer.valueOf(dataSnapshot.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("today_clicks").getValue().toString());
                                        today_Clicks = clicks;

                                    } else {
                                        today_Clicks = 0;
                                        reference.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("today_clicks").setValue(today_Clicks);

                                    }
                                    if (dataSnapshot.child("User_Data").child(mAuth.getCurrentUser().getUid()).hasChild("Daily_Bonus_Count")) {
                                        countDailybonus = Integer.valueOf(dataSnapshot.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("Daily_Bonus_Count").getValue().toString());


                                    } else {
                                        countDailybonus = 0;
                                        reference.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("Daily_Bonus_Count").setValue(countDailybonus);
                                    }
                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }

                            @Override
                            protected void finalize() throws Throwable {
                                super.finalize();
                            }
                        });


                    } else {
                        if (mAuth.getCurrentUser() != null) {
                            countVedio = 0;
                            reference.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("today_vedio_count").setValue(countVedio);
                            todaycoins = 0;
                            reference.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("today_earned_coins").setValue(todaycoins);
                            today_Clicks = 0;
                            reference.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("today_clicks").setValue(today_Clicks);
                            countDailybonus = 0;
                            reference.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("Daily_Bonus_Count").setValue(countDailybonus);


                        }

                    }





                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        timer.schedule(doAsynchronousTask, 0, Long.parseLong("5000"));
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                final String currentDate = android.text.format.DateFormat.format("dd/MM/yyyy", new Date((new Date()).getTime())).toString();
//                final String uid = mAuth.getCurrentUser().getUid();
//
//            }
//
//            @Override
//            protected void finalize() throws Throwable {
//                super.finalize();
//
//            }
//        }, 10000);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mAuth.getCurrentUser() != null) {
                    if (dataSnapshot.child("User_Data").child(mAuth.getCurrentUser().getUid()).hasChild("total_Coins")) {
                        totalCoins = Integer.valueOf(dataSnapshot.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("total_Coins").getValue().toString());

                    } else {
                        totalCoins = 0;
                        reference.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("total_Coins").setValue(totalCoins);

                    }
                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        final PollFish.ParamsBuilder paramsBuilder = new PollFish.ParamsBuilder("7a0df0f4-14bb-4a3d-bff6-ead8962a05a0")
                .indicatorPosition(Position.BOTTOM_LEFT)
                .requestUUID(mAuth.getCurrentUser().getUid())
                .userLayout((ViewGroup) getActivity().getWindow().getDecorView())
                .releaseMode(true)
                .customMode(true)
                .build();
        paramsBuilder.pollfishSurveyReceivedListener(new PollfishSurveyReceivedListener() {
            @Override
            public void onPollfishSurveyReceived(boolean b, int i) {
                Toast.makeText(getContext(), "Wait Survey is loaded"+i, Toast.LENGTH_SHORT).show();

            }
        }).pollfishSurveyCompletedListener(new PollfishSurveyCompletedListener() {
            @Override
            public void onPollfishSurveyCompleted(boolean b, int i) {
                Toast.makeText(getContext(), "You Won" + "" + "+" + survey_count_add_for_vedio + " " + " vedio count", Toast.LENGTH_SHORT).show();
                //count_spin[0]=count_spin[0]+1;
                //textView_spin_count.setText(String.valueOf(count_spin[0]));
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        countVedio = Integer.valueOf
                                (dataSnapshot.child("User_Data")
                                        .child(mAuth.getCurrentUser().getUid()).child("today_vedio_count").getValue().toString());


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                countVedio=dailyvediocount-survey_count_add_for_vedio;
                reference.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("today_vedio_count").setValue(countVedio);
                AppLovinSdk.getInstance(getContext()).getAdService().loadNextAd(AppLovinAdSize.INTERSTITIAL, new AppLovinAdLoadListener() {
                    @Override
                    public void adReceived(AppLovinAd ad) {
                        loadedAd = ad;
                    }

                    @Override
                    public void failedToReceiveAd(int errorCode) {
                        // Look at AppLovinErrorCodes.java for list of error codes.
                    }
                });
                AppLovinInterstitialAdDialog interstitialAd = AppLovinInterstitialAd.create(AppLovinSdk.getInstance(getContext()), getContext());

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
        }).pollfishSurveyNotAvailableListener(new PollfishSurveyNotAvailableListener() {
            @Override
            public void onPollfishSurveyNotAvailable() {
                Toast.makeText(getContext(), "Survey not available", Toast.LENGTH_SHORT).show();
                builder.setTitle("Survey Not Recived");
                builder.setMessage("Currently Survey not available! Try Again.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(), "Survey not available", Toast.LENGTH_SHORT).show();

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();


            }
        }).pollfishUserNotEligibleListener(new PollfishUserNotEligibleListener() {
            @Override
            public void onUserNotEligible() {
                Toast.makeText(getContext(), "You are not Eligible", Toast.LENGTH_SHORT).show();

            }
        }).build();

        myIncent = AppLovinIncentivizedInterstitial.create(getContext());
        myIncent.preload(null);
        watch_earn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: watch and earn click");



                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (mAuth.getCurrentUser() != null) {
                            if (dataSnapshot.child("User_Data").child(mAuth.getCurrentUser().getUid()).hasChild("today_vedio_count")) {


                                countVedio = Integer.valueOf(dataSnapshot.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("today_vedio_count").getValue().toString());
                            }else{
                                countVedio =0;
                                reference.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("today_vedio_count").setValue(countVedio);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                if (countVedio < dailyvediocount) {
                    Log.d(TAG, "onClick: " + countVedio + dailyvediocount);


                    playRewarded();


                } else if (countVedio >= dailyvediocount) {
                    epicDialog.setContentView(R.layout.earn_vedio_start_survey);
                    close_popup = (ImageView) epicDialog.findViewById(R.id.close_popup);
                    //watchpoint= (TextView) epicDialog.findViewById(R.id.earnpointswatch);
                    startServey = (Button) epicDialog.findViewById(R.id.takesurvey);
                    //watchpoint.setText(vedio_rewarded_point);


                    close_popup.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AppLovinSdk.getInstance(getContext()).getAdService().loadNextAd(AppLovinAdSize.INTERSTITIAL, new AppLovinAdLoadListener() {
                                @Override
                                public void adReceived(AppLovinAd ad) {
                                    loadedAd = ad;
                                }

                                @Override
                                public void failedToReceiveAd(int errorCode) {
                                    // Look at AppLovinErrorCodes.java for list of error codes.
                                }
                            });
                            AppLovinInterstitialAdDialog interstitialAd = AppLovinInterstitialAd.create(AppLovinSdk.getInstance(getContext()), getContext());

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

                            PollFish.initWith(getActivity(), paramsBuilder);
                            PollFish.show();
                            AppLovinSdk.getInstance(getContext()).getAdService().loadNextAd(AppLovinAdSize.INTERSTITIAL, new AppLovinAdLoadListener() {
                                @Override
                                public void adReceived(AppLovinAd ad) {
                                    loadedAd = ad;
                                }

                                @Override
                                public void failedToReceiveAd(int errorCode) {
                                    // Look at AppLovinErrorCodes.java for list of error codes.
                                }
                            });
                            AppLovinInterstitialAdDialog interstitialAd = AppLovinInterstitialAd.create(AppLovinSdk.getInstance(getContext()), getContext());

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


                }

            }

        });
        final String currentDate = android.text.format.DateFormat.format("dd/MM/yyyy", new Date((new Date()).getTime())).toString();

        if (currentDate.equals(lastLoginDate.getText().toString())) {
            Log.d(TAG, "onCreateView: inside date condition");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {


        }

        activity_bonus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mAuth.getCurrentUser() != null) {


                    if (countDailybonus == 0) {
                        countDailybonus = 1;
                        reference.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("Daily_Bonus_Count").setValue(countDailybonus);

                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                todaycoins = Integer.valueOf(dataSnapshot.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("today_earned_coins").getValue().toString());
                                totalCoins = Integer.valueOf(dataSnapshot.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("total_Coins").getValue().toString());


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                daily_rewarde_points = Integer.valueOf(dataSnapshot.child("Update offer data").child("daily_rewarded_coins").getValue().toString());


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        todaycoins = todaycoins + daily_rewarde_points;
                        totalCoins = totalCoins + daily_rewarde_points;
                        epicDialog.setContentView(R.layout.popupforearnmoneydailybonus);
                        close_popup = (ImageView) epicDialog.findViewById(R.id.close_popup);
                        dailypoint = (TextView) epicDialog.findViewById(R.id.earnpointsbonus);
                        okdaily = (Button) epicDialog.findViewById(R.id.dailybonus_button);
                        dailypoint.setText("+" + String.valueOf(daily_rewarde_points) + " " + "Points");


                        close_popup.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AppLovinSdk.getInstance(getContext()).getAdService().loadNextAd(AppLovinAdSize.INTERSTITIAL, new AppLovinAdLoadListener() {
                                    @Override
                                    public void adReceived(AppLovinAd ad) {
                                        loadedAd = ad;
                                    }

                                    @Override
                                    public void failedToReceiveAd(int errorCode) {
                                        // Look at AppLovinErrorCodes.java for list of error codes.
                                    }
                                });
                                AppLovinInterstitialAdDialog interstitialAd = AppLovinInterstitialAd.create(AppLovinSdk.getInstance(getContext()), getContext());

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
                        okdaily.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AppLovinSdk.getInstance(getContext()).getAdService().loadNextAd(AppLovinAdSize.INTERSTITIAL, new AppLovinAdLoadListener() {
                                    @Override
                                    public void adReceived(AppLovinAd ad) {
                                        loadedAd = ad;
                                    }

                                    @Override
                                    public void failedToReceiveAd(int errorCode) {
                                        // Look at AppLovinErrorCodes.java for list of error codes.
                                    }
                                });
                                AppLovinInterstitialAdDialog interstitialAd = AppLovinInterstitialAd.create(AppLovinSdk.getInstance(getContext()), getContext());

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
                        reference.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("today_earned_coins").setValue(todaycoins);
                        reference.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("total_Coins").setValue(totalCoins);
                        countDailybonus = 1;
                        reference.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("Daily_Bonus_Count").setValue(countDailybonus);


                    } else {

                        builder.setTitle("Claim Rejected");
                        builder.setMessage("You have already earned Daily Bonus. Try again after 24 hrs");

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // startAd();

                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }


                }
            }
        });




        return view;
    }
    public void setProgress(int Seconds, final ProgressBar progressBar) {

        new CountDownTimer(Seconds * 1000 + 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
//                .setText(" Resend after" + " " + String.format("%02d", minutes)
//                        + ":" + String.format("%02d", seconds));
                progressBar.setVisibility(View.VISIBLE);

            }

            public void onFinish() {
//                tv.setText("Wait or Resend Code");
//                resend.setEnabled(true);
//                resend.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        }.start();
    }



    public void playRewarded() {
        // Check to see if a rewarded video is available.
        if (myIncent.isAdReadyToDisplay()) {
            // A rewarded video is available.  Call the show method with the listeners you want to use.
            // We will use the display listener to preload the next rewarded video when this one finishes.
            myIncent.show(getActivity(), new
                    AppLovinAdRewardListener() {
                        @Override
                        public void userRewardVerified(AppLovinAd appLovinAd, Map<String, String> map) {
                            epicDialog.setContentView(R.layout.popupforearnmoneywatchvedio);
                            close_popup = (ImageView) epicDialog.findViewById(R.id.close_popup);
                            watchpoint = (TextView) epicDialog.findViewById(R.id.earnpointswatch);
                            watchagin = (Button) epicDialog.findViewById(R.id.watch_agin_button);
                            watchpoint.setText("+" + String.valueOf(vedio_rewarded_point) + "  " + "Points");


                            close_popup.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AppLovinSdk.getInstance(getContext()).getAdService().loadNextAd(AppLovinAdSize.INTERSTITIAL, new AppLovinAdLoadListener() {
                                        @Override
                                        public void adReceived(AppLovinAd ad) {
                                            loadedAd = ad;
                                        }

                                        @Override
                                        public void failedToReceiveAd(int errorCode) {
                                            // Look at AppLovinErrorCodes.java for list of error codes.
                                        }
                                    });
                                    AppLovinInterstitialAdDialog interstitialAd = AppLovinInterstitialAd.create(AppLovinSdk.getInstance(getContext()), getContext());

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
                            watchagin.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AppLovinSdk.getInstance(getContext()).getAdService().loadNextAd(AppLovinAdSize.INTERSTITIAL, new AppLovinAdLoadListener() {
                                        @Override
                                        public void adReceived(AppLovinAd ad) {
                                            loadedAd = ad;
                                        }

                                        @Override
                                        public void failedToReceiveAd(int errorCode) {
                                            // Look at AppLovinErrorCodes.java for list of error codes.
                                        }
                                    });
                                    AppLovinInterstitialAdDialog interstitialAd = AppLovinInterstitialAd.create(AppLovinSdk.getInstance(getContext()), getContext());

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
                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    todaycoins = Integer.valueOf(dataSnapshot.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("today_earned_coins").getValue().toString());
                                    totalCoins = Integer.valueOf(dataSnapshot.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("total_Coins").getValue().toString());
                                    vedio_rewarded_point = Integer.valueOf(dataSnapshot.child("Update offer data").child("vedio_rewarded_coins").getValue().toString());

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            todaycoins = todaycoins + vedio_rewarded_point;
                            reference.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("today_earned_coins").setValue(todaycoins);
                            totalCoins = totalCoins + vedio_rewarded_point;

                            reference.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("total_Coins").setValue(totalCoins);

                        }

                        @Override
                        public void userOverQuota(AppLovinAd appLovinAd, Map<String, String> map) {


                        }

                        @Override
                        public void userRewardRejected(AppLovinAd appLovinAd, Map<String, String> map) {

                        }

                        @Override
                        public void validationRequestFailed(AppLovinAd appLovinAd, int i) {

                        }

                        @Override
                        public void userDeclinedToViewAd(AppLovinAd appLovinAd) {

                        }
                    }, new AppLovinAdVideoPlaybackListener() {
                @Override
                public void videoPlaybackBegan(AppLovinAd appLovinAd) {


                }

                @Override
                public void videoPlaybackEnded(AppLovinAd appLovinAd, double v, boolean b) {

                }
            }, new AppLovinAdDisplayListener() {
                @Override
                public void adDisplayed(AppLovinAd appLovinAd) {
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child("User_Data").child(mAuth.getCurrentUser().getUid()).hasChild("today_vedio_count")) {
                                countVedio = Integer.valueOf(dataSnapshot.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("today_vedio_count").getValue().toString());

                            } else {
                                countVedio = 5;
                                reference.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("today_vedio_count").setValue(countVedio);
                            }
                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    countVedio = countVedio + 1;
                    reference.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("today_vedio_count").setValue(countVedio);
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child("User_Data").child(mAuth.getCurrentUser().getUid()).hasChild("today_clicks")) {

                                today_Clicks = Integer.valueOf(dataSnapshot.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("today_clicks").getValue().toString());
                            } else {
                                today_Clicks = 0;
                                reference.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("today_clicks").setValue(today_Clicks);
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    today_Clicks = today_Clicks + 1;
                    reference.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("today_clicks").setValue(today_Clicks);

                    // A rewarded video is being displayed.
                }

                @Override
                public void adHidden(AppLovinAd appLovinAd) {
                    // A rewarded video was closed.  Preload the next video now.  We won't use a load listener.
                    myIncent.preload(null);
                }

            });
            new AppLovinAdLoadListener() {
                @Override
                public void adReceived(AppLovinAd appLovinAd) {
                    Toast.makeText(getContext(), "Wait untill vedio is loading", Toast.LENGTH_LONG).show();

                }

                @Override
                public void failedToReceiveAd(int i) {
                    Toast.makeText(getContext(), "No vedio contains", Toast.LENGTH_LONG).show();

                }
            };


        } else {
            Log.d(TAG, "playRewarded: no add contains try agin in new session");
            Toast.makeText(getContext(),"Currently add not available",Toast.LENGTH_LONG).show();
            // No ad is currently available.  Perform failover logic...

        }
    }

    public void updateAllActivity() {
        Log.d(TAG, "updateAllActivity: update all data in firebase");
        final String uid = mAuth.getCurrentUser().getUid().toString();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    if (dataSnapshot.child("User_Data").child(mAuth.getCurrentUser().getUid()).hasChild("today_earned_coins")) {
                        int coins = Integer.valueOf(dataSnapshot.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("today_earned_coins").getValue().toString());

                        today_Earn_TextView.setText(String.valueOf(coins));
                    } else {
                        int cointoday = 0;
                        today_Earn_TextView.setText(String.valueOf(cointoday));
                    }
                    if (dataSnapshot.child("User_Data").child(mAuth.getCurrentUser().getUid()).hasChild("today_clicks")) {
                        int clicks = Integer.valueOf(dataSnapshot.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("today_clicks").getValue().toString());
                        totalclicktextView.setText(String.valueOf(clicks));

                    } else {
                        int clicks = 0;
                        totalclicktextView.setText(String.valueOf(clicks));

                    }
                    if (dataSnapshot.child("User_Data").child(mAuth.getCurrentUser().getUid()).hasChild("total_Coins")) {
                        int totalcoins = Integer.valueOf(dataSnapshot.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("total_Coins").getValue().toString());
                        totalCoinstextview.setText(String.valueOf(totalcoins));

                    } else {
                        int coins_ = 0;
                        totalCoinstextview.setText(String.valueOf(coins_));
                    }
                    if (dataSnapshot.child("Update offer data").hasChild("vedio_rewarded_coins")) {
                        vedio_rewarded_point = Integer.valueOf(dataSnapshot.child("Update offer data").child("vedio_rewarded_coins").getValue().toString());
                    } else {
                        vedio_rewarded_point = 5;
                        reference.child("Update offer data").child("vedio_rewarded_coins").setValue(vedio_rewarded_point);
                    }
                    if (dataSnapshot.child("Update offer data").hasChild("daily_rewarded_coins")) {
                        daily_rewarde_points = Integer.valueOf(dataSnapshot.child("Update offer data").child("daily_rewarded_coins").getValue().toString());

                    } else {
                        daily_rewarde_points = 10;
                        reference.child("Update offer data").child("daily_rewarded_coins").setValue(daily_rewarde_points);

                    }

                    if (dataSnapshot.child("Update offer data").hasChild("daily_vedio_count")) {
                        dailyvediocount = Integer.valueOf(dataSnapshot.child("Update offer data").child("daily_vedio_count").getValue().toString());

                    } else {
                        dailyvediocount = 5;
                        reference.child("Update offer data").child("daily_vedio_count").setValue(dailyvediocount);
                    }
                    if (dataSnapshot.child("Update offer data").hasChild("survey_count_vedio")) {
                        survey_count_add_for_vedio = Integer.valueOf(dataSnapshot.child("Update offer data").child("survey_count_vedio").getValue().toString());

                    } else {
                        survey_count_add_for_vedio = 5;
                        reference.child("Update offer data").child("survey_count_vedio").setValue(survey_count_add_for_vedio);
                    }

//                if (dataSnapshot.child("Update offer data").hasChild("daily_rewarded_coins")) {
//                    daily_rewarde_points = Integer.valueOf(dataSnapshot.child("Update offer data").child("daily_rewarded_coins").getValue().toString());
//
//                } else {
//                    daily_rewarde_points = 10;
//                    reference.child("Update offer data").child("daily_rewarded_coins").setValue(daily_rewarde_points);
//
//                }
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        watchandearndailybonus.setText(String.valueOf("Watch video ads and earn" + " " + "+" + vedio_rewarded_point + " " + "coins"));
        dailybonusbody.setText(String.valueOf(" Click here to earn" + " " + "+" + daily_rewarde_points + " " + "coins daily"));
    }
}




