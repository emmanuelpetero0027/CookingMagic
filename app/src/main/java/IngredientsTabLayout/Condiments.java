package IngredientsTabLayout;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.example.cookingmagic.R;

import java.util.List;

import Model.Generate.CheckboxesDataName;
import Model.Generate.IngredientsModel;
import ViewModel.Generate.ListOfIngredientsGeneratingDishViewModel;


public class Condiments extends Fragment {
    //Elements Variables
    LinearLayout layout_condiments_fragment;
    //List of Ingredients Live Data
    ListOfIngredientsGeneratingDishViewModel listOfIngredientsGeneratingDishViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.generate_fragment_condiments, container, false);
        layout_condiments_fragment = rootView.findViewById(R.id.layout_condiments_fragment);

        //Instantiating Live Data
        listOfIngredientsGeneratingDishViewModel = new ViewModelProvider(this).get(ListOfIngredientsGeneratingDishViewModel.class);

        //Setting up checkboxes
        CheckboxesDataName condimentsCheckboxesDataName = new CheckboxesDataName();
        String[] condimentsList = condimentsCheckboxesDataName.getCondimentsIngredientsList();
        for(int i = 0; i < condimentsList.length; i++) {
            CheckBox checkBox = new CheckBox(requireContext());
            checkBox.setText(condimentsList[i]);
            checkBox.setId(View.generateViewId());
            checkBox.setPadding(50, 0, 0, 0);
            checkBox.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            checkBox.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
            checkBox.setHeight(200);

            //Checking Live Data if the Ingredients Exist Updates the Checkbox State
            listOfIngredientsGeneratingDishViewModel.getLiveData().observe(getViewLifecycleOwner(), new Observer<List<IngredientsModel>>() {
                @Override
                public void onChanged(List<IngredientsModel> ingredientsModels) {
                    boolean isChecked = false;
                    String checkboxName = checkBox.getText().toString();

                    for(IngredientsModel names: ingredientsModels){
                        if(names.getName().equalsIgnoreCase(checkboxName)){
                            isChecked = true;
                            break;
                        }
                    }
                    checkBox.setChecked(isChecked);

                }
            });

            // Add checkbox to the parent layout
            layout_condiments_fragment.addView(checkBox);

            // Add divider (horizontal line) after each checkbox, except the last one
            if (i < condimentsList.length - 1) {
                View divider = new View(requireContext());
                LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1); // Height of divider, adjust as needed
                divider.setLayoutParams(dividerParams);
                divider.setBackgroundColor(ContextCompat.getColor(requireContext(),
                        android.R.color.darker_gray)); // Divider color
                layout_condiments_fragment.addView(divider);
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
        return rootView;
    }


}