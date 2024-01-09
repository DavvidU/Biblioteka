package com.example.biblioteka;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.core.view.WindowCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblioteka.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    public static final int NEW_BOOK_ACTIVITY_REQUEST_CODE = 1;

    private BookViewModel bookViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // -----
        setContentView(R.layout.activity_main);


        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final BookAdapter adapter = new BookAdapter(); // -----
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bookViewModel = new ViewModelProvider(this).get(BookViewModel.class);
        bookViewModel.findAll().observe(this, adapter::setBooks);

        FloatingActionButton addBookButton = findViewById(R.id.add_button);
        addBookButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EditBookActivity.class);
            startActivityForResult(intent, NEW_BOOK_ACTIVITY_REQUEST_CODE);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { //nullable import gradle
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_BOOK_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Book book = new Book(data.getStringExtra(EditBookActivity.EXTRA_EDIT_BOOK_TITLE),
                    data.getStringExtra(EditBookActivity.EXTRA_EDIT_BOOK_AUTHOR));
            bookViewModel.insert(book);
            Snackbar.make(findViewById(R.id.main_layout), getString(R.string.book_added),
                    Snackbar.LENGTH_LONG).show();
        }
        else {
            Snackbar.make(findViewById(R.id.main_layout),
                            getString(R.string.empty_not_saved),
                            Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    private static class BookHolder extends RecyclerView.ViewHolder {
        private TextView bookTitleTextView;
        private TextView bookAuthorTextView;

        // ------

        public BookHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.book_list_item, parent, false));

            bookTitleTextView = itemView.findViewById(R.id.book_title);
            bookAuthorTextView = itemView.findViewById(R.id.book_author); // edit_book_author
        }

        public void bind(Book book) {
            bookTitleTextView.setText(book.getTitle());
            bookAuthorTextView.setText(book.getAuthor());
        }
    }
    private class BookAdapter extends RecyclerView.Adapter<BookHolder> {
        private List<Book> books;

        @NonNull
        @Override
        public BookHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new BookHolder(getLayoutInflater(), parent);
        }

        @Override
        public void onBindViewHolder(BookHolder holder, int position) {
            if (books != null) {
                Book book = books.get(position);
                holder.bind(book);
            } else {
                Log.d("MainActivity", "No books");
            }
        }

        public int getItemCount() {
            if (books != null) {
                return books.size();
            } else {
                return 0;
            }
        }

        void setBooks(List<Book> books) {
            this.books = books;
            notifyDataSetChanged();
        }
    }
}