// IBookMananger.aidl
package com.richaelguitar.ipcdemo;
import com.richaelguitar.ipcdemo.Book;
// Declare any non-default types here with import statements
import com.richaelguitar.ipcdemo.IOnNewBookArrivedListener;
interface IBookMananger {

    List<Book> getBookList();
    void addBook(in Book book);
    void registerListener(IOnNewBookArrivedListener listener);
    void unregisterListener(IOnNewBookArrivedListener listener);
}
