package Model.Bookmark;

public class BookmarkModel {



    int dishId;
    String dishName;
    String imageName;
    String description;
    int quantity;
    String ingredients;
    String measurement;
    String procedure;
    String link = "";
    String bookmarkDate = "";

    public BookmarkModel(int dishId, String dishName, String imageName, String description, int quantity, String ingredients, String measurement, String procedure, String link, String bookmarkDate) {

        this.dishId = dishId;
        this.dishName = dishName;
        this.imageName = imageName;
        this.description = description;
        this.quantity = quantity;
        this.ingredients = ingredients;
        this.measurement = measurement;
        this.procedure = procedure;
        this.link = link;
        this.bookmarkDate = bookmarkDate;
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

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }

    public String getProcedure() {
        return procedure;
    }

    public void setProcedure(String procedure) {
        this.procedure = procedure;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getBookmarkDate() {
        return bookmarkDate;
    }

    public void setBookmarkDate(String bookmarkDate) {
        this.bookmarkDate = bookmarkDate;
    }
}
