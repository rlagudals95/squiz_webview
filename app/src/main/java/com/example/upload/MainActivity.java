package com.example.upload;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
    private final int REQ_CODE = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 웹뷰 시작
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.setDownloadListener(new MyWebViewClient()); // 파일다운로드 권한
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

        mWebView.loadUrl("http://randomsquiz.s3-website.ap-northeast-2.amazonaws.com"); // 웹뷰 사이트 주소 및 시작



    }

    private class MyWebViewClient extends WebViewClient implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
//            Log.d(TAG, "***** onDownloadStart()");
//
//            Log.d(TAG,"***** onDownloadStart() - url : "+url);
//            Log.d(TAG,"***** onDownloadStart() - userAgent : "+userAgent);
//            Log.d(TAG,"***** onDownloadStart() - contentDisposition : "+contentDisposition);
//            Log.d(TAG,"***** onDownloadStart() - mimeType : "+mimeType);

            //권한 체크
//          if(권한 여부) {
            //권한이 있으면 처리
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            try {
                contentDisposition = URLDecoder.decode(contentDisposition, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            contentDisposition = contentDisposition.substring(0, contentDisposition.lastIndexOf(";"));
            request.setMimeType(mimeType);

            //------------------------COOKIE!!------------------------
            String cookies = CookieManager.getInstance().getCookie(url);
            request.addRequestHeader("cookie", cookies);
            //------------------------COOKIE!!------------------------
            request.addRequestHeader("User-Agent", userAgent);
            request.setDescription("Downloading file...");
            request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType));
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimeType));
            DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            dm.enqueue(request);
            Toast.makeText(getApplicationContext(), "파일을 다운로드합니다.", Toast.LENGTH_LONG).show();

//          } else {
            //권한이 없으면 처리
//          }
        }

        // 카카오 로그인
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            return checkUrl(url);
//        }
//
//        @TargetApi(Build.VERSION_CODES.N)
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//            String url=request.getUrl().toString();
//            return checkUrl(url);
//        }
//
//        private boolean checkUrl(String url) {
//            //웹뷰 환경에서 '카카오로그인'버튼을 눌러서 MY_KAKAO_LOGIN_URL 로 이동하려고 한다.
//            if(url.contains("/MY_KAKAO_LOGIN_URL")) {
//                //실제 카카오톡 로그인 기능을 실행할 LoginActivity 를 실행시킨다.
//                Intent intent = new Intent(this, LoginActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivityForResult(intent, REQ_CODE);
//                return true;	//리턴 true 하면, 웹뷰에서 실제로 위 URL 로 이동하지는 않는다.
//            }
//            return false;
//        }
    }



    @Override
    public void onBackPressed() {
        if(mWebView.canGoBack()){
            mWebView.goBack();
        }else{
            super.onBackPressed();
        }
    }

    /*public void touchUpGoToDestinationButton() {
        final Button button = (Button) findViewById(R.id.goToDestinationButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWebView.loadUrl("http://192.168.0.105:8080/egovCourseList.do");
            }
        });

    }*/

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