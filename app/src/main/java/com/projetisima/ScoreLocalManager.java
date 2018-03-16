package com.projetisima;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ScoreLocalManager {
    private static final String TABLE_NAME = "scoreLocal";
    public static final String KEY_ID = "id";
    public static final String KEY_DATE = "date";
    public static final String KEY_SCORE = "score";
    public static final String CREATE_TABLE_SCORE_LOCAL = "CREATE TABLE " + TABLE_NAME+
            " (" +
            " " + KEY_ID + " INTEGER primary key," +
            " " + KEY_DATE + " INTEGER," +
            " " + KEY_SCORE + " INTEGER" +
            ");";
    private MySQLite maBaseSQLite; // notre gestionnaire du fichier SQLite
    private SQLiteDatabase db;

    // Constructeur
    public ScoreLocalManager(Context context)
    {
        maBaseSQLite = MySQLite.getInstance(context);
    }

    public void open()
    {
        //on ouvre la table en lecture/écriture
        db = maBaseSQLite.getWritableDatabase();
    }

    public void close()
    {
        //on ferme l'accès à la BDD
        db.close();
    }

    public long addScoreLocal(ScoreLocal sc) {
        // Ajout d'un enregistrement dans la table

        ContentValues values = new ContentValues();
        values.put(KEY_DATE, sc.getDate());
        values.put(KEY_SCORE, sc.getScore());

        // insert() retourne l'id du nouvel enregistrement inséré, ou -1 en cas d'erreur
        return db.insert(TABLE_NAME,null, values);
    }

    public int updateScoreLocal(ScoreLocal sc) {
        // modification d'un enregistrement
        // valeur de retour : (int) nombre de lignes affectées par la requête

        ContentValues values = new ContentValues();
        values.put(KEY_DATE, sc.getDate());
        values.put(KEY_SCORE, sc.getScore());

        String where = KEY_ID+ " = ?";
        String[] whereArgs = {sc.getId()+""};

        return db.update(TABLE_NAME, values, where, whereArgs);
    }

    public int removeScoreLocal(ScoreLocal sc) {
        // suppression d'un enregistrement
        // valeur de retour : (int) nombre de lignes affectées par la clause WHERE, 0 sinon

        String where = KEY_ID + " = ?";
        String[] whereArgs = {sc.getId() + ""};

        return db.delete(TABLE_NAME, where, whereArgs);
    }

    public ScoreLocal getScoreLocal(int id) {
        ScoreLocal a = new ScoreLocal(0, new Long(0), 0);

        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_ID + "=" + id, null);
        if (c.moveToFirst()) {
            a.setId(c.getInt(c.getColumnIndex(KEY_ID)));
            a.setDate(c.getLong(c.getColumnIndex(KEY_DATE)));
            a.setScore(c.getInt(c.getColumnIndex(KEY_SCORE)));
            c.close();
        }

        return a;
    }

    public ScoreLocal getScoreLocalMin(){
        ScoreLocal a = new ScoreLocal(0, new Long(0), 0);

        Cursor c = db.rawQuery("SELECT MIN(" + KEY_SCORE + "), " + KEY_ID + ", " + KEY_DATE + ", " + KEY_SCORE + " FROM " + TABLE_NAME, null);
        if (c.moveToFirst()) {
            a.setId(c.getInt(c.getColumnIndex(KEY_ID)));
            a.setDate(c.getLong(c.getColumnIndex(KEY_DATE)));
            a.setScore(c.getInt(c.getColumnIndex(KEY_SCORE)));
            c.close();
        }

        return a;
    }

    public int getNbTables(){
        Cursor c = db.rawQuery("SELECT COUNT(*) as nb FROM " + TABLE_NAME, null);
        int nb = 0;
        if (c.moveToFirst()) {
            nb = c.getInt(c.getColumnIndex("nb"));
            c.close();
        }
        return nb;
    }

    public Cursor getAllScoreLocal() {
        // sélection de tous les enregistrements de la table par ordre des scores et des dates
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + KEY_SCORE + " DESC, " + KEY_DATE + " DESC", null);
    }

}