package com.example.zcj.myemail.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zcj.myemail.R;
import com.example.zcj.myemail.Utils.EmailFormatUtil;
import com.example.zcj.myemail.Utils.HttpUtil;
import com.example.zcj.myemail.Utils.SpUtil;

/**
 * Created by zcj on 2017/2/9.
 */
public class LoginActivity extends ActivityBase implements TextWatcher, View.OnClickListener {
    private static final String TAG = "sessionTest";
    private EditText emailAddress;
    private EditText password;
    private Button clearAddress;
    private Button emailLogin;
    private ProgressDialog dialog;
    private CheckBox cb_remenber;
    private CheckBox cb_autologin;
    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_login);
        initView();
        isRemenberPwd();
    }

    private void initView() {
        context  = LoginActivity.this;
        emailAddress = (EditText) findViewById(R.id.emailAddress);
        password = (EditText) findViewById(R.id.password);
        clearAddress = (Button) findViewById(R.id.clear_address);
        emailLogin = (Button) findViewById(R.id.login_btn);
        cb_remenber = (CheckBox) findViewById(R.id.remenberPassword);
        cb_autologin = (CheckBox) findViewById(R.id.autoLogin);

        clearAddress.setOnClickListener(this);
        emailAddress.addTextChangedListener(this);
        emailLogin.setOnClickListener(this);

        cb_remenber.setOnClickListener(this);
        cb_autologin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clear_address:
                emailAddress.setText("");
                break;
            case R.id.login_btn:
                loginEmail();
                break;
            case R.id.remenberPassword:
                remenberPwd();
                break;
            case R.id.autoLogin:
                break;
        }
    }

    /**
     * 记住密码
     */
    private void remenberPwd(){
        boolean isRbPwd=SpUtil.getIsRbPwd(context);
        if(isRbPwd){
            SpUtil.putIsRbPwd(context,false);
            cb_remenber.setChecked(false);
        }else{
            SpUtil.putIsRbPwd(context,true);
            SpUtil.putAddress(context,emailAddress.getText().toString());
            SpUtil.putPwd(context,password.getText().toString());
            cb_remenber.setChecked(true);

        }
    }
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(MyApplication.session==null){

                dialog.dismiss();
                Toast.makeText(LoginActivity.this, "账号或密码错误", Toast.LENGTH_SHORT).show();
            }else{
                Log.d(TAG, "session   : "+MyApplication.session);
                dialog.dismiss();
                Intent intent=new Intent(LoginActivity.this, MailEditActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    };


    private void loginEmail() {
        String address=emailAddress.getText().toString().trim();
        String pwd=password.getText().toString().trim();
        if(TextUtils.isEmpty(address)){
            Toast.makeText(LoginActivity.this, "地址不能为空", Toast.LENGTH_SHORT).show();
            return;
        }else{
            if(TextUtils.isEmpty(pwd)){
                Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        /**
         * 校验邮箱格式
         */
        if(!EmailFormatUtil.emailFormat(address)){
            Toast.makeText(LoginActivity.this, "邮箱格式不正确", Toast.LENGTH_SHORT).show();
        }else{
            String host="smtp."+address.substring(address.lastIndexOf("@")+1);
            MyApplication.info.setMailServerHost(host);
            //25端口（SMTP）：25端口为SMTP（Simple Mail TransferProtocol，简单邮件传输协议）服务所开放的，是用于发送邮件。
            MyApplication.info.setMailServerPort("587");
            MyApplication.info.setUserName(address);
            MyApplication.info.setPassword(pwd);
            MyApplication.info.setValidate(true);

            /**
             * 进度条
             */
            dialog=new ProgressDialog(LoginActivity.this);
            dialog.setMessage("正在登入，请稍后");
            dialog.show();

            /**
             * 访问网络
             */
            new Thread(){
                @Override
                public void run() {
                    //登入操作
                    HttpUtil util=new HttpUtil();
                    MyApplication.session=util.login();
                    Message message=handler.obtainMessage();
                    message.sendToTarget();
                }
            }.start();
        }
    }

    /**
     * 是否记住密码
     */
    private void isRemenberPwd() {
        boolean isRbPwd = SpUtil.getIsRbPwd(LoginActivity.this);
        if (isRbPwd) {
            String addr = SpUtil.getAddress(LoginActivity.this);
            String pwd = SpUtil.getPwd(LoginActivity.this);
            emailAddress.setText(addr);
            password.setText(pwd);
            cb_remenber.setChecked(true);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    /**
     * 文本监听事件
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!TextUtils.isEmpty(s)) {
            clearAddress.setVisibility(View.VISIBLE);
        } else {
            clearAddress.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
