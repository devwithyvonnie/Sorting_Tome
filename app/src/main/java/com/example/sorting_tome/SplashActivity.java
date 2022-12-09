package com.example.sorting_tome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {


    private FirebaseAuth fireBaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        fireBaseAuth = FirebaseAuth.getInstance();
        // Start Main Screen after 2 seconds
        new Handler().postDelayed(() -> {
            //start main screen
            startActivity(new Intent(SplashActivity.this , MainActivity.class));
            finish(); //finish
        }, 2000 ); //2000 = 2 seconds
    }

    private void checkUser(){
        FirebaseUser firebaseUser = fireBaseAuth.getCurrentUser();
        if(firebaseUser == null)
        {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }
    else{

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String userType = ""+snapshot.child("userType").getValue();
                            if(userType.equals("user")){
                                startActivity(new Intent(SplashActivity.this, DashboardUserActivity.class));
                            }
                            else if(userType.equals("admin")){
                                startActivity(new Intent(SplashActivity.this, DashboardAdminActivity.class));
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        }

    }

}