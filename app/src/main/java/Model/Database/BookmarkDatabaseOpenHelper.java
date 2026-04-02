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

import Model.Bookmark.BookmarkModel;
import Model.ViewAll.DishInformationModel;

public class BookmarkDatabaseOpenHelper extends SQLiteOpenHelper {


        public static final String BOOKMARK_TABLE = "BOOKMARK_TABLE";

        // Column names
        public static final String BOOKMARK_DISH_ID = "dishID";
        public static final String BOOKMARK_DISH_NAME = "dishName";
        public static final String BOOKMARK_IMAGE_NAME = "imageName";
        public static final String BOOKMARK_DESCRIPTION = "description";
        public static final String BOOKMARK_QUANTITY = "quantity";
        public static final String BOOKMARK_INGREDIENTS = "ingredients";
        public static final String BOOKMARK_MEASUREMENT = "measurement";
        public static final String BOOKMARK_PROCEDURE = "procedure";
        public static final String BOOKMARK_LINK = "link";
        public static final String BOOKMARK_DATE = "bookmarkDate";


        private static BookmarkDatabaseOpenHelper instance;

        //Singleton for Bookmark
        //Ingredients List for Generating Dish SINGLETON
        public static BookmarkDatabaseOpenHelper getInstance(Context context){
            if(instance == null){
                instance = new BookmarkDatabaseOpenHelper(context.getApplicationContext());
            }
            return instance;
        }

        public BookmarkDatabaseOpenHelper(@Nullable Context context) {
        super(context, "BookMarkDatabasev3", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create table SQL statement
        final String TABLE_CREATE =
                "CREATE TABLE " + BOOKMARK_TABLE + " (" +
                        BOOKMARK_DISH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        BOOKMARK_DISH_NAME + " TEXT NOT NULL, " +
                        BOOKMARK_IMAGE_NAME + " TEXT, " +
                        BOOKMARK_DESCRIPTION + " TEXT, " +
                        BOOKMARK_QUANTITY + " INTEGER, " +
                        BOOKMARK_INGREDIENTS + " TEXT, " +
                        BOOKMARK_MEASUREMENT + " TEXT, " +
                        BOOKMARK_PROCEDURE + " TEXT, " +
                        BOOKMARK_LINK + " TEXT, " +
                        BOOKMARK_DATE + " TEXT);";

        sqLiteDatabase.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean addOneBookmark(String bookMarkName, String bookmarkImageName,
                                  String bookmarkDescription, int bookmarkQuantity, String bookmarkIngredients,
                                  String bookmarkMeasurement, String bookmarkProcedure, String bookmarkLink,String bookmarkDate){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(BOOKMARK_DISH_NAME, bookMarkName);
        cv.put(BOOKMARK_IMAGE_NAME, bookmarkImageName);
        cv.put(BOOKMARK_DESCRIPTION, bookmarkDescription);
        cv.put(BOOKMARK_QUANTITY, bookmarkQuantity);
        cv.put(BOOKMARK_INGREDIENTS, bookmarkIngredients);
        cv.put(BOOKMARK_MEASUREMENT, bookmarkMeasurement);
        cv.put(BOOKMARK_PROCEDURE, bookmarkProcedure);
        cv.put(BOOKMARK_LINK, bookmarkLink);
        cv.put(BOOKMARK_DATE, bookmarkDate);

        long insert = db.insert(BOOKMARK_TABLE, null, cv);

        if(insert == -1){
            return false;
        }
        else{
            return true;
        }
    }

    public void removeOneBookmark(int dishId){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(BOOKMARK_TABLE, BOOKMARK_DISH_ID + " = ?", new String[]{String.valueOf(dishId)});
        db.close();
    }


    public List<BookmarkModel> getBookmark(){
        List<BookmarkModel> returnList = new ArrayList<>();

        BookmarkModel bookmarkModel = null;

        String queryString = "SELECT * FROM " + BOOKMARK_TABLE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String image = cursor.getString(2);
                String description = cursor.getString(3);
                int quantity = cursor.getInt(4);
                String ingredients = cursor.getString(5);
                String measurement = cursor.getString(6);
                String procedure = cursor.getString(7);
                String link = cursor.getString(8);
                String date = cursor.getString(9);


                bookmarkModel = new BookmarkModel(id, name, image, description, quantity,
                        ingredients, measurement, procedure, link, date);
                returnList.add(bookmarkModel);
            }
            cursor.close();
        }

        db.close();
        return returnList;
    }



}
