package BottomNavigationBar;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.viewpager2.widget.ViewPager2;

import android.provider.MediaStore;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cookingmagic.R;
import com.google.android.material.tabs.TabLayout;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import IngredientsTabLayout.ViewPagerAdapter;
import Model.Generate.CheckboxesDataName;
import Model.Generate.IngredientsModel;
import Model.Generate.MachineLearning;
import ViewModel.Generate.ListOfIngredientsGeneratingDishViewModel;
import View.GenerateDishes;
import io.github.muddz.styleabletoast.StyleableToast;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.core.content.ContextCompat;

import org.tensorflow.lite.Interpreter;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class Generate extends Fragment{

    MachineLearning machineLearning;

    //Tab layout for categories
    TabLayout tl_categories;
    //Page Information for Generate
    ViewPager2 vp_pageInformation;
    //Setting up an ViewPagerAdapter for the fragments of the tabs
    ViewPagerAdapter viewPagerAdapter;

    //AutoComplete Search Bar
    AutoCompleteTextView at_searchBar;
    //Voice Recognition Button
    ImageButton btn_voiceRecognitionSearch;
    //Camera Object Detection
    ImageButton btn_camera;
    //Floating Button Counter List
    Button btn_floatingButtonCounterList;


    View rootView;

    @SuppressLint("ResourceType")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.main_fragment_generate, container, false);

        //getting tab values
        tl_categories = rootView.findViewById(R.id.tl_categories);
        vp_pageInformation = rootView.findViewById(R.id.vp_pageInformation);
        viewPagerAdapter = new ViewPagerAdapter(this);
        //vp_pageInformation.setOffscreenPageLimit(6);
        viewPagerAdapter.notifyDataSetChanged();
        vp_pageInformation.setAdapter(viewPagerAdapter);



        //tab listener for the fragments
        tl_categories.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp_pageInformation.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        //getting the icons for the tabs
        Objects.requireNonNull(tl_categories.getTabAt(0)).setIcon(R.drawable.generate_tab_layout_pork_icon);
        Objects.requireNonNull(tl_categories.getTabAt(1)).setIcon(R.drawable.generate_tab_layout_chicken_icon);
        Objects.requireNonNull(tl_categories.getTabAt(2)).setIcon(R.drawable.generate_tab_layout_beef_icon);
        Objects.requireNonNull(tl_categories.getTabAt(3)).setIcon(R.drawable.generate_tab_layout_fish_icon);
        Objects.requireNonNull(tl_categories.getTabAt(4)).setIcon(R.drawable.generate_tab_layout_vegetables_icon);
        Objects.requireNonNull(tl_categories.getTabAt(5)).setIcon(R.drawable.generate_tab_layout_herbs_icon);
        Objects.requireNonNull(tl_categories.getTabAt(6)).setIcon(R.drawable.generate_tab_layout_condiments_icon);
        Objects.requireNonNull(tl_categories.getTabAt(7)).setIcon(R.drawable.generate_tab_layout_fruits_icon);
        Objects.requireNonNull(tl_categories.getTabAt(8)).setIcon(R.drawable.generate_tab_layout_pantry_icon);


        //swiping on tabs
        vp_pageInformation.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tl_categories.getTabAt(position).select();
            }
        });

        //AutoComplete Text View
        // Search Bar
        at_searchBar = rootView.findViewById(R.id.at_searchBar);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, gettingAllIngredients());
        at_searchBar.setAdapter(adapter);
        at_searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE ||
                        (keyEvent != null && keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                                keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)){

                    String searchText = at_searchBar.getText().toString();
                    boolean checkIngredientsName = false;
                    for(String checkAllIngredients : gettingAllIngredients()){
                        if(checkAllIngredients.contains(searchText)){
                            checkIngredientsName = true;
                            at_searchBar.setText("");
                            System.out.println("Ingredient Found " + searchText);
                            break;
                        }
                        else{
                            checkIngredientsName = false;
                        }
                    }
                    if(checkIngredientsName){
                        popUpWindow(searchText);
                        Log.d(TAG, "found");
                    }
                    else{
                        //Toast.makeText(requireContext(), "No Ingredients Found!", Toast.LENGTH_SHORT).show();
                        StyleableToast.makeText(requireContext(), "No Ingredients Found!", R.style.exampleToast).show();
                    }

                }
                return false;
            }
        });

        // Voice Search Button
        btn_voiceRecognitionSearch = rootView.findViewById(R.id.btn_voiceRecognitionSearch);
        btn_voiceRecognitionSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });
        //Camera Button
        btn_camera = rootView.findViewById(R.id.btn_camera);
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {capture();}
        });

        //Floating Button Counter List
        btn_floatingButtonCounterList = rootView.findViewById(R.id.btn_floatingButtonCounterList);
        ListOfIngredientsGeneratingDishViewModel listOfIngredientsGeneratingDishViewModel = new ListOfIngredientsGeneratingDishViewModel();
        listOfIngredientsGeneratingDishViewModel.getCounterLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                btn_floatingButtonCounterList.setText(integer.toString());
                if(integer.equals(0)){
                    btn_floatingButtonCounterList.setText("");
                }
            }
        });
        btn_floatingButtonCounterList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ingredientsListPopupWindow();
            }
        });



        // Inflate the layout for this fragment
        return rootView;
    }

    //Pop up Window for Search Bar
    private void popUpWindow(String searchText) {
        TextView tv_noResult;
        Button btn_cancel_searchBox;
        LinearLayout ll_checkBoxes;

        View popupView = getLayoutInflater().inflate(R.layout.generate_search_box_pop_up_window, null);
        // Create the PopupWindow
        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        // Set an elevation value for popup window (optional)
        popupWindow.setElevation(20);
        // Show the popup window
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        //searchName_searchBox = popupView.findViewById(R.id.tv_searchName);
        btn_cancel_searchBox = popupView.findViewById(R.id.btn_cancel_searchBox);
        ll_checkBoxes = popupView.findViewById(R.id.ll_checkBoxes);
        tv_noResult = popupView.findViewById(R.id.tv_noResult);

        // Split the sentence into words
        String[] toSearch = searchText.toLowerCase().split("\\s+");

        //Holds the Check IngredientsName
        List<String> resultsName = new ArrayList<>();

        for(String gettingAllIngredientsName: gettingAllIngredients()){
            if(gettingAllIngredientsName.toLowerCase().contains(searchText.toLowerCase())){
                if(!resultsName.contains(gettingAllIngredientsName)){
                    resultsName.add(gettingAllIngredientsName);
                }

            }

        }
        for(String gettingAllIngredientsName: gettingAllIngredients()){
            for(String searchName: toSearch){
                if(gettingAllIngredientsName.toLowerCase().contains(searchName.toLowerCase())){
                    if(!resultsName.contains(gettingAllIngredientsName)){
                        resultsName.add(gettingAllIngredientsName);
                    }
                }
            }
        }

        System.out.println(resultsName);
        System.out.println(resultsName.size());

        for(int i = 0; i < resultsName.size(); i++) {
            CheckBox checkBox = new CheckBox(requireContext());
            checkBox.setText(resultsName.get(i));
            checkBox.setPadding(50, 0, 0, 0);
            checkBox.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            checkBox.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
            checkBox.setHeight(200);

            ll_checkBoxes.addView(checkBox);
            // Add divider (horizontal line) after each checkbox, except the last one
            if (i < resultsName.size() - 1) {
                View divider = new View(requireContext());
                LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1); // Height of divider, adjust as needed
                divider.setLayoutParams(dividerParams);
                divider.setBackgroundColor(ContextCompat.getColor(requireContext(),
                        android.R.color.darker_gray)); // Divider color
                ll_checkBoxes.addView(divider);
            }

            //Adding Checkbox name on the Ingredients List for Generating Dish
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if(isChecked) {
                        if(ListOfIngredientsGeneratingDishViewModel.getInstance().getIngredientsModelLiveData().contains(checkBox.getText().toString())){
                            System.out.println("Added Already!" + checkBox.getText().toString());
                        }
                        else{
                            ListOfIngredientsGeneratingDishViewModel.getInstance().addingIngredientsModelName(new IngredientsModel(checkBox.getText().toString()));
                            System.out.println(ListOfIngredientsGeneratingDishViewModel.getInstance().getIngredientsModelLiveData());
                        }
                    }
                    else{
                        ListOfIngredientsGeneratingDishViewModel.getInstance().removeIngredientsModelName(checkBox.getText().toString());
                    }
                }
            });

        }

        // Dismiss the popup window when touched
        btn_cancel_searchBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the popup window
                popupWindow.dismiss();
            }
        });

        if(resultsName.isEmpty()){
            tv_noResult.setText("No Result for : " + searchText);
        }else{
            tv_noResult.setText("");
        }
    }

    //Voice Recognition
    private final int REQUEST_CODE_SPEECH_INPUT = 100;
    public void speak(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Start Speaking . . .");
        startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
    }
    private void capture(){
        // Check if the app has camera permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            // If permission is granted, open the camera
            openCamera();
        } else {
            // Request permission if not granted
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // If permission is granted, open the camera
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            // Permission denied
            if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                // The user has permanently denied the permission and checked "Don't ask again"
                showPermissionDeniedDialog();
            } else {
                // Permission denied but can be requested again
                //Toast.makeText(requireContext(), "Camera permission is required to take photos.", Toast.LENGTH_SHORT).show();
                StyleableToast.makeText(requireContext(), "Camera permission is required to take photos!", R.style.exampleToast).show();
            }
        }
    }
    private void openCamera(){
        machineLearning = new MachineLearning(requireContext());
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA_INPUT);
    }
    private void showPermissionDeniedDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Permission Denied")
                .setMessage("Camera permission is required to take photos. Please enable it in the app settings.")
                .setPositiveButton("Go to Settings", (dialog, which) -> {
                    // Open app settings to allow the user to manually enable the permission
                    openAppSettings();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void openAppSettings() {
        // Open the app's settings page
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }


    //Camera Object Detection Popup Window
    private final int REQUEST_CODE_CAMERA_INPUT = 100;
    ImageView imageView;
    Button btn_cancel_camera;
    LinearLayout layout_camera;
    Button btn_add_camera;
    TextView tv_cameraNoResult;

    int imageSize = 224;
    public void cameraPopupWindow(Bitmap image){
        View popupView = getLayoutInflater().inflate(R.layout.generate_camera_popup_window, null);
        imageView = popupView.findViewById(R.id.imageView);
        btn_cancel_camera = popupView.findViewById(R.id.btn_cancel_camera);
        layout_camera = popupView.findViewById(R.id.layout_camera);
        tv_cameraNoResult = popupView.findViewById(R.id.tv_cameraNoResult);

        // Create the PopupWindow
        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        // Set an elevation value for popup window (optional)
        popupWindow.setElevation(20);
        // Show the popup window
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        int dimension = Math.min(image.getWidth(), image.getHeight());
        image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
        imageView.setImageBitmap(image);
        image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);


        //Image Capturing
        machineLearning.imageRecognition(image);

        ///

        //Holds the Check IngredientsName
        List<String> resultsName = new ArrayList<>();


        for(String gettingAllIngredientsName: gettingAllIngredients()){
            for (String imageCapture : machineLearning.captureImage) {
                if(gettingAllIngredientsName.equals(imageCapture)){
                    if(!resultsName.contains(gettingAllIngredientsName)){
                        resultsName.add(gettingAllIngredientsName);
                    }
                }
            }
        }

        for(int i = 0; i < resultsName.size(); i++) {
            CheckBox checkBox = new CheckBox(requireContext());
            checkBox.setText(resultsName.get(i));
            checkBox.setPadding(50, 0, 0, 0);
            checkBox.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            checkBox.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
            checkBox.setHeight(200);

            layout_camera.addView(checkBox);
            // Add divider (horizontal line) after each checkbox, except the last one
            if (i < resultsName.size() - 1) {
                View divider = new View(requireContext());
                LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1); // Height of divider, adjust as needed
                divider.setLayoutParams(dividerParams);
                divider.setBackgroundColor(ContextCompat.getColor(requireContext(),
                        android.R.color.darker_gray)); // Divider color
                layout_camera.addView(divider);
            }
        //Adding Checkbox name on the Ingredients List for Generating Dish
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    if(ListOfIngredientsGeneratingDishViewModel.getInstance().getIngredientsModelLiveData().contains(checkBox.getText().toString())){
                        System.out.println("Added Already!" + checkBox.getText().toString());
                    }
                    else{
                        ListOfIngredientsGeneratingDishViewModel.getInstance().addingIngredientsModelName(new IngredientsModel(checkBox.getText().toString()));
                        System.out.println(ListOfIngredientsGeneratingDishViewModel.getInstance().getIngredientsModelLiveData());
                    }
                }
                else{
                    ListOfIngredientsGeneratingDishViewModel.getInstance().removeIngredientsModelName(checkBox.getText().toString());
                }
            }
            });
        }

        // Dismiss the popup window when touched
        btn_cancel_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the popup window
                popupWindow.dismiss();
            }
        });

        if(resultsName.isEmpty()){
            tv_cameraNoResult.setText("No Result");
        }else{
            tv_cameraNoResult.setText("");
        }
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
                popUpWindow(voiceResult);
            }
        }
        //Camera Ouput
        if (requestCode == REQUEST_CODE_CAMERA_INPUT && resultCode == RESULT_OK && data != null) {
            // Handle image capture result
            Bitmap image = (Bitmap) data.getExtras().get("data");
            if (image != null) {
                cameraPopupWindow(image);
            }
        }
    }

    //Popup Window for Ingredients List
    private void ingredientsListPopupWindow(){
        Button btn_backIngredientsList;
        ListView lv_listOfIngredients;
        //Cooking Magic
        Button btn_cookingMagic;

        View popupView = getLayoutInflater().inflate(R.layout.generate_ingredients_list_popup_window, null);
        // Create the PopupWindow
        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        // Set an elevation value for popup window (optional)
        popupWindow.setElevation(20);
        // Show the popup window
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        btn_backIngredientsList = popupView.findViewById(R.id.btn_backIngredientsList);
        lv_listOfIngredients = popupView.findViewById(R.id.lv_listOfIngredients);
        btn_cookingMagic = popupView.findViewById(R.id.btn_cookingMagic);

        // Accessing the singleton ArrayList
        List<String> ingredientsName = ListOfIngredientsGeneratingDishViewModel.getInstance().getIngredientsModelLiveData();
        List<String> data = new ArrayList<>(ingredientsName);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1,
                data);
        // Set the adapter to the ListView
        lv_listOfIngredients.setAdapter(adapter);
        // Set item click listener to remove item on click
        lv_listOfIngredients.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = data.get(position);
            // Remove the item from the ArrayList
            for(String namesToRemove : ListOfIngredientsGeneratingDishViewModel.getInstance().getIngredientsModelLiveData()){
                if(namesToRemove.equalsIgnoreCase(selectedItem)){
                    ListOfIngredientsGeneratingDishViewModel.getInstance().removeIngredientsModelName(selectedItem);
                }
            }
            data.remove(position);
            // Notify the adapter that the data set has changed
            adapter.notifyDataSetChanged();
        });

        btn_backIngredientsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the popup window
                popupWindow.dismiss();
            }
        });

        //Cooking Magic
        btn_cookingMagic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(), GenerateDishes.class);
                startActivity(intent);
            }
        });

    }

    //Getting All Ingredients Name
    public static ArrayList<String> gettingAllIngredients(){
        ArrayList<String> names = new ArrayList<>();
        CheckboxesDataName nameList = new CheckboxesDataName();

        names.addAll(Arrays.asList(nameList.getPorkIngredientsList()));
        names.addAll(Arrays.asList(nameList.getChickenIngredientsList()));
        names.addAll(Arrays.asList(nameList.getBeefIngredientsList()));
        names.addAll(Arrays.asList(nameList.getFishIngredientsList()));
        names.addAll(Arrays.asList(nameList.getVegetableIngredientList()));
        names.addAll(Arrays.asList(nameList.getCondimentsIngredientsList()));
        names.addAll(Arrays.asList(nameList.getSaucesIngredientsList()));
        names.addAll(Arrays.asList(nameList.getFruitsIngredientsList()));
        names.addAll(Arrays.asList(nameList.getPantryIngredientsList()));
        return  names;
    }



}
