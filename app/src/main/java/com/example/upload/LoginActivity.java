//package com.example.upload;
//
//import android.os.Bundle;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import com.kakao.auth.AuthType;
//import com.kakao.auth.ISessionCallback;
//import com.kakao.auth.Session;
//import com.kakao.network.ErrorResult;
//import com.kakao.usermgmt.UserManagement;
//import com.kakao.usermgmt.callback.MeV2ResponseCallback;
//import com.kakao.usermgmt.response.MeV2Response;
//import com.kakao.util.exception.KakaoException;
//
//
//public class LoginActivity extends AppCompatActivity {
//
//    private boolean isNeedLogin = true;
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        Session.getCurrentSession().removeCallback(mKakaoSessionCallback);
//    }
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        Session.getCurrentSession().close();
//        Session.getCurrentSession().addCallback(mKakaoSessionCallback);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if(isNeedLogin) {
//            isNeedLogin = false;
//            Session.getCurrentSession().open(AuthType.KAKAO_LOGIN_ALL, this);
//        }
//    }
//
//
//    /**
//     * 카카오 세션 콜백.
//     */
//    private ISessionCallback mKakaoSessionCallback = new ISessionCallback() {
//        @Override
//        public void onSessionOpened() {
//
//            if(Session.getCurrentSession().isOpened()){
//                Log.e("LoginActivity", "카카오 로그인 성공");
//            }
//
//            List<String> keys = new ArrayList<>();
//            keys.add("kakao_account.profile");
//            keys.add("kakao_account.email");
//
//            UserManagement.getInstance().me(keys, new MeV2ResponseCallback() {
//                @Override
//                public void onSessionClosed(ErrorResult errorResult) {
//                    finishWithError(errorResult.getErrorMessage());
//                }
//
//                @Override
//                public void onSuccess(MeV2Response result) {
//                    Log.e("LoginActivity", "카카오톡 프로필 가져오기 성공했다.");
//                    String kakaoId = result.getId() +"";
//                    String email = result.getKakaoAccount().getEmail();
//                    String koAccessToken = Session.getCurrentSession().getTokenInfo().getAccessToken();
//                    String photoUrl = result.getKakaoAccount().getProfile().getProfileImageUrl();
//                    String name = result.getKakaoAccount().getProfile().getNickname();
//
//                    finishWithSuccess(name, email, photoUrl, kakaoId, koAccessToken);
//                }
//
//                @Override
//                public void onFailure(ErrorResult errorResult) {
//                    super.onFailure(errorResult);
//                    finishWithError(errorResult.getErrorMessage());
//                }
//            });
//        }
//
//        @Override
//        public void onSessionOpenFailed(KakaoException ex) {
//            Log.e("LoginActivity", "온 쎄션 오픈 페일드.");
//            finishWithError(ex.getLocalizedMessage());
//        }
//    };
//
//
//    private void finishWithError(String err) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(" 로그인");
//        builder.setMessage(err);
//        builder.setCancelable(false);
//        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                finish();
//            }
//        }).show();
//    }
//
//
//    private void finishWithSuccess(String name, String email, String photoUrl, String kkId, String kkAccessToken) {
//        Intent intent = new Intent();
//        intent.putExtra("name", name);
//        intent.putExtra("email", email);
//        intent.putExtra("photoUrl", photoUrl);
//        intent.putExtra("kkId", kkId);
//        intent.putExtra("kkAccessToken", kkAccessToken);
//        setResult(Activity.RESULT_OK, intent);
//        finish();
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
//            return;
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }
//}