package com.klfront.iprogram.user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.klfront.iprogram.R;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class LoginActivity extends AppCompatActivity {

    private ImageView imgClose;
    private EditText etPhone;
    private EditText etCode;
    private TextView tvGetCode;
    private ImageView ivOk;
    private LinearLayout pnlOk;
    private ImageView loginWechat;
    private ImageView loginQq;
    private ImageView loginWeibo;
    private LinearLayout pnlThirdpartylogin;
    private LinearLayout pnlOtherLogin;
    private Handler mHandler;

    /**
     * 使用静态的内部类 + 弱引用可以解决内存泄漏问题
     * 静态的内部类,是随着类的加载而加载,只能访问静态的变量,解决引用持有问题
     */
    private static class MyHandler extends Handler {
        private final WeakReference<Activity> mActivity;

        public MyHandler(Activity activity) {
            mActivity = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mActivity.get() != null) {
                LoginActivity instance = (LoginActivity) mActivity.get();
                int event = msg.arg1;
                int result = msg.arg2;
                Object data = msg.obj;
                if(event==2&&result==-1){
                    Toast.makeText(instance,"验证码已发送到手机",Toast.LENGTH_SHORT);
                }
                else if(event==3&&result==-1){
                  HashMap<String,String> map = ( HashMap<String,String>) data ;
                  // String countryCode =  map.get("country");
                  Intent intent = new Intent();
                  intent.putExtra("phone",map.get("phone"));
                  instance.setResult(Activity.RESULT_OK,intent);
                  instance.finish();
                }
            }
        }
    }

    EventHandler eh = new EventHandler() {
        @Override
        public void afterEvent(int event, int result, Object data) {
            Message msg = new Message();
            msg.arg1 = event;
            msg.arg2 = result;
            msg.obj = data;
            mHandler.sendMessage(msg);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initEvent();

        mHandler = new MyHandler(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SMSSDK.registerEventHandler(eh);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //用完回调要注销掉，否则可能会出现内存泄露
        SMSSDK.unregisterEventHandler(eh);
    }


    private void initView() {
        imgClose = (ImageView) findViewById(R.id.img_close);
        etPhone = (EditText) findViewById(R.id.et_phone);
        etCode = (EditText) findViewById(R.id.et_code);
        tvGetCode = (TextView) findViewById(R.id.tv_get_code);
        ivOk = (ImageView) findViewById(R.id.iv_ok);
        pnlOk = (LinearLayout) findViewById(R.id.pnl_ok);
        loginWechat = (ImageView) findViewById(R.id.login_wechat);
        loginQq = (ImageView) findViewById(R.id.login_qq);
        loginWeibo = (ImageView) findViewById(R.id.login_weibo);
        pnlThirdpartylogin = (LinearLayout) findViewById(R.id.pnl_thirdpartylogin);
        pnlOtherLogin = (LinearLayout) findViewById(R.id.pnl_other_login);
    }

    private void initEvent() {
        tvGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCode(LoginActivity.this);
            }
        });

        ivOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    public void sendCode(Context context) {
        //注册回调监听，放到发送和验证前注册，注意这里是子线程需要传到主线程中去操作后续提示
        String phone = etPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "手机号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        // 请求验证码，其中country表示国家代码，如“86”；phone表示手机号码，如“13800138000”
        SMSSDK.getVerificationCode("86", phone);
    }

    private void submit() {
        // validate
        String phone = etPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "手机号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String code = etCode.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(this, "验证码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 提交验证码，其中的code表示验证码，如“1357”
        SMSSDK.submitVerificationCode("86", phone, code);
    }
}
