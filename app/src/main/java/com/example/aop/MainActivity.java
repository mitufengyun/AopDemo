package com.example.aop;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;

import com.example.aop.annotation.BehaviorTrace;
import com.example.aop.annotation.UserInfoBehaviorTrace;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @BehaviorTrace("摇一摇")
    public void mShake(View view){
        SystemClock.sleep(new Random().nextInt(2000));
    }
    @BehaviorTrace("语音消息")
    public void mAudio(View view){
        SystemClock.sleep(new Random().nextInt(2000));
    }
    @UserInfoBehaviorTrace("视频消息")
    @BehaviorTrace("视频消息")
    public void mVideo(View view ){
        SystemClock.sleep(new Random().nextInt(2000));
    }
    @UserInfoBehaviorTrace("说说功能")
    public void saySomething(View view){
        SystemClock.sleep(new Random().nextInt(2000));
    }
}
