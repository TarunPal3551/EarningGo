package com.avilaksh.earningo;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.irvingryan.VerifyCodeView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class WelcomeActivity extends AppCompatActivity {
    private ViewPager mpager;
    private int[] layouts = {R.layout.firstslide, R.layout.secondslide
            , R.layout.fourthslide};
    private MpagerAdapter mpagerAdapter;
    public LinearLayout dot_layout;
    public ImageView[] dots;
    RelativeLayout loginScreen;
    private static final String TAG = "WelcomeActivity";
    TextView policyCheckbox;
    String phoneVerificationId;
    FirebaseAuth mAuth;
    int countForcheckbox = 0;
    ProgressBar progressBar, progressBarotp;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    Dialog epicDialog;
    CheckBox policycheckbox;
    Button submitpolicy;
    private SignInButton googeleSignin;
    private int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleApiClient;
    Button signInbutton;
    ProgressDialog dialog;
    ///0 for unchecked and 1 for checked


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        dialog =new ProgressDialog(WelcomeActivity.this);
//        timer = (TextView) findViewById(R.id.timer);
//        verificationinfo = (TextView) findViewById(R.id.verification_text_info);
//        validate = (Button) findViewById(R.id.validatenumberwithotp);
//        verify = (Button) findViewById(R.id.verifyotp);
//        resend = (Button) findViewById(R.id.resend);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        //progressBarotp = (ProgressBar) findViewById(R.id.progressBar2);
//        progressBarotp.setVisibility(View.GONE);
        googeleSignin = (SignInButton) findViewById(R.id.googlesigninbutton);

        signInbutton = (Button) findViewById(R.id.loginbutton);
        signInbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googeleSignin.performClick();
            }
        });


        loginScreen = (RelativeLayout) findViewById(R.id.loginsectionlayout);
//        otpVerificationlayout = (RelativeLayout) findViewById(R.id.verifyotplayout);
//        phone_number = (EditText) findViewById(R.id.edittextloginmobilenumber);

        //  otp = (VerifyCodeView) findViewById(R.id.edittextotp);
        policyCheckbox = (TextView) findViewById(R.id.checkBoxlogin);
        String text = "By clicking on Sign up button, you agree to our <b><i><u>Terms of use</u></i></b> and <b><i><u>Payout policy</u></i></b> that you have read and accepted.";
        policyCheckbox.setText(Html.fromHtml(text));
        welcomeScreenTopScreenAllStuff();
        loginScreen.setVisibility(View.VISIBLE);

        //otpVerificationlayout.setVisibility(View.GONE);
        epicDialog = new Dialog(this);

        policyCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                epicDialog.setContentView(R.layout.welcome_policy);
                submitpolicy = (Button) epicDialog.findViewById(R.id.buttonsubmit);
                policycheckbox = (CheckBox) epicDialog.findViewById(R.id.checkedbox);
                submitpolicy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (policycheckbox.isChecked()) {
                            Toast.makeText(WelcomeActivity.this, "Thanks You ! for accepting Terms and Policy", Toast.LENGTH_LONG).show();

                            epicDialog.dismiss();
                        } else {
                            Toast.makeText(WelcomeActivity.this, "Accept Term and Policy", Toast.LENGTH_LONG).show();


                        }

                    }
                });
                epicDialog.onBackPressed();
                epicDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                epicDialog.show();
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("110706985796-jt38lq248a5epo64mfolql26sno8m829.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext()).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                Toast.makeText(WelcomeActivity.this, "Something Wrong", Toast.LENGTH_LONG).show();
            }
        }).addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

//        validate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (isNetworkConnectionAvailable()) {
//                    if (phone_number.getText().toString().isEmpty()) {
//                        Toast.makeText(WelcomeActivity.this, "Please Enter Your Mobile Number ", Toast.LENGTH_LONG).show();
//
//                    } else if (countForcheckbox == 0) {
//                        Toast.makeText(WelcomeActivity.this, "Please confirm app policies ", Toast.LENGTH_LONG).show();
//                    } else if (phone_number.getText().toString().isEmpty() && countForcheckbox == 0) {
//                        Toast.makeText(WelcomeActivity.this, "Provide All details", Toast.LENGTH_LONG).show();
//
//                    } else {
//                        progressBar.setVisibility(View.VISIBLE);
//                        sendCode();
//
//
//                    }
//                }
//
//            }
//        });
//        verify.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                progressBarotp.setVisibility(View.VISIBLE);
//                verifyCode();
//
//            }
//        });
//        resend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                progressBarotp.setVisibility(View.VISIBLE);
//                resendCode();
//                reverseTimer(60, timer);
//            }
//        });
        googeleSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        setGooglePlusButtonText(googeleSignin, "Sign up with Google");


    }

    public void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                dialog.dismiss();
                Toast.makeText(WelcomeActivity.this,"Sign up failed",Toast.LENGTH_LONG).show();
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithCredential:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                                    // Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                                    updateUI(null);
                                }

                                // ...
                            }
                        });
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
        dialog = ProgressDialog.show(WelcomeActivity.this, "Sign up With Google",
                "Loading. Please wait!", true);
    }

    public void checkNetworkConnection() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("No internet Connection");
        builder.setMessage("Please turn on internet connection to continue");
        builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                WelcomeActivity.super.onBackPressed();


            }
        });
        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public boolean isNetworkConnectionAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();
        if (isConnected) {
            Log.d("Network", "Connected");
            return true;
        } else {
            checkNetworkConnection();
            Log.d("Network", "Not Connected");
            return false;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser);
        }


    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            // reference.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("User_Mobile_Number").setValue(mAuth.getCurrentUser().getPhoneNumber());
            // String currentDateTimeString = DateFormat.getDateTimeInstance()
            //  .format(new Date());
//            reference.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("Last_Login_Date").setValue(currentDateTimeString);


            Intent intent = new Intent(WelcomeActivity.this, Home_Main_Activity.class);
            startActivity(intent);
            finish();

        } else {
            Intent intent = new Intent(WelcomeActivity.this, WelcomeActivity.class);
            startActivity(intent);
            finish();


        }
    }

//    public void reverseTimer(int Seconds, final TextView tv) {
//
//        new CountDownTimer(Seconds * 1000 + 1000, 1000) {
//            public void onTick(long millisUntilFinished) {
//                int seconds = (int) (millisUntilFinished / 1000);
//                int minutes = seconds / 60;
//                seconds = seconds % 60;
//                tv.setText("Resend after" + " " + String.format("%02d", minutes)
//                        + ":" + String.format("%02d", seconds));
//
//            }
//
//            public void onFinish() {
//                tv.setText("Wait or Resend Code");
//                resend.setEnabled(true);
//                resend.setVisibility(View.VISIBLE);
//            }
//        }.start();
//    }

//    public void sendCode() {
//        String phoneNumber ="+91"+phone_number.getText().toString();
//        Log.d(TAG, "sendCode: send code");
//        setUpVerificationCallback();
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber
//                , 60,
//                TimeUnit.SECONDS, this, verificationStateChangedCallbacks);
//        reverseTimer(60, timer);
//
//
//    }

//    private void setUpVerificationCallback() {
//
//        verificationStateChangedCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//            @Override
//            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
//                Log.d(TAG, "onVerificationCompleted: +verified");
//                loginScreen.setVisibility(View.GONE);
//                otpVerificationlayout.setVisibility(View.VISIBLE);
//                progressBar.setVisibility(View.GONE);
//                verificationinfo.setText(String.valueOf("Verification code has been sent to " + phone_number.getText().toString() + " " + "via SMS"));
//                //signInWithPhoneAuthCredentials(phoneAuthCredential);
//
//            }
//
//            @Override
//            public void onVerificationFailed(FirebaseException e) {
//                Toast.makeText(WelcomeActivity.this, "Enter Valid Mobile Number ", Toast.LENGTH_LONG).show();
//                progressBar.setVisibility(View.GONE);
//                progressBarotp.setVisibility(View.GONE);
//                Log.d(TAG, "onVerificationFailed: " + e);
//
//
//            }
//
//            @Override
//            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//                phoneVerificationId = s;
//                resendingToken = forceResendingToken;
//                verify.setEnabled(true);
//                otp.setEnabled(true);
//                // resend.setEnabled(true);
//                resend.setVisibility(View.GONE);
//
//            }
//
//            @Override
//            public void onCodeAutoRetrievalTimeOut(String s) {
//
//                super.onCodeAutoRetrievalTimeOut(s);
//
//            }
//        };
//    }
//
//    public void verifyCode() {
//        String code = otp.getText();
//
//        if (!code.isEmpty()) {
//            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneVerificationId, code);
//            signInWithPhoneAuthCredentials(credential);
//
//        } else {
//            Toast.makeText(WelcomeActivity.this, "Enter Verification Code", Toast.LENGTH_LONG).show();
//            progressBarotp.setVisibility(View.GONE);
//        }
//
//
//    }
//
//
//    public void resendCode() {
//        String phoneNumber = phone_number.getText().toString();
//        setUpVerificationCallback();
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber
//                , 60,
//                TimeUnit.SECONDS, this, verificationStateChangedCallbacks, resendingToken);
//        progressBarotp.setVisibility(View.GONE);
//
//
//    }
//
//    private void signInWithPhoneAuthCredentials(PhoneAuthCredential credential) {
//        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if (task.isSuccessful()) {
//                    FirebaseUser user = mAuth.getCurrentUser();
//                    updateUI(user);
//
////                    Intent intent = new Intent(WelcomeActivity.this, Home_Main_Activity.class);
////                    startActivity(intent);
////                    finish();
//
//                } else if(!task.isSuccessful()) {
//                    Toast.makeText(WelcomeActivity.this, "Incorrect Verification Code ", Toast.LENGTH_LONG).show();
//                    progressBarotp.setVisibility(View.GONE);
//
//
//                }
//
//
//            }
//        });
//
//
//    }


//    public  void updatePhoneNumber(PhoneAuthCredential credential){
//
//        mAuth.
//
//    }


    ////-------------------------------------Login Page top screen All stuff---------------------------------------------------
    public void welcomeScreenTopScreenAllStuff() {


        mpager = (ViewPager) findViewById(R.id.viewpager);
        mpagerAdapter = new MpagerAdapter(layouts, this);
        mpager.setAdapter(mpagerAdapter);
        dot_layout = findViewById(R.id.dotslayout);
        createdots(0);
        mpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                createdots(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        Timer timer = new Timer();
        timer.schedule(new MyTimerTask(), 2000, 2000);


    }

    public void createdots(int current_position) {
        if (dot_layout != null) {
            dot_layout.removeAllViews();
        }
        dots = new ImageView[layouts.length];
        for (int i = 0; i < layouts.length; i++) {
            dots[i] = new ImageView(this);
            if (i == current_position) {
                dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.active_dots));
            } else {
                dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.default_dots));
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(4, 0, 4, 0);
            dot_layout.addView(dots[i], params);
        }
    }

    public class MyTimerTask extends TimerTask {
        int position = mpager.getCurrentItem();


        @Override
        public void run() {
            WelcomeActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (position == 0) {

                        position++;
                        mpager.setCurrentItem(position);

                    } else if (position == 1) {

                        position++;
                        mpager.setCurrentItem(position);

                    } else if (position == 2) {

                        position++;
                        mpager.setCurrentItem(position);


                    } else if (position == 3) {

                        position = 0;
                        mpager.setCurrentItem(position);

                    }

                }
            });
        }

        ////--------------------------------------------------------///////--------------------------------------------------------------------------

    }


    ////--------------------------------------------------------///////--------------------------------------------------------------------------


}




