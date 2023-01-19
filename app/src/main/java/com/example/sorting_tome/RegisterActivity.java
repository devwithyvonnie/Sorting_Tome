package com.example.sorting_tome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Region;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.sorting_tome.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    //view binding

    private ActivityRegisterBinding binding;

    //firebase auth
    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);


        //handle click, go back
        binding.backBtn.setOnClickListener(v -> onBackPressed());

        //handle click, begin register
        binding.registerBtn.setOnClickListener(v -> validateData());
    }

    private String name = "", email = "", password = "";

        private void  validateData() {
            /*before creating account*/
            name = binding.nameEt.getText().toString().trim();
            email = binding.emailEt.getText().toString().trim();
            password = binding.passwordEt.getText().toString().trim();
            String cPassword = binding.passwordEt.getText().toString().trim();

            if (TextUtils.isEmpty(name)) {

                Toast.makeText(this, "Enter your name ", Toast.LENGTH_SHORT).show();
            }
            else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Invalid Email pattern...! ", Toast.LENGTH_SHORT).show();
            }
            else if (TextUtils.isEmpty(password))  {
                Toast.makeText(this, "Enter password...! ", Toast.LENGTH_SHORT).show();
            }
           else if (TextUtils.isEmpty(cPassword))  {
                Toast.makeText(this, "Invalid Email pattern...! ", Toast.LENGTH_SHORT).show();
            }

           else if(!password.equals(cPassword))
           {
               Toast.makeText(this,"Password does not match...!", Toast.LENGTH_SHORT).show();
           }
           else{
               createUserAccount();
            }

        }
        private void createUserAccount()
        {
            //progress


            progressDialog.setMessage("Creating account");
            progressDialog.show();

            //create user in firebase auth
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        //account creation success
                       updateUserInfo();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, ""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    });


        }

    private void updateUserInfo() {
        progressDialog.setMessage("Saving user info...");

        //timestamp
        long timestamp = System.currentTimeMillis();
        // get current user id
        String uid;
        uid = firebaseAuth.getUid();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("email", email);
        hashMap.put("name",name);
        hashMap.put("profileImage", "");
        hashMap.put("userType", "user");
        hashMap.put("timestamp",timestamp);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        assert uid != null;
        ref.child(uid)
                .setValue(hashMap)
                .addOnSuccessListener(unused -> {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this,"Account created...", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(RegisterActivity.this, DashboardUserActivity.class));
                    finish();

                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }

}