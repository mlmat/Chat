package com.example.mmatusze.chatlab5;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainChat extends AppCompatActivity {

    ArrayList<String> listItems = new ArrayList<>();

    ArrayAdapter<String> adapter;

    String nick;
    String ip;
    ListView listView;
    Button sendButton;
    EditText msgText;
    TextView nickText;

    MqttClient sampleClient = null;
    private void startMQTT(){
        String clientId;
        MemoryPersistence persistence = new MemoryPersistence();

        try{
            String broker = "tcp://"+ip+":1883";
            clientId = nick;
            sampleClient = new MqttClient(broker, clientId,persistence);
            sampleClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    throwable.printStackTrace();
                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    Message msg = myHandler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("NICK", s.substring(s.lastIndexOf("/") + 1));
                    bundle.putString("MSG", mqttMessage.toString());
                    msg.setData(bundle);
                    myHandler.sendMessage(msg);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                    System.out.println("Delivery complete");
                }
            });

            MqttConnectOptions connOptns = new MqttConnectOptions();
            connOptns.setCleanSession(true);
            System.out.println("Connecting to broker: "+broker);
            sampleClient.connect(connOptns);
            System.out.println("Connected");
            sampleClient.subscribe("#");
        } catch (MqttException e){
            e.printStackTrace();
        }
    }

    Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            listItems.add("<"+msg.getData().getString("NICK")+">" +
                    msg.getData().getString("MSG"));

            adapter.notifyDataSetChanged();
            listView.setSelection(listItems.size()-1);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);
        nickText = (TextView) findViewById(R.id.nickText);
        nick = getIntent().getStringExtra(MainActivity.NICK);
        System.out.println(nick);
        ip = getIntent().getStringExtra(MainActivity.IP);
        msgText = (EditText) findViewById(R.id.msgText);
        nickText.setText(nick);
        listView = (ListView) findViewById(R.id.chatListView);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        listView.setAdapter(adapter);
        sendButton = (Button) findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Message msg = myHandler.obtainMessage();
//                Bundle bundle = new Bundle();
//                bundle.putString("NICK", nick);
//                bundle.putString("MSG", msgText.getText().toString());
//                msg.setData(bundle);
//                myHandler.sendMessage(msg);
                MqttMessage mqttMessage = new MqttMessage(msgText.getText().toString().getBytes());
                try{
                    sampleClient.publish("chat11/"+nick, mqttMessage);
                }catch (MqttException e){
                    e.printStackTrace();
                }


            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                startMQTT();
            }
        }).start();


    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(sampleClient != null){
            try{
                sampleClient.disconnect();
            }catch(MqttException e){
                e.printStackTrace();
            }
        }
    }
}
