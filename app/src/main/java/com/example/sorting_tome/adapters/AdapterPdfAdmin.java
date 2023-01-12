package com.example.sorting_tome.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sorting_tome.databinding.RowPdfAdminBinding;
import com.example.sorting_tome.models.ModelPdf;
import com.github.barteksc.pdfviewer.PDFView;


import java.util.ArrayList;



public class AdapterPdfAdmin extends RecyclerView.Adapter<AdapterPdfAdmin.HolderPdfAdmin> {
    //Context
    private Context context;

    //arrayList to hold list of data of type ModelPdf
    private ArrayList<ModelPdf> pdfArrayList;

    //view binding row_pdf_admin.xml
    private RowPdfAdminBinding binding;

    //constructor
    public AdapterPdfAdmin(Context context, ArrayList<ModelPdf> pdfArray) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
    }

    @NonNull
    @Override
    public HolderPdfAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //bind layout using view binding
        binding = RowPdfAdminBinding.inflate(LayoutInflater.from(context), parent, false);

        return new HolderPdfAdmin(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPdfAdmin holder, int position) {
        /*---Get data, set data, handle clicks, etc---*/

        //get data
        ModelPdf model = pdfArrayList.get(position);
        String title = model.getTitle();
        String description = model.getDescription();
        long timestamp = model.getTimestamp();
    }

    @Override
    public int getItemCount() {
        return pdfArrayList.size(); // return number of records / list size
    }

    /*---View Holder Class for row_pdf_admin.xml---*/
    class HolderPdfAdmin extends RecyclerView.ViewHolder{

        //UI Views of row_pdf_admin.xml
        PDFView pdfView;
        ProgressBar progressBar;
        TextView titleTv, descriptionTv, categoryTv, sizeTv, dateTv;
        ImageButton moreBtn;



        public HolderPdfAdmin(@NonNull View itemView) {
            super(itemView);

            //init UI Views
            pdfView = binding.pdfView;
            progressBar = binding.progressBar;
            titleTv = binding.titleTv;
            descriptionTv = binding.descriptionTV;
            categoryTv = binding.categoryTv;
            sizeTv = binding.sizeTv;
            dateTv = binding.dateTv;
            moreBtn = binding.moreBtn;

        }
    }
}
