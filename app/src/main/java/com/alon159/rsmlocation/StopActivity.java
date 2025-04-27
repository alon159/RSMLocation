package com.alon159.rsmlocation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class StopActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stop);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.stop), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void onClickStop(View v) {
        if (v.getId() == R.id.stopbutton) {
            Log.i("StopActivity", "Stopping service\n");

            Intent message = new Intent(this, SensorService.class);
            stopService(message);

            Intent intent = new Intent(this, StartActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
