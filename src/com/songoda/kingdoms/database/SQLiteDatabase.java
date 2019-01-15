package com.songoda.kingdoms.database;

import com.google.gson.JsonSyntaxException;
import com.songoda.kingdoms.main.Kingdoms;
import org.bukkit.Bukkit;
import org.sqlite.SQLiteConnection;
import org.sqlite.SQLiteConnectionConfig;

import java.io.File;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

@SuppressWarnings("Duplicates")
public class SQLiteDatabase<T> extends Database<T> {
    private final Type type;
    private File location;
    private String tablename;
    private String dbname;
    private Connection conn;
    private Queue<PreparedStatement> saveStatements;
    private boolean saveBusy = false;
    public SQLiteDatabase(File location, String dbname, String tablename,Type type) {
        saveStatements = new ArrayDeque<>();
        this.type = type;
        this.location = location;
        this.dbname = dbname;
        this.tablename = tablename;
        String url = "jdbc:sqlite:"+ Kingdoms.getInstance().getDataFolder().getAbsolutePath()+File.separator+dbname;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(url);
            if (conn!=null) {
                DatabaseMetaData meta = conn.getMetaData();
            }
            conn = DriverManager.getConnection(url);
            initTable();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public T load(String key, T def) {
        T result = def;
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT `data` FROM %table WHERE `id` = ?;".replace("%table",tablename));
            stmt.setString(1, key);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String ser = rs.getString("data");
                try {
                    //noinspection unchecked
                    result = (T) deserialize(ser, type);
                }catch (JsonSyntaxException e){
                    e.printStackTrace();
                    return def;
                }
                if (result==null)return def;
            }
            stmt.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void save(String key, T value) {
        try {
            if(!(value==null)){
                PreparedStatement stmt = conn.prepareStatement("REPLACE INTO %table (`id`,`data`) VALUES(?,?);".replace("%table",tablename));
                stmt.setString(1,key);
                String json = serialize(value,type);
                stmt.setString(2,json);
                if(saveStatements.isEmpty()){
                    DatabaseSave(stmt);
                }else{
                saveStatements.add(stmt);
                }
            }else{
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM %table WHERE id = ?".replace("%table", tablename));
                stmt.setString(1,key);
                if (saveStatements.isEmpty()){
                    DatabaseSave(stmt);
                }else {
                    saveStatements.add(stmt);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void DatabaseSave(PreparedStatement stmt){
        saveBusy = true;
        try {
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (!saveStatements.isEmpty()){
            DatabaseSave(saveStatements.poll());
        }
    }
    @Override
    public boolean has(String key) {
        boolean result = false;
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT `id` FROM %table WHERE `id` = ?;".replace("%table",tablename));
            stmt.setString(1,key);
            ResultSet rs = stmt.executeQuery();
            result = rs.next();
            rs.close();
            stmt.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void clear() {
        try {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM %table;".replace("%table",tablename));
            stmt.executeQuery();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Set<String> getKeys() {
        Set<String> tempset = new HashSet<>();
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT `id` FROM %table;".replace("%table",tablename));
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                tempset.add(rs.getString("id"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tempset;
    }

    private void initTable() throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS %table (`id` CHAR(36) PRIMARY KEY, `data` TEXT);".replace("%table",tablename));
        stmt.executeUpdate();
        stmt.close();
    }
}
