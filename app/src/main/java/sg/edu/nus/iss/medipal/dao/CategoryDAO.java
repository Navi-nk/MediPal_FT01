package sg.edu.nus.iss.medipal.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import java.util.ArrayList;

import sg.edu.nus.iss.medipal.manager.DataBaseManager;
import sg.edu.nus.iss.medipal.pojo.Category;
import sg.edu.nus.iss.medipal.utils.DataBaseUtility;

/**
 * Created by zhiguo on 13/3/17.
 */

public class CategoryDAO extends DataBaseUtility {

    private static final String WHERE_ID_EQUALS = DataBaseManager.CATEGORY_ID + " =?";

    public CategoryDAO(Context context) {

        super(context);
    }

    //Method to insert data to category table
    public long insert(Category category)throws SQLException
    {
        //return value
        long retCode = 0;
        //use the category pojo to populate the table column values
        ContentValues values =  new ContentValues();
        values.put(DataBaseManager.CATEGORY_NAME, category.getCategory_name());
        values.put(DataBaseManager.CATEGORY_CODE, category.getCategory_code());
        values.put(DataBaseManager.CATEGORY_DES, category.getCategory_des());


        //Insert data into category table. If insertion is successfull then the method returns the row ID, else -1
        try {
            retCode = database.insertOrThrow(DataBaseManager.CATEGORY_TABLE, null, values);
        }
        catch (SQLException sqlE)
        {
            sqlE.printStackTrace(); //unexpected error while inserting.
            retCode = -1; //set return value to error code so that caller can handle error
        }
        return retCode;
    }

    //Category  are not allowed to deleted after added in database
//    public long delete(Category category) throws SQLException
//    {
//        long retCode=0;
//
//        //Delete the medicine from SQLite
//        retCode =   database.delete(DataBaseManager.MEDICINE_TABLE,WHERE_ID_EQUALS,new String[] { String.valueOf(category.getId()) });
//
//        return retCode;
//    }

    //method to update the category table
    public long update(Category category) {
        //return value
        long retCode = 0;

        //use the category pojo to populate the table column values
        ContentValues values = new ContentValues();

        values.put(DataBaseManager.CATEGORY_NAME, category.getCategory_name());
        values.put(DataBaseManager.CATEGORY_CODE, category.getCategory_code());
        values.put(DataBaseManager.CATEGORY_DES, category.getCategory_des());

        //method returns number of rows affected. so if it is zero some error handling needs to be done by caller
        try {
            retCode = database.update(DataBaseManager.CATEGORY_TABLE, values,
                    WHERE_ID_EQUALS,
                    new String[]{String.valueOf(category.getId())});
        }catch (SQLException sqlE){
            sqlE.printStackTrace(); //unexpected error while inserting.
            retCode = -1; //set return value to error code so that caller can handle error
        }
        return retCode;

    }

    //get list of categorys
    public ArrayList<Category> getCategorys() {
        ArrayList<Category> categorys = new ArrayList<Category>();

        //similiar to query "select * from categorys"
        Cursor cursor = database.query(DataBaseManager.CATEGORY_TABLE,
                new String[] {
                        DataBaseManager.CATEGORY_ID,
                        DataBaseManager.CATEGORY_NAME,
                        DataBaseManager.CATEGORY_CODE,
                        DataBaseManager.CATEGORY_DES,
                }, null, null, null, null, null);

        //loop through each result set to populate the appointment pojo and add to the list each time
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String code = cursor.getString(2);
            String des=cursor.getString(3);

            Category category = new Category(id,name,code,des);
            categorys.add(category);

        }
        return categorys;
    }
}