package com.example.test1;

import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import com.example.test1.R;

import android.widget.TextView;
import android.sax.RootElement;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.test1.databinding.ActivityMainBinding;

import android.content.SharedPreferences;
import android.content.Context;
import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate and get instance of binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    Window window = getWindow();
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    window.setStatusBarColor(getResources().getColor(R.color.black));
}

        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        
        String email = prefs.getString("email", "null"); //2nd argument is for default value if not found
        String passwd = prefs.getString("passwd", "null");

        if (!email.equals("null") && !passwd.equals("null")) {
            Intent ChatActivity = new Intent(MainActivity.this, ChatActivity.class);
            startActivity(ChatActivity);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else {
        
            // set content view to binding's root
        setContentView(binding.getRoot());


        Button loginBtn = findViewById(R.id.login);
        Button registerBtn = findViewById(R.id.register);
        
        loginBtn.setText("Login");
        
        loginBtn.setOnClickListener(v -> {
            
         Intent LoginAcitivity = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(LoginAcitivity);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        
        registerBtn.setOnClickListener(v -> {
            
         Intent RegisterActivity = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(RegisterActivity);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
   }
  }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }
}
