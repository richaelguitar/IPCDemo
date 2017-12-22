package com.richaelguitar.ipcdemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;


import com.richaelguitar.ipcdemo.Book;
import com.richaelguitar.ipcdemo.IBookMananger;
import com.richaelguitar.ipcdemo.IOnNewBookArrivedListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BookManagerService extends Service {

    private CopyOnWriteArrayList<Book> books = new CopyOnWriteArrayList<>();

    private RemoteCallbackList<IOnNewBookArrivedListener> listeners = new RemoteCallbackList<>();

    public BookManagerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return iBookManager.asBinder();
    }

    IBookMananger iBookManager = new IBookMananger.Stub(){
        @Override
        public List<Book> getBookList() throws RemoteException {
            return books;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            if(!books.contains(book)){
                books.add(book);

                for(int i=0;i<listeners.beginBroadcast();i++){
                    ((IOnNewBookArrivedListener)listeners.getBroadcastCookie(i)).onNewBookArrived(book);
                }
                listeners.finishBroadcast();
                Log.i(BookManagerService.class.getSimpleName(),"新增图书信息："+book.toString());
            }else{
                Log.i(BookManagerService.class.getSimpleName(),"图书已存在，请不要重复添加："+book.toString());
            }
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {

            listeners.register(listener);
            Log.i(BookManagerService.class.getSimpleName(),"监听注册成功："+listener.toString());

        }

        @Override
        public void unregisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
            listeners.unregister(listener);
            Log.i(BookManagerService.class.getSimpleName(),"监听已经取消："+listener.toString());
        }
    };
}
