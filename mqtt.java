package com.example.controlexp;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
//test
public class mqtt extends AppCompatActivity {
    public static final String TAG = "MQTT";
    private static final int CONNECTED = 1;
    private static final int GET_MSG = 2;
    private static MqttAndroidClient client;
    private MqttConnectOptions conOpt;
    private TextView headText;
    private ListView msg_list;
    private ArrayAdapter adapter;
    private List<String> list = new ArrayList<>();

    private Handler handler;
    private String host = "tcp://10.0.2.2:61613";
    private String userName = "admin";
    private String passWord = "password";
    private static String myTopic = "Tdoor";
    private String clientId = "rknr";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mqtt);
        init();
        new Thread(new Runnable() {
            @Override
            public void run() {
            if(!client.isConnected()){
                try{
                    client.connect(conOpt,null,iMqttActionListener);
                    Log.d(TAG, "run: success");
                    Message tmp = new Message();
                    tmp.what = CONNECTED;
                    handler.sendMessage(tmp);
                }catch (MqttException e){
                    Log.d(TAG, "run: "+e.getMessage());
                }
            }
            }
        });
    }
    private void init(){
        headText = (TextView) findViewById(R.id.headText);
        msg_list = (ListView) findViewById(R.id.msg_list);
        adapter = new ArrayAdapter(this,R.layout.item,R.id.msg_item,list);
        msg_list.setAdapter(adapter);
        String uri = host;
        client = new MqttAndroidClient(this,uri,clientId);
        client.setCallback(mqttCallback);
        conOpt = new MqttConnectOptions();

        conOpt.setCleanSession(true);
        conOpt.setKeepAliveInterval(20);
        conOpt.setConnectionTimeout(10);
        conOpt.setUserName(userName);
        conOpt.setPassword(passWord.toCharArray());

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                switch (msg.what) {
                    case CONNECTED:
                        headText.setText("Connected.waiting...");
                        break;
                    case  GET_MSG:
                        headText.setText("get message!");
                        break;
                    default :
                        break;
                }
            }
        };
    }
    private IMqttActionListener iMqttActionListener = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
            try{
                client.subscribe(myTopic,1);
            }catch (MqttException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

        }
    };
    private MqttCallback mqttCallback = new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {
            Log.d(TAG,"connectionLost");
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            String str = new String(message.getPayload());
            Message tmp = new Message();
            tmp.what = GET_MSG;
            handler.sendMessage(tmp);
            list.add(str);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    };

}
