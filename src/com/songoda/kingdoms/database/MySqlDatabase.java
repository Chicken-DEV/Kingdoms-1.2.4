package com.songoda.kingdoms.database;

import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("Duplicates")
public class MySqlDatabase<T> extends Database<T>{
    private final Type type;
    private String tablename;
    private String dbname;
    private Connection conn;
    public MySqlDatabase(String host, String dbname, String tablename,String username,String password, Type type) {
        this.type = type;
        this.dbname = dbname;
        this.tablename = tablename;
        String url = "jdbc:mysql://"+host+"/"+dbname;

        try {
            conn = DriverManager.getConnection(url,username,password);
            initTable();
        } catch (SQLException e) {
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
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO %table VALUES (?,?) ON DUPLICATE KEY UPDATE `data` = ?".replace("%table",tablename));
                stmt.setString(1,key);
                stmt.setString(2,serialize(value,type));
                stmt.setString(3,serialize(value,type));
                stmt.executeUpdate();
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean has(String key) {
        boolean result = false;
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT `id` FROM %table WHERE `id` = ?".replace("%table",tablename));
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
            PreparedStatement stmt = conn.prepareStatement("TRUNCATE TABLE %table".replace("%table",tablename));
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
            PreparedStatement stmt = conn.prepareStatement("SELECT `id` FROM %table".replace("%table",tablename));
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
        String tablequery = "CREATE TABLE IF NOT EXISTS %table (`id` CHAR(36) PRIMARY KEY, `data` TEXT);".replace("%table",tablename);
        PreparedStatement stmt = conn.prepareStatement(tablequery);
        stmt.executeUpdate();

    }
}
