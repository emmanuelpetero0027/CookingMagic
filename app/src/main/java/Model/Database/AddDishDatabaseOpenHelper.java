package Model.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import Model.AddDish.AddModel;
import Model.Bookmark.BookmarkModel;

public class AddDishDatabaseOpenHelper extends SQLiteOpenHelper {

    public static final String ADD_TABLE = "ADD_TABLE";

    // Column names
    public static final String ADD_DISH_ID = "dishID";
    public static final String ADD_DISH_NAME = "dishName";
    public static final String ADD_DESCRIPTION = "description";
    public static final String ADD_INGREDIENTS = "ingredients";
    public static final String ADD_PROCEDURE = "procedure";
    public static final String ADD_IMAGEPATH = "imagePath";

    private static AddDishDatabaseOpenHelper instance;

    //Singleton for Bookmark
    //Ingredients List for Generating Dish SINGLETON
    public static AddDishDatabaseOpenHelper getInstance(Context context){
        if(instance == null){
            instance = new AddDishDatabaseOpenHelper(context.getApplicationContext());
        }
        return instance;
    }



    public AddDishDatabaseOpenHelper(@Nullable Context context) {
        super(context, "AddDatabasev1", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create table SQL statement
        final String TABLE_CREATE =
                "CREATE TABLE " + ADD_TABLE + " (" +
                        ADD_DISH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        ADD_DISH_NAME + " TEXT NOT NULL, " +
                        ADD_DESCRIPTION + " TEXT, " +
                        ADD_INGREDIENTS + " TEXT, " +
                        ADD_PROCEDURE + " TEXT, " +
                        ADD_IMAGEPATH + " TEXT);";

        sqLiteDatabase.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public boolean addOneDish(String addName,
                              String addDescription,
                              String addIngredients,
                              String addProcedure,
                              String addImagePath){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ADD_DISH_NAME, addName);
        cv.put(ADD_DESCRIPTION, addDescription);
        cv.put(ADD_INGREDIENTS, addIngredients);
        cv.put(ADD_PROCEDURE, addProcedure);
        cv.put(ADD_IMAGEPATH, addImagePath);

        long insert = db.insert(ADD_TABLE, null, cv);

        if(insert == -1){
            return false;
        }
        else{
            return true;

        }
    }


    public List<AddModel> getAddDish(){
        List<AddModel> returnList = new ArrayList<>();

        AddModel addModel = null;

        String queryString = "SELECT * FROM " + ADD_TABLE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String description = cursor.getString(2);
                String ingredients = cursor.getString(3);
                String procedure = cursor.getString(4);
                String imagePath = cursor.getString(5);


                addModel = new AddModel(id, name, description, ingredients, procedure, imagePath);
                returnList.add(addModel);
            }
            cursor.close();
        }

        db.close();
        return returnList;
    }

    public void removeOneBookmark(int dishId){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ADD_TABLE, ADD_DISH_ID + " = ?", new String[]{String.valueOf(dishId)});
        db.close();
    }
}
