package com.avilaksh.earningo;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Date;

public class Home_Main_Activity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    AlertDialog.Builder builder;
    private static final String TAG = "Home_Main_Activity";
    private int RC_SIGN_IN=1;
    private GoogleApiClient mGoogleApiClient;
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home__main_);
        if (isNetworkConnectionAvailable()) {
            firebaseDatabase = FirebaseDatabase.getInstance();
            reference = firebaseDatabase.getReference();
            mAuth = FirebaseAuth.getInstance();
            //open_Menu=(ImageView)findViewById(R.id.menuButton);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setUpViewPager();
            setSupportActionBar(toolbar);
            //getSupportActionBar().setDisplayShowHomeEnabled(true);
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setTitle(Html.fromHtml("<b>EarninGo</b>"));
            toolbar.setTitleTextColor(getResources().getColor(R.color.active));
            builder = new AlertDialog.Builder(Home_Main_Activity.this);
//        MyFirebaseMessagingService myFirebaseMessagingService=new MyFirebaseMessagingService();
//        myFirebaseMessagingService.sendNotification("tarun",null,"True");
           // FirebaseMessaging.getInstance().subscribeToTopic("offer");
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("110706985796-jt38lq248a5epo64mfolql26sno8m829.apps.googleusercontent.com")
                    .requestEmail()
                    .build();
            mGoogleApiClient=new GoogleApiClient.Builder(getApplicationContext()).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                @Override
                public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                    Toast.makeText(Home_Main_Activity.this,"Something Wrong",Toast.LENGTH_LONG).show();
                }
            }).addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                    .build();
           dialog =new ProgressDialog(Home_Main_Activity.this);

        }




    }

    private void subscribeToPushService() {
        FirebaseMessaging.getInstance().subscribeToTopic("offer");

        Log.d("AndroidBash", "Subscribed");
        Toast.makeText(Home_Main_Activity.this, "Subscribed", Toast.LENGTH_SHORT).show();

        String token = FirebaseInstanceId.getInstance().getToken();

        // Log and toast
        Log.d("AndroidBash", token);
        Toast.makeText(Home_Main_Activity.this, token, Toast.LENGTH_SHORT).show();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuNotification:
                Intent intent = new Intent(Home_Main_Activity.this, Notification_Activity.class);
                startActivity(intent);
                return true;

            case R.id.checkupdate:
                openPlaystore();


                return true;
            case R.id.menuDeleteAccount:
                builder.setTitle("Delete Account");
                builder.setMessage("Are you sure you want to delete your account?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAccount();





                    }


                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Home_Main_Activity.this, WelcomeActivity.class);
                        startActivity(intent);
                        finish();

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);

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
                deleteuser(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google account delete failed", e);
                dialog.dismiss();
                Toast.makeText(Home_Main_Activity.this,"Account delete failed",Toast.LENGTH_LONG).show();

                // ...
            }
        }
    }

    private void deleteuser(GoogleSignInAccount account) {
            Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());


            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "User account deleted.");
                            reference.child("User_Data").child(user.getUid()).removeValue();
                            if (mAuth.getCurrentUser()==null) {
                                Intent intent = new Intent(Home_Main_Activity.this, WelcomeActivity.class);
                                startActivity(intent);
                                finish();
                                dialog.dismiss();
                            }



                        }
                    });

                }
            });
        }


    private void deleteAccount() {
        Intent signInIntent =Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
        dialog = ProgressDialog.show(Home_Main_Activity.this, "Deleting Your Account",
                "Loading. Please wait!", true);
    }


    public void setUpViewPager() {
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new MyOfferFragment());
        pagerAdapter.addFragment(new MyAccountFragment());
        pagerAdapter.addFragment(new AppPolicies());
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText("My Offers");
        tabLayout.getTabAt(1).setText("My Account");
        tabLayout.getTabAt(2).setText("APP Policy");
        tabLayout.setTabTextColors(getResources().getColor(R.color.pageindicatorinvisible), getResources().getColor(R.color.active));
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.active));


    }

    public void checkNetworkConnection() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("No internet Connection");
        builder.setMessage("Please turn on internet connection to continue");
        builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Home_Main_Activity.super.onBackPressed();


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

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {

        if (isNetworkConnectionAvailable()) {
            if (doubleBackToExitPressedOnce) {


                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
            final String currentDate = android.text.format.DateFormat.format("dd/MM/yyyy", new Date((new Date()).getTime())).toString();

            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                reference.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("Last_Login_Date").setValue(currentDate);

            } else {
                Intent intent = new Intent(Home_Main_Activity.this, WelcomeActivity.class);
                startActivity(intent);
                finish();
            }

        }


    }

    @Override
    protected void onDestroy() {
        if (isNetworkConnectionAvailable()) {
            final String currentDate = android.text.format.DateFormat.format("dd/MM/yyyy", new Date((new Date()).getTime())).toString();
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                reference.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("Last_Login_Date").setValue(currentDate);

            } else {
                Intent intent = new Intent(Home_Main_Activity.this, WelcomeActivity.class);
                startActivity(intent);
                finish();
            }
        }


        super.onDestroy();
    }

    @Override
    protected void onResume() {
        if (isNetworkConnectionAvailable()) {
            final String currentDate = android.text.format.DateFormat.format("dd/MM/yyyy", new Date((new Date()).getTime())).toString();

            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                reference.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("Last_Login_Date").setValue(currentDate);

            } else {
                Intent intent = new Intent(Home_Main_Activity.this, WelcomeActivity.class);
                startActivity(intent);
                finish();
            }
        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        if (isNetworkConnectionAvailable()) {
            final String currentDate = android.text.format.DateFormat.format("dd/MM/yyyy", new Date((new Date()).getTime())).toString();
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                reference.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("Last_Login_Date").setValue(currentDate);

            } else {
                Intent intent = new Intent(Home_Main_Activity.this, WelcomeActivity.class);
                startActivity(intent);
                finish();
            }
        }
        super.onStop();
    }

    @Override
    protected void onRestart() {
        if (isNetworkConnectionAvailable()) {
            final String currentDate = android.text.format.DateFormat.format("dd/MM/yyyy", new Date((new Date()).getTime())).toString();

            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                reference.child("User_Data").child(mAuth.getCurrentUser().getUid()).child("Last_Login_Date").setValue(currentDate);

            } else {
                Intent intent = new Intent(Home_Main_Activity.this, WelcomeActivity.class);
                startActivity(intent);
                finish();
            }
        }

        super.onRestart();

    }

    public void openPlaystore() {

        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + Home_Main_Activity.this.getPackageName())));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + Home_Main_Activity.this.getPackageName())));
        }
    }

}
