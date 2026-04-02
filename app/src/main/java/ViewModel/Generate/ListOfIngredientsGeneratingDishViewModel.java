package ViewModel.Generate;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import Model.Generate.IngredientsModel;

public class ListOfIngredientsGeneratingDishViewModel extends ViewModel {

    //Ingredients List Model instance
    private static ListOfIngredientsGeneratingDishViewModel instance;
    //Ingredients List for Generating Dish SINGLETON
    public static ListOfIngredientsGeneratingDishViewModel getInstance(){
        if(instance == null){
            instance = new ListOfIngredientsGeneratingDishViewModel();
        }
        return instance;
    }

    //LiveData for the IngredientsModel
    private MutableLiveData<List<IngredientsModel>> ingredientsModelLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> counter = new MutableLiveData<>();
    //Initializing Live Data
    public ListOfIngredientsGeneratingDishViewModel(){
        ingredientsModelLiveData.setValue(new ArrayList<>());
        counter.setValue(0);
    }

    //MutableLiveData Getter
    public LiveData<List<IngredientsModel>> getLiveData(){
        return ListOfIngredientsGeneratingDishViewModel.getInstance().ingredientsModelLiveData;
    }
    public LiveData<Integer> getCounterLiveData(){
        return ListOfIngredientsGeneratingDishViewModel.getInstance().counter;
    }


    //Getting Live Data List
    public List<String> getIngredientsModelLiveData(){
        List<IngredientsModel> currentIngredientsModel = ingredientsModelLiveData.getValue();
        List<String> returnList =  new ArrayList<>();
        for(IngredientsModel returnListName: currentIngredientsModel){
            returnList.add(returnListName.getName());
        }
        return returnList;
    }
    //Adding IngredientsModelName
    public void addingIngredientsModelName(IngredientsModel ingredientsModel){
        List<IngredientsModel> currentIngredientsModel = ingredientsModelLiveData.getValue();
        currentIngredientsModel.add(ingredientsModel);
        ingredientsModelLiveData.setValue(currentIngredientsModel);
        //Adding Counter
        int _counter = counter.getValue();
        _counter++;
        counter.setValue(_counter);

    }
    //Removing IngredientsModelName
    public void removeIngredientsModelName(String removeName){
        IngredientsModel objectToRemove = null;
        for(IngredientsModel removeNameObject : ingredientsModelLiveData.getValue()){
            if(removeNameObject.getName().equalsIgnoreCase(removeName)){
                objectToRemove = removeNameObject;
            }
        }
        if (objectToRemove != null) {
            List<IngredientsModel> currentIngredientsModel = ingredientsModelLiveData.getValue();
            currentIngredientsModel.remove(objectToRemove);
            ingredientsModelLiveData.setValue(currentIngredientsModel);
            System.out.println("Removing : " + objectToRemove.getName());
            //Minus Counter
            int _counter = counter.getValue();
            _counter--;
            counter.setValue(_counter);
        }
    }




}
