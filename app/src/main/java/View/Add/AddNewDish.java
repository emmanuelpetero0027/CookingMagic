package View.Add;

import static android.content.ContentValues.TAG;

import static BottomNavigationBar.Generate.gettingAllIngredients;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import View.LoadingDialog;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cookingmagic.R;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Model.Database.AddDishDatabaseOpenHelper;
import io.github.muddz.styleabletoast.StyleableToast;

public class AddNewDish extends AppCompatActivity {

    int stepCounter = 1;

    EditText et_dishName;
    EditText et_description;
    AutoCompleteTextView auto_tv_ingredients;
    EditText et_measurements;
    Button btn_addIngredients;
    ListView lv_addIngredients;
    ListView lv_procedure;
    EditText et_procedure;
    Button btn_addProcedure;
    ImageView img_dishImageUpload;
    Button btn_uploadImage;
    Button btn_save;
    TextView tv_ingredientsRequired;
    TextView tv_procedureRequired;
    TextView tv_imageRequired;

    ArrayList<String> ingredientsItem;
    ArrayList<String> proceduresItem;

    String dishName = "";
    String description = "";
    String ingredientsList = "";
    String procedure = "";
    String image = "";
    String imagePath = " ";
    Uri imageUri;
    String filename = "";
    String imageUriString = "";


    LoadingDialog loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.add_new_dish);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            //v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        et_dishName = findViewById(R.id.et_dishName);
        et_description = findViewById(R.id.et_description);
        auto_tv_ingredients = findViewById(R.id.auto_tv_ingredients);
        et_measurements = findViewById(R.id.et_measurement);
        btn_addIngredients = findViewById(R.id.btn_addIngredients);
        lv_addIngredients = findViewById(R.id.lv_addIngredients);
        lv_procedure = findViewById(R.id.lv_procedure);
        et_procedure = findViewById(R.id.et_procedure);
        btn_addProcedure = findViewById(R.id.btn_addProcedure);
        img_dishImageUpload = findViewById(R.id.img_dishImageUpload);
        btn_uploadImage = findViewById(R.id.btn_uploadImage);
        btn_save = findViewById(R.id.btn_save);
        tv_ingredientsRequired = findViewById(R.id.tv_ingredientsRequired);
        tv_procedureRequired = findViewById(R.id.tv_procedureRequired);
        tv_imageRequired = findViewById(R.id.tv_imageRequired);


        loading = new LoadingDialog(AddNewDish.this);

        ingredientsItem = new ArrayList<>();
        proceduresItem = new ArrayList<>();

        //AutoComplete Text View
        ArrayAdapter<String> dropDownIngredientsList = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, gettingAllIngredients());
        auto_tv_ingredients.setAdapter(dropDownIngredientsList);

        ArrayAdapter<String> ingredientsListItemAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1, // Built-in layout for list item
                ingredientsItem
        );

        btn_addIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = auto_tv_ingredients.getText().toString().trim();
                if(!input.isEmpty()){
                    ingredientsItem.add(auto_tv_ingredients.getText().toString() + "  ( " + et_measurements.getText().toString().toUpperCase() + " )");
                    // Set the adapter to the ListView
                    lv_addIngredients.setAdapter(ingredientsListItemAdapter);
                    auto_tv_ingredients.setText("");
                    et_measurements.setText("");
                    tv_ingredientsRequired.setText("");
                }
                else{
                    //Toast.makeText(AddNewDish.this, "Please Enter Ingredient!", Toast.LENGTH_SHORT).show();
                    StyleableToast.makeText(AddNewDish.this, "Please Enter Ingredient!", R.style.exampleToast).show();

                }
            }
        });

        // Set an item click listener to remove the item
        lv_addIngredients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Remove the clicked item from the data set
                ingredientsItem.remove(position);
                // Notify the adapter that the data set has changed
                ingredientsListItemAdapter.notifyDataSetChanged();
            }
        });

        ArrayAdapter<String> procedureListItemAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1, // Built-in layout for list item
                proceduresItem
        );

        ////Adding Procedure
        btn_addProcedure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = et_procedure.getText().toString().trim();
                if(!input.isEmpty()){
                    proceduresItem.add("Step " + stepCounter + ": " + et_procedure.getText().toString());
                    lv_procedure.setAdapter(procedureListItemAdapter);
                    et_procedure.setText("");
                    stepCounter ++;
                    tv_procedureRequired.setText("");
                }
            }
        });
        lv_procedure.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Remove the clicked item from the data set
                proceduresItem.remove(position);
                // Notify the adapter that the data set has changed
                procedureListItemAdapter.notifyDataSetChanged();
                stepCounter --;
            }
        });

        btn_uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dishName = et_dishName.getText().toString();
                description = et_description.getText().toString();
                ingredientsList = String.join("\n", ingredientsItem);
                procedure = String.join("\n", proceduresItem);
                image = img_dishImageUpload.toString();

                if(!dishName.isEmpty() && !description.isEmpty() && !ingredientsList.isEmpty() && !procedure.isEmpty() && !imageUriString.isEmpty()){

                    loading.startLoadingDialog();
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    // Run heavy tasks in the background
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            // Perform the background work
                            saveImageToInternalStorage();
                            boolean added = AddDishDatabaseOpenHelper.getInstance(AddNewDish.this).addOneDish(
                                    dishName,
                                    description,
                                    ingredientsList,
                                    procedure,
                                    filename
                            );
                            // Ensure UI updates happen on the main thread

                            Handler mainHandler = new Handler(Looper.getMainLooper());
                            mainHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if(added){
                                        System.out.println(added);
                                        System.out.println("added now");
                                        // UI operations on the main thread
                                        et_dishName.setText("");
                                        et_description.setText("");
                                        ingredientsItem.clear();
                                        proceduresItem.clear();
                                        ingredientsListItemAdapter.notifyDataSetChanged();
                                        procedureListItemAdapter.notifyDataSetChanged();
                                        img_dishImageUpload.setImageDrawable(null);
                                        loading.dismiss();
                                        //Toast.makeText(AddNewDish.this, "New Dish Added!", Toast.LENGTH_SHORT).show();

                                        StyleableToast.makeText(AddNewDish.this, "New Dish Added!", R.style.exampleToast).show();

                                    }
                                }
                            }, 1000);
                        }
                    });


                }
                else{
                    //Toast.makeText(AddNewDish.this, "Please Enter Required Fields!", Toast.LENGTH_SHORT).show();
                    StyleableToast.makeText(AddNewDish.this, "Please Enter Required Fields!", R.style.exampleToast).show();


                }


                //Prompt Required Fields
                if(dishName.isEmpty()){
                    et_dishName.setError("Dish Name Required!");
                }
                if(description.isEmpty()){
                    et_description.setError("Description Required!");
                }
                if(ingredientsList.isEmpty()){
                    tv_ingredientsRequired.setText("Add Ingredients Required");
                    tv_ingredientsRequired.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                }
                if(procedure.isEmpty()){
                    tv_procedureRequired.setText("Add Procedure Required");
                    tv_procedureRequired.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                }
                if(imageUriString.isEmpty()){
                    tv_imageRequired.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                }

            }
        });

    }
    private static final int PICK_IMAGE = 1;
    void openGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            img_dishImageUpload.setImageURI(imageUri);
            img_dishImageUpload.setVisibility(View.VISIBLE);

            imageUriString = String.valueOf(imageUri);

            // Save the image path
            //imagePath = getRealPathFromURI(imageUri);
            System.out.println(imagePath);
            tv_imageRequired.setText("");
        }
    }

    private void saveImageToInternalStorage() {
        // Get the bitmap from ImageView
        BitmapDrawable drawable = (BitmapDrawable) img_dishImageUpload.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        // Define the filename and path
        filename = "local"+et_dishName.getText().toString()+"_image.png";
        FileOutputStream fos = null;

        try {
            // Open a file output stream to write the bitmap
            fos = openFileOutput(filename, MODE_PRIVATE);
            // Compress the bitmap and write it to the file
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

