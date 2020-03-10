# SaaS 点播上传SDK使用文档

## 准备工作
必要的权限配置

```
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />

```

必要的依赖包添加

```
    implementation 'com.aliyun.dpa:oss-android-sdk:+'
    implementation 'com.squareup.okhttp3:okhttp:4.4.0'
    implementation 'com.squareup.okio:okio:2.4.3'
```

## 开始使用
1. 初始化上传SDK；

```
    /**
     *
     * @param context   必需使用ApplicationContext
     * @param APPKey    与pc端获取SDK权限信息界面 APPKey对应
     * @param SecretKey 与pc端获取SDK权限信息界面 SecretKey对应
     * @param callback  初始化回调，可以为null
     */
    public void initData(Context context, String APPKey, String SecretKey, final NormalCallback callback) {};
    
//范例
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
```

2.创建上传工具对象；

```
    /**
     * Flash 活动上传工具
     * @param file   待上传文件，支持格式："rmvb", "mp4", "avi", "wmv", "mkv", "flv", "mov", "mp3", "wav"
     * @param videoName  生成的回放标题，不能为空
     * @param subjectName 生成的活动标题，不能为空
     * @param callback  上传回调
     * @return  生成的实例对象，生成失败则反回null
     */
    public UploadPart createFlashUploadPart(File file, String videoName, String subjectName
            , VhUploadCallback callback) {}
 
     /**
     * H5 活动上传工具
     * @param file   待上传文件，支持格式："rmvb", "mp4", "avi", "wmv", "mkv", "flv", "mov", "mp3", "wav"
     * @param videoName  生成的回放标题，不能为空
     * @param subjectName 生成的活动标题，不能为空
     * @param callback  上传回调
     * @return  生成的实例对象，生成失败则反回null
     */
    public UploadPart createH5UploadPart(File file, String videoName, String subjectName
            , VhUploadCallback callback) {}

//范例
part = VhalluploadKit.getInstance().createFlashUploadPart(new File(filePath), videoName, subjectName, uploadCallback);

part = VhalluploadKit.getInstance().createH5UploadPart(new File(filePath), videoName, subjectName, uploadCallback);
                        
```

3.通过上传工具对象上传文件并返回活动id；

```
    /**
     * 简单上传
     */
    public void sampleUpload() {}
    
    /**
     * 断点续传
     * 断点记录不在本地持久保存
     */
    @Override
    public void resumableUpload() {}
    
    /**
     * 断点续传
     * 断点记录在本地长久保持，取消上传时默认删除
     *
     * @param recordDirectory 本地记录保存位置
     */
    @Override
    public void resumableUpload(String recordDirectory) {}
    
    /**
     * 断点续传
     *
     * @param recordDirectory 断点记录文件路径
     * @param deleteOnCancel  取消上传时是否删除断点文件，默认true； false 不删除，true删除
     */
    @Override
    public void resumableUpload(String recordDirectory, boolean deleteOnCancel) {}
    
    //范例
    part.sampleUpload();
    
```

### 上传回调说明
```
public interface VhUploadCallback {

	/**
     * 
     * @param fileKey  文件信息
     * @param webinarId 生成的活动id
     * @param recordId  回放id
     */
    void onSuccess(String fileKey, String webinarId, String recordId);

    void onFailure(int errorCode, String errMsg);

    void onProgress(long currentSize, long totalSize);

}
```

### 错误码说明

|错误码关键字|错误码|错误信息|备注|
|:-:|:-:|:-:|:-:|
|ERROR_INIT|1000|SDk is not available,please init first!|SDK不可用|
|ERROR_CONNECT|1001|Error connect,please try later!|连接异常|
|ERROR_FILE|1002|Not supported file types!|文件不支持|
|ERROR_FILE_UPLOADING|1003|The file is being uploaded!|文件上传中|
|ERROR_JSON|1004|JSONException|解析异常|
|ERROR_SERVICE|1005|OSS service error|服务异常|
|ERROR_PARAM|1006|Param error|参数错误|