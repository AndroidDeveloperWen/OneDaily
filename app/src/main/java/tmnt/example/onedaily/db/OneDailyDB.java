package tmnt.example.onedaily.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import rx.schedulers.Schedulers;
import tmnt.example.onedaily.Rx.Operation;
import tmnt.example.onedaily.Rx.RxUilt;
import tmnt.example.onedaily.bean.msg.Collect;
import tmnt.example.onedaily.bean.msg.NoteInfo;
import tmnt.example.onedaily.mvp.CallBack;

/**
 * Created by tmnt on 2017/4/23.
 */

public class OneDailyDB {
    private Context mContext;
    private SQLiteDatabase mDatabase;
    private DBHelper helper;
    private static final String HISTORY = "history";
    private static final String TABLE_History = "t_searchHistory";
    private static final String TABLE_NOTE = "t_note";
    private static final String TABLE_COLLECT = "t_collect";

    private static final String ID = "hId";
    private static final String NID = "id";

    private static OneDailyDB mOneDailyDB;

    private static final String TAG = "OneDailyDB";
    private RxUilt mRxUilt;

    private OneDailyDB(Context context) {
        mContext = context;
        helper = new DBHelper(context);
        mRxUilt = RxUilt.getInstance();

    }

    public static OneDailyDB newInstance(Context context) {
        if (mOneDailyDB == null) {
            mOneDailyDB = new OneDailyDB(context);
        }
        return mOneDailyDB;
    }

    public void insertHistory(String history) {
        ContentValues values = new ContentValues();
        values.put(HISTORY, history);

        mRxUilt.createAndResult(Schedulers.io(), () -> {
                    mDatabase = helper.getWritableDatabase();
                    mDatabase.insert(TABLE_History, ID, values);
                    return null;
                }, new CallBack<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                }
        );

    }

    public void insertNote(NoteInfo noteInfo, CallBack<Boolean> callBack) {
        ContentValues values = new ContentValues();
        mRxUilt.createAndResult(Schedulers.io(), () -> {
            mDatabase = helper.getWritableDatabase();
            String d = String.valueOf(new Date().getTime());
            noteInfo.setId(d);
            String note = new Gson().toJson(noteInfo);
            values.put("nId", d);
            values.put("note", note);
            long id = mDatabase.insert(TABLE_NOTE, NID, values);
            if (id == -1) {
                return false;
            }
            return true;
        }, callBack);
    }

    public void queryHistory(CallBack<List<String>> callBack) {

        mRxUilt.createAndResult(Schedulers.io(), new Operation<List<String>>() {
            @Override
            public List<String> operation() {
                ArrayList<String> arrayList = new ArrayList<>();
                mDatabase = helper.getReadableDatabase();
                Cursor cursor = mDatabase.query(TABLE_History, null, null, null, null,
                        null, null);
                while (cursor.moveToNext()) {
                    arrayList.add(cursor.getString(cursor.getColumnIndex(HISTORY)));
                }
                cursor.close();
                if (arrayList == null || arrayList.size() == 0) {
                    return Collections.emptyList();
                }
                return arrayList;
            }
        }, callBack);
    }

    public void queryNote(CallBack<List<NoteInfo>> callBack) {
        mRxUilt.createAndResult(Schedulers.io(), () -> {
            ArrayList<NoteInfo> list = new ArrayList();
            mDatabase = helper.getReadableDatabase();
            Cursor cursor = mDatabase.query(TABLE_NOTE, null, null, null, null,
                    null, null);
            while (cursor.moveToNext()) {
                String id = cursor.getColumnName(cursor.getColumnIndex("nId"));
                String note = cursor.getString(cursor.getColumnIndex("note"));
                NoteInfo noteInfo = new Gson().fromJson(note, NoteInfo.class);
                list.add(noteInfo);
            }
            Log.i(TAG, "queryNote: " + list.size());
            cursor.close();
            if (list == null || list.size() == 0) {
                return Collections.emptyList();
            } else {
                return list;
            }
        }, callBack);
    }

    public int queryNoteCount() {
        mDatabase = helper.getReadableDatabase();
        Cursor cursor = mDatabase.query(TABLE_NOTE, null, null, null, null,
                null, null);
        return cursor.getCount();
    }

    public void updateNote(String id, NoteInfo noteInfo) {
        mRxUilt.createAndResult(Schedulers.io(), () -> {
            ContentValues values = new ContentValues();
            String note = new Gson().toJson(noteInfo);
            values.put("note", note);
            mDatabase = helper.getWritableDatabase();
            mDatabase.update(TABLE_NOTE, values, "nId=?", new String[]{id});
            return true;
        }, new CallBack<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {

            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }

    public void deleteNote(String id) {
        mRxUilt.createAndResult(Schedulers.io(), () -> {
            mDatabase = helper.getWritableDatabase();
            int re = mDatabase.delete(TABLE_NOTE, "nId=?", new String[]{id});
            if (re == 0) {
                return false;
            } else {
                return true;
            }
        }, new CallBack<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {

            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }

    public void clearHistory() {
        mDatabase = helper.getReadableDatabase();
        mDatabase.execSQL("delete from t_searchHistory");
        mDatabase.execSQL("update sqlite_sequence SET seq = 0 where name ='t_searchHistory'");
    }

    public void insertCollect(Collect collect) {
        mRxUilt.createAndResult(Schedulers.io(), () -> {
            ContentValues values = new ContentValues();
            String c = new Gson().toJson(collect);
            values.put("cId", collect.getId());
            values.put("collect", c);
            mDatabase = helper.getWritableDatabase();
            long id = mDatabase.insert(TABLE_COLLECT, "cId", values);
            if (id == -1) {
                return false;
            }
            return true;
        }, new CallBack<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {

            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }

    public void deleteCollect(String id) {
        mRxUilt.createAndResult(Schedulers.io(), () -> {
            mDatabase = helper.getWritableDatabase();
            int re = mDatabase.delete(TABLE_COLLECT, "id=?", new String[]{id});
            if (re == 0) {
                return false;
            }
            return true;
        }, new CallBack<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {

            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }

    public void queryCollect(CallBack<List<Collect>> callBack) {
        mRxUilt.createAndResult(Schedulers.io(), () -> {
            ArrayList<Collect> list = new ArrayList();
            mDatabase = helper.getReadableDatabase();
            Cursor cursor = mDatabase.query(TABLE_COLLECT, null, null, null, null,
                    null, null);
            while (cursor.moveToNext()) {
                String collect = cursor.getString(cursor.getColumnIndex("collect"));
                Collect collect1 = new Gson().fromJson(collect, Collect.class);
                list.add(collect1);
            }
            Log.i(TAG, "queryNote: " + list.size());
            cursor.close();
            if (list == null || list.size() == 0) {
                return Collections.emptyList();
            } else {
                return list;
            }
        }, callBack);
    }

    public void queryCollectBook(String id, CallBack<Boolean> callBack) {
        mRxUilt.createAndResult(Schedulers.io(), () -> {
            mDatabase = helper.getReadableDatabase();
            Cursor cursor = mDatabase.query(TABLE_COLLECT, new String[]{"id"}, "cId=?", new String[]{id}, null, null,
                    null, null);
            boolean isHave = cursor.isNull(0);
            return isHave;
        }, callBack);
    }

}
