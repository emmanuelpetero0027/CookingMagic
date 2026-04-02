package ViewModel.Generate;


import static android.view.Gravity.apply;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cookingmagic.DishInformation;
import com.example.cookingmagic.R;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import Model.Generate.CookingMagicDishes;
import Model.ViewAll.DishInformationModel;
import ViewModel.ViewAll.DishInformationRecyclerViewAdapter;
import ViewModel.ViewAll.RecyclerViewInterface;

public class CookingMagicRecyclerViewAdapter extends DishInformationRecyclerViewAdapter {

    public CookingMagicRecyclerViewAdapter(Context context, List<DishInformationModel> dishInformationModel, RecyclerViewInterface recyclerViewInterface) {
        super(context, dishInformationModel, recyclerViewInterface);
    }

}
