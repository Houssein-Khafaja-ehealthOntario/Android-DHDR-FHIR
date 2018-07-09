package ca.ehealth.ontario.dhdr_fhirprototype.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * This class is used to initialize our local database and fill it with test data.
 * We're only storing the name and HCN. Other information is acquired from querying PCR and DHDR.
 */
public class LocalSQLOpenHelper extends android.database.sqlite.SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "LocalPatients.db";
    public static final int DATABASE_VERSION = 1;
    private static final String SQL_CREATE = "CREATE TABLE patients ( _id INTEGER PRIMARY KEY, name TEXT, hcn TEXT)";

    LocalSQLOpenHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Any changes made to this onCreate will only appear after reinstalling the app on your phone.
     * If you're debugging this application on android, go to the settings -- > apps on your phone, then find the app in the app list and make sure it gets uninstalled.
     * On the next debug session, the app will be installed and this onCreate function will execute.
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(SQL_CREATE);

        ContentValues contentValues = new ContentValues(); // to hold a list of column:value pairs for database inserts.

        contentValues.put("name", "Joshua Kaitlyn Kelly");
        contentValues.put("hcn", "9274702019");
        db.insert("patients", null, contentValues);

        contentValues.put("name", "Benjamin Kayla Gonzalez");
        contentValues.put("hcn", "8449992539");
        db.insert("patients", null, contentValues);

        contentValues.put("name", "Madison Foster");
        contentValues.put("hcn", "1868176460");
        db.insert("patients", null, contentValues);
//
//        contentValues.put("name", "David Hernandez");
//        contentValues.put("hcn", "6408383104");
//        db.insert("patients", null, contentValues);
//
//        contentValues.put("name", "Olivia Hernandez");
//        contentValues.put("hcn", "8028261884");
//        db.insert("patients", null, contentValues);
//
//        contentValues.put("name", "Farah Naz");
//        contentValues.put("hcn", "2900000684");
//        db.insert("patients", null, contentValues);
//
//        contentValues.put("name", "Samuel Sean Baker");
//        contentValues.put("hcn", "2081645117");
//        db.insert("patients", null, contentValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }
}
