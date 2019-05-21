package com.example.weather.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import com.example.weather.model.City;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

public class WeatherDatabase extends SQLiteOpenHelper {
    private Context context;
    private final static String TAG = "WeatherDatabase";
    private final static String DATA_FILE = "world_city.data";

    private final static String DATABASE_NAME = "weather";
    private final static int DATABASE_VERSION = 1;

    private final static String TABLE_CITY = "cities";

    public final static String COLUMN_CITY_ID = "_id";
    public final static String COLUMN_CITY_NAME = "cityName";
    public final static String COLUMN_CITY_COUNTRY = "cityCountry";
    public final static String COLUMN_CITY_LAT = "cityLat";
    public final static String COLUMN_CITY_LNG = "cityLng";

    public WeatherDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createCitiesTableQuery = String.format(
                "CREATE TABLE %s(%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT, %s REAL, %s REAL)",
                TABLE_CITY,
                COLUMN_CITY_ID,
                COLUMN_CITY_NAME,
                COLUMN_CITY_COUNTRY,
                COLUMN_CITY_LAT,
                COLUMN_CITY_LNG
        );
        db.execSQL(createCitiesTableQuery);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    context.getAssets().open(DATA_FILE), StandardCharsets.UTF_8));
            String line;
            boolean formatFlag = true;
            ArrayList<City> data = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                String[] temp = line.split(" ");
                if (temp.length == 5) {
                    int cityId = Integer.parseInt(temp[0]);
                    String cityName = temp[1].replace("_", " ");
                    String cityCountry = temp[2].replace("_", " ");
                    float cityLat = Float.parseFloat(temp[3]);
                    float cityLng = Float.parseFloat(temp[4]);
                    data.add(new City(cityId, cityName, cityCountry, cityLat, cityLng));
                } else {
                    Log.d(TAG, "world_city.data format is wrong!: " + line);
                    formatFlag = false;
                    break;
                }
            }
            if (formatFlag) {
                Log.d(TAG, "Read data successfully!");
                Set<Integer> idSet = new TreeSet<>();
                for (City city: data) {
                    if (idSet.contains(city.getId())) {
                        continue;
                    }
                    idSet.add(city.getId());
                    String insertQuery = String.format(
                            "INSERT INTO %s (%s, %s, %s, %s) VALUES (\"%s\", \"%s\", %s, %s)",
                            TABLE_CITY,
                            COLUMN_CITY_NAME,
                            COLUMN_CITY_COUNTRY,
                            COLUMN_CITY_LAT,
                            COLUMN_CITY_LNG,
                            city.getCityName(),
                            city.getCityCountry(),
                            Float.toString(city.getCityLat()),
                            Float.toString(city.getCityLng())
                    );
                    db.execSQL(insertQuery);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Can't read world_city.data!");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropCitiesTableQuery = String.format(
                "DROP TABLE IF EXISTS %s",
                TABLE_CITY
        );
        db.execSQL(dropCitiesTableQuery);
        onCreate(db);
    }

    private Cursor getCitySuggestion(String cityName) {
        if (cityName == null || cityName.length() <= 1) return null;
        cityName = cityName.toLowerCase();
        SQLiteDatabase db = this.getReadableDatabase();
        String getQuery = String.format(
                "SELECT %s, %s, %s, lower(%s) FROM %s WHERE lower(%s) LIKE \"%s%%\" ORDER BY %s ASC LIMIT 0, 5",
                COLUMN_CITY_ID,
                COLUMN_CITY_NAME,
                COLUMN_CITY_COUNTRY,
                COLUMN_CITY_NAME,
                TABLE_CITY,
                COLUMN_CITY_NAME,
                cityName,
                COLUMN_CITY_NAME
        );
        return db.rawQuery(getQuery, null);
    }

    public CursorAdapter getCitySuggestionAdapter(String cityName) {
        Cursor cursor = getCitySuggestion(cityName);
        return new SimpleCursorAdapter(
                context,
                android.R.layout.simple_list_item_2,
                cursor,
                new String[] {COLUMN_CITY_NAME, COLUMN_CITY_COUNTRY},
                new int[] {android.R.id.text1, android.R.id.text2},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        );
    }

    public City getCity(int cityId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = String.format(
                "SELECT * FROM %s WHERE %s = %s",
                TABLE_CITY,
                COLUMN_CITY_ID,
                Integer.toString(cityId)
        );
        Cursor cursor = db.rawQuery(selectQuery, null);
        City result = null;
        if (cursor.moveToFirst()) {
            result = new City(
                    cursor.getInt(cursor.getColumnIndex(COLUMN_CITY_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_CITY_NAME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_CITY_COUNTRY)),
                    cursor.getFloat(cursor.getColumnIndex(COLUMN_CITY_LAT)),
                    cursor.getFloat(cursor.getColumnIndex(COLUMN_CITY_LNG))
            );
        }
        cursor.close();
        return result;
    }
}