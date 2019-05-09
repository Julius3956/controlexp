package com.example.controlexp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.segway.robot.algo.Pose2D;
import com.segway.robot.algo.minicontroller.CheckPoint;
import com.segway.robot.algo.minicontroller.CheckPointStateListener;
import com.segway.robot.sdk.base.bind.ServiceBinder;
import com.segway.robot.sdk.locomotion.head.Head;
import com.segway.robot.sdk.locomotion.sbv.Base;
import com.segway.robot.sdk.locomotion.sbv.BasePose;

public class MainActivity extends BaseActivity {
    private static final String TAG = "RobotControl";
    private Button forward_button;
    private boolean pressed = false;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        forward_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pressed) {
                    forward_button.setText("End");
                    pressed = true;
                    mBase.cleanOriginalPoint();
                    mPose2d = mBase.getOdometryPose(-1);
                    mBase.setOriginalPoint(mPose2d);
                    mBase.setControlMode(Base.CONTROL_MODE_NAVIGATION);
                    mBase.setOnCheckPointArrivedListener(new CheckPointStateListener() {
                        @Override
                        public void onCheckPointArrived(CheckPoint checkPoint, Pose2D realPose, boolean isLast) {
                            Log.d(TAG, "onCheckPointArrived: true");


                        }

                        @Override
                        public void onCheckPointMiss(CheckPoint checkPoint, Pose2D realPose, boolean isLast, int reason) {
                            Log.d(TAG, "conCheckPointMiss:true");
                        }
                    });
                    mBase.addCheckPoint(1f, 0,theta);
                    mHead.setWorldYaw(0);

                }else{
                    mBase.setControlMode(Base.CONTROL_MODE_RAW);
                    mBase.setLinearVelocity(0f);
                    mBase.setAngularVelocity(0);
                    mHead.setWorldPitch(0);
                    mHead.setWorldYaw(0);
                    pressed = false;
                    forward_button.setText("start");
                }
            }
        });

    }
    private void init(){

        forward_button = (Button)findViewById(R.id.forward_button);
        
    }
}
