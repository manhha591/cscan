package com.example.cscan.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.cscan.R;
import com.example.cscan.main_utils.Constant;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.shockwave.pdfium.PdfDocument;

import java.io.File;

public class PdfViewerActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int PDF_SELECTION_CODE = 200;

    private PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);
        setTitle(Constant.data.getDataName());
        pdfView = findViewById(R.id.pdfView);
        displayPDF(Constant.data.getDataValue());
        Constant.type = "Pdf";
    }

    private void displayPDF(String filePath) {
        File file = new File(filePath);
        Uri fileUri = Uri.fromFile(file);

        pdfView.fromUri(fileUri)
                .defaultPage(0)
                .enableSwipe(true)
                .enableDoubletap(true)
                .swipeHorizontal(false)
                .onLoad(nbPages -> {
                    // Handle onLoad event if needed
                })
                .onPageChange((page, pageCount) -> {
                    // Handle onPageChange event if needed
                })
                .scrollHandle(new DefaultScrollHandle(this))
                .enableAnnotationRendering(true)
                .password(null)
                .pageFitPolicy(FitPolicy.WIDTH)
                .load();
    }
}