package ru.alimov.repeatenglish.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * DB(under repository) implementation.
 */
public class Database extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "repeatEnglish.db";
    private static final int SCHEMA_VERSION = 1;

    public Database(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String script = "CREATE TABLE [word_dictionary] ( id INTEGER PRIMARY KEY, word_original TEXT NOT NULL,"
                + "word_translated TEXT NOT NULL, dateCreated INTEGER NOT NULL, dateUpdated INTEGER NOT NULL,"
                + "dateShowed INTEGER, add_counter INTEGER NOT NULL, correct_check_counter INTEGER NOT NULL,"
                + "incorrect_check_counter INTEGER NOT NULL, rating INTEGER NOT NULL);";
        db.execSQL(script);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public <T> T getObject(String sql, Function<Cursor, T> constructor, String[] sqlParams) {
        T dbObject = null;
        Cursor dbCursor = null;
        try {
            SQLiteDatabase database = this.getReadableDatabase();
            dbCursor = database.rawQuery(sql, sqlParams);
            if (dbCursor.moveToFirst()) {
                dbObject = constructor.apply(dbCursor);
            }
        } catch (Exception ex) {
            Log.e("Database", ex.getMessage(), ex);
        } finally {
            if (dbCursor != null) {
                dbCursor.close();
            }
            this.close();
        }
        return dbObject;
    }

    public <T> List<T> getObjectList(String sql, Function<Cursor, T> constructor, String[] sqlParams) {
        Cursor dbCursor = null;
        List<T> resultList = new ArrayList<>();
        try {
            SQLiteDatabase database = this.getReadableDatabase();
            dbCursor = database.rawQuery(sql, sqlParams);
            while (dbCursor.moveToNext()) {
                resultList.add(constructor.apply(dbCursor));
            }
        } catch (Exception ex) {
            Log.e("Database", ex.getMessage(), ex);
            resultList = null;
        } finally {
            if (dbCursor != null) {
                dbCursor.close();
            }
            this.close();
        }
        return resultList;
    }

    public Optional<Long> insertRow(String tableName, ContentValues values) {
        Optional<Long> resultRowId = Optional.empty();
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            resultRowId = Optional.of(database.insert(tableName, null, values));
        } catch (Exception ex) {
            Log.e("Database", ex.getMessage(), ex);
        } finally {
            this.close();
        }
        return resultRowId;
    }

    public Optional<Integer> updateRow(String tableName, String idColumn, long idColumnValue, ContentValues values) {
        String whereClause = idColumn + " = ?";
        String[] whereArgs = new String[]{String.valueOf(idColumnValue)};
        Optional<Integer> affectedRows = Optional.empty();
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            affectedRows = Optional.of(database.update(tableName, values, whereClause, whereArgs));
        } catch (Exception ex) {
            Log.e("Database", ex.getMessage(), ex);
        } finally {
            this.close();
        }
        return affectedRows;
    }

    public int deleteRow(String tableName, String idColumn, long idColumnValue, long id) {
        int rawsAffected = 0;
        String whereClause = idColumn + " = ?";
        String[] whereArgs = new String[]{String.valueOf(idColumnValue)};
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            rawsAffected = database.delete(tableName, whereClause, whereArgs);
        } catch (Exception ex) {
            Log.e("Database", ex.getMessage(), ex);
        } finally {
            this.close();
        }
        return rawsAffected;
    }

    public int deleteAllRows(String tableName) {
        int rowsAffected = 0;
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            rowsAffected = database.delete(tableName, "1", null);
        } catch (Exception ex) {
            Log.e("Database", ex.getMessage(), ex);
        } finally {
            this.close();
        }
        return rowsAffected;
    }

    public void executeQuery(String sql, Object[] args) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            database.execSQL(sql, args);
        } catch (Exception ex) {
            Log.e("Database", ex.getMessage(), ex);
        } finally {
            this.close();
        }
    }
}
