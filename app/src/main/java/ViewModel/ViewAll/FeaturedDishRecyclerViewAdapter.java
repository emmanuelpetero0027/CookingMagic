package ViewModel.ViewAll;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cookingmagic.R;

import java.util.List;

import Model.ViewAll.DishInformationModel;

public class FeaturedDishRecyclerViewAdapter extends RecyclerView.Adapter<FeaturedDishRecyclerViewAdapter.MyViewHolderFeatured> {
    Context context;
    List<DishInformationModel> dishInformationModel;

    private FeaturedInterface featuredInterface;

    public FeaturedDishRecyclerViewAdapter(Context context, List<DishInformationModel> dishInformationModel, FeaturedInterface featuredInterface) {
        this.context = context;
        this.dishInformationModel = dishInformationModel;
        this.featuredInterface = featuredInterface;

    }

    @NonNull
    @Override
    public FeaturedDishRecyclerViewAdapter.MyViewHolderFeatured onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.view_all_dishes_featured, parent, false);

        return new FeaturedDishRecyclerViewAdapter.MyViewHolderFeatured(view, featuredInterface);
    }

    public void seeMore(List<DishInformationModel> newDishInformationModel){
        this.dishInformationModel.clear();
        this.dishInformationModel.addAll(newDishInformationModel);
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(@NonNull FeaturedDishRecyclerViewAdapter.MyViewHolderFeatured holder, int position) {

        //dishname
        holder.tv_dishNameFeatured.setText(dishInformationModel.get(holder.getAdapterPosition()).getDishName());

        // Get the image name from your data model
        String imageName = dishInformationModel.get(holder.getAdapterPosition()).getImageName();

        // Check if imageName is null or empty
        if (imageName == null || imageName.isEmpty()) {
            // Handle the case where imageName is null or empty
            Glide.with(context)
                    .load(R.drawable.placeholder)
                    .into(holder.img_dishImageFeatured);

            return;
        }

        // Convert image name to resource ID
        int imageResId = context.getResources().getIdentifier(imageName, "raw", context.getPackageName());

        // Load the image using Glide
        Glide.with(context)
                .load(imageResId != 0 ? imageResId : R.drawable.placeholder)// Use the raw resource ID or placeholder
                .placeholder(R.drawable.placeholder) // Placeholder image while loading
                .error(R.drawable.placeholder) // Image to show on error
                .into(holder.img_dishImageFeatured);
    }

    @Override
    public int getItemCount() {
        return 20;
    }


    public static class MyViewHolderFeatured extends RecyclerView.ViewHolder{

        CardView cv_dishItemFeatured;
        ImageView img_dishImageFeatured;
        TextView tv_dishNameFeatured;

        public MyViewHolderFeatured(@NonNull View itemView, FeaturedInterface featuredInterface) {
            super(itemView);
            cv_dishItemFeatured = itemView.findViewById(R.id.cv_newAddedDish);
            img_dishImageFeatured = itemView.findViewById(R.id.img_dishImageFeatured);
            tv_dishNameFeatured = itemView.findViewById(R.id.tv_dishNameFeatured);

            cv_dishItemFeatured.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    featuredInterface.onItemClickFeatured(getAdapterPosition());
                }
            });

        }

    }
}
