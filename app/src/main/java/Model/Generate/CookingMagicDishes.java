package Model.Generate;

import java.util.ArrayList;
import java.util.List;

import Model.ViewAll.DishInformationModel;

public class CookingMagicDishes extends DishInformationModel {

    int ingredientsCounter;
    int percentMatching;
    int mainIngredientsMatching;
    int subIngredientsMatching;

    ArrayList<String> availableIngredients;

    ArrayList<String> notAvailableIngredients;

    public CookingMagicDishes(int dishID, String dishName, String imageName, String description, int quantity, String ingredients, String measurement, String procedure, String link) {
        super(dishID, dishName, imageName, description, quantity, ingredients, measurement, procedure, link);
    }

    public CookingMagicDishes(int dishID,
                              String dishName,
                              String imageName,
                              String description,
                              int quantity,
                              String ingredients,
                              String measurement,
                              String procedure,
                              String link,
                              int ingredientsCounter,
                              int percentMatching,
                              ArrayList<String> availableIngredients,
                              ArrayList<String> notAvailableIngredients,
                              int mainIngredientsMatching,
                              int subIngredientsMatching) {

        super(dishID, dishName, imageName, description, quantity, ingredients, measurement, procedure, link);

        this.ingredientsCounter = ingredientsCounter;
        this.percentMatching = percentMatching;
        this.availableIngredients = availableIngredients;
        this.notAvailableIngredients = notAvailableIngredients;
        this.mainIngredientsMatching = mainIngredientsMatching;
        this.subIngredientsMatching = subIngredientsMatching;

    }

    public int getIngredientsCounter(){
        return ingredientsCounter;
    }

    public int getPercentMatching() {
        return percentMatching;
    }

    public ArrayList<String> getAvailableIngredients(){
        return availableIngredients;
    }

    public ArrayList<String> getNotAvailableIngredients(){
        return notAvailableIngredients;
    }

    @Override
    public int getMainIngredientsMatching() {
        return mainIngredientsMatching;
    }


    @Override
    public int getSubIngredientsMatching() {
        return subIngredientsMatching;
    }


    @Override
    public String toString() {
        return "CookingMagicDishes{" +
                ", percentMatching=" + percentMatching +
                ", mainIngredientsMatching=" + mainIngredientsMatching +
                ", subIngredientsMatching=" + subIngredientsMatching +
                ", availableIngredients=" + availableIngredients +
                ", notAvailableIngredients=" + notAvailableIngredients +
                '}';
    }
}
