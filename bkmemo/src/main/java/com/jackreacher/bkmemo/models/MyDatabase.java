package com.jackreacher.bkmemo.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JackReacher on 15/10/2016.
 */
public class MyDatabase extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "bkmemo_db";

    // Tables name
    private static final String TABLE_GROUPS = "groups";
    private static final String TABLE_PLACES = "places";
    private static final String TABLE_EVENTS = "events";

    // Tables Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_GROUP_ID = "group_id";
    private static final String KEY_TIME = "time";
    private static final String KEY_PLACE_ID = "place_id";

    public MyDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_GROUPS_TABLE = "CREATE TABLE " + TABLE_GROUPS +
                "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT UNIQUE )";
        db.execSQL(CREATE_GROUPS_TABLE);
        String CREATE_PLACES_TABLE = "CREATE TABLE " + TABLE_PLACES +
                "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT UNIQUE,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_LATITUDE + " TEXT,"
                + KEY_LONGITUDE + " TEXT,"
                + KEY_ADDRESS + " TEXT,"
                + KEY_GROUP_ID + " INTEGER, FOREIGN KEY(" + KEY_GROUP_ID + ") REFERENCES " + TABLE_GROUPS + "(" + KEY_ID + "))";
        db.execSQL(CREATE_PLACES_TABLE);
        String CREATE_EVENTS_TABLE = "CREATE TABLE " + TABLE_EVENTS +
                "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT UNIQUE,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_TIME + " TEXT,"
                + KEY_PLACE_ID + " INTEGER,"
                + "FOREIGN KEY(" + KEY_PLACE_ID + ") REFERENCES " + TABLE_PLACES + "(" + KEY_ID + ") )";
        db.execSQL(CREATE_EVENTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        if (oldVersion >= newVersion)
            return;
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLACES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUPS);

        // Create tables again
        onCreate(db);
    }

    // Adding new group
    public int addGroup(Group group){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_NAME , group.getName());

        // Inserting Row
        long ID = db.insert(TABLE_GROUPS, null, values);
        db.close();
        return (int) ID;
    }

    // Getting single group
    public Group getGroup(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_GROUPS, new String[]
                        {
                                KEY_ID,
                                KEY_NAME
                        }, KEY_ID + "=?",

                new String[] {String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Group group = new Group(Integer.parseInt(cursor.getString(0)), cursor.getString(1));

        return group;
    }

    // Getting all groups
    public List<Group> getAllGroups(){
        List<Group> groupList = new ArrayList<>();

        // Select all Query
        String selectQuery = "SELECT * FROM " + TABLE_GROUPS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Looping through all rows and adding to list
        if(cursor.moveToFirst()){
            do{
                Group group = new Group();
                group.setId(Integer.parseInt(cursor.getString(0)));
                group.setName(cursor.getString(1));

                // Adding group to list
                groupList.add(group);
            } while (cursor.moveToNext());
        }
        return groupList;
    }

    public int getDefaultGroupId(){
        String selectQuery = "SELECT MIN(" + KEY_ID + ") FROM " + TABLE_GROUPS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.getCount()>0)
            cursor.moveToFirst();
        return cursor.getInt(0);
    }

    // Getting groups Count
    public int getGroupsCount(){
        String countQuery = "SELECT * FROM " + TABLE_GROUPS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery,null);
        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    // Updating single group
    public int updateGroup(Group group){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME , group.getName());

        // Updating row
        return db.update(TABLE_GROUPS, values, KEY_ID + "=?",
                new String[]{String.valueOf(group.getId())});
    }

    // Deleting single group
    public void deleteGroup(Group group){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GROUPS, KEY_ID + "=?",
                new String[]{String.valueOf(group.getId())});
        db.close();
    }

    public void deleteAllGroups(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GROUPS, null, null);
        db.close();
    }

    // Adding new Place
    public int addPlace(Place place){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_NAME , place.getName());
        values.put(KEY_DESCRIPTION , place.getDescription());
        values.put(KEY_LATITUDE , place.getLatitude());
        values.put(KEY_LONGITUDE , place.getLongitude());
        values.put(KEY_ADDRESS, place.getAddress());
        values.put(KEY_GROUP_ID , place.getGroupId());

        // Inserting Row
        long ID = db.insert(TABLE_PLACES, null, values);
        db.close();
        return (int) ID;
    }

    // Getting single place
    public Place getPlace(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PLACES, new String[]
                        {
                                KEY_ID,
                                KEY_NAME,
                                KEY_DESCRIPTION,
                                KEY_LATITUDE,
                                KEY_LONGITUDE,
                                KEY_ADDRESS,
                                KEY_GROUP_ID
                        }, KEY_ID + "=?",

                new String[] {String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Place place = new Place(Integer.parseInt(cursor.getString(0)), cursor.getString(1),
                cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), Integer.parseInt(cursor.getString(6)));

        return place;
    }

    // Getting all places
    public List<Place> getAllPlaces(){
        List<Place> placeList = new ArrayList<>();

        // Select all Query
        String selectQuery = "SELECT * FROM " + TABLE_PLACES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Looping through all rows and adding to list
        if(cursor.moveToFirst()){
            do{
                Place place = new Place();
                place.setId(Integer.parseInt(cursor.getString(0)));
                place.setName(cursor.getString(1));
                place.setDescription(cursor.getString(2));
                place.setLatitude(cursor.getString(3));
                place.setLongitude(cursor.getString(4));
                place.setAddress(cursor.getString(5));
                place.setGroupId(Integer.parseInt(cursor.getString(6)));

                // Adding place to list
                placeList.add(place);
            } while (cursor.moveToNext());
        }
        return placeList;
    }

    // Getting places Count
    public int getPlacesCount(){
        String countQuery = "SELECT * FROM " + TABLE_PLACES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery,null);
        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    // Updating single place
    public int updatePlace(Place place){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME , place.getName());
        values.put(KEY_DESCRIPTION , place.getDescription());
        values.put(KEY_LATITUDE , place.getLatitude());
        values.put(KEY_LONGITUDE , place.getLongitude());
        values.put(KEY_ADDRESS , place.getLongitude());
        values.put(KEY_GROUP_ID , place.getGroupId());

        // Updating row
        return db.update(TABLE_PLACES, values, KEY_ID + "=?",
                new String[]{String.valueOf(place.getId())});
    }

    // Deleting single place
    public void deletePlace(Place place){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PLACES, KEY_ID + "=?",
                new String[]{String.valueOf(place.getId())});
        db.close();
    }

    public void deleteAllPlaces(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PLACES, null, null);
        db.close();
    }

    // Adding new event
    public int addEvent(Event event){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_NAME , event.getName());
        values.put(KEY_DESCRIPTION , event.getDescription());
        values.put(KEY_TIME , event.getTime());
        values.put(KEY_PLACE_ID , event.getPlaceId());

        // Inserting Row
        long ID = db.insert(TABLE_EVENTS, null, values);
        db.close();
        return (int) ID;
    }

    // Getting single event
    public Event getEvent(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_EVENTS, new String[]
                        {
                                KEY_ID,
                                KEY_NAME,
                                KEY_DESCRIPTION,
                                KEY_TIME,
                                KEY_PLACE_ID
                        }, KEY_ID + "=?",

                new String[] {String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Event event = new Event(Integer.parseInt(cursor.getString(0)), cursor.getString(1),
                cursor.getString(2), cursor.getString(3), Integer.parseInt(cursor.getString(4)));

        return event;
    }

    // Getting all events
    public List<Event> getAllEvents(){
        List<Event> eventList = new ArrayList<>();

        // Select all Query
        String selectQuery = "SELECT * FROM " + TABLE_EVENTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Looping through all rows and adding to list
        if(cursor.moveToFirst()){
            do{
                Event event = new Event();
                event.setId(Integer.parseInt(cursor.getString(0)));
                event.setName(cursor.getString(1));
                event.setDescription(cursor.getString(2));
                event.setTime(cursor.getString(3));
                event.setPlaceId(Integer.parseInt(cursor.getString(4)));

                // Adding event to list
                eventList.add(event);
            } while (cursor.moveToNext());
        }
        return eventList;
    }

    // Getting events Count
    public int getEventsCount(){
        String countQuery = "SELECT * FROM " + TABLE_EVENTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery,null);
        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    // Updating single event
    public int updateEvent(Event event){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME , event.getName());
        values.put(KEY_DESCRIPTION , event.getDescription());
        values.put(KEY_LONGITUDE , event.getTime());
        values.put(KEY_PLACE_ID , event.getPlaceId());

        // Updating row
        return db.update(TABLE_EVENTS, values, KEY_ID + "=?",
                new String[]{String.valueOf(event.getId())});
    }

    // Deleting single event
    public void deleteEvent(Event event){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EVENTS, KEY_ID + "=?",
                new String[]{String.valueOf(event.getId())});
        db.close();
    }

    public void deleteAllEvents(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EVENTS, null, null);
        db.close();
    }

    public int getPlacesCountByGroup(Group group){
        String countQuery = "SELECT * FROM " + TABLE_PLACES + " WHERE " + KEY_GROUP_ID + " = " + group.getId();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery,null);
        int count = cursor.getCount();
        cursor.close();

        return count;
    }
}
