package com.example.cscan.activity;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.cscan.R;
import com.example.cscan.main_utils.Constant;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.VideoResult;
import com.otaliastudios.cameraview.controls.Facing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ScannerActivity extends AppCompatActivity {
    public CameraView cameraView;
    protected ImageView iv_take_picture;
    protected ImageView iv_back_camera;
    private static final String FRAGMENT_DIALOG = "dialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_scanner);
        cameraView = (CameraView) findViewById(R.id.cameraView);
        cameraView.setLifecycleOwner(this);
        init();
        bindView();


    }

    @Override
    public void onPause() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                cameraView.close();
            }
        }, 200);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, "android.permission.CAMERA") == PackageManager.PERMISSION_GRANTED) {
            cameraView.open();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.CAMERA")) {
            // ConfirmationDialogFragment.newInstance(R.string.camera_permission_confirmation, new String[]{"android.permission.CAMERA"}, 1, R.string.camera_permission_not_granted).show(getSupportFragmentManager(), FRAGMENT_DIALOG);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.CAMERA"}, 1);
        }


    }


    private void init() {
        iv_take_picture = (ImageView) findViewById(R.id.iv_take_picture);
        iv_back_camera = (ImageView) findViewById(R.id.iv_back_camera);
        // iv_gallery = (ImageView) findViewById(R.id.iv_gallery);


    }

    public void onClick(View view) {
        int i = 1;
        switch ((view.getId())) {
            case R.id.iv_back_camera:
                finish();
                return;
            case R.id.iv_take_picture:
                if (cameraView != null) {
//                    progressBar.setVisibility(View.VISIBLE);
                    cameraView.takePicture();
                    return;
                } else {
                    return;
                }
            case R.id.iv_gallery:
                openImagePicker();
                return;
            case R.id.iv_switch_camera:

                if (cameraView != null) {
                    Facing facing = cameraView.getFacing();

                    if (facing == Facing.BACK) {
                        cameraView.setFacing(Facing.FRONT);
                    } else {
                        cameraView.setFacing(Facing.BACK);
                    }
//                    cameraView.setFacing(i);
                    return;
                }
                return;
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    private void bindView() {

        if (cameraView != null) {
            cameraView.addCameraListener(new CameraListener() {
                @Override
                public void onPictureTaken(PictureResult result) {
                    // Access the raw data if needed.
                    byte[] data = result.getData();
                    Log.e(TAG, "onPictureTaken " + data.length);
                    Toast.makeText(ScannerActivity.this, "Picture Taken", Toast.LENGTH_SHORT).show();

                    File externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    File file = new File(externalFilesDir, System.currentTimeMillis() + ".jpg");
                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        fileOutputStream.write(data);
                        fileOutputStream.close();
                        Bitmap decodeByteArray = BitmapFactory.decodeByteArray(data, 0, data.length);
                        Bitmap scalePreserveRatio = scalePreserveRatio(decodeByteArray, decodeByteArray.getWidth() / 2, decodeByteArray.getHeight() / 2);
                        int cameraPhotoOrientation = getCameraPhotoOrientation(file.getPath());
                        Matrix matrix = new Matrix();
                        matrix.postRotate((float) cameraPhotoOrientation);
                        Constant.original = Bitmap.createBitmap(scalePreserveRatio, 0, 0, scalePreserveRatio.getWidth(), scalePreserveRatio.getHeight(), matrix, true);

                        startActivity(new Intent(ScannerActivity.this, CropDocumentActivity.class));
                        finish();
                    } catch (IOException e) {
                        Log.w(TAG, "Cannot write to " + file, e);
                    }

                }

                @Override
                public void onVideoTaken(VideoResult result) {
                    // A Video was taken!
                }

                // And much more
            });
        }

    }


    public static int getCameraPhotoOrientation(String str) {
        try {
            int attributeInt = new ExifInterface(new File(str).getAbsolutePath()).getAttributeInt(androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION, 1);
            if (attributeInt == 3) {
//                return CipherSuite.TLS_DHE_PSK_WITH_NULL_SHA256;
            }
            if (attributeInt == 6) {
                return 90;
            }
            if (attributeInt != 8) {
                return 0;
            }
            return 270;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static Bitmap scalePreserveRatio(Bitmap bitmap, int i, int i2) {
        if (i2 <= 0 || i <= 0 || bitmap == null) {
            return bitmap;
        }
        float f = (float) i;
        float width = (float) bitmap.getWidth();
        float f2 = f / width;
        float f3 = (float) i2;
        float height = (float) bitmap.getHeight();
        float f4 = f3 / height;
        int floor = (int) Math.floor((double) (width * f2));
        int floor2 = (int) Math.floor((double) (f2 * height));
        if (floor > i || floor2 > i2) {
            floor = (int) Math.floor((double) (width * f4));
            floor2 = (int) Math.floor((double) (height * f4));
        }
        Bitmap createScaledBitmap = Bitmap.createScaledBitmap(bitmap, floor, floor2, true);
        Bitmap createBitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        float f5 = ((float) floor) / ((float) floor2);
        float f6 = f / f3;
        float f7 = 0.0f;
        float f8 = f5 >= f6 ? 0.0f : ((float) (i - floor)) / 2.0f;
        if (f5 >= f6) {
            f7 = ((float) (i2 - floor2)) / 2.0f;
        }
        canvas.drawBitmap(createScaledBitmap, f8, f7, (Paint) null);
        return createBitmap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                Constant.original = bitmap;
                startActivity(new Intent(ScannerActivity.this, CropDocumentActivity.class));
                finish();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static class ConfirmationDialogFragment extends DialogFragment {
        private static final String ARG_MESSAGE = "message";
        private static final String ARG_NOT_GRANTED_MESSAGE = "not_granted_message";
        private static final String ARG_PERMISSIONS = "permissions";
        private static final String ARG_REQUEST_CODE = "request_code";

        public static ConfirmationDialogFragment newInstance(int i, String[] strArr, int i2, int i3) {
            ConfirmationDialogFragment confirmationDialogFragment = new ConfirmationDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(ARG_MESSAGE, i);
            bundle.putStringArray(ARG_PERMISSIONS, strArr);
            bundle.putInt(ARG_REQUEST_CODE, i2);
            bundle.putInt(ARG_NOT_GRANTED_MESSAGE, i3);
            confirmationDialogFragment.setArguments(bundle);
            return confirmationDialogFragment;
        }

        @Override
        public Dialog onCreateDialog(Bundle bundle) {
            final Bundle arguments = getArguments();
            return new AlertDialog.Builder(getActivity()).setMessage(arguments.getInt(ARG_MESSAGE)).setPositiveButton(android.R.string.ok, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String[] stringArray = arguments.getStringArray(ConfirmationDialogFragment.ARG_PERMISSIONS);
                    if (stringArray != null) {
                        ActivityCompat.requestPermissions(ConfirmationDialogFragment.this.getActivity(), stringArray, arguments.getInt(ConfirmationDialogFragment.ARG_REQUEST_CODE));
                        return;
                    }
                    throw new IllegalArgumentException();
                }
            }).setNegativeButton(android.R.string.cancel, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(ConfirmationDialogFragment.this.getActivity(), arguments.getInt(ConfirmationDialogFragment.ARG_NOT_GRANTED_MESSAGE), Toast.LENGTH_SHORT).show();
                }
            }).create();
        }
    }
}