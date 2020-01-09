package com.github.boxapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import androidx.fragment.app.FragmentActivity;
import com.github.server.ITestInterface;

public class MainActivity extends FragmentActivity implements View.OnClickListener {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    findViewById(R.id.base64).setOnClickListener(this);
    findViewById(R.id.hex).setOnClickListener(this);
    findViewById(R.id.xor).setOnClickListener(this);
    findViewById(R.id.aes).setOnClickListener(this);
    findViewById(R.id.aidl).setOnClickListener(this);
  }

  @Override
  protected void onResume() {
    super.onResume();
    new Util().print();
  }

  @Override public void onClick(View v) {
    switch (v.getId()) {
      //case R.id.base64:
      //  Base64StringCipher base64 = new Base64StringCipher();
      //  String ee = base64.ee("this is base64");
      //  Log.e("cipher", "Base64加密：" + ee);
      //  String dd = base64.dd(ee);
      //  Log.e("cipher", "Base64解密：" + dd);
      //  break;
      //case R.id.hex:
      //  HexStringCipher hexStringCipher = new HexStringCipher();
      //  String hex = hexStringCipher.ee("this is hex");
      //  Log.e("cipher", "HEX加密：" + hex);
      //  String hexString = hexStringCipher.dd(hex);
      //  Log.e("cipher", "HEX解密：" + hexString);
      //  break;
      //case R.id.xor:
      //  XorStringCipher xor = new XorStringCipher();
      //  String xorString = xor.ee("this is xor");
      //  Log.e("cipher", "XOR加密：" + xorString);
      //  String string = xor.dd(xorString);
      //  Log.e("cipher", "XOR解密：" + string);
      //
      //  break;
      //case R.id.aes:
      //  AesStringCipher aes = new AesStringCipher();
      //  String aesString = aes.ee("this is aes");
      //  Log.e("cipher", "AES加密：" + aesString);
      //  String aesDecrypt = aes.dd(aesString);
      //  Log.e("cipher", "AES解密：" + aesDecrypt);
      //
      //  break;
      case R.id.aidl:
        Intent intent = new Intent();
        intent.setAction("com.github.service.ACTION");
        ComponentName component =
            new ComponentName("com.github.server", "com.github.server.StringCipherService");
        intent.setComponent(component);
        Log.e("wh", "client ready to bind");
        bindService(intent, new Conn(), Context.BIND_AUTO_CREATE);
      default:
        break;
    }
  }

  static class Conn implements ServiceConnection {

    @Override public void onServiceConnected(ComponentName name, IBinder service) {
      ITestInterface remote = ITestInterface.Stub.asInterface(service);
      try {
        String value = remote.romoteValue();
        Log.e("wh", "value return from service: " + value);
      } catch (RemoteException e) {
        e.printStackTrace();
      }
    }

    @Override public void onServiceDisconnected(ComponentName name) {

    }
  }
}
