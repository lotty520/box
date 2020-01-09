package com.github.boxapp;

import android.app.Application;
import com.github.applib.Lib;

public class App extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    new Lib().print();
  }
}
