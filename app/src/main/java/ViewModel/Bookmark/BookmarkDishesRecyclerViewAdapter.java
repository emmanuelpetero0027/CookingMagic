package ViewModel.Bookmark;


import static View.Bookmark.DateSetterBookmark.formattedDate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cookingmagic.R;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import BottomNavigationBar.Bookmark;
import Model.Bookmark.BookmarkModel;
import Model.Database.BookmarkDatabaseOpenHelper;
import Model.ViewAll.DishInformationModel;
import View.Bookmark.AddBookmark;
import View.Bookmark.DateSetterBookmark;
import io.github.muddz.styleabletoast.StyleableToast;

public class BookmarkDishesRecyclerViewAdapter extends RecyclerView.Adapter<BookmarkDishesRecyclerViewAdapter.MyViewHolderBookmark>{

    Context context;

    List<BookmarkModel> bookmarkModel;

    private BookmarkInterface bookmarkInterface;

    public BookmarkDishesRecyclerViewAdapter(Context context, List<BookmarkModel> bookmarkModel, BookmarkInterface bookmarkInterface) {
        this.context = context;
        this.bookmarkModel = bookmarkModel;
        this.bookmarkInterface = bookmarkInterface;
    }


    //DATE SORTING
    public void dateSort(){

        List<BookmarkModel> bookmarksModel = new ArrayList<>();
        bookmarkModel.clear();
        for(BookmarkModel bookmarks: BookmarkDatabaseOpenHelper.getInstance(context).getBookmark()){
            if(bookmarks.getBookmarkDate() != null){
                if(bookmarks.getBookmarkDate().equals(DateSetterBookmark.formattedDate)){
                    bookmarksModel.add(bookmarks);
                }
            }
        }

        bookmarkModel.addAll(bookmarksModel);
        notifyDataSetChanged();
    }

    // Optionally, you can notify the adapter of all changes
    public void updateList(List<BookmarkModel> newList) {
        bookmarkModel.clear();
        bookmarkModel.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookmarkDishesRecyclerViewAdapter.MyViewHolderBookmark onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.bookmark_all_dishes, parent, false);

        return new MyViewHolderBookmark(view, bookmarkInterface);
    }


    @Override
    public void onBindViewHolder(@NonNull BookmarkDishesRecyclerViewAdapter.MyViewHolderBookmark holder, int position) {


        //dishname
        holder.tv_dishNameBookmark.setText(bookmarkModel.get(holder.getAdapterPosition()).getDishName());

        // Get the image name from your data model
        String imageName = bookmarkModel.get(holder.getAdapterPosition()).getImageName();

        // Check if imageName is null or empty
        if (imageName == null || imageName.isEmpty()) {
            // Handle the case where imageName is null or empty
            Glide.with(context)
                    .load(R.drawable.placeholder)
                    .into(holder.img_dishImageBookmark);
            return;
        }
        // Convert image name to resource ID
        int imageResId = context.getResources().getIdentifier(imageName, "raw", context.getPackageName());

        // Load the image using Glide
        Glide.with(context)
                .load(imageResId != 0 ? imageResId : R.drawable.placeholder)// Use the raw resource ID or placeholder
                .placeholder(R.drawable.placeholder) // Placeholder image while loading
                .error(R.drawable.placeholder) // Image to show on error
                .into(holder.img_dishImageBookmark);

        String filename = bookmarkModel.get(holder.getAdapterPosition()).getImageName();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        loadImageFromInternalStorage(holder.img_dishImageBookmark, filename);
                    }
                });
            }
        });
        //Remove Bookmark
        holder.btn_bookmarkRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookmarkPopupWindow(bookmarkModel.get(holder.getAdapterPosition()).getDishName(),
                        bookmarkModel.get(holder.getAdapterPosition()).getDishId());
            }
        });

        //Setting Up Month Calendar
        String date = (bookmarkModel.get(holder.getAdapterPosition()).getBookmarkDate() != null) ? bookmarkModel.get(holder.getAdapterPosition()).getBookmarkDate() : "";
        String monthSetter = date.split("/")[0];
        holder.tv_month.setText(monthSetter);

        if(date.length() >  1){
            // Split the string by the '/' delimiter
            String[] parts = date.split("/");

            // The day is the second part (index 1)
            String day = parts[1];


            holder.tv_day.setText(day);
        }
        else{
            holder.tv_day.setText("");
        }


    }

    private void loadImageFromInternalStorage(ImageView imageView, String filename) {
        FileInputStream fis = null;
        try {
            fis = context.openFileInput(filename);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return bookmarkModel.size();
    }

    public static class MyViewHolderBookmark extends RecyclerView.ViewHolder {

        CardView cv_dishItemBookmark;
        ImageView img_dishImageBookmark;
        TextView tv_dishNameBookmark;
        TextView tv_bookmarkDate;
        ImageButton btn_bookmarkRemove;
        TextView tv_month;
        TextView tv_day;

        public MyViewHolderBookmark(@NonNull View itemView, BookmarkInterface bookmarkInterface) {
            super(itemView);

            cv_dishItemBookmark = itemView.findViewById(R.id.cv_dishItemBookmark);
            img_dishImageBookmark = itemView.findViewById(R.id.img_dishImageBookmark);
            tv_dishNameBookmark = itemView.findViewById(R.id.tv_dishNameBookmark);
            tv_bookmarkDate = itemView.findViewById(R.id.tv_bookmarkDate);
            btn_bookmarkRemove = itemView.findViewById(R.id.btn_bookmarkRemove_popup);
            tv_month = itemView.findViewById(R.id.tv_month);
            tv_day = itemView.findViewById(R.id.tv_day);

            cv_dishItemBookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bookmarkInterface.onItemClickBookmark(getAdapterPosition());
                }
            });
        }
    }

    private void bookmarkPopupWindow(String bookmarkName, int dishId){

        TextView tv_bookmarkName;
        Button btn_bookmarkRemove_popup;
        Button btn_cancelbookmark_popup;

        View popupView = LayoutInflater.from(context).inflate(R.layout.bookmark_removebookmark_popup_window, null);
        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        // Set an elevation value for popup window (optional)
        popupWindow.setElevation(20);
        // Show the popup window
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        tv_bookmarkName = popupView.findViewById(R.id.tv_bookmarkName);
        btn_bookmarkRemove_popup = popupView.findViewById(R.id.btn_bookmarkRemove_popup);
        btn_cancelbookmark_popup = popupView.findViewById(R.id.btn_cancelbookmark_popup);

        tv_bookmarkName.setText(bookmarkName);
        btn_cancelbookmark_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        btn_bookmarkRemove_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BookmarkDatabaseOpenHelper.getInstance(context).removeOneBookmark(dishId);
                //Toast.makeText(context, "Bookmark Remove!", Toast.LENGTH_SHORT).show();
                StyleableToast.makeText(context, "Bookmark Remove!", R.style.exampleToast).show();


                if(formattedDate == ""){
                    System.out.println("No Date UpdateList!");
                    updateList(BookmarkDatabaseOpenHelper.getInstance(context).getBookmark());
                }
                else{
                    System.out.println("With Date DateSort!");
                    dateSort();
                }

                popupWindow.dismiss();
            }
        });

    }

}
