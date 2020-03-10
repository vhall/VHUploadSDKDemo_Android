package com.vhall.uploaddemo;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.vhall.vhuploadsdk.NormalCallback;
import com.vhall.vhuploadsdk.UploadPart;
import com.vhall.vhuploadsdk.VhUploadCallback;
import com.vhall.vhuploadsdk.VhalluploadKit;

import java.io.File;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    private EditText edtAppKey, edtSecrketKey, edtVideoName, edtSubjectName;
    private TextView tvInitMsg, tvFileMsg, tvResult;
    private ProgressBar progressBar;
    private RadioGroup rgUploadType;
    private int uploadType = 1; //默认上传flash
    private String filePath = "";

    private String APPKey = "";// 微吼APPKEY
    private String APPSecrket = "";// 微吼SECRETKEY
    private static int REQUEST_STORAGE = 2;
    private static int REQUEST_PICK_FILE = 1;
    private UploadPart part;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtAppKey = findViewById(R.id.edt_input_key);
        edtAppKey.setText(APPKey);
        edtSecrketKey = findViewById(R.id.edt_input_secret);
        edtSecrketKey.setText(APPSecrket);
        edtVideoName = findViewById(R.id.edt_file_rename);
        edtSubjectName = findViewById(R.id.edt_subject_name);
        tvInitMsg = findViewById(R.id.tv_init_msg);
        tvFileMsg = findViewById(R.id.tv_file_msg);
        tvResult = findViewById(R.id.tv_result);
        rgUploadType = findViewById(R.id.rg_upload_type);
        progressBar = findViewById(R.id.pb_upload);
        progressBar.setMax(100);

        rgUploadType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_type_flash:
                        uploadType = 1;
                        break;
                    case R.id.rb_type_h5:
                        uploadType = 2;
                        break;
                }
            }
        });
    }

    public void initSDK(View view) {
        APPKey = edtAppKey.getText().toString().trim();
        APPSecrket = edtSecrketKey.getText().toString().trim();
        VhalluploadKit.getInstance().initData(getApplicationContext(), APPKey, APPSecrket, new NormalCallback() {
            @Override
            public void onSuccess() {
                tvInitMsg.setText("初始化成功");
            }

            @Override
            public void onFaiure(int errorCode, String msg) {
                tvInitMsg.setText("初始化失败：" + msg);
            }
        });
    }


    public void chooseFile(View view) {
        if (getStoragePermission()) {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_PICK_FILE);
        }

    }

    private boolean getStoragePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);
        return false;
    }

    public void sampleUpload(View view) {
        if (VhalluploadKit.getInstance().isEnable()) {
            String subjectName = edtSubjectName.getText().toString().trim();
            String videoName = edtVideoName.getText().toString().trim();
            if (TextUtils.isEmpty(subjectName)) {
                Toast.makeText(this, "回放标题不能为空！", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(videoName)) {
                Toast.makeText(this, "活动标题不能为空！", Toast.LENGTH_SHORT).show();
            } else {
                if (uploadType == 1) {
                    part = VhalluploadKit.getInstance().createFlashUploadPart(new File(filePath), videoName, subjectName, uploadCallback);
                } else {
                    part = VhalluploadKit.getInstance().createH5UploadPart(new File(filePath), videoName, subjectName, uploadCallback);
                }
                if (part != null) {
                    part.sampleUpload();
                }
            }
        } else {
            tvInitMsg.setText("SDK 不可用，请先初始化");
        }

    }

    public void resumeableUpload(View view) {
        if (VhalluploadKit.getInstance().isEnable()) {
            String subjectName = edtSubjectName.getText().toString().trim();
            String videoName = edtVideoName.getText().toString().trim();
            if (TextUtils.isEmpty(subjectName)) {
                Toast.makeText(this, "回放标题不能为空！", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(videoName)) {
                Toast.makeText(this, "活动标题不能为空！", Toast.LENGTH_SHORT).show();
            } else {
                if (uploadType == 1) {
                    part = VhalluploadKit.getInstance().createFlashUploadPart(new File(filePath), videoName, subjectName, uploadCallback);
                } else {
                    part = VhalluploadKit.getInstance().createH5UploadPart(new File(filePath), videoName, subjectName, uploadCallback);
                }
                if (part != null) {
                    part.resumableUpload();
                }
            }
        } else {
            tvInitMsg.setText("SDK 不可用，请先初始化");
        }
    }

    public void cancelUpload(View view) {
        if (part != null) {
            part.cancel();
        }
    }

    VhUploadCallback uploadCallback = new VhUploadCallback() {
        @Override
        public void onSuccess(String fileKey, String webinarId, String recordId) {
            tvResult.setText("文件：" + fileKey + "上传成功，生成的活动id为:" + webinarId + "  文件id为：" + recordId);
        }

        @Override
        public void onFailure(int errorCode, String errMsg) {
            tvResult.setText("上传失败，erroCode：" + errorCode + "  errMsg:" + errMsg);
        }

        @Override
        public void onProgress(long currentSize, long totalSize) {
            progressBar.setProgress((int) (currentSize * 100 / totalSize));
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 选取图片的返回值
        if (requestCode == REQUEST_PICK_FILE) {
            //
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();

                if (uri != null) {
                    filePath = getPath(this, uri);
                    if (filePath != null) {
                        File file = new File(filePath);
                        if (file.exists()) {
                            filePath = file.toString();
                            tvFileMsg.setText(filePath);
                        }
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
//                Log.i(TAG,"isExternalStorageDocument***"+uri.toString());
//                Log.i(TAG,"docId***"+docId);
//                以下是打印示例：
//                isExternalStorageDocument***content://com.android.externalstorage.documents/document/primary%3ATset%2FROC2018421103253.wav
//                docId***primary:Test/ROC2018421103253.wav
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                try {
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
//                Log.i(TAG,"isMediaDocument***"+uri.toString());
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
//            Log.i(TAG,"content***"+uri.toString());
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
//            Log.i(TAG,"file***"+uri.toString());
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_PICK_FILE);

            }
        }
    }
}
