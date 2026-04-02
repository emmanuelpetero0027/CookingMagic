package ViewModel.ViewAll;


import static android.view.Gravity.apply;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cookingmagic.DishInformation;
import com.example.cookingmagic.R;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import BottomNavigationBar.Add;
import Model.Database.BookmarkDatabaseOpenHelper;
import Model.Database.DishInformationDatabaseOpenHelper;
import Model.ViewAll.DishInformationModel;
import View.Bookmark.AddBookmark;
import ViewModel.Add.AddDishRecyclerViewAdapter;
import io.github.muddz.styleabletoast.StyleableToast;

public class DishInformationRecyclerViewAdapter extends RecyclerView.Adapter<DishInformationRecyclerViewAdapter.MyViewHolder>{

    String date = "";

    Context context;
    List<DishInformationModel> dishInformationModel;
    private RecyclerViewInterface recyclerViewInterface;

    List<DishInformationModel> copyDishInformationModel;

    public DishInformationRecyclerViewAdapter(Context context, List<DishInformationModel> dishInformationModel, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.dishInformationModel = dishInformationModel;
        this.recyclerViewInterface = recyclerViewInterface;
        copyDishInformationModel = new ArrayList<>(this.dishInformationModel);
    }

    List<DishInformationModel> filteredDishes;
    //Mix and Match
    public void mixAndmatch(List<String> ingredients){
        filteredDishes = new ArrayList<>();

        for(DishInformationModel dishes: this.dishInformationModel){
            boolean allIngredientsFound = true;

            String[] dishIngredients = dishes.getIngredients().split("\n");

            for(String ing : ingredients){
                boolean ingredientFound = false;

                for(String dishIngredient: dishIngredients){
                    if(dishIngredient.equalsIgnoreCase(ing)){
                        ingredientFound = true;
                        break;
                    }
                }
                // If any of my ingredients are not found in the dish, mark as false
                if (!ingredientFound) {
                    allIngredientsFound = false;
                    break;
                }
            }
            // If all ingredients are found, add the dish to the result list
            if (allIngredientsFound) {
                filteredDishes.add(dishes);
            }
        }

        this.dishInformationModel.clear();
        this.dishInformationModel.addAll(filteredDishes);
        notifyDataSetChanged();
    }

    public void reset(){
        this.dishInformationModel.clear();
        this.dishInformationModel.addAll(copyDishInformationModel);
        notifyDataSetChanged();
    }


    //SORTING DISHES
    public void bestDishSort(){
        // Sort the list using a custom comparator
        Collections.sort(this.dishInformationModel, new Comparator<DishInformationModel>() {
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
        notifyDataSetChanged();
    }
    public void ingredientsCount(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.dishInformationModel.sort(Comparator.comparingInt(DishInformationModel::getIngredientsCounter).reversed());
        }
        notifyDataSetChanged();
    }

    public void percentageSort(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.dishInformationModel.sort(Comparator.comparingInt(DishInformationModel::getPercentMatching).reversed());
        }
        notifyDataSetChanged();
    }
    public void ascendingSort(){
        Collections.sort(this.dishInformationModel, (p1, p2) -> p1.getDishName().compareTo(p2.getDishName()));
        notifyDataSetChanged();
    }
    public void descendingSort(){
        Collections.sort(this.dishInformationModel, (p1, p2) -> p2.getDishName().compareTo(p1.getDishName()));
        notifyDataSetChanged();
    }
    //END OF SORTING DISH

    public void searchDish(List<DishInformationModel> searchName){
        if(!searchName.isEmpty()){
            this.dishInformationModel.clear();
            this.dishInformationModel.addAll(searchName);
            notifyDataSetChanged();
            System.out.println(dishInformationModel.size());
        }
    }

    public void resetDishes(List<DishInformationModel> dishes){
        this.dishInformationModel.clear();
        this.dishInformationModel.addAll(dishes);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public DishInformationRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.view_all_dishes, parent, false);


        return new DishInformationRecyclerViewAdapter.MyViewHolder(view, recyclerViewInterface);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DishInformationRecyclerViewAdapter.MyViewHolder holder, int position) {

        //Getting Date
        date = AddBookmark.bookmarkDate;

        //dishname
        holder.tv_dishName.setText(dishInformationModel.get(holder.getAdapterPosition()).getDishName());

        // Get the image name from your data model
        String imageName = dishInformationModel.get(holder.getAdapterPosition()).getImageName();

        // Check if imageName is null or empty
        if (imageName == null || imageName.isEmpty()) {
            // Handle the case where imageName is null or empty
            Glide.with(context)
                    .load(R.drawable.placeholder)
                    .into(holder.iv_dishImage);
            return;
        }

        // Convert image name to resource ID
        int imageResId = context.getResources().getIdentifier(imageName, "raw", context.getPackageName());


        // Load the image using Glide
        Glide.with(context)
                .load(imageResId != 0 ? imageResId : R.drawable.placeholder)// Use the raw resource ID or placeholder
                .placeholder(R.drawable.placeholder) // Placeholder image while loading
                .error(R.drawable.placeholder) // Image to show on error
                .into(holder.iv_dishImage);

        String filename = dishInformationModel.get(holder.getAdapterPosition()).getImageName();
        loadImageFromInternalStorage(holder.iv_dishImage, filename);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                //loadImageFromInternalStorage(holder.iv_dishImage, filename);
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        loadImageFromInternalStorage(holder.iv_dishImage, filename);
                    }
                });
            }
        });


        holder.cv_dishItem.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.viewall_animation_one));


        if(dishInformationModel.get(holder.getAdapterPosition()).getIngredientsCounter() != 0){
            holder.tv_quantity.setText("Available Ingredients : " + dishInformationModel.get(holder.getAdapterPosition()).getAvailableIngredients().size() + " / " + dishInformationModel.get(holder.getAdapterPosition()).getQuantity());
        }
        else{
            holder.tv_quantity.setText("No. of Ingredients : " + dishInformationModel.get(holder.getAdapterPosition()).getQuantity());
        }


        holder.cb_bookMark.setOnCheckedChangeListener(null); // Avoid unwanted triggering
        holder.cb_bookMark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    bookmarkPopupWindow(dishInformationModel.get(holder.getAdapterPosition()).getDishName(),
                            dishInformationModel.get(holder.getAdapterPosition()).getImageName(),
                            dishInformationModel.get(holder.getAdapterPosition()).getDescription(),
                            dishInformationModel.get(holder.getAdapterPosition()).getQuantity(),
                            dishInformationModel.get(holder.getAdapterPosition()).getIngredients(),
                            dishInformationModel.get(holder.getAdapterPosition()).getMeasurement(),
                            dishInformationModel.get(holder.getAdapterPosition()).getProcedure(),
                            dishInformationModel.get(holder.getAdapterPosition()).getLink(),
                            date);
                }
                else{

                }
            }
        });
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
        return dishInformationModel.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{

        CardView cv_dishItem;
        ImageView iv_dishImage;
        TextView tv_dishName;
        TextView tv_quantity;
        CheckBox cb_bookMark;

        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            tv_dishName = itemView.findViewById(R.id.tv_newAddedDish);
            iv_dishImage = itemView.findViewById(R.id.image_newAddedDish);
            cv_dishItem = itemView.findViewById(R.id.cv_newAddedDish);
            tv_quantity = itemView.findViewById(R.id.tv_quantity);
            cb_bookMark = itemView.findViewById(R.id.cb_bookMark);

            cv_dishItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recyclerViewInterface.onItemClick(getAdapterPosition());
                }
            });

        }
    }

    private void bookmarkPopupWindow(String bookMarkName, String bookmarkImageName,
                                     String bookmarkDescription, int bookmarkQuantity, String bookmarkIngredients,
                                     String bookmarkMeasurement, String bookmarkProcedure, String bookmarkLink,String bookmarkDate){

        TextView tv_bookmarkName;
        Button btn_addBookmark;
        Button btn_cancelBookmark;

        View popupView = LayoutInflater.from(context).inflate(R.layout.generate_bookmark_popup_window, null);
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
                BookmarkDatabaseOpenHelper.getInstance(context).addOneBookmark(bookMarkName, bookmarkImageName,
                        bookmarkDescription, bookmarkQuantity, bookmarkIngredients,
                        bookmarkMeasurement, bookmarkProcedure, bookmarkLink, date);
                //Toast.makeText(context, "Bookmark Added!", Toast.LENGTH_SHORT).show();
                StyleableToast.makeText(context, "Bookmark Added!", R.style.exampleToast).show();
                popupWindow.dismiss();
            }
        });
    }

}
