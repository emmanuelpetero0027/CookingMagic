package BottomNavigationBar;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cookingmagic.DishInformation;
import com.example.cookingmagic.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import Model.Bookmark.BookmarkModel;
import Model.Database.BookmarkDatabaseOpenHelper;
import View.Bookmark.AddBookmark;
import View.Bookmark.DateSetterBookmark;
import ViewModel.Bookmark.BookmarkDishesRecyclerViewAdapter;
import ViewModel.Bookmark.BookmarkInterface;

public class Bookmark extends Fragment implements BookmarkInterface{


    FloatingActionButton fab_addBookmark;
    FloatingActionButton fab_calendar;
    TextView tv_date;
    TextView tv_addBookmark;

    String formattedDate = "";

    View rootView;

    RecyclerView rv_bookmarkList;

    List<BookmarkModel> bookmarkModels;
    private LinearLayoutManager linearLayoutManager;
    BookmarkDishesRecyclerViewAdapter bookmarkDishesRecyclerViewAdapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.main_fragment_bookmark, container, false);

        fab_addBookmark = rootView.findViewById(R.id.fab_addDateBookmark);
        fab_calendar = rootView.findViewById(R.id.fab_calendar);
        tv_date = rootView.findViewById(R.id.tv_date);
        rv_bookmarkList = rootView.findViewById(R.id.rv_bookmarkList);
        tv_addBookmark = rootView.findViewById(R.id.tv_addBookmark);

        bookmarkModels = new ArrayList<>();
        bookmarkModels = BookmarkDatabaseOpenHelper.getInstance(requireContext()).getBookmark();
        linearLayoutManager = new LinearLayoutManager(requireContext());
        rv_bookmarkList.setLayoutManager(linearLayoutManager);
        rv_bookmarkList.setItemAnimator(new DefaultItemAnimator());
        bookmarkDishesRecyclerViewAdapter = new BookmarkDishesRecyclerViewAdapter(requireContext(), bookmarkModels, this);
        rv_bookmarkList.setAdapter(bookmarkDishesRecyclerViewAdapter);

        for(BookmarkModel bookmarkModel: BookmarkDatabaseOpenHelper.getInstance(requireContext()).getBookmark()){
            System.out.println(bookmarkModel.getBookmarkDate());
        }

        fab_addBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(requireActivity(), AddBookmark.class);
                startActivity(intent);
            }
        });

        fab_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showDatePickerDialog();
                Intent intent = new Intent(requireActivity(), DateSetterBookmark.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onItemClickBookmark(int adapterPosition) {
        Intent intent = new Intent(requireActivity(), DishInformation.class);
        intent.putExtra("dishName", bookmarkModels.get(adapterPosition).getDishName());
        intent.putExtra("dishImage", bookmarkModels.get(adapterPosition).getImageName());
        intent.putExtra("description", bookmarkModels.get(adapterPosition).getDescription());
        intent.putExtra("measurement", bookmarkModels.get(adapterPosition).getMeasurement());
        intent.putExtra("procedure", bookmarkModels.get(adapterPosition).getProcedure());
        intent.putExtra("youtubeLink", bookmarkModels.get(adapterPosition).getLink());
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        List<BookmarkModel> newList = BookmarkDatabaseOpenHelper.getInstance(requireContext()).getBookmark();
        bookmarkDishesRecyclerViewAdapter.updateList(newList);

        if(!bookmarkModels.isEmpty()){
            tv_addBookmark.setText("");
        }
    }
}