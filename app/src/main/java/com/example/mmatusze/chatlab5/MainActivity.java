package com.example.mmatusze.chatlab5;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    public static String IP = "ip";
    public static String NICK = "nick";

    Button startButton;
    EditText ipEntry, nickEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startButton = (Button) findViewById(R.id.startButton);
        ipEntry = (EditText) findViewById(R.id.editText);
        nickEntry = (EditText) findViewById(R.id.editText2);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainChat.class);
                intent.putExtra(IP, ipEntry.getText().toString());
                intent.putExtra(NICK, nickEntry.getText().toString());
                startActivity(intent);
            }
        });
    }

}
