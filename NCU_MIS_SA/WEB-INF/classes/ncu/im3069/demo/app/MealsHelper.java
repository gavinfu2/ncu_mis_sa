package ncu.im3069.demo.app;

import java.sql.*;
import java.time.LocalDateTime;

import org.json.*;

import ncu.im3069.demo.util.DBMgr;
import ncu.im3069.demo.app.Meals;

public class MealsHelper {
    private MealsHelper() {
        
    }
    
    private static MealsHelper mh;
    private Connection conn = null;
    private PreparedStatement pres = null;
    
    public static MealsHelper getHelper() {
        /** Singleton檢查是否已經有MealsHelper物件，若無則new一個，若有則直接回傳 */
        if(mh == null) mh = new MealsHelper();
        
        return mh;
    }
    
    public JSONObject getAll() {
        /** 新建一個 Meals 物件之 m 變數，用於紀錄每一位查詢回之餐點資料 */
    	Meals m = null;
        /** 用於儲存所有檢索回之餐點，以JSONArray方式儲存 */
        JSONArray jsa = new JSONArray();
        /** 記錄實際執行之SQL指令 */
        String exexcute_sql = "";
        /** 紀錄程式開始執行時間 */
        long start_time = System.nanoTime();
        /** 紀錄SQL總行數 */
        int row = 0;
        /** 儲存JDBC檢索資料庫後回傳之結果，以 pointer 方式移動到下一筆資料 */
        ResultSet rs = null;
        
        try {
            /** 取得資料庫之連線 */
            conn = DBMgr.getConnection();
            /** SQL指令 */
            String sql = "SELECT * FROM `missa`.`meals`";
            
            /** 將參數回填至SQL指令當中，若無則不用只需要執行 prepareStatement */
            pres = conn.prepareStatement(sql);
            /** 執行查詢之SQL指令並記錄其回傳之資料 */
            rs = pres.executeQuery();

            /** 紀錄真實執行的SQL指令，並印出 **/
            exexcute_sql = pres.toString();
            System.out.println(exexcute_sql);
            
            /** 透過 while 迴圈移動pointer，取得每一筆回傳資料 */
            while(rs.next()) {
                /** 每執行一次迴圈表示有一筆資料 */
                row += 1;
                
                /** 將 ResultSet 之資料取出 */
                int meals_id = rs.getInt("meals_id");
                int restaurant_id = rs.getInt("restaurant_id");
                String name = rs.getString("name");
                int price = rs.getInt("price");
                String image = rs.getString("image");
                String description = rs.getString("description");
                int kcal = rs.getInt("kcal");
                
                /** 將每一筆餐點資料產生一名新MealsProduct物件 */
                m = new Meals(meals_id, restaurant_id, name, price, image, description, kcal);
                /** 取出該項餐點之資料並封裝至 JSONsonArray 內 */
                jsa.put(m.getData());
            }

        } catch (SQLException e) {
            /** 印出JDBC SQL指令錯誤 **/
            System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            /** 若錯誤則印出錯誤訊息 */
            e.printStackTrace();
        } finally {
            /** 關閉連線並釋放所有資料庫相關之資源 **/
            DBMgr.close(rs, pres, conn);
        }
        
        /** 紀錄程式結束執行時間 */
        long end_time = System.nanoTime();
        /** 紀錄程式執行時間 */
        long duration = (end_time - start_time);
        
        /** 將SQL指令、花費時間、影響行數與所有會員資料之JSONArray，封裝成JSONObject回傳 */
        JSONObject response = new JSONObject();
        response.put("sql", exexcute_sql);
        response.put("row", row);
        response.put("time", duration);
        response.put("data", jsa);

        return response;
    }
    
    public JSONObject getByIdList(String data) {
        /** 新建一個 Meals 物件之 m 變數，用於紀錄每一位查詢回之餐點資料 */
        Meals m = null;
        /** 用於儲存所有檢索回之餐點，以JSONArray方式儲存 */
        JSONArray jsa = new JSONArray();
        /** 記錄實際執行之SQL指令 */
        String exexcute_sql = "";
        /** 紀錄程式開始執行時間 */
        long start_time = System.nanoTime();
        /** 紀錄SQL總行數 */
        int row = 0;
        /** 儲存JDBC檢索資料庫後回傳之結果，以 pointer 方式移動到下一筆資料 */
        ResultSet rs = null;

        try {
            /** 取得資料庫之連線 */
            conn = DBMgr.getConnection();
            String[] in_para = DBMgr.stringToArray(data, ",");
            /** SQL指令 */
            String sql = "SELECT * FROM `missa`.`meals` WHERE `meals`.`meals_id`";
            for (int i=0 ; i < in_para.length ; i++) {
                sql += (i == 0) ? "in (?" : ", ?";
                sql += (i == in_para.length-1) ? ")" : "";
            }
            
            /** 將參數回填至SQL指令當中，若無則不用只需要執行 prepareStatement */
            pres = conn.prepareStatement(sql);
            for (int i=0 ; i < in_para.length ; i++) {
              pres.setString(i+1, in_para[i]);
            }
            /** 執行查詢之SQL指令並記錄其回傳之資料 */
            rs = pres.executeQuery();

            /** 紀錄真實執行的SQL指令，並印出 **/
            exexcute_sql = pres.toString();
            System.out.println(exexcute_sql);
            
            /** 透過 while 迴圈移動pointer，取得每一筆回傳資料 */
            while(rs.next()) {
                /** 每執行一次迴圈表示有一筆資料 */
                row += 1;
                
                /** 將 ResultSet 之資料取出 */
                int meals_id = rs.getInt("meals_id");
                int restaurant_id = rs.getInt("restaurant_id");
                String name = rs.getString("name");
                int price = rs.getInt("price");
                String image = rs.getString("image");
                String description = rs.getString("description");
                int kcal = rs.getInt("kcal");
                
                /** 將每一筆餐點資料產生一名新MealsProduct物件 */
                m = new Meals(meals_id, restaurant_id, name, price, image, description, kcal);
                /** 取出該項餐點之資料並封裝至 JSONsonArray 內 */
                jsa.put(m.getData());
            }

        } catch (SQLException e) {
            /** 印出JDBC SQL指令錯誤 **/
            System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            /** 若錯誤則印出錯誤訊息 */
            e.printStackTrace();
        } finally {
            /** 關閉連線並釋放所有資料庫相關之資源 **/
            DBMgr.close(rs, pres, conn);
        }
        
        /** 紀錄程式結束執行時間 */
        long end_time = System.nanoTime();
        /** 紀錄程式執行時間 */
        long duration = (end_time - start_time);
        
        /** 將SQL指令、花費時間、影響行數與所有餐點資料之JSONArray，封裝成JSONObject回傳 */
        JSONObject response = new JSONObject();
        response.put("sql", exexcute_sql);
        response.put("row", row);
        response.put("time", duration);
        response.put("data", jsa);

        return response;
    }
    
    public Meals getById(String id) {	//此id為meals_id
        /** 新建一個 Meals 物件之 m 變數，用於紀錄每一位查詢回之餐點資料 */
        Meals m = null;
        /** 記錄實際執行之SQL指令 */
        String exexcute_sql = "";
        /** 儲存JDBC檢索資料庫後回傳之結果，以 pointer 方式移動到下一筆資料 */
        ResultSet rs = null;
        
        try {
            /** 取得資料庫之連線 */
            conn = DBMgr.getConnection();
            /** SQL指令 */
            String sql = "SELECT * FROM `missa`.`meals` WHERE `meals`.`meals_id` = ? LIMIT 1";
            
            /** 將參數回填至SQL指令當中，若無則不用只需要執行 prepareStatement */
            pres = conn.prepareStatement(sql);
            pres.setString(1, id);
            /** 執行查詢之SQL指令並記錄其回傳之資料 */
            rs = pres.executeQuery();

            /** 紀錄真實執行的SQL指令，並印出 **/
            exexcute_sql = pres.toString();
            System.out.println(exexcute_sql);
            
            /** 透過 while 迴圈移動pointer，取得每一筆回傳資料 */
            while(rs.next()) {
                /** 將 ResultSet 之資料取出 */
                int meals_id = rs.getInt("meals_id");
                int restaurant_id = rs.getInt("restaurant_id");
                String name = rs.getString("name");
                int price = rs.getInt("price");
                String image = rs.getString("image");
                String description = rs.getString("description");
                int kcal = rs.getInt("kcal");
                
                /** 將每一筆餐點資料產生一名新Meals物件 */
                m = new Meals(meals_id, restaurant_id, name, price, image, description, kcal);
            }

        } catch (SQLException e) {
            /** 印出JDBC SQL指令錯誤 **/
            System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            /** 若錯誤則印出錯誤訊息 */
            e.printStackTrace();
        } finally {
            /** 關閉連線並釋放所有資料庫相關之資源 **/
            DBMgr.close(rs, pres, conn);
        }

        return m;
    }
}
