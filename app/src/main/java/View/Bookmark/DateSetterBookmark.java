package View.Bookmark;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookingmagic.DishInformation;
import com.example.cookingmagic.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import Model.Bookmark.BookmarkModel;
import Model.Database.BookmarkDatabaseOpenHelper;
import ViewModel.Bookmark.BookmarkDishesRecyclerViewAdapter;
import ViewModel.Bookmark.BookmarkInterface;

public class DateSetterBookmark extends AppCompatActivity implements BookmarkInterface {

    CalendarView calendarView;
    Calendar calendar;
    FloatingActionButton fab_addDateBookmark;
    TextView tv_date_format;

    public static String formattedDate = "";

    RecyclerView rv_dateBookmarks;

    private LinearLayoutManager linearLayoutManager;
    BookmarkDishesRecyclerViewAdapter bookmarkDishesRecyclerViewAdapter;
    List<BookmarkModel> bookmarkModelList;

    private static final String CHANNEL_ID = "default_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.bookmark_date_setter);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        calendarView = findViewById(R.id.calendarView);
        fab_addDateBookmark = findViewById(R.id.fab_addDateBookmark);
        rv_dateBookmarks = findViewById(R.id.rv_dateBookmarks);
        tv_date_format = findViewById(R.id.tv_date_format);
        calendar = Calendar.getInstance();


        getDate();
        tv_date_format.setText("Date : " + formattedDate);

        bookmarkModelList = new ArrayList<>();
        for (BookmarkModel bookmarks : BookmarkDatabaseOpenHelper.getInstance(DateSetterBookmark.this).getBookmark()) {
            if (bookmarks.getBookmarkDate() != null) {
                if (bookmarks.getBookmarkDate().equals(formattedDate)) {
                    bookmarkModelList.add(bookmarks);
                }
            }
        }
        linearLayoutManager = new LinearLayoutManager(DateSetterBookmark.this);
        rv_dateBookmarks.setLayoutManager(linearLayoutManager);
        rv_dateBookmarks.setItemAnimator(new DefaultItemAnimator());
        bookmarkDishesRecyclerViewAdapter = new BookmarkDishesRecyclerViewAdapter(DateSetterBookmark.this, bookmarkModelList, this);
        rv_dateBookmarks.setAdapter(bookmarkDishesRecyclerViewAdapter);


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                String getFormattedMonth = getMonthString(month);
                formattedDate = getFormattedMonth + "/" + day + "/" + year;
                tv_date_format.setText("Date : " + formattedDate);
                bookmarkDishesRecyclerViewAdapter.dateSort();

            }
        });

        fab_addDateBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(DateSetterBookmark.this, AddBookmark.class);
                intent.putExtra("date", formattedDate);
                startActivity(intent);

            }
        });
    }

    private String getMonthString(int month) {
        // Create a Calendar instance and set the month
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month);

        // Use SimpleDateFormat to get the month name
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    private void getDate(){
        long date = calendarView.getDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM/dd/yyyy", Locale.getDefault());
        calendar.setTimeInMillis(date);
        formattedDate = simpleDateFormat.format(calendar.getTime());
        System.out.println(formattedDate);
    }

    @Override
    public void onItemClickBookmark(int adapterPosition) {
        Intent intent = new Intent(DateSetterBookmark.this, DishInformation.class);
        intent.putExtra("dishName", bookmarkModelList.get(adapterPosition).getDishName());
        intent.putExtra("dishImage", bookmarkModelList.get(adapterPosition).getImageName());
        intent.putExtra("description", bookmarkModelList.get(adapterPosition).getDescription());
        intent.putExtra("measurement", bookmarkModelList.get(adapterPosition).getMeasurement());
        intent.putExtra("procedure", bookmarkModelList.get(adapterPosition).getProcedure());
        intent.putExtra("youtubeLink", bookmarkModelList.get(adapterPosition).getLink());
        startActivity(intent);
    }


    @Override
    public void onResume() {
        super.onResume();
        bookmarkDishesRecyclerViewAdapter.dateSort();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        formattedDate = "";
    }

}