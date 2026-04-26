package com.example.test1;

import android.os.Message;
import android.text.Layout;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import okhttp3.WebSocket;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.widget.LinearLayout;
import android.view.Gravity;
import android.widget.ScrollView;
import android.view.View;

import java.net.URI;
import java.net.URISyntaxException;
import tech.gusavila92.websocketclient.WebSocketClient;


public class MessageActivity extends AppCompatActivity {
    
private LinearLayout rootLayout;

public String usr = "";
public String usrs = "";

private WebSocketClient webSocketClient;

private void createWebSocketClient() {
	URI uri;
        try {
            uri = new URI("wss://nullwire.us.to/wsMsg");
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        webSocketClient = new WebSocketClient(uri) {
            
    @Override
    public void onOpen() {
    System.out.println("onOpen");
    runOnUiThread(() -> 
        Toast.makeText(MessageActivity.this, "onOpen", Toast.LENGTH_SHORT).show()
    );

    JSONObject jsonObject = new JSONObject();
    try {
        jsonObject.put("usr", usr);
    } catch (JSONException e) {
        e.printStackTrace();
    }

    // Send the JSON as a string via WebSocket
    webSocketClient.send(jsonObject.toString());
}


            @Override
            public void onTextReceived(String message) {
                
                // the empty whitespace is cz of message json
                // if put inside the try then it will get fixed
                // well if msg2 does not contain the msg json
                // then it will through error
                // which would prevent it from happening
                // and all set ;)
                
                System.out.println("onTextReceived");
                
                runOnUiThread(() -> {
                
                try {
                    
                    LinearLayout div = new LinearLayout(MessageActivity.this);
                    TextView text = new TextView(MessageActivity.this);
                    
                    JSONObject msg = new JSONObject(message);
                        
                    String msg2 = msg.getString("msg");
                        
                    text.setText(msg2);
                        
                    Toast.makeText(MessageActivity.this, "onTxtReceived: " + msg2 , Toast.LENGTH_SHORT).show();
                        
                        
                        LinearLayout.LayoutParams params1 =  new LinearLayout.LayoutParams (
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                100
                    );
                    
                    params1.topMargin = 10;
                    
                    div.setLayoutParams(params1);
                
                    div.setBackgroundColor(getResources().getColor(android.R.color.black));
                     
                        
                    div.addView(text);
                    rootLayout.addView(div);
                    
                
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                
                
                
                }
                );
                
               // LinearLayout div = new LinearLayout(MessageActivity.this);
               // TextView text = new TextView(MessageActivity.this);
                
                //text.setText(message);
                
               // div.addView(text);
              //  rootLayout.addView(div);
                
            }

            @Override
            public void onBinaryReceived(byte[] data) {
                System.out.println("onBinaryReceived");
            }

            @Override
            public void onPingReceived(byte[] data) {
                System.out.println("onPingReceived");
            }

            @Override
            public void onPongReceived(byte[] data) {
                System.out.println("onPongReceived");
            }

            @Override
public void onException(Exception e) {
    Log.e("WebSocket Exception", Log.getStackTraceString(e));
    runOnUiThread(() -> 
        Toast.makeText(MessageActivity.this, "WebSocket Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
    );
}


            @Override
public void onCloseReceived() {
    System.out.println("onCloseReceived");
    runOnUiThread(() -> Toast.makeText(MessageActivity.this, "WebSocket Closed", Toast.LENGTH_SHORT).show());
}

        };

        webSocketClient.setConnectTimeout(10000);
        webSocketClient.setReadTimeout(60000);
        webSocketClient.addHeader("Origin", "http://developer.example.com");
        webSocketClient.enableAutomaticReconnection(2000);
        webSocketClient.connect();
}
        
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_message);

        // Initialize Fast Android Networking and Android Websocket
        AndroidNetworking.initialize(getApplicationContext());
        createWebSocketClient();
        
        // set the status bar to black
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.black));
        }
        
        //vars
        usr = getIntent().getStringExtra("usr");
        usrs = getIntent().getStringExtra("usrs");
        
        rootLayout = findViewById(R.id.rootLayout);
        
        EditText msgInput = findViewById(R.id.msgInput);
        
        ScrollView ScrollLayout = findViewById(R.id.ScrollLayout);
        Button send = findViewById(R.id.send);
        
        Toast.makeText(MessageActivity.this, usr + " " + usrs, Toast.LENGTH_SHORT).show();
        
        
        send.setOnClickListener(v -> {
        
            String msgToSend = msgInput.getText().toString();
            
            LinearLayout div = new LinearLayout(MessageActivity.this);
            TextView text = new TextView(MessageActivity.this);
                
            
            LinearLayout.LayoutParams params1 =  new LinearLayout.LayoutParams (
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                100
                    );
                    
                    params1.topMargin = 10;
                    
                    div.setLayoutParams(params1);
                
                    div.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
                     
                    text.setText(msgToSend);
                        
                    div.addView(text);
                    rootLayout.addView(div);
                        
            
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("sendUsrs", usrs);
                jsonObject.put("msg", msgToSend);
                jsonObject.put("usr", usr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        
        
            new Thread(() -> webSocketClient.send(jsonObject.toString())).start();

            msgInput.setText("");

           // webSocketClient.send(jsonObject.toString());
            }
        );
        
        
        // this is to retrieve the all messages
        sendPostRequest(usr, usrs, new PostRequestCallback() {
                    @Override
                    public void onSuccess(JSONObject code) {
                        Toast.makeText(MessageActivity.this, "done, check the logs", Toast.LENGTH_SHORT).show();
                        Log.d("sigma boi logs: ", code.toString());
                
                        JSONArray obj1 = new JSONArray();
                        JSONObject obj2 = new JSONObject();
                
                        try {
                        obj1 = code.getJSONArray("allMessages"); // THIS TAKES THE allMessages key and convert its value to jsonArray THE OUTPUT DOES INDEED RETURNS THE JSONOBJECT BUT THE allMessages
                       // obj2 = obj1.getJSONObject(0);
                        } catch (JSONException e) {
                            Toast.makeText(MessageActivity.this, "JSON Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                
                        Log.d("1st: ", obj1.toString());
                        //Log.d("2nd: ", obj2.toString());
                        
                        
                        int i = 0;
                
                        while (i < obj1.length()) {
                        LinearLayout div = new LinearLayout(MessageActivity.this);
                        TextView text = new TextView(MessageActivity.this);
                    
                       // div.setBackgroundColor(getResources().getColor(android.R.color.black));
                            
                        LinearLayout.LayoutParams params1 =  new LinearLayout.LayoutParams (
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                100
                    );
                    
                    params1.topMargin = 10;
                    
                    div.setLayoutParams(params1);
                
                        try {
                    
                        //text.setText(obj1.getJSONObject(i).getString("msg").toString());
                        
                        // TO LOWERCASE MUST BE ADDED TO THE FUCKING SERVER
                        
                            if (obj1.getJSONObject(i).getString("sentTo").toString().toLowerCase().equals(usr)) {
                                
                                text.setText(obj1.getJSONObject(i).getString("msg").toString());
                                div.setBackgroundColor(getResources().getColor(R.color.black));
                        
                        } else if (obj1.getJSONObject(i).getString("sentTo").toString().toLowerCase().equals(usrs)) {
                            text.setText(obj1.getJSONObject(i).getString("msg").toString());
                            div.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
                        }
                        } catch (JSONException e) {
                            Toast.makeText(MessageActivity.this, "JSON Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        
                            div.addView(text);
                            rootLayout.addView(div);
                            //ScrollLayout.post(() -> ScrollLayout.fullScroll(View.FOCUS_DOWN));

                        
                        i++;
                        }
                        
                        
                    }

                    @Override
                    public void onFailure(int code) {
                        Toast.makeText(MessageActivity.this, "Error connecting to server", Toast.LENGTH_SHORT).show();
                        
                    }
                });

        
    }

    
    // sendPostRequest
    public void sendPostRequest(String usr, String usrs, PostRequestCallback callback) {
        JSONObject jsonObjectToSend = new JSONObject();
        try {
            jsonObjectToSend.put("usr", usr);
            jsonObjectToSend.put("usrs", usrs);
            
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post("https://nullwire.us.to/msgRetreiver") //retriever's spell is wrong in server
            .addJSONObjectBody(jsonObjectToSend)
            .build()
            .getAsJSONObject( new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    
                        if (!response.equals("")) {
                        runOnUiThread(() -> callback.onSuccess(response)); // UI thread for Toast
                }

                    // runOnUiThread(() -> callback.onSuccess(69)); // UI thread for Toast
                }

                @Override
                public void onError(ANError anError) {
                    Log.d("server response", anError.toString());
                    runOnUiThread(() -> callback.onFailure(404)); // UI thread for Toast
                }
            });
    }

    // Callback interface for handling success and failure
    interface PostRequestCallback {
        void onSuccess(JSONObject resp);
        void onFailure(int code);
    }
    
    

@Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        //WebSocket.close(10, 'closed');
        webSocketClient.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
