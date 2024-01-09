package com.example.biblioteka;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Book.class}, version = 1, exportSchema = false)
public abstract class BookDatabase extends RoomDatabase {
    private static BookDatabase databaseInstance;
    static final ExecutorService databaseWriteExecutor= Executors.newSingleThreadExecutor();
    public abstract BookDao bookDao();
    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                BookDao dao = databaseInstance.bookDao();


                Book book1 = new Book("Clean code", "Robert C. Martin");
                dao.insert(book1);
                Book book2 = new Book("Game of Thrones", "George R. R. Martin");
                dao.insert(book2);
                Book book3 = new Book("Blood of Elves", "Andrzej Sapkowski");
                dao.insert(book3);
            });
        }
    };
    static BookDatabase getDatabase(final Context context){
        if(databaseInstance==null){
            databaseInstance= Room.databaseBuilder(context.getApplicationContext(),BookDatabase.class,"book_database").addCallback(sRoomDatabaseCallback).build();
        }
        return databaseInstance;
    }
}
