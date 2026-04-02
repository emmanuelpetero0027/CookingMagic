package BottomNavigationBar;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static android.database.sqlite.SQLiteDatabase.openDatabase;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cookingmagic.DishInformation;
import com.example.cookingmagic.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import Model.AddDish.AddModel;
import Model.Database.AddDishDatabaseOpenHelper;
import Model.Database.DishInformationDatabaseOpenHelper;
import Model.ViewAll.DishInformationModel;
import ViewModel.ViewAll.DishInformationRecyclerViewAdapter;
import ViewModel.ViewAll.FeaturedDishRecyclerViewAdapter;
import ViewModel.ViewAll.FeaturedInterface;
import ViewModel.ViewAll.RecyclerViewInterface;

public class ViewAll extends Fragment implements RecyclerViewInterface, FeaturedInterface {

    View rootView;
    RecyclerView rv_dishInformation;
    RecyclerView rv_dishInformationFeatured;
    Button btn_seeMore;

    AutoCompleteTextView autoComplete_searchBar;

    ImageButton btn_voiceRecognitionSearch;

    List<DishInformationModel> dishList;

    private int pastVisibleItems, visibleItemCount, totalItemCount;
    private LinearLayoutManager linearLayoutManager;
    private LinearLayoutManager linearLayoutManagerFeatured;
    DishInformationRecyclerViewAdapter dishInformationRecyclerViewAdapter;
    FeaturedDishRecyclerViewAdapter featuredDishRecyclerViewAdapter;


    private static final int PAGE_SIZE = 20; // Number of items per page
    private int currentPage = 0;
    private boolean isLastPage = false;
    private boolean isLoading = false;

    Button btn_searchReset;


    List<AddModel> addModels;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.main_fragment_view_all, container, false);

        rv_dishInformation = rootView.findViewById(R.id.rv_dishInformation);
        autoComplete_searchBar = rootView.findViewById(R.id.autoComplete_searchBar);
        rv_dishInformationFeatured = rootView.findViewById(R.id.rv_dishInformationFeatured);
        btn_voiceRecognitionSearch = rootView.findViewById(R.id.btn_voiceRecognitionSearch);
        btn_searchReset = rootView.findViewById(R.id.btn_searchReset);
        btn_seeMore = rootView.findViewById(R.id.btn_seeMore);

        dishList = new ArrayList<>();


        addModels = new ArrayList<>();
        addModels = AddDishDatabaseOpenHelper.getInstance(requireContext()).getAddDish();
        for(AddModel add: addModels){
            dishList.add(new DishInformationModel(add.getDishId(), add.getDishName(), add.getImagePath(),
                    add.getDescription(), 0, " ", add.getMeasurements(), add.getProcedure(), " "));
        }


        //All Dishes
        linearLayoutManager = new LinearLayoutManager(requireContext());
        rv_dishInformation.setLayoutManager(linearLayoutManager);
        rv_dishInformation.setItemAnimator(new DefaultItemAnimator());
        dishInformationRecyclerViewAdapter = new DishInformationRecyclerViewAdapter(requireContext(), dishList, this);
        rv_dishInformation.setAdapter(dishInformationRecyclerViewAdapter);


        //RecyclerView Featured
        featuredDish();
        linearLayoutManagerFeatured = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        rv_dishInformationFeatured.setHasFixedSize(true);
        rv_dishInformationFeatured.setLayoutManager(linearLayoutManagerFeatured);
        featuredDishRecyclerViewAdapter = new FeaturedDishRecyclerViewAdapter(requireContext(), suggestedDishes, this);
        rv_dishInformationFeatured.setAdapter(featuredDishRecyclerViewAdapter);

        btn_seeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                suggestedDishes.clear();
                suggestedDishes = getRandomItems(featuredDishInformationModel, 20);
                featuredDishRecyclerViewAdapter.seeMore(suggestedDishes);
            }
        });

        setUpPagination();
        loadMoreData();

        //autocomplete search bar
        autoCompleteSearchbar();


        btn_voiceRecognitionSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speak();
            }
        });

        btn_searchReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<DishInformationModel> dishes = DishInformationDatabaseOpenHelper.getInstance(requireContext()).getDish();

                List<AddModel> addModels2 = new ArrayList<>();
                addModels2 = AddDishDatabaseOpenHelper.getInstance(requireContext()).getAddDish();
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
                btn_searchReset.setVisibility(View.INVISIBLE);
                autoComplete_searchBar.setText("");
            }
        });

        return rootView;
    }

    //Voice Recognition
    private final int REQUEST_CODE_SPEECH_INPUT = 100;
    void speak(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Start Speaking . . .");
        startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
    }

    private void setUpPagination(){
        rv_dishInformation.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if(dy > 0 ){
                     visibleItemCount = linearLayoutManager.getChildCount();
                     totalItemCount = linearLayoutManager.getItemCount();
                     pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();

                    if (!isLoading && !isLastPage) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            isLoading = true;
                            loadMoreData();
                        }
                    }
                }
            }
        });
    }

    private void loadMoreData() {
        // Fetch the next page of data
        List<DishInformationModel> newDishes = DishInformationDatabaseOpenHelper.getInstance(requireContext()).getDish(currentPage, PAGE_SIZE);

        if (newDishes.isEmpty()) {
            isLastPage = true;
        } else {
            // Add new data to your existing data list and notify the adapter
            dishList.addAll(newDishes);
            dishInformationRecyclerViewAdapter.notifyDataSetChanged();
            currentPage++;
        }
        isLoading = false;
    }


    //autoComplete Searchbar
    private void autoCompleteSearchbar(){
        //List<DishInformationModel> dishes = DishInformationDatabaseOpenHelper.getInstance(requireContext()).getDish();
        autoComplete_searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String searchText = autoComplete_searchBar.getText().toString();

                List<DishInformationModel> searchDish = new ArrayList<>();

                for(DishInformationModel dishLis2t: dishList){
                    if(dishLis2t.getDishName().toLowerCase().contains(searchText)){
                        searchDish.add(dishLis2t);
                    }
                }

                dishInformationRecyclerViewAdapter.searchDish(searchDish);

                btn_searchReset.setVisibility(View.VISIBLE);
                btn_searchReset.setText(searchText + "\t(X)");

                return false;
            }
        });
    }


    List<DishInformationModel> featuredDishInformationModel;
    List<DishInformationModel> suggestedDishes;
    private void featuredDish() {

        // Get the current date
        Calendar calendar = Calendar.getInstance();
        // Create a SimpleDateFormat instance with the pattern for the day of the week
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault()); // "EEEE" gives full day name
        // Get the day name from the current date
        String dayName = sdf.format(calendar.getTime());
        // Output the day name (for example, log it or show in TextView)
        System.out.println("Today is: " + dayName);


        List<DishInformationModel> dish = DishInformationDatabaseOpenHelper.getInstance(requireContext()).getDish();
        featuredDishInformationModel = new ArrayList<>();

        for(DishInformationModel dishes : dish){
            if(Objects.equals(dishes.getCategory(), "Seafood") && dayName.equals("Monday")){
                featuredDishInformationModel.add(new DishInformationModel(
                        dishes.getDishID(),
                        dishes.getDishName(),
                        dishes.getImageName(),
                        dishes.getDescription(),
                        dishes.getQuantity(),
                        dishes.getIngredients(),
                        dishes.getMeasurement(),
                        dishes.getProcedure(),
                        dishes.getLink()
                ));
            }
            else if(Objects.equals(dishes.getCategory(), "Chicken") && dayName.equals("Tuesday")){
                featuredDishInformationModel.add(new DishInformationModel(
                        dishes.getDishID(),
                        dishes.getDishName(),
                        dishes.getImageName(),
                        dishes.getDescription(),
                        dishes.getQuantity(),
                        dishes.getIngredients(),
                        dishes.getMeasurement(),
                        dishes.getProcedure(),
                        dishes.getLink()
                ));
            }
            else if(Objects.equals(dishes.getCategory(), "Vegetables") && dayName.equals("Wednesday")){
                featuredDishInformationModel.add(new DishInformationModel(
                        dishes.getDishID(),
                        dishes.getDishName(),
                        dishes.getImageName(),
                        dishes.getDescription(),
                        dishes.getQuantity(),
                        dishes.getIngredients(),
                        dishes.getMeasurement(),
                        dishes.getProcedure(),
                        dishes.getLink()
                ));
            }
            else if(Objects.equals(dishes.getCategory(), "Fish") && dayName.equals("Thursday")){
                featuredDishInformationModel.add(new DishInformationModel(
                        dishes.getDishID(),
                        dishes.getDishName(),
                        dishes.getImageName(),
                        dishes.getDescription(),
                        dishes.getQuantity(),
                        dishes.getIngredients(),
                        dishes.getMeasurement(),
                        dishes.getProcedure(),
                        dishes.getLink()
                ));
            }
            else if(Objects.equals(dishes.getCategory(), "Vegetables") && dayName.equals("Friday")){
                featuredDishInformationModel.add(new DishInformationModel(
                        dishes.getDishID(),
                        dishes.getDishName(),
                        dishes.getImageName(),
                        dishes.getDescription(),
                        dishes.getQuantity(),
                        dishes.getIngredients(),
                        dishes.getMeasurement(),
                        dishes.getProcedure(),
                        dishes.getLink()
                ));
            }
            else if(Objects.equals(dishes.getCategory(), "Pork") && dayName.equals("Saturday")){
                featuredDishInformationModel.add(new DishInformationModel(
                        dishes.getDishID(),
                        dishes.getDishName(),
                        dishes.getImageName(),
                        dishes.getDescription(),
                        dishes.getQuantity(),
                        dishes.getIngredients(),
                        dishes.getMeasurement(),
                        dishes.getProcedure(),
                        dishes.getLink()
                ));
            }
            else if(Objects.equals(dishes.getCategory(), "Beef") && dayName.equals("Sunday")){
                featuredDishInformationModel.add(new DishInformationModel(
                        dishes.getDishID(),
                        dishes.getDishName(),
                        dishes.getImageName(),
                        dishes.getDescription(),
                        dishes.getQuantity(),
                        dishes.getIngredients(),
                        dishes.getMeasurement(),
                        dishes.getProcedure(),
                        dishes.getLink()
                ));
            }
        }

        suggestedDishes = getRandomItems(featuredDishInformationModel, 20);

    }

    // Method to get 'n' random items from the list
    private <T> List<T> getRandomItems(List<T> list, int n) {
        List<T> copyList = new ArrayList<>(list); // Make a copy to avoid modifying the original list
        Collections.shuffle(copyList);  // Shuffle the list randomly
        return copyList.subList(0, Math.min(n, copyList.size()));  // Return the first 'n' items
    }

    @Override
    public void onItemClickFeatured(int position) {
        Intent intent = new Intent(requireActivity(), DishInformation.class);
        intent.putExtra("dishName", featuredDishInformationModel.get(position).getDishName());
        intent.putExtra("dishImage", featuredDishInformationModel.get(position).getImageName());
        intent.putExtra("description", featuredDishInformationModel.get(position).getDescription());
        intent.putExtra("quantity", featuredDishInformationModel.get(position).getQuantity());
        intent.putExtra("ingredients", featuredDishInformationModel.get(position).getIngredients());
        intent.putExtra("measurement", featuredDishInformationModel.get(position).getMeasurement());
        intent.putExtra("procedure", featuredDishInformationModel.get(position).getProcedure());
        intent.putExtra("youtubeLink", featuredDishInformationModel.get(position).getLink());
        startActivity(intent);
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(requireActivity(), DishInformation.class);
        intent.putExtra("dishName", dishList.get(position).getDishName());
        intent.putExtra("dishImage", dishList.get(position).getImageName());
        intent.putExtra("description", dishList.get(position).getDescription());
        intent.putExtra("quantity", dishList.get(position).getQuantity());
        intent.putExtra("ingredients", dishList.get(position).getIngredients());
        intent.putExtra("measurement", dishList.get(position).getMeasurement());
        intent.putExtra("procedure", dishList.get(position).getProcedure());
        intent.putExtra("youtubeLink", dishList.get(position).getLink());
        startActivity(intent);
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
                System.out.println(voiceResult);
                voiceSearch(voiceResult);
                btn_searchReset.setVisibility(View.VISIBLE);
                btn_searchReset.setText(voiceResult + "\t(X)");
            }
        }
    }
    private void voiceSearch(String searchText){
        List<DishInformationModel> dishes = DishInformationDatabaseOpenHelper.getInstance(requireContext()).getDish();

        List<DishInformationModel> searchDish = new ArrayList<>();

        for(DishInformationModel dishList: dishes){
            if(dishList.getDishName().toLowerCase().contains(searchText)){
                searchDish.add(dishList);
            }
        }
        dishInformationRecyclerViewAdapter.searchDish(searchDish);
    }

}