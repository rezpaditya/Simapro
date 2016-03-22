package id.co.pln.simapro.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLException;

/**
 * Created by Rezpa Aditya on 3/18/2016.
 */
public class DatabaseConnector {
    private static final String DATABASE_NAME = "unit_area.db";
    private SQLiteDatabase database;
    private DatabaseOpenHelper databaseOpenHelper;

    public DatabaseConnector (Context context){
        databaseOpenHelper = new DatabaseOpenHelper(context, DATABASE_NAME, null, 1);
    }

    public void open() throws SQLException{
        database = databaseOpenHelper.getWritableDatabase();
    }

    public void close(){
        if(database != null){
            database.close();
        }
    }

    //insert to table unit
    public void insertUnit(String id_unit, String nm_unit) throws SQLException {
        ContentValues new_unit = new ContentValues();
        new_unit.put("id_unit", id_unit);
        new_unit.put("nm_unit", nm_unit);

        open();
        String selectQuery = "select * from unit where id_unit = '" + id_unit +"'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        if(cursor.getCount() == 0)
        {
            database.insert("unit", null, new_unit);
            Log.d("unit insert", nm_unit);
        }
        else
        {
            database.update("unit", new_unit, "id_unit='" + id_unit + "'", null);
            Log.d("unit update", nm_unit);
        }
        cursor.close();
        close();
    }

    //insert to table area
    public void insertArea(String id_unitp, String id_bursare_unitp, String nm_unitp, String id_unit_unitp) throws SQLException {
        ContentValues new_area = new ContentValues();
        new_area.put("id_unitp", id_unitp);
        new_area.put("id_bursare_unitp", id_bursare_unitp);
        new_area.put("nm_unitp", nm_unitp);
        new_area.put("id_unit_unitp", id_unit_unitp);

        open();
        String selectQuery = "select * from area where id_unitp= '" + id_unitp +"'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        if(cursor.getCount() == 0)
        {
            database.insert("area", null, new_area);
            Log.d("area insert", nm_unitp);
        }
        else
        {
            database.update("area", new_area, "id_unitp='" + id_unitp+"'", null);
            Log.d("area update", nm_unitp);
        }
        cursor.close();
        close();
    }

    //update row table unit
    public void updateUnit() throws SQLException {
        ContentValues edit_unit = new ContentValues();
        edit_unit.put("","");
        open();
        database.update("unit", edit_unit, "_id" + "", null);
        close();
    }

    //update row table area
    public void updateArea() throws SQLException {
        ContentValues edit_area = new ContentValues();
        edit_area.put("","");
        open();
        database.update("area", edit_area, "_id" + "", null);
        close();
    }

    //get all data unit
    public Cursor getALlUnit(){
        return database.query("unit", new String[] {"id_unit", "nm_unit"},
                null, null, null, null, "id_unit");
    }

    //get all data area
    public Cursor getALlArea(){
        return database.query("area", new String[] {"_id", "nama"},
                null, null, null, null, "nama");
    }

    //get area by unit id
    public Cursor getAreaByUnit(String id_bursare_unitp){
        return database.query("area", null, "id_unit_unitp = " + id_bursare_unitp,
                null, null, null, null);
    }

    //get one unit
    public Cursor getUnit(long id){
        return database.query("unit", null, "id=" + id,
                null, null, null, null);
    }

    //get one area
    public Cursor getArea(long id){
        return database.query("area", null, "id=" + id,
                null, null, null, null);
    }

    //delete one unit row
    public void deleteUnit(long id) throws SQLException {
        open();
        database.delete("unit", "id=" + id, null);
        close();
    }

    //delete one unit row
    public void deleteArea(long id) throws SQLException {
        open();
        database.delete("area", "id=" + id, null);
        close();
    }

    private class DatabaseOpenHelper extends SQLiteOpenHelper{

        public DatabaseOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d("main", "create table jalan");
            String createUnit = "CREATE TABLE unit(\n" +
                    "id_unit text primary key not null,\n" +
                    "nm_unit text\n" +
                    ")";
            String createArea = "CREATE TABLE area(\n" +
                    "id_unitp text primary key not null,\n" +
                    "id_unit_unitp text,\n" +
                    "nm_unitp text,\n" +
                    "id_bursare_unitp text,\n" +
                    "FOREIGN KEY(id_unit_unitp) REFERENCES unit(id_unit)\n" +
                    ");";
            db.execSQL(createUnit);
            db.execSQL(createArea);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

}
