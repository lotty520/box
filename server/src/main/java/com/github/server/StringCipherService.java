package com.github.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import androidx.annotation.Nullable;

/**
 * @author lotty
 */
public class StringCipherService extends Service {

  @Override public void onCreate() {
    super.onCreate();
    Log.e("wh", "service create");
  }

  @Override public void onDestroy() {
    super.onDestroy();
    Log.e("wh", "service destroy");
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    Log.e("wh", "service onBind");
    return new ITestInterface.Stub() {
      @Override public String romoteValue() throws RemoteException {
        return "this is from service.";
      }
    };
  }
}
