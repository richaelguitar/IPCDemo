package com.richaelguitar.ipcdemo;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.richaelguitar.ipcdemo.service.BookManagerService;

public class MainActivity extends AppCompatActivity {

    private Button bindBtn,unbindBtn,addBookBtn;
    private TextView textView;

    private IBookMananger iBookMananger;

    private boolean isBind;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindBtn = findViewById(R.id.btn_bind);
        unbindBtn = findViewById(R.id.btn_unbind);
        addBookBtn = findViewById(R.id.btn_addBook);
        textView = findViewById(R.id.tv_info);

        cacultMemorySize();

        bindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(iBookMananger!=null){
                    try {
                        iBookMananger.registerListener(onNewBookArrivedListener);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        unbindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(iBookMananger!=null){
                   try {
                       iBookMananger.unregisterListener(onNewBookArrivedListener);
                   } catch (RemoteException e) {
                       e.printStackTrace();
                   }
               }
            }
        });

        addBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(iBookMananger!=null){
                    Book book = new Book();
                    book.setBookId(10001);
                    book.setBookName("Android 开发探索者");
                    try {
                        iBookMananger.addBook(book);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void cacultMemorySize() {

        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        textView.setText("memory="+manager.getMemoryClass()+"  max heap:"+manager.getLargeMemoryClass());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!isBind){
            Intent intent = new Intent(BookManagerService.class.getSimpleName());
            intent.setPackage(getPackageName());
            bindService(intent,serviceConnection,BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(serviceConnection);
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            iBookMananger = IBookMananger.Stub.asInterface(iBinder);
            isBind = true;

            Log.i(MainActivity.class.getSimpleName(),"service bind success");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
                isBind = false;
                iBookMananger = null;
            Log.i(MainActivity.class.getSimpleName(),"service unbind success");
        }
    };

    IOnNewBookArrivedListener onNewBookArrivedListener = new IOnNewBookArrivedListener.Stub() {
        @Override
        public void onNewBookArrived(final Book book) throws RemoteException {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this,"有新书到了："+book.toString(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    };
}
