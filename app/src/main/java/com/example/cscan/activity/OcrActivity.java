package com.example.cscan.activity;


import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import com.example.cscan.R;
import com.example.cscan.main_utils.Constant;
import com.example.cscan.models.Datas;
import com.example.cscan.models.Images;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

/**
 * @author 30415
 */

public class OcrActivity extends AppCompatActivity {
    SpinKitView process;
    ImageButton copy, edit, share;
    ImageView back;
    ShapeableImageView imageButton;
    MaterialTextView textView;
    private Bitmap bitmap;
    ActivityResultLauncher<Intent> editActivityResultLauncher;

    int cnt = 0;
    ContactsContract.Data ocrItem;
    private boolean oldItem;
    String imageUri = "", dateStr = "";
    private boolean changed = false;
    TextRecognizer textRecognizer;
    private int engineNum;
    String str = "";

    private void initUi() {
        process = findViewById(R.id.spin_kit);
        share = findViewById(R.id.buttonShare);
        edit = findViewById(R.id.buttonEdit);
        copy = findViewById(R.id.buttonCopy);
        textView = findViewById(R.id.textView);
        back = findViewById(R.id.iv_back);
        back.setOnClickListener(v -> {
            Constant.type = "Doc";
            startActivity(new Intent(OcrActivity.this, DataActivity.class));
            finish();
        });
        copy.setOnClickListener(view -> {
            if (!Objects.requireNonNull(textView.getText()).toString().isEmpty()) {
                ClipData clipData = ClipData.newPlainText("", textView.getText());
                ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(clipData);
                Toast.makeText(this, getString(R.string.copied), Toast.LENGTH_SHORT).show();
            }
        });
        edit.setOnClickListener(v -> {
            if (!textView.getText().toString().isEmpty()) {
                Intent intent = new Intent(this, EditActivity.class);
                intent.putExtra("text", textView.getText().toString());
                editActivityResultLauncher.launch(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this, textView, "text"));
            }
        });
        share.setOnClickListener(v -> {
            if (!textView.getText().toString().isEmpty()) {
                try {
                    CharSequence res = textView.getText();
                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                    sendIntent.setType("text/plain");
                    sendIntent.putExtra(Intent.EXTRA_TEXT, res.toString());
                    startActivity(Intent.createChooser(sendIntent, getString(R.string.share)));
                } catch (Exception e) {
                    Toast.makeText(this, "???", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (changed) {
                finish();
            } else {
                finishAfterTransition();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initEditActivityResultLauncher() {
        editActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getData() != null && result.getResultCode() == Activity.RESULT_OK) {
                textView.setText(result.getData().getCharSequenceExtra("text"));
                changed = true;
                overwriteFileContent(Constant.data.getDataValue(), textView.getText().toString());
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);

        initUi();
        initEditActivityResultLauncher();
        if (Constant.type.equals("Doc")) {
            textView.setText(readFileContent(Constant.data.getDataValue()));

        } else {
            textRecognizer = TextRecognition.getClient(new ChineseTextRecognizerOptions.Builder().build());

            for (Images images : Constant.imagesList) {
                String imageDataPath = images.getImageData(); // Đường dẫn ảnh

                // Kiểm tra đường dẫn ảnh không rỗng
                if (!TextUtils.isEmpty(imageDataPath)) {
                    File imageFile = new File(imageDataPath);
                    Uri imageDataUri = Uri.fromFile(imageFile);

                    try {
                        // Tải hình ảnh từ Uri
                        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                        process.setVisibility(View.VISIBLE);
                        // Thực hiện các tác vụ với bitmap ở đây
                        getText(bitmap);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }


    }

    private void getText(Bitmap bitmap) {
        textView.setText("");
        textView.setHint(R.string.processing___);
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        textRecognizer.process(image)
                .addOnSuccessListener(visionText -> {

                    StringBuilder stringBuilder = new StringBuilder();
                    for (Text.TextBlock textBlock : visionText.getTextBlocks()) {
                        for (Text.Line textLines : textBlock.getLines()) {
                            stringBuilder.append(textLines.getText()).append(" ");
                        }
                        stringBuilder.append("\n");
                    }
                    if (stringBuilder.toString().isEmpty()) {
                        textView.setText(getString(R.string.nothing));
                    } else {
                        cnt++;
                        textView.setText(textView.getText() + stringBuilder.toString());
                        if (cnt == Constant.imagesList.size()) {
                            process.setVisibility(View.INVISIBLE);
                            Toast.makeText(OcrActivity.this, "hello", Toast.LENGTH_LONG).show();
                            String outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                            String outputFilePath = outputDir + File.separator + Constant.group_current.getGroupName() + ".txt";
                            overwriteFileContent(outputFilePath, textView.getText().toString());
                            Datas doc = new Datas(Constant.group_current.getGroupName(), outputFilePath, Constant.Doc.getDataTypeId(), Constant.getDateTime("yyyy-MM-dd  hh:mm a"));
                            DataActivity.insertData(doc);
                            Constant.type = "Doc";
                        }
                    }
                }).addOnFailureListener(f -> textView.setText(getString(R.string.nothing)));
    }

    public void overwriteFileContent(String filePath, String content) {
        try {
            File file = new File(filePath);

            // Mở tệp với chế độ ghi (overwrite)
            FileOutputStream outputStream = new FileOutputStream(file, false);

            // Ghi nội dung mới vào tệp
            outputStream.write(content.getBytes());
            outputStream.close();
        } catch (IOException e) {
            Log.e("FileIO", "Error overwriting file: " + e.getMessage());
        }
    }

    public String readFileContent(String filePath) {
        StringBuilder content = new StringBuilder();

        try {
            File file = new File(filePath);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line);
                content.append('\n');
            }

            bufferedReader.close();
            fileReader.close();
        } catch (IOException e) {
            Log.e("FileIO", "Error reading file: " + e.getMessage());
        }

        return content.toString();
    }

}
