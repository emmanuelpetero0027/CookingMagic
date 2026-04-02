package Model.AddDish;

public class AddModel {


    int dishId;
    String dishName;
    String description;
    String ingredientsList;
    String procedure;
    String imagePath;

    public AddModel(int dishId, String dishName, String description, String ingredientsList, String procedure, String imagePath) {
        this.dishId = dishId;
        this.dishName = dishName;
        this.description = description;
        this.ingredientsList = ingredientsList;
        this.procedure = procedure;
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getDishId() {
        return dishId;
    }

    public void setDishId(int dishId) {
        this.dishId = dishId;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMeasurements() {
        return ingredientsList;
    }

    public void setIngredientsList(String ingredientsList) {
        this.ingredientsList = ingredientsList;
    }

    public String getProcedure() {
        return procedure;
    }

    public void setProcedure(String procedure) {
        this.procedure = procedure;
    }
}
