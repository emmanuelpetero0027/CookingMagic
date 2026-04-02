package View;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookingmagic.DishInformation;
import com.example.cookingmagic.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Model.Database.AddDishDatabaseOpenHelper;
import Model.Database.DishInformationDatabaseOpenHelper;
import Model.Generate.CookingMagicDishes;
import Model.Generate.IngredientsModel;
import Model.ViewAll.DishInformationModel;
import View.Add.AddNewDish;
import ViewModel.Generate.CookingMagicRecyclerViewAdapter;
import ViewModel.Generate.ListOfIngredientsGeneratingDishViewModel;
import ViewModel.ViewAll.DishInformationRecyclerViewAdapter;
import ViewModel.ViewAll.RecyclerViewInterface;
import io.github.muddz.styleabletoast.StyleableToast;

public class GenerateDishes extends AppCompatActivity implements RecyclerViewInterface{

    RecyclerView rv_cookingMagic;
    private LinearLayoutManager linearLayoutManager;

    List<DishInformationModel> cookingMagicDishes;
    CookingMagicRecyclerViewAdapter cookingMagicRecyclerViewAdapter;

    Spinner spinner_filter;

    LoadingDialog loading;

    Button btn_clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.generate_activity_dishes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rv_cookingMagic = findViewById(R.id.rv_cookingMagic);
        cookingMagicDishes = new ArrayList<>();
        spinner_filter = findViewById(R.id.spinner_filter);
        loading = new LoadingDialog(GenerateDishes.this);
        btn_clear = findViewById(R.id.btn_clear);

        String bestMatch = "The Main Food Item in the Mix";
        String percetage = "Most Matching Dishes ";
        String ingredientsCount = "Most Available Ingredients";
        String ascending = "A - Z";
        String descending = "Z - A";


        String[] spinnerItems = {bestMatch, ingredientsCount, percetage, ascending, descending};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_filter.setAdapter(adapter);

        //All Dishes
        linearLayoutManager = new LinearLayoutManager(this);
        rv_cookingMagic.setLayoutManager(linearLayoutManager);
        rv_cookingMagic.setItemAnimator(new DefaultItemAnimator());

        loading.startLoadingDialog();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                boolean isRunning = generateDish();
                cookingMagicRecyclerViewAdapter = new CookingMagicRecyclerViewAdapter(GenerateDishes.this, cookingMagicDishes, GenerateDishes.this);
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(isRunning){}
                        loading.dismiss();
                        rv_cookingMagic.setAdapter(cookingMagicRecyclerViewAdapter);

                        spinner_filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                // Get the selected item
                                String selectedItem = adapterView.getItemAtPosition(i).toString();
                                // Do something with the selected item
                                if(selectedItem.equals(bestMatch)){
                                    System.out.println("Best Dish");
                                    StyleableToast.makeText(GenerateDishes.this, "Best Matching Dish ", R.style.exampleToast).show();
                                    cookingMagicRecyclerViewAdapter.bestDishSort();
                                }
                                else if(selectedItem.equals(ingredientsCount)){
                                    //Toast.makeText(GenerateDishes.this, "Selected: Percentage % ", Toast.LENGTH_SHORT).show();
                                    StyleableToast.makeText(GenerateDishes.this, "Most Available Ingredients % ", R.style.exampleToast).show();
                                    cookingMagicRecyclerViewAdapter.ingredientsCount();
                                }
                                else if(selectedItem.equals(percetage)){
                                    //Toast.makeText(GenerateDishes.this, "Selected: Percentage % ", Toast.LENGTH_SHORT).show();
                                    StyleableToast.makeText(GenerateDishes.this, "Selected: Percentage % ", R.style.exampleToast).show();
                                    cookingMagicRecyclerViewAdapter.percentageSort();
                                }
                                else if(selectedItem.equals(ascending)){
                                    //Toast.makeText(GenerateDishes.this, "Selected: A - Z ", Toast.LENGTH_SHORT).show();
                                    StyleableToast.makeText(GenerateDishes.this, "Selected: A - Z ", R.style.exampleToast).show();
                                    cookingMagicRecyclerViewAdapter.ascendingSort();
                                }
                                else if(selectedItem.equals(descending)){
                                    //Toast.makeText(GenerateDishes.this, "Selected: Z - A ", Toast.LENGTH_SHORT).show();
                                    StyleableToast.makeText(GenerateDishes.this, "Selected: Z - A ", R.style.exampleToast).show();
                                    cookingMagicRecyclerViewAdapter.descendingSort();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });

                    }
                });
            }
        });


        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cookingMagicRecyclerViewAdapter.reset();
                popUpWindow();
            }
        });
    }

    private boolean generateDish(){
        List<String> ingredients = ListOfIngredientsGeneratingDishViewModel.getInstance().getIngredientsModelLiveData();
        List<DishInformationModel> dish = DishInformationDatabaseOpenHelper.getInstance(this).getDish();

        try {
            for (DishInformationModel dishList : dish) {

                int ingredientsCounter = 0;

                int mainCounter = 0;
                int subCounter = 0;

                List<String> mainDishIngredientsList = new ArrayList<>();
                if (dishList.getMainIngredients() != null) {
                    // Split the string into an array of words
                    String[] mainDishIngredients = dishList.getMainIngredients().split("\n");
                    // Convert the array to a List
                    mainDishIngredientsList = new ArrayList<>(Arrays.asList(mainDishIngredients));
                }
                for (String main : mainDishIngredientsList) {
                    for (String ing : ingredients) {
                        if (main.toLowerCase().contains(ing.toLowerCase())) {
                            mainCounter++;
                        }
                    }
                }

                List<String> subDishIngredientsList = new ArrayList<>();
                if (dishList.getSubIngredients() != null) {
                    String[] subDishIngredients = dishList.getSubIngredients().split("\n");
                    subDishIngredientsList = new ArrayList<>(Arrays.asList(subDishIngredients));
                }
                for (String sub : subDishIngredientsList) {
                    for (String ing : ingredients) {
                        if (sub.toLowerCase().contains(ing.toLowerCase())) {
                            subCounter++;

                        }
                    }
                }

                // Split the string into an array of words
                String[] dishIngredients = dishList.getIngredients().split("\n");
                // Convert the array to a List
                List<String> dishIngredientsList = new ArrayList<>(Arrays.asList(dishIngredients));


                ArrayList<String> availableIngredients = new ArrayList<>();
                ArrayList<String> notAvailableIngredients = new ArrayList<>();

                for (String dishesList : dishIngredientsList) {
                    boolean isAvailable = false; // Flag to track if any ingredient is found
                    for (String ingredient : ingredients) {
                        if (dishesList.toLowerCase().contains(ingredient.toLowerCase())) {
                            isAvailable = true; // Set flag to true if ingredient is found
                            availableIngredients.add(dishesList);
                            ingredientsCounter++;
                        }
                        if(dishList.getCategory().equalsIgnoreCase("Pork") && ingredient.equalsIgnoreCase("Baboy (Pork)")){
                            isAvailable = true; // Set flag to true if ingredient is found
                            mainCounter++;
                        }
                        else if(dishList.getCategory().equalsIgnoreCase("Chicken") && ingredient.equalsIgnoreCase("Manok (Chicken)")){
                            isAvailable = true; // Set flag to true if ingredient is found
                            mainCounter++;
                        }
                        else if(dishList.getCategory().equalsIgnoreCase("Beef") && ingredient.equalsIgnoreCase("Baka (Beef)")){
                            isAvailable = true; // Set flag to true if ingredient is found
                            mainCounter++;
                        }
                        else if(dishList.getCategory().equalsIgnoreCase("Seafood") || dishList.getCategory().equalsIgnoreCase("Fish") && ingredient.equalsIgnoreCase("Isda (Fish)")){
                            isAvailable = true; // Set flag to true if ingredient is found
                            mainCounter++;
                        }


                    }
                    // After checking all ingredients, if none were found, add to notAvailableIngredients
                    if (!isAvailable) {
                        notAvailableIngredients.add(dishesList);
                    }
                }



                if (mainCounter > 0 || subCounter > 0) {
                    float quantity = dishList.getQuantity();
                    float percentageCounter = (Integer.parseInt(String.valueOf(availableIngredients.size())) / quantity) * 100;
                    int percentMatching = (int) percentageCounter;
                    cookingMagicDishes.add(new CookingMagicDishes(
                            dishList.getDishID(),
                            dishList.getDishName(),
                            dishList.getImageName(),
                            dishList.getDescription(),
                            dishList.getQuantity(),
                            dishList.getIngredients(),
                            dishList.getMeasurement(),
                            dishList.getProcedure(),
                            dishList.getLink(),
                            ingredientsCounter,
                            percentMatching,
                            availableIngredients,
                            notAvailableIngredients,
                            mainCounter,
                            subCounter));

                }
            }

            // Sort the list using a custom comparator
            Collections.sort(cookingMagicDishes, new Comparator<DishInformationModel>() {
                @Override
                public int compare(DishInformationModel d1, DishInformationModel d2) {
                    // Compare MainCounter first (descending order)
                    Integer main1 = d1.getMainIngredientsMatching();
                    Integer main2 = d2.getMainIngredientsMatching();

                    // Handle nulls: treat null as the smallest value (so it appears last)
                    if (main1 == null && main2 == null) {
                        return 0; // Both null are equal
                    }
                    if (main1 == null) {
                        return 1; // Null is considered less, so d1 is placed after d2
                    }
                    if (main2 == null) {
                        return -1; // Null is considered less, so d2 is placed after d1
                    }

                    // Compare non-null values in descending order
                    int mainCompare = main2.compareTo(main1);
                    if (mainCompare != 0) {
                        return mainCompare;
                    }

                    // If MainCounter is the same, compare SubCounter (descending order)
                    Integer sub1 = d1.getSubIngredientsMatching();
                    Integer sub2 = d2.getSubIngredientsMatching();

                    // Handle nulls for SubCounter
                    if (sub1 == null && sub2 == null) {
                        return 0; // Both null are equal
                    }
                    if (sub1 == null) {
                        return 1; // Null is considered less, so d1 is placed after d2
                    }
                    if (sub2 == null) {
                        return -1; // Null is considered less, so d2 is placed after d1
                    }

                    // Compare non-null values in descending order
                    return sub2.compareTo(sub1);
                }
            });


        }
        catch (Exception e){
            StyleableToast.makeText(GenerateDishes.this, "Error!", R.style.exampleToast).show();
        }
        finally {
            return true;

        }
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, DishInformation.class);
        intent.putExtra("dishName", cookingMagicDishes.get(position).getDishName());
        intent.putExtra("dishImage", cookingMagicDishes.get(position).getImageName());
        intent.putExtra("description", cookingMagicDishes.get(position).getDescription());
        intent.putExtra("measurement", cookingMagicDishes.get(position).getMeasurement());
        intent.putExtra("procedure", cookingMagicDishes.get(position).getProcedure());
        intent.putExtra("youtubeLink", cookingMagicDishes.get(position).getLink());
        intent.putStringArrayListExtra("availableIngredients", cookingMagicDishes.get(position).getAvailableIngredients());
        intent.putStringArrayListExtra("notAvailableIngredients", cookingMagicDishes.get(position).getNotAvailableIngredients());
        startActivity(intent);
    }


    List<String> filterNames;
    //Pop up Window for Search Bar
    private void popUpWindow() {
        LinearLayout ll_checkBoxes;
        Button btn_cancel_searchBox;

        View popupView = getLayoutInflater().inflate(R.layout.generate_search_box_pop_up_window, null);
        // Create the PopupWindow
        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        // Set an elevation value for popup window (optional)
        popupWindow.setElevation(20);
        // Show the popup window
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        btn_cancel_searchBox = popupView.findViewById(R.id.btn_cancel_searchBox);
        ll_checkBoxes = popupView.findViewById(R.id.ll_checkBoxes);

        filterNames = new ArrayList<>();

        List<String> ingredients = ListOfIngredientsGeneratingDishViewModel.getInstance().getIngredientsModelLiveData();

        for(int i = 0; i < ingredients.size(); i++) {
            CheckBox checkBox = new CheckBox(GenerateDishes.this);
            checkBox.setText(ingredients.get(i));
            checkBox.setPadding(50, 0, 0, 0);
            checkBox.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            checkBox.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
            checkBox.setHeight(200);

            ll_checkBoxes.addView(checkBox);
            // Add divider (horizontal line) after each checkbox, except the last one
            if (i < ingredients.size() - 1) {
                View divider = new View(GenerateDishes.this);
                LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1); // Height of divider, adjust as needed
                divider.setLayoutParams(dividerParams);
                divider.setBackgroundColor(ContextCompat.getColor(GenerateDishes.this,
                        android.R.color.darker_gray)); // Divider color
                ll_checkBoxes.addView(divider);
            }

            //Adding Checkbox name on the Ingredients List for Generating Dish
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if(isChecked) {
                        //cookingMagicRecyclerViewAdapter.mixAndmatch(ingredients);
                        System.out.println(checkBox.getText().toString());
                        filterNames.add(checkBox.getText().toString());
                    }
                    else{
                        filterNames.remove(checkBox.getText().toString());
                    }
                }
            });

            // Dismiss the popup window when touched
            btn_cancel_searchBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Dismiss the popup window
                    popupWindow.dismiss();
                    if(!ingredients.isEmpty()){
                        cookingMagicRecyclerViewAdapter.mixAndmatch(filterNames);
                        String listOfIngredients = filterNames.toString().replace("[", "").replace("]", "");
                        btn_clear.setText("Mix & Match : " + listOfIngredients);
                    }

                }
            });
        }
    }
}