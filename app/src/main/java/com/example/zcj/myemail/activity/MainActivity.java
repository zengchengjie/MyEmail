package com.example.zcj.myemail.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.example.zcj.myemail.R;

/**
 * Created by zcj on 2017/2/10.
 */
public class MainActivity extends ActivityBase implements View.OnClickListener{
    private Button btn_send;
    private Button btn_rec;
    private Button btn_contact;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        btn_send = (Button) findViewById(R.id.turnToSend);
        btn_rec = (Button) findViewById(R.id.turnToRec);
        btn_contact = (Button) findViewById(R.id.contactList);
        btn_send.setOnClickListener(this);
        btn_rec.setOnClickListener(this);
        btn_contact.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.turnToSend:
               new MailEditActivity().sendThreeEmail();
                break;
            case R.id.turnToRec:
                intent.setClass(MainActivity.this,RecEmailActivity.class);
                break;
            case R.id.contactList:
//                intent.setClass(MainActivity.this,ContactActivity.class);
                break;
        }
        startActivity(intent);
    }
}
