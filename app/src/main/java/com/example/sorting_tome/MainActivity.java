package com.example.sorting_tome;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.example.sorting_tome.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //view binding
        com.example.sorting_tome.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //loginBtn click
        binding.loginBtn.setOnClickListener(view -> startActivity(new Intent( MainActivity.this,LoginActivity.class)));

        //skipBtn click
        binding.skipBtn.setOnClickListener(view -> startActivity(new Intent( MainActivity.this, DashboardAdminActivity.class)));
    }
}