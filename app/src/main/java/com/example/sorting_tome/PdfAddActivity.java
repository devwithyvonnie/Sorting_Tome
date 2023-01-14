package com.example.sorting_tome;

import static android.content.ContentValues.TAG;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.example.sorting_tome.models.ModelCategory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.example.sorting_tome.databinding.ActivityPdfAddBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class PdfAddActivity extends AppCompatActivity {

    private ActivityPdfAddBinding binding;



    //FirebaseAuth
    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    private ArrayList<ModelCategory> categoryArrayList;

  private Uri pdfUri = null;

  private static final int PDF_PICK_CODE = 1000;

    //Tag for bug
    private static final String Tag = "ADD_PDF_Tag";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        loadPdfCategories();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("PLease wait");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.attachBtn.setOnClickListener(new View.OnClickListener(){
            @Override
                public void onClick(View v){
              pdfPickIntent();
            }
        });

        binding.categoryTv.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v ){
            categoryPickDialog();

        }
        });

        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v)
            {
                validateData();
            }

        });

    }
private String title = "",description = "", category = "";
    private void validateData(){
        Log.d(TAG, "validateData: validating data...");

//get data
        title = binding.titleEt.getText().toString().trim();
        description = binding.descriptionTilEt.getText().toString().trim();
        category = binding.categoryTv.getText().toString().trim();
//validate data

        if(TextUtils.isEmpty(title)){
            Toast.makeText(this,"Enter Title...", Toast.LENGTH_SHORT).show();


        }
        else if(TextUtils.isEmpty(description)){
            Toast.makeText(this,"Enter Description...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(category)){
            Toast.makeText(this,"Pick Category...", Toast.LENGTH_SHORT).show();
        }
        else if(pdfUri == null){
            Toast.makeText(this,"Pick Pdf...", Toast.LENGTH_SHORT).show();
        }
        else {
            uploadPdfToStorage();

        }
    }

    private void uploadPdfToStorage(){
        Log.d(TAG, "uploadPdfToStorage: upload to storage...");
//show progress
        progressDialog.setMessage("Upload Pdf...");
        progressDialog.show();

        long timestamp = System.currentTimeMillis();
        String filePathAndName = "Books/" +timestamp;

        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "onSuccess: PDF uploaded to storage...");
                        Log.d(TAG, "onSuccess: getting pdf url...");


                        //get pdf url
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        String uploadPdfUrl = ""+ uriTask.getResult();
                        //upload to firebase db
                        uploadPdfInfoToDB(uploadPdfUrl, timestamp);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onFailure: PDF upload failed due" + e.getMessage());
                        Toast.makeText(PdfAddActivity.this,  "PDF upload failed due to"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void uploadPdfInfoToDB(String uploadPdfUrl, long timestamp) {
        // upload Pdf info to firebase db
        Log.d(TAG, "uploadPdfToStorage: uploading Pdf to firebase db...");

        progressDialog.setMessage("Uploading pdf info");
        String uid = firebaseAuth.getUid();
        // setup Data
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", ""+uid);
        hashMap.put("id", ""+timestamp);
        hashMap.put("description", ""+description);
        hashMap.put("url", ""+uploadPdfUrl);
        hashMap.put("category", ""+category);
        hashMap.put("timestamp", ""+timestamp);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(""+timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Log.d(TAG, "OnSuccess: Successfully uploaded...");
                        Toast.makeText(PdfAddActivity.this, "Succssfuly uploaded...", Toast.LENGTH_SHORT).show();

                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onFailure: Failed to upload to db due to "+e.getMessage());
                        Toast.makeText(PdfAddActivity.this, "Failed to upload to db due to"+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });










    }

    private void pdfPickIntent(){
        Log.d(Tag, "pdfPickIntent: starting pdf pick intent");

       Intent intent = new Intent();
       intent.setType("application/pdf");
       intent.setAction(Intent.ACTION_GET_CONTENT);

       startActivityForResult(Intent.createChooser(intent, "Select PDF"), PDF_PICK_CODE);

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if(requestCode == PDF_PICK_CODE){
            Log.d(TAG, "onActivityResult: PDF Picked");

            pdfUri = data.getData();
            Log.d(TAG, "onActivityResult: URI;" + pdfUri);

        }
    }

    else
        {
            Log.d(TAG, "onActivityResult: Cancelled picking pdf");
            Toast.makeText(this, "cancelled picking pdf",Toast.LENGTH_SHORT).show();

        }

    }



    private void loadPdfCategories() {
        Log.d(TAG, "loadPdfCategories: Loading pdf categories...");
        categoryArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryArrayList.clear();//clear before adding data
                for(DataSnapshot ds: snapshot.getChildren()){

                    ModelCategory model = ds.getValue(ModelCategory.class);
                    categoryArrayList.add(model);
                    Log.d(TAG, "onDataChange: "+model.getCategory());

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void categoryPickDialog() {
        Log.d(TAG, "categoryPickDialog: showing category pick dialog");
        String[] categoriesArray = new String[  categoryArrayList.size()];
        for(int i =0; i<categoryArrayList.size();i++) {
            categoriesArray[i] = categoryArrayList.get(i).getCategory();


        }

        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Category")
                .setItems(categoriesArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //handle item click
                        //get clicked item from list
                        String category = categoriesArray[which];
                        binding.categoryTv.setText(category);

                        Log.d(TAG, "onClick: Selected Category"+ category);
                    }
                })
                .show();

    }

    }