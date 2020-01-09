package com.github.server;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author lotty
 */
public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  public void start(View view) {
    Intent intent = new Intent(this, StringCipherService.class);
    intent.setAction("com.github.service.ACTION");
    startService(intent);
  }
}
