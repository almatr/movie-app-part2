package com.example.android.myapplication.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.HashMap;

public class MoviesProvider extends ContentProvider {

    static final String PROVIDER_NAME = "com.example.android.myapplication.Data";
    static final String URL = "content://" + PROVIDER_NAME + "/movies";
    public static final Uri CONTENT_URI = Uri.parse(URL);

    public static final String MOVIE_ID = "movie_id";
    public static final String MOVIE_TITLE = "movie_title";

    static final int URI_MOVIES = 1;
    static final int URI_MOVIES_ID = 2;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "movies", URI_MOVIES);
        uriMatcher.addURI(PROVIDER_NAME, "movies/#", URI_MOVIES_ID);
    }

    private DatabaseHelper dbHelper;
    static final String DATABASE_NAME = "movies_db";
    static final String MOVIES_TABLE_NAME = "movies";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLE =
            " CREATE TABLE " + MOVIES_TABLE_NAME +
                    " (" + MOVIE_ID + " TEXT NOT NULL, " +
                    MOVIE_TITLE + " TEXT NOT NULL);";

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " +  MOVIES_TABLE_NAME);
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHelper = new DatabaseHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
            final SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor retCursor;

            switch (uriMatcher.match(uri)) {
                case URI_MOVIES:
                    retCursor = db.query(MOVIES_TABLE_NAME,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            sortOrder);
                    break;

                case URI_MOVIES_ID:
                    String mSelection = "movie_id=?";
                    String[] mSelectionArgs = new String[] {uri.getPathSegments().get(1)};
                    retCursor = db.query(MOVIES_TABLE_NAME,
                            projection,
                            mSelection,
                            mSelectionArgs,
                            null,
                            null,
                            sortOrder);
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
            retCursor.setNotificationUri(getContext().getContentResolver(), uri);
            return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)){
            case URI_MOVIES:
                return "vnd.android.cursor.dir/vnd.favorite.movies";
            case URI_MOVIES_ID:
                return "vnd.android.cursor.item/vnd.favorite.movies";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowID = db.insert(	MOVIES_TABLE_NAME, "", values);
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = 0;
        switch (uriMatcher.match(uri)){
            case URI_MOVIES:
                count = db.delete(MOVIES_TABLE_NAME, selection, selectionArgs);
                break;
            case URI_MOVIES_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete( MOVIES_TABLE_NAME, MOVIE_ID +  " = " + id +
                                (!TextUtils.isEmpty(selection) ?
                                        "  AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case URI_MOVIES:
                count = db.update(MOVIES_TABLE_NAME, values, selection, selectionArgs);
                break;

            case URI_MOVIES_ID:
                count = db.update(MOVIES_TABLE_NAME, values,
                        MOVIE_ID + " = " + uri.getPathSegments().get(1) +
                                (!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
