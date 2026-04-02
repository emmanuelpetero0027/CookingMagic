package com.example.cookingmagic;

import static android.provider.MediaStore.Video.Thumbnails.VIDEO_ID;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import BottomNavigationBar.Add;
import Model.Database.BookmarkDatabaseOpenHelper;
import View.Bookmark.AddBookmark;
import ViewModel.Add.AddDishRecyclerViewAdapter;
import io.github.muddz.styleabletoast.StyleableToast;

public class DishInformation extends AppCompatActivity {


    TextView tv_dishName;
    TextView tv_description;
    TextView tv_measurement;
    TextView tv_procedure;
    TextView tv_availableIngredients;
    TextView tv_notAvailableIngredients;
    LinearLayout layout_available;
    TextView tv_youtubelink;
    CheckBox cb_dishInfoBookMark;

    YouTubePlayerView youTubePlayerView;

    ImageView img_dishImage;

    String date = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dish_information);

        //Getting Date
        date = AddBookmark.bookmarkDate;


        tv_dishName = findViewById(R.id.tv_dishname);
        tv_description = findViewById(R.id.tv_description);
        tv_measurement = findViewById(R.id.tv_measurement);
        tv_procedure = findViewById(R.id.tv_procedure);
        tv_availableIngredients = findViewById(R.id.tv_available);
        tv_notAvailableIngredients = findViewById(R.id.tv_notAvailable);
        layout_available = findViewById(R.id.layout_available);
        tv_youtubelink = findViewById(R.id.tv_youtubeLink);
        cb_dishInfoBookMark = findViewById(R.id.cb_dishInfoBookMark);
        HashMap<String, Boolean> checkedStatesBookmark  = new HashMap<>();;


        img_dishImage = findViewById(R.id.img_dishImage);

        String dishName = getIntent().getStringExtra("dishName");
        String dishImage = getIntent().getStringExtra("dishImage");
        String description = getIntent().getStringExtra("description");
        String descriptionTrim = description.trim();
        int quantity = getIntent().getIntExtra("quantity", 0);
        String ingredients = getIntent().getStringExtra("ingredients");
        String measurement = getIntent().getStringExtra("measurement");
        String procedure = getIntent().getStringExtra("procedure");
        String youtubeLink = getIntent().getStringExtra("youtubeLink");


        cb_dishInfoBookMark.setOnCheckedChangeListener(null); // Avoid unwanted triggering
        cb_dishInfoBookMark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    bookmarkPopupWindow(dishName,
                            dishImage,
                            description,
                            quantity,
                            ingredients,
                            measurement,
                            procedure,
                            youtubeLink,
                            "");
                }
                else{

                }
            }
        });



        // Convert image name to resource ID
        int imageResId = this.getResources().getIdentifier(dishImage, "raw", this.getPackageName());

        if(dishImage.contains("local")){
            loadImageFromInternalStorage(img_dishImage, dishImage);
        }
        else{
            // Load the image using Glide
            Glide.with(this)
                    .load(imageResId != 0 ? imageResId : R.drawable.placeholder)// Use the raw resource ID or placeholder
                    .placeholder(R.drawable.placeholder) // Placeholder image while loading
                    .error(R.drawable.placeholder) // Image to show on error
                    .into(img_dishImage);
        }


        tv_dishName.setText(dishName);
        tv_description.setText(descriptionTrim);
        tv_measurement.setText(measurement);
        tv_procedure.setText(procedure + "\n\n\n");
        tv_youtubelink.setText(youtubeLink);
        Linkify.addLinks(tv_youtubelink, Linkify.WEB_URLS);


        youTubePlayerView = findViewById(R.id.yt);
        getLifecycle().addObserver(youTubePlayerView);
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                super.onReady(youTubePlayer);

                String videoId = "";

                if(youtubeLink != null){
                    videoId = extractVideoId(youtubeLink);
                }


                try {
                    if (videoId != null) {
                        youTubePlayer.loadVideo(videoId, 0);
                    } else {
                        //Toast.makeText(DishInformation.this, "Failed to extract video ID " + videoId, Toast.LENGTH_SHORT).show();
                        StyleableToast.makeText(DishInformation.this, "Failed to extract video ID " + videoId, R.style.exampleToast).show();
                    }
                } catch (Exception e) {
                    //Toast.makeText(DishInformation.this, "An error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    StyleableToast.makeText(DishInformation.this, "An error occurred: " + e.getMessage() + videoId, R.style.exampleToast).show();
                }

            }
        });


        ArrayList<String> receivedAvailable = getIntent().getStringArrayListExtra("availableIngredients");
        ArrayList<String> receivedNotAvailable = getIntent().getStringArrayListExtra("notAvailableIngredients");

        if(receivedAvailable == null){
            layout_available.setVisibility(View.GONE);
        }
        else{
            layout_available.setVisibility(View.VISIBLE);

            //Available
            StringBuilder stringBuilder1 = new StringBuilder();
            for (String item : receivedAvailable) {
                stringBuilder1.append(item).append("\n"); // Append each item with a newline
            }
            tv_availableIngredients.setText(stringBuilder1.toString());


            //Not Available
            StringBuilder stringBuilder2 = new StringBuilder();
            for (String item : receivedNotAvailable) {
                stringBuilder2.append(item).append("\n"); // Append each item with a newline
            }
            tv_notAvailableIngredients.setText(stringBuilder2.toString());
        }

    }

    private void loadImageFromInternalStorage(ImageView imageView, String filename) {
        FileInputStream fis = null;
        try {
            fis = openFileInput(filename);
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

    public String extractVideoId(String youtubeUrl) {
        String videoId = null;

        // Check if the URL is a standard YouTube URL
        if (youtubeUrl.contains("youtube.com/watch?v=")) {
            videoId = youtubeUrl.substring(youtubeUrl.indexOf("v=") + 2);
            if (videoId.contains("&")) {
                videoId = videoId.substring(0, videoId.indexOf("&"));
            }
        } else if (youtubeUrl.contains("youtu.be/")) {
            videoId = youtubeUrl.substring(youtubeUrl.lastIndexOf("/") + 1);
            // Check if there's a query string in the youtu.be URL
            if (videoId.contains("?")) {
                videoId = videoId.substring(0, videoId.indexOf("?"));
            }
        }
        // Ensure the video ID is 11 characters long (YouTube video ID standard)
        if (videoId != null && videoId.length() != 11) {
            videoId = null; // Invalid ID
        }
        return videoId;
    }
    private void bookmarkPopupWindow(String bookMarkName, String bookmarkImageName,
                                     String bookmarkDescription, int bookmarkQuantity, String bookmarkIngredients,
                                     String bookmarkMeasurement, String bookmarkProcedure, String bookmarkLink,String bookmarkDate){

        TextView tv_bookmarkName;
        Button btn_addBookmark;
        Button btn_cancelBookmark;

        View popupView = LayoutInflater.from(DishInformation.this).inflate(R.layout.generate_bookmark_popup_window, null);
        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        // Set an elevation value for popup window (optional)
        popupWindow.setElevation(20);
        // Show the popup window
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);


        tv_bookmarkName = popupView.findViewById(R.id.tv_bookmarkName);
        btn_addBookmark = popupView.findViewById(R.id.btn_addbookmark);
        btn_cancelBookmark = popupView.findViewById(R.id.btn_cancelbookmark_popup);

        tv_bookmarkName.setText(bookMarkName);

        btn_cancelBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        btn_addBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BookmarkDatabaseOpenHelper.getInstance(DishInformation.this).addOneBookmark(bookMarkName, bookmarkImageName,
                        bookmarkDescription, bookmarkQuantity, bookmarkIngredients,
                        bookmarkMeasurement, bookmarkProcedure, bookmarkLink, date);
                //Toast.makeText(DishInformation.this, "Bookmark Added!", Toast.LENGTH_SHORT).show();
                StyleableToast.makeText(DishInformation.this, "Bookmark Added!", R.style.exampleToast).show();
                popupWindow.dismiss();
            }
        });
    }

}