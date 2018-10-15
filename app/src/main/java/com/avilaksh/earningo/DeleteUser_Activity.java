package com.avilaksh.earningo;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.irvingryan.VerifyCodeView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class DeleteUser_Activity extends AppCompatActivity {
    //Tarun Pal
    VerifyCodeView otp;
    Button delete;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationStateChangedCallbacks;
    PhoneAuthProvider.ForceResendingToken resendingToken;
    private static final String TAG = "DeleteUser_Activity";
    String phoneVerificationId;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Button resend;
    TextView verificationinfo, timer;
    FirebaseAuth mAuth;
    ProgressBar progressBardelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user_);
        otp = (VerifyCodeView) findViewById(R.id.textViewOtp);
        delete = (Button) findViewById(R.id.buttondelete);
        resend = (Button) findViewById(R.id.buttonresend);
        verificationinfo = (TextView) findViewById(R.id.textviewverificationdetail);
        progressBardelete=(ProgressBar)findViewById(R.id.progressBar4);
        progressBardelete.setVisibility(View.GONE);
        timer = (TextView) findViewById(R.id.timer);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        verificationinfo.setText(String.valueOf("Verification code has been sent to" + " " + mAuth.getCurrentUser().getPhoneNumber() + " " + "via SMS"));
        resend.setEnabled(false);
        reauthenticatesendcode();
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyCode();

            }
        });
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reauthenticatesendcode();
                reverseTimer(60, timer);
            }
        });


    }

    private void verifyCode() {
        String code = otp.getText();
        progressBardelete.setVisibility(View.VISIBLE);
        if (!code.isEmpty()) {

            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneVerificationId, code);
            reAuthenticate(credential);
            progressBardelete.setVisibility(View.VISIBLE);



        } else {
            Toast.makeText(DeleteUser_Activity.this, "Enter otp ", Toast.LENGTH_LONG).show();
            progressBardelete.setVisibility(View.GONE);



        }


    }

    public void reverseTimer(int Seconds, final TextView tv) {

        new CountDownTimer(Seconds * 1000 + 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                tv.setText(" Resend after" + " " + String.format("%02d", minutes)
                        + ":" + String.format("%02d", seconds));

            }

            public void onFinish() {
                tv.setText("Wait or Resend Code");
                resend.setEnabled(true);
                resend.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    private void reAuthenticate(


            PhoneAuthCredential credential) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {


                                user.delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "User account deleted.");
                                                    databaseReference.child("User_Data").child(user.getUid()).removeValue();
                                                    Intent intent = new Intent(DeleteUser_Activity.this, WelcomeActivity.class);
                                                    startActivity(intent);
                                                    finish();

                                                }
                                            }
                                        });
                            } else if(!task.isSuccessful()){
                                Toast.makeText(DeleteUser_Activity.this, "Incorrect Verification Code ", Toast.LENGTH_LONG).show();


                            }


                        }
                    });
        }



    }

    public void reauthenticatesendcode() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String phone = user.getPhoneNumber();
            setUpVerificationCallback();
            PhoneAuthProvider.getInstance().verifyPhoneNumber(phone
                    , 60,
                    TimeUnit.SECONDS, this, verificationStateChangedCallbacks, resendingToken);
            reverseTimer(60, timer);
            progressBardelete.setVisibility(View.VISIBLE);
            // progressBarotp.setVisibility(View.GONE);


        }


    }

    private void setUpVerificationCallback() {
        verificationStateChangedCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted: +verified");
//                loginScreen.setVisibility(View.GONE);
//                otpVerificationlayout.setVisibility(View.VISIBLE);
//                progressBar.setVisibility(View.GONE);
                //signInWithPhoneAuthCredentials(phoneAuthCredential);
                progressBardelete.setVisibility(View.GONE);


            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(DeleteUser_Activity.this, "Incorrect otp or Something Wrong", Toast.LENGTH_LONG).show();
                progressBardelete.setVisibility(View.GONE);


            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                phoneVerificationId = s;
                resendingToken = forceResendingToken;
//                verify.setEnabled(true);
//                 otp.setEnabled(true);
//                resend.setEnabled(true);
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {

                super.onCodeAutoRetrievalTimeOut(s);

            }
        };

    }
}
