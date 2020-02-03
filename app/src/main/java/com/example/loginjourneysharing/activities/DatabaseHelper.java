package com.example.loginjourneysharing.activities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "UsersDB";
    private static final String TABLE_NAME = "User";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "token_id";
    private static final String[] COLUMNS = { KEY_ID, KEY_NAME};

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATION_TABLE = "CREATE TABLE User ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "token_id TEXT )";

        db.execSQL(CREATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // you can implement here migration process
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }

    /*
    public void deleteOne(Player player) {
        // Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "id = ?", new String[] { String.valueOf(player.getId()) });
        db.close();
    }
*/
    /*
    public Player getPlayer(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, // a. table
                COLUMNS, // b. column names
                " id = ?", // c. selections
                new String[] { String.valueOf(id) }, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit

        if (cursor != null)
            cursor.moveToFirst();

        Player player = new Player();
        player.setId(Integer.parseInt(cursor.getString(0)));
        player.setName(cursor.getString(1));
        player.setPosition(cursor.getString(2));
        player.setHeight(Integer.parseInt(cursor.getString(3)));

        return player;
    }

    public List<Player> allPlayers() {

        List<Player> players = new LinkedList<Player>();
        String query = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Player player = null;

        if (cursor.moveToFirst()) {
            do {
                player = new Player();
                player.setId(Integer.parseInt(cursor.getString(0)));
                player.setName(cursor.getString(1));
                player.setPosition(cursor.getString(2));
                player.setHeight(Integer.parseInt(cursor.getString(3)));
                players.add(player);
            } while (cursor.moveToNext());
        }

        return players;
    }
*/
    public void addUserToken(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, user.getTokenId());
        // insert
        db.insert(TABLE_NAME,null, values);
        db.close();
    }
/*
    public int updatePlayer(Player player) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, player.getName());
        values.put(KEY_POSITION, player.getPosition());
        values.put(KEY_HEIGHT, player.getHeight());

        int i = db.update(TABLE_NAME, // table
                values, // column/value
                "id = ?", // selections
                new String[] { String.valueOf(player.getId()) });

        db.close();

        return i;
    }*/
    /*
    private static final String TAG="DataabseHelper";
    private static final String TABLE_NAME="User-Table";
    private static final String Col1="ID";
    private static final String Col2="Token-ID";

    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable="CREATE TABLE "+ TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + Col2 + " TEXT)";
        db.execSQL(createTable);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean appData(String item){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(Col2, item);

        Log.d(TAG, "addData: Adding" + item + " to " + TABLE_NAME);
        long result=db.insert(TABLE_NAME, null,contentValues );

        if (result == -1){
            return false;
        }else {
            return true;
        }

    }*/


}
