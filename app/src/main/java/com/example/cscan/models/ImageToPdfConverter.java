package com.example.cscan.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ImageToPdfConverter {

    public static void convertToPdf(List<String> imagePaths, String outputFilePath) {
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream(outputFilePath));
            document.open();

            for (String imagePath : imagePaths) {
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                Image image = Image.getInstance(imagePath);
                image.scaleToFit(document.getPageSize());
                document.add(image);
            }

            document.close();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }
}
