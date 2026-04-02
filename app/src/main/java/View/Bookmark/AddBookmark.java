package View.Bookmark;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookingmagic.DishInformation;
import com.example.cookingmagic.R;

import java.util.ArrayList;
import java.util.List;

import BottomNavigationBar.Bookmark;
import Model.AddDish.AddModel;
import Model.Database.AddDishDatabaseOpenHelper;
import Model.Database.DishInformationDatabaseOpenHelper;
import Model.ViewAll.DishInformationModel;
import ViewModel.ViewAll.DishInformationRecyclerViewAdapter;
import ViewModel.ViewAll.RecyclerViewInterface;

public class AddBookmark extends AppCompatActivity implements RecyclerViewInterface {



    RecyclerView rv_dishInformation_bookmark;
    DishInformationRecyclerViewAdapter dishInformationRecyclerViewAdapter;
    private LinearLayoutManager linearLayoutManager;

    List<DishInformationModel> dishList;
    public static String bookmarkDate = "";

    AutoCompleteTextView autoComplete_searchBar_bookmark;
    ImageButton btn_voiceRecognitionSearch_bookmark;
    Button btn_bookmarkResetDish;

    List<AddModel> addModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.bookmark_adding_dish);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rv_dishInformation_bookmark = findViewById(R.id.rv_dishInformation_bookmark);
        autoComplete_searchBar_bookmark = findViewById(R.id.autoComplete_searchBar_bookmark);
        btn_voiceRecognitionSearch_bookmark = findViewById(R.id.btn_voiceRecognitionSearch_bookmark);
        btn_bookmarkResetDish = findViewById(R.id.btn_bookmarkResetDish);


        dishList = DishInformationDatabaseOpenHelper.getInstance(AddBookmark.this).getDish();
        /*
        addModels = AddDishDatabaseOpenHelper.getInstance(AddBookmark.this).getAddDish();
        for(AddModel add: addModels){
            dishList.add(new DishInformationModel(add.getDishId(), add.getDishName(), add.getImagePath(),
                    add.getDescription(), 0, " ", add.getMeasurements(), add.getProcedure(), " "));
            System.out.println(add.getDishId());
        }
         */
        dishList = DishInformationDatabaseOpenHelper.getInstance(AddBookmark.this).getDish();
        addModels = AddDishDatabaseOpenHelper.getInstance(AddBookmark.this).getAddDish();
        for (AddModel add : addModels) {
            DishInformationModel dishInfo = new DishInformationModel(
                    add.getDishId(),
                    add.getDishName(),
                    add.getImagePath(),
                    add.getDescription(),
                    0,
                    " ",
                    add.getMeasurements(),
                    add.getProcedure(),
                    " "
            );
            // Insert dishInfo at the first position (index 0) of the dishList
            dishList.add(0, dishInfo);
        }


        //All Dishes
        linearLayoutManager = new LinearLayoutManager(AddBookmark.this);
        rv_dishInformation_bookmark.setLayoutManager(linearLayoutManager);
        rv_dishInformation_bookmark.setItemAnimator(new DefaultItemAnimator());
        dishInformationRecyclerViewAdapter = new DishInformationRecyclerViewAdapter(AddBookmark.this, dishList, this);
        rv_dishInformation_bookmark.setAdapter(dishInformationRecyclerViewAdapter);


        //dishInformationRecyclerViewAdapter.notifyItemRangeChanged(addModels.size() - dishList.size(), dishList.size());


        btn_bookmarkResetDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<DishInformationModel> dishes = DishInformationDatabaseOpenHelper.getInstance(AddBookmark.this).getDish();

                List<AddModel> addModels2 = new ArrayList<>();
                addModels2 = AddDishDatabaseOpenHelper.getInstance(AddBookmark.this).getAddDish();
                for (AddModel add : addModels2) {
                    DishInformationModel dishInfo = new DishInformationModel(
                            add.getDishId(),
                            add.getDishName(),
                            add.getImagePath(),
                            add.getDescription(),
                            0,
                            " ",
                            add.getMeasurements(),
                            add.getProcedure(),
                            " "
                    );
                    // Insert dishInfo at the first position (index 0) of the dishList
                    dishes.add(0, dishInfo);
                }


                dishInformationRecyclerViewAdapter.resetDishes(dishes);
                btn_bookmarkResetDish.setVisibility(View.INVISIBLE);
                autoComplete_searchBar_bookmark.setText("");
            }
        });

        autoComplete_searchBar_bookmark.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                //List<DishInformationModel> dishes = DishInformationDatabaseOpenHelper.getInstance(AddBookmark.this).getDish();
                String searchText = autoComplete_searchBar_bookmark.getText().toString();

                List<DishInformationModel> searchDish = new ArrayList<>();

                for(DishInformationModel dishList: dishList){
                    if(dishList.getDishName().toLowerCase().contains(searchText)){
                        searchDish.add(dishList);
                    }
                }

                dishInformationRecyclerViewAdapter.searchDish(searchDish);
                btn_bookmarkResetDish.setVisibility(View.VISIBLE);
                btn_bookmarkResetDish.setText(searchText + "\t(X)");
                return false;
            }
        });

        btn_voiceRecognitionSearch_bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speak();
            }
        });

        bookmarkDate = getIntent().getStringExtra("date");
        System.out.println(bookmarkDate);
    }
    //Voice Recognition
    private final int REQUEST_CODE_SPEECH_INPUT = 100;
    void speak(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Start Speaking . . .");
        startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
    }

    //Voice Result // Camera Result
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Voice Recognition Output
        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            // Handle speech recognition result
            ArrayList<String> voiceResults = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (voiceResults != null && !voiceResults.isEmpty()) {
                String voiceResult = voiceResults.get(0); // Assuming you want the first result
                btn_bookmarkResetDish.setVisibility(View.VISIBLE);
                btn_bookmarkResetDish.setText(voiceResult + "\t(X)");
                System.out.println(voiceResult);
                voiceSearch(voiceResult);

            }
        }
    }
    private void voiceSearch(String searchText){
        List<DishInformationModel> dishes = DishInformationDatabaseOpenHelper.getInstance(AddBookmark.this).getDish();

        List<DishInformationModel> searchDish = new ArrayList<>();

        for(DishInformationModel dishList: dishes){
            if(dishList.getDishName().toLowerCase().contains(searchText)){
                searchDish.add(dishList);
            }
        }
        dishInformationRecyclerViewAdapter.searchDish(searchDish);

    }


    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(AddBookmark.this, DishInformation.class);
        intent.putExtra("dishName", dishList.get(position).getDishName());
        intent.putExtra("dishImage", dishList.get(position).getImageName());
        intent.putExtra("description", dishList.get(position).getDescription());
        intent.putExtra("measurement", dishList.get(position).getMeasurement());
        intent.putExtra("procedure", dishList.get(position).getProcedure());
        intent.putExtra("youtubeLink", dishList.get(position).getLink());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bookmarkDate = "";
    }
}