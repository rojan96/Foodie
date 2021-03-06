package com.chiragawale.foodie.dao.impl;

import android.util.Log;

import com.chiragawale.foodie.dao.FoodDao;
import com.chiragawale.foodie.model.ApiFoodEntry;
import com.chiragawale.foodie.model.RealmFoodEntry;
import com.chiragawale.foodie.utilities.TimeUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;


public class FoodDaoImpl  implements FoodDao {
    private Realm realm = Realm.getDefaultInstance();
    private Calendar calobj = Calendar.getInstance();

    @Override
    public void quickAddFood(final RealmFoodEntry food) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmFoodEntry db_food = realm.createObject(RealmFoodEntry.class);
                db_food.setName(food.getName());
                db_food.setCalories(food.getCalories());
                db_food.setProtein(food.getProtein());
                db_food.setCarbs(food.getCarbs());
                db_food.setTotalFat(food.getTotalFat());
                db_food.setEntryTime(food.getEntryTime());
                db_food.setMealTimeCode(food.getMealTimeCode());
            }
        });
    }

    @Override
        public List<ApiFoodEntry> getAllFood() {
        return realm.where(ApiFoodEntry.class).sort("entryTime", Sort.DESCENDING).findAll();
    }

    @Override
    public List<RealmFoodEntry> getFoodByDate(int daysFromToday) {
        Log.e("DATE LOG ", TimeUtils.getFormattedDate(daysFromToday) +  " TO  " + TimeUtils.getFormattedDate(daysFromToday+1));
        return realm.where(RealmFoodEntry.class).greaterThanOrEqualTo("entryTime",TimeUtils.getDate(daysFromToday)).lessThan("entryTime",TimeUtils.getDate(daysFromToday+1)).findAll();
    }

    @Override
    public List<List<RealmFoodEntry>>  getFoodByMealCode(int daysFromToday) {
        List<List<RealmFoodEntry>> realmFoodEntryLists = new ArrayList<>();
        List<RealmFoodEntry> realmFoodEntries = new ArrayList<>();
        for(int mealTimeCode = 0;mealTimeCode<4 ;mealTimeCode++) {
            realmFoodEntryLists.add(realm.where(RealmFoodEntry.class).equalTo("mealTimeCode", mealTimeCode).greaterThanOrEqualTo("entryTime", TimeUtils.getDate(daysFromToday)).lessThan("entryTime", TimeUtils.getDate(daysFromToday + 1)).findAll());
        }
        return realmFoodEntryLists;
    }

    @Override
    public void addSearchHistory(ApiFoodEntry food) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ApiFoodEntry db_food = realm.createObject(ApiFoodEntry.class);
                db_food.setName(food.getName());
                db_food.setCalories(food.getCalories());
                db_food.setProtein(food.getProtein());
                db_food.setCarbs(food.getCarbs());
                db_food.setFat(food.getFat());
                db_food.setEntryTime(food.getEntryTime());
                db_food.setMealTimeCode(food.getMealTimeCode());
            }
        });
    }

    //removes last entry from the realm
    @Override
    public void removeEntry(long timeStamp) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<ApiFoodEntry> rows = realm.where(ApiFoodEntry.class).equalTo("entryTime",timeStamp).findAll();
                rows.deleteAllFromRealm();
            }
        });
    }
}
