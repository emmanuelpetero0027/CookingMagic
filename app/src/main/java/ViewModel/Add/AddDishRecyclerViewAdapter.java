package ViewModel.Add;

import static View.Bookmark.DateSetterBookmark.formattedDate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cookingmagic.R;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import BottomNavigationBar.Add;
import Model.AddDish.AddModel;
import Model.Bookmark.BookmarkModel;
import Model.Database.AddDishDatabaseOpenHelper;
import Model.Database.BookmarkDatabaseOpenHelper;
import ViewModel.Bookmark.BookmarkInterface;
import io.github.muddz.styleabletoast.StyleableToast;

public class AddDishRecyclerViewAdapter extends RecyclerView.Adapter<AddDishRecyclerViewAdapter.ViewHolder>{

    List<AddModel> addModel;

    Context context;
    private AddDishInterface addDishInterface;

    public AddDishRecyclerViewAdapter(Context context, List<AddModel> addModel, AddDishInterface addDishInterface) {
        this.context = context;
        this.addModel = addModel;
        this.addDishInterface = addDishInterface;
    }

    @NonNull
    @Override
    public AddDishRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_all_dishes, parent, false);
        return new ViewHolder(view, addDishInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull AddDishRecyclerViewAdapter.ViewHolder holder, int position) {

        holder.tv_newAddedDish.setText(addModel.get(holder.getAdapterPosition()).getDishName());

        holder.btn_removeDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //AddDishDatabaseOpenHelper.getInstance(context).removeOneBookmark(addModel.get(holder.getAdapterPosition()).getDishId());
                //System.out.println("Removing!");
                //updateList(AddDishDatabaseOpenHelper.getInstance(context).getAddDish());

                addPopupWindow(addModel.get(holder.getAdapterPosition()).getDishName(),
                        addModel.get(holder.getAdapterPosition()).getDishId());

            }
        });

        String filename = addModel.get(holder.getAdapterPosition()).getImagePath();
        loadImageFromInternalStorage(holder.image_newAddedDish, filename);

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

    // Optionally, you can notify the adapter of all changes
    public void updateList(List<AddModel> newList) {
        addModel.clear();
        addModel.addAll(newList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return addModel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CardView cv_newAddedDish;
        TextView tv_newAddedDish;
        ImageButton btn_removeDish;
        ImageView image_newAddedDish;

        public ViewHolder(@NonNull View itemView, AddDishInterface addDishInterface) {
            super(itemView);

            cv_newAddedDish = itemView.findViewById(R.id.cv_newAddedDish);
            tv_newAddedDish  = itemView.findViewById(R.id.tv_newAddedDish);
            btn_removeDish  = itemView.findViewById(R.id.btn_removeDish);
            image_newAddedDish = itemView.findViewById(R.id.image_newAddedDish);

            cv_newAddedDish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addDishInterface.onItemClickAddDish(getAdapterPosition());
                }
            });

        }
    }

    private void addPopupWindow(String bookmarkName, int dishId){

        TextView tv_bookmarkName;
        Button btn_bookmarkRemove_popup;
        Button btn_cancelbookmark_popup;

        View popupView = LayoutInflater.from(context).inflate(R.layout.bookmark_removebookmark_popup_window, null);
        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        // Set an elevation value for popup window (optional)
        popupWindow.setElevation(20);
        // Show the popup window
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        tv_bookmarkName = popupView.findViewById(R.id.tv_bookmarkName);
        btn_bookmarkRemove_popup = popupView.findViewById(R.id.btn_bookmarkRemove_popup);
        btn_cancelbookmark_popup = popupView.findViewById(R.id.btn_cancelbookmark_popup);

        tv_bookmarkName.setText(bookmarkName);
        btn_cancelbookmark_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        btn_bookmarkRemove_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //BookmarkDatabaseOpenHelper.getInstance(context).removeOneBookmark(dishId);
                AddDishDatabaseOpenHelper.getInstance(context).removeOneBookmark(dishId);
                //Toast.makeText(context, "Bookmark Remove!", Toast.LENGTH_SHORT).show();
                StyleableToast.makeText(context, "Bookmark Remove!", R.style.exampleToast).show();


                String filename = bookmarkName+"_image.png";

                // Get the path to the file in internal storage
                File file = new File(context.getFilesDir(), filename);
                // Check if the file exists
                if (file.exists()) {
                    // Delete the file
                    boolean deleted = file.delete();
                }

                if(formattedDate == ""){
                    System.out.println("No Date UpdateList!");
                    updateList(AddDishDatabaseOpenHelper.getInstance(context).getAddDish());
                }
                else{
                    System.out.println("With Date DateSort!");
                    //dateSort();
                }

                popupWindow.dismiss();
            }
        });

    }
}
