package Model.ViewAll;

import java.util.ArrayList;
import java.util.List;

public class DishInformationModel{

    int ingredientsCounter;
    int percentMatching;
    int mainIngredientsMatching;
    int subIngredientsMatching;

    ArrayList<String> availableIngredients;
    ArrayList<String> notAvailableIngredients;

    int dishID;
    String dishName;
    String imageName;
    String description;
    int quantity;

    String ingredients;
    String measurement;
    String procedure;
    String link = "";
    String mainIngredients;
    String subIngredients;
    String category;


    public DishInformationModel(int dishID,
                                String dishName,
                                String imageName,
                                String description,
                                int quantity,
                                String ingredients,
                                String measurement,
                                String procedure,
                                String link) {
        this.dishID = dishID;
        this.dishName = dishName;
        this.imageName = imageName;
        this.description = description;
        this.quantity = quantity;
        this.ingredients = ingredients;
        this.measurement = measurement;
        this.procedure = procedure;
        this.link = link;
    }
    public DishInformationModel(int dishID,
                                String dishName,
                                String imageName,
                                String description,
                                int quantity,
                                String ingredients,
                                String measurement,
                                String procedure,
                                String link,
                                String mainIngredients,
                                String subIngredients,
                                String category) {
        this.dishID = dishID;
        this.dishName = dishName;
        this.imageName = imageName;
        this.description = description;
        this.quantity = quantity;
        this.ingredients = ingredients;
        this.measurement = measurement;
        this.procedure = procedure;
        this.link = link;
        this.mainIngredients = mainIngredients;
        this.subIngredients = subIngredients;
        this.category = category;
    }

    public DishInformationModel(int dishId, String dishName, String imageName) {
        this.dishID = dishId;
        this.dishName = dishName;
        this.imageName = imageName;
    }


    public int getDishID() {
        return dishID;
    }

    public String getDishName() {
        return dishName;
    }

    public String getImageName() {
        return imageName;
    }

    public String getDescription() {
        return description;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getMeasurement() {
        return measurement;
    }

    public String getProcedure() {
        return procedure;
    }

    public String getLink() {
        return link;
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
    public int getIngredientsCounter(){
        return ingredientsCounter;
    }

    public String getMainIngredients() {
        return mainIngredients;
    }

    public String getSubIngredients() {
        return subIngredients;
    }

    public String getCategory() {
        return category;
    }

    public int getMainIngredientsMatching() {
        return mainIngredientsMatching;
    }

    public int getSubIngredientsMatching() {
        return subIngredientsMatching;
    }


    @Override
    public String toString() {
        return "DishInformationModel{" +
                "dishID=" + dishID +
                ", dishName='" + dishName + '\'' +
                ", imageName='" + imageName + '\'' +
                ", description='" + description + '\'' +
                ", quantity=" + quantity +
                ", ingredients=" + ingredients +
                ", measurement='" + measurement + '\'' +
                ", procedure='" + procedure + '\'' +
                ", link='" + link + '\'' +
                '}';
    }


}
