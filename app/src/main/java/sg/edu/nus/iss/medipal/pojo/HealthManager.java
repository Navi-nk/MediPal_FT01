package sg.edu.nus.iss.medipal.pojo;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import sg.edu.nus.iss.medipal.asynTask.AddCategory;
import sg.edu.nus.iss.medipal.asynTask.AddMedicine;
import sg.edu.nus.iss.medipal.asynTask.AddReminder;
import sg.edu.nus.iss.medipal.asynTask.DeleteMedicine;
import sg.edu.nus.iss.medipal.asynTask.ListCategory;
import sg.edu.nus.iss.medipal.asynTask.ListMedicine;
import sg.edu.nus.iss.medipal.asynTask.UpdateCategory;
import sg.edu.nus.iss.medipal.asynTask.UpdateMedicine;
import sg.edu.nus.iss.medipal.asynTask.UpdateReminder;

/**
 * Created by zhiguo on 15/3/17.
 */

public class HealthManager {

    private ArrayList<Medicine> medicines;
    private ArrayList<Category> categorys;
    private ArrayList<Reminder> reminders;

    private AddMedicine     taskAddMedicine;
    private ListMedicine    taskListMedicine;
    private UpdateMedicine  taskUpdateMedicine;
    private DeleteMedicine  taskDeleteMedicine;

    private AddCategory     taskAddCategory;
    private ListCategory    taskListCategory;
    private UpdateCategory  taskUpdateCategory;

    private AddReminder     taskAddReminder;
    private UpdateReminder  taskUpdateReminder;


    public HealthManager(){
        this.medicines  =   new ArrayList<Medicine>();
        this.categorys  =   new ArrayList<Category>();
        this.reminders  =   new ArrayList<Reminder>();
    }

    public Medicine getMedicine(int id){
        Iterator<Medicine> i = medicines.iterator();

        while(i.hasNext()){
            Medicine m = i.next();
            if( m.getId() == id)
            {
                return m;
            }
        }

        return null;
    }

    //SQLite get medicine list
    public List<Medicine> getMedicines(Context context) {
        taskListMedicine = new ListMedicine(context);
        taskListMedicine.execute((Void)null);

        try {
            medicines = taskListMedicine.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if(medicines == null){
            medicines = new ArrayList<Medicine>();
        }

        Log.v("DEBUG","-------------------------HealthManager++++++++++++++++++++++ "+medicines.toString());
        return  medicines;

    }

    //SQLite add medicine
    public Medicine addMedicine(int id, String medicine_name, String medicine_des, int cateId,
                                int reminderId, Boolean reminder, int quantity, int dosage,
                                String dateIssued, int expireFactor,Context context){

        Medicine medicine = new Medicine(id, medicine_name, medicine_des,cateId, reminderId, reminder, quantity, dosage, dateIssued, expireFactor);

        taskAddMedicine = new AddMedicine(context);
        taskAddMedicine.execute(medicine);

        return medicine;

    }

    //SQLite add medicine
    public Medicine updateMedicine(int id, String medicine_name, String medicine_des, int cateId,
                                int reminderId, Boolean reminder, int quantity, int dosage,
                                String dateIssued, int expireFactor,Context context){

        Medicine medicine = new Medicine(id, medicine_name, medicine_des,cateId, reminderId, reminder, quantity, dosage, dateIssued, expireFactor);

        taskUpdateMedicine = new UpdateMedicine(context);
        taskUpdateMedicine.execute(medicine);

        return medicine;

    }

    //SQLite delete medicine

    public void deleteMedicine(int id,Context context){

        Medicine m = getMedicine(id);

        if(m != null)
        {
            taskDeleteMedicine = new DeleteMedicine(context);
            taskDeleteMedicine.execute(m);
        }

    }


    public Category getCategory(int id){

        Iterator<Category> i = categorys.iterator();

        while(i.hasNext()){
            Category c = i.next();
            if( c.getId() == id)
            {
                return c;
            }
        }

        return null;
    }

    //SQLite get Category list
    public ArrayList<Category> getCategorys(Context context) {
        taskListCategory = new ListCategory(context);
        taskListCategory.execute((Void)null);

        try{
            categorys = taskListCategory.get();
        }catch (InterruptedException e){
            e.printStackTrace();
        }catch (ExecutionException e){
            e.printStackTrace();
        }

        if(categorys == null){
            categorys = new ArrayList<Category>();
        }

        return categorys;

    }

    //SQLite add medicine
    public Category updateCategory(int id, String name, String code, String des,Context context){

        Category category = new Category(id, name, code,des);

        taskUpdateCategory = new UpdateCategory(context);
        taskUpdateCategory.execute(category);

        return category;

    }

    public String[] getCategoryNameList(Context context){

        String c_name[]= new String[getCategorys(context).size()];
        Iterator<Category> c_list = getCategorys(context).iterator();

        for(int i =0;c_list.hasNext();i++){

            Category c = c_list.next();
            if( c.getCategory_name() != null)
            {
                c_name[i]=c.getCategory_name();

                Log.v("TAG","---------------HealthManager "+c_name[i]);
            }
        }


        return c_name;

    }

    //SQLite add medicine
    public Category addCategory(int id, String category_name,String category_code,String category_des,Context context){

        Category category = new Category(id, category_name,category_code,category_des);

        taskAddCategory = new AddCategory(context);
        taskAddCategory.execute(category);

        return category;

    }




    //--------------------------------------Reminder-----------------------------------
    public Reminder getReminder(int id){

        Iterator<Reminder> i = reminders.iterator();

        while(i.hasNext()){
            Reminder c = i.next();
            if( c.getId() == id)
            {
                return c;
            }
        }

        return null;
    }

    public Reminder updateReminder(int id, int fre, String stime, int interval,Context context){

            Reminder reminder = new Reminder(id, fre, stime,interval);

            taskUpdateReminder = new UpdateReminder(context);
            taskUpdateReminder.execute(reminder);

            return reminder;

    }


    //SQLite add reminder
    public Reminder addReminder(int id, int frequency,String stime,int interval,Context context){

        Reminder reminder = new Reminder(id, frequency,stime,interval);

        taskAddReminder = new AddReminder(context);
        taskAddReminder.execute(reminder);

        return reminder;

    }

}