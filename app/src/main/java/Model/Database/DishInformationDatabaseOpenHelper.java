package Model.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Model.ViewAll.DishInformationModel;

public class DishInformationDatabaseOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_DISHDATABASE = "DishInformationDatabasev20.2.db"; // Name of the database file in assets
    private static final String DATABASE_PATH = "/data/data/com.example.cookingmagic/databases/";
    private final Context context;
    private SQLiteDatabase database;

    //Singleton Pattern
    private static DishInformationDatabaseOpenHelper instance;

    //Ingredients List for Generating Dish SINGLETON
    public static DishInformationDatabaseOpenHelper getInstance(Context context){
        if(instance == null){
            instance = new DishInformationDatabaseOpenHelper(context.getApplicationContext());
        }
        return instance;
    }

    public DishInformationDatabaseOpenHelper(Context context) {
        super(context, DATABASE_DISHDATABASE, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void openDatabase(){
        String dbPath = context.getDatabasePath(DATABASE_DISHDATABASE).getPath();

        if(database != null && database.isOpen()){
            return;
        }
        database = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
    }
    public void closeDatabase(){
        if(database != null){
            database.close();
        }
    }

    public void onCreateDatabase() throws IOException{
        boolean dbExist = checkDatabase();
        if (dbExist) {
            // Database exists, no need to copy
        } else {
            // Copy the database from assets
            this.getReadableDatabase();
            copyDatabase();
        }
    }
    public boolean checkDatabase(){
        SQLiteDatabase checkDB =  null;
        try {
            String myPath = DATABASE_PATH + DATABASE_DISHDATABASE;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
            System.out.println("Database Exist!");
        } catch (Exception e) {
            System.out.println("Database Dont Exist!");
            // Database does not exist
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null;
    }

    public boolean copyDatabase(){
        try{
            InputStream myInput = context.getAssets().open(DATABASE_DISHDATABASE);
            String outFileName = DATABASE_PATH + DATABASE_DISHDATABASE;
            OutputStream myOutput = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            myOutput.flush();
            myOutput.close();
            myInput.close();

            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


    //Getting the dishname for viewall recyclerview with pagination
    public List<DishInformationModel> getDish(int page, int pageSize){

        int offset = page * pageSize;

        DishInformationModel dishInformationModel = null;
        List<DishInformationModel> dishInformationModelList  = new ArrayList<>();
        openDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM DishInformation LIMIT " + pageSize + " OFFSET " + offset, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            //dishInformationModel = new DishInformationModel(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
            dishInformationModel = new DishInformationModel(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getInt(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8),
                    cursor.getString(9),
                    cursor.getString(10),
                    cursor.getString(11));

            dishInformationModelList.add(dishInformationModel);
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();
        return dishInformationModelList;
    }


    //getting dishname
    public List<DishInformationModel> getDish(){

        DishInformationModel dishInformationModel = null;
        List<DishInformationModel> dishInformationModelList  = new ArrayList<>();
        openDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM DishInformation " , null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){

            //int,
            // string,
            // string,
            // string,
            // int,
            // list,
            // string,
            // string,
            // string
            //dishInformationModel = new DishInformationModel(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
            dishInformationModel = new DishInformationModel(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getInt(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8),
                    cursor.getString(9),
                    cursor.getString(10),
                    cursor.getString(11));

            dishInformationModelList.add(dishInformationModel);
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();
        return dishInformationModelList;
    }


}
