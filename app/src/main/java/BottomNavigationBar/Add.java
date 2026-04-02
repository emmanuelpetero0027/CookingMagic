package BottomNavigationBar;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cookingmagic.DishInformation;
import com.example.cookingmagic.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Model.AddDish.AddModel;
import Model.Bookmark.BookmarkModel;
import Model.Database.AddDishDatabaseOpenHelper;
import Model.Database.BookmarkDatabaseOpenHelper;
import View.Add.AddNewDish;
import View.Add.DishInformationAddDish;
import ViewModel.Add.AddDishInterface;
import ViewModel.Add.AddDishRecyclerViewAdapter;
import ViewModel.Bookmark.BookmarkDishesRecyclerViewAdapter;

public class Add extends Fragment implements AddDishInterface {

    View rootView;
    FloatingActionButton fab_addNewDish;
    RecyclerView rv_addList;
    TextView tv_addDish;

    private AddDishRecyclerViewAdapter addDishRecyclerViewAdapter;
    private LinearLayoutManager linearLayoutManager;
    List<AddModel> addModels;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.main_fragment_add, container, false);

        fab_addNewDish = rootView.findViewById(R.id.fab_addNewDish);
        rv_addList = rootView.findViewById(R.id.rv_addList);
        tv_addDish = rootView.findViewById(R.id.tv_addDish);

        addModels = new ArrayList<>();
        addModels = AddDishDatabaseOpenHelper.getInstance(requireContext()).getAddDish();
        linearLayoutManager = new LinearLayoutManager(requireContext());
        rv_addList.setLayoutManager(linearLayoutManager);
        rv_addList.setItemAnimator(new DefaultItemAnimator());

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                addDishRecyclerViewAdapter = new AddDishRecyclerViewAdapter(requireContext(), addModels, Add.this);

                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        rv_addList.setAdapter(addDishRecyclerViewAdapter);
                    }
                });
            }
        });


        fab_addNewDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), AddNewDish.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onItemClickAddDish(int adapterPosition) {
        Intent intent = new Intent(requireActivity(), DishInformationAddDish.class);
        intent.putExtra("dishName", addModels.get(adapterPosition).getDishName());
        intent.putExtra("dishImage", addModels.get(adapterPosition).getImagePath());
        intent.putExtra("description", addModels.get(adapterPosition).getDescription());
        intent.putExtra("measurement", addModels.get(adapterPosition).getMeasurements());
        intent.putExtra("procedure", addModels.get(adapterPosition).getProcedure());
        startActivity(intent);
    }
    @Override
    public void onResume() {
        super.onResume();
        List<AddModel> newList = AddDishDatabaseOpenHelper.getInstance(requireContext()).getAddDish();
        addDishRecyclerViewAdapter.updateList(newList);

        if(!addModels.isEmpty()){
            tv_addDish.setText("");
        }
    }
}