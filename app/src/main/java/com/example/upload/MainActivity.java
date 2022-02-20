package com.example.upload;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class MainActivity extends AppCompatActivity {

    private WebView mWebView;
    private WebSettings mWebSettings;
    public static final int IMAGE_SELECTOR_REQ = 1;
    private ValueCallback mFilePathCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 웹뷰 시작
        mWebView = (WebView) findViewById(R.id.webview);
        //

        mWebView.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {

                try {
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                    request.setMimeType(mimeType);
                    request.addRequestHeader("User-Agent", userAgent);
                    request.setDescription("Downloading file");
                    String fileName = contentDisposition.replace("inline; filename=", "");
                    fileName = fileName.replaceAll("\"", "");
                    request.setTitle(fileName);
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                    DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    dm.enqueue(request);
                    Toast.makeText(getApplicationContext(), "Downloading File", Toast.LENGTH_LONG).show();
                } catch (Exception e) {

                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        // Should we show an explanation?w
                        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            Toast.makeText(getBaseContext(), "첨부파일 다운로드를 위해\n동의가 필요합니다.", Toast.LENGTH_LONG).show();
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    110);
                        } else {
                            Toast.makeText(getBaseContext(), "첨부파일 다운로드를 위해\n동의가 필요합니다.", Toast.LENGTH_LONG).show();
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    110);
                        }
                    }
                }
            }
        });

        //

//        mWebView.setDownloadListener(new DownloadListener() {
//            @RequiresApi(api = Build.VERSION_CODES.N)
//            @Override
//            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
//                try {
//                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
//                    DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
//
//                    contentDisposition = URLDecoder.decode(contentDisposition,"UTF-8"); //디코딩
//                    String FileName = contentDisposition.replace("attachment; filename=", ""); //attachment; filename*=UTF-8''뒤에 파일명이있는데 파일명만 추출하기위해 앞에 attachment; filename*=UTF-8''제거
//
//                    String cookie = CookieManager.getInstance().getCookie(url);
//                    request.addRequestHeader("Cookie", cookie);
//
//                    String fileName = FileName; //위에서 디코딩하고 앞에 내용을 자른 최종 파일명
//                    request.setMimeType(mimetype);
//                    request.addRequestHeader("User-Agent", userAgent);
//                    request.setDescription("Downloading File");
//                    request.setAllowedOverMetered(true);
//                    request.setAllowedOverRoaming(true);
//                    request.setTitle(fileName);
//                    request.setRequiresCharging(false);
//
//                    request.allowScanningByMediaScanner();
//                    request.setAllowedOverMetered(true);
//                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
//                    dm.enqueue(request);
//                    Toast.makeText(getApplicationContext(),"파일이 다운로드됩니다.", Toast.LENGTH_LONG).show();
//                }
//                catch (Exception e) {
//                    if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                            Toast.makeText(getBaseContext(), "다운로드를 위해\n권한이 필요합니다.", Toast.LENGTH_LONG).show();
//                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1004);
//                        }
//                        else {
//                            Toast.makeText(getBaseContext(), "다운로드를 위해\n권한이 필요합니다.", Toast.LENGTH_LONG).show();
//                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1004);
//                        }
//                    }
//                }
//            }
//        });



        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onCreateWindow(final WebView view, boolean dialog,
                                          boolean userGesture, Message resultMsg)
            {
                WebView newWebView = new WebView(MainActivity.this);
                WebView.WebViewTransport transport
                        = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();

                newWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                        browserIntent.setData(Uri.parse(url));
                        startActivity(browserIntent);
                        return true;
                    }
                });

                return true;
            }

        });


        setmWebViewFileUploadPossible();


        mWebSettings = mWebView.getSettings(); //각종 환경 설정 가능여부
        mWebSettings.setJavaScriptEnabled(true); // 자바스크립트 허용여부
        mWebSettings.setSupportMultipleWindows(false); // 윈도우 여러개 사용여부
        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); // 컨텐츠사이즈 맞추기
        mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); // 캐시 허용 여부
        mWebSettings.setUseWideViewPort(true); // wide viewport 사용 여부
        mWebSettings.setSupportZoom(true); // Zoom사용여부
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(false); // 자바스크립트가 window.open()사용할수있는지 여부
        mWebSettings.setLoadWithOverviewMode(true); // 메타태그 허용 여부
        mWebSettings.setBuiltInZoomControls(true); // 화면 확대 축소 허용 여부
        mWebSettings.setDomStorageEnabled(true); // 로컬저장소 허용 여부
        mWebSettings.getAllowFileAccess();
        CookieManager cm = CookieManager.getInstance();
        cm.setAcceptCookie(true);
        cm.setAcceptThirdPartyCookies(mWebView, true);

        mWebView.setWebViewClient(new WebViewClient());
        // http://192.168.0.142:8080/
        // http://randomsquiz.s3-website.ap-northeast-2.amazonaws.com
        mWebView.loadUrl("http://randomsquiz.s3-website.ap-northeast-2.amazonaws.com"); // 웹뷰 사이트 주소 및 시작
    }

    @Override
    public void onBackPressed() {
        if(mWebView.canGoBack()){
            mWebView.goBack();
        }else{
            super.onBackPressed();
        }
    }
    
    // 웹뷰 파일업로드 허용
    protected void setmWebViewFileUploadPossible() {
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback filePathCallback, FileChooserParams fileChooserParams) {
                mFilePathCallback = filePathCallback;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

                // 여러장의 사진을 선택하는 경우
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");

                startActivityForResult(Intent.createChooser(intent, "Select picture"), IMAGE_SELECTOR_REQ);
                return true;
            }
            // 새창열기
            @Override
            public boolean onCreateWindow(final WebView view, boolean dialog,
                                          boolean userGesture, Message resultMsg)
            {
                WebView newWebView = new WebView(MainActivity.this);
                WebView.WebViewTransport transport
                        = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();

                newWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                        browserIntent.setData(Uri.parse(url));
                        startActivity(browserIntent);
                        return true;
                    }
                });

                return true;
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_SELECTOR_REQ) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    Uri[] uris = new Uri[count];
                    for (int i = 0; i < count; i++) {
                        uris[i] = data.getClipData().getItemAt(i).getUri();
                    }
                    mFilePathCallback.onReceiveValue(uris);
                }
                else if (data.getData() != null) {
                    mFilePathCallback.onReceiveValue((new Uri[]{data.getData()}));
                }
            }
        }
    }
}