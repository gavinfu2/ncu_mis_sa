package ncu.im3069.demo.app;

import java.sql.*;
import java.util.*;

import org.json.*;

import ncu.im3069.demo.util.DBMgr;
import ncu.im3069.demo.app.Ordered_DetailsHelper;

public class OrderHelper {
    
    private static OrderHelper oh;
    private Connection conn = null;
    private PreparedStatement pres = null;
    private Ordered_DetailsHelper odh =  Ordered_DetailsHelper.getHelper();
    
    private OrderHelper() {
    }
    
    public static OrderHelper getHelper() {
        if(oh == null) oh = new OrderHelper();
        
        return oh;
    }
    
    public JSONObject create(Order order) {
        /** 記錄實際執行之SQL指令 */
        String exexcute_sql = "";
        int id = -1;
        JSONArray opa = new JSONArray();
        
        try {
            /** 取得資料庫之連線 */
            conn = DBMgr.getConnection();
            /** SQL指令 */
            String sql = "INSERT INTO `missa`.`order`(`customer_id`, `deliveryman_id`, `order_status`, `delivery_fee`, `total_price`, `total_kcal`)"
                    + " VALUES(?, ?, ?, ?, ?, ?)";
            
            /** 取得所需之參數 */
            int customer_id = order.getCustomerId();
            int deliveryman_id = order.getDeliverymanId();
            int order_status = order.getOrderStatus();
            double delivery_fee = order.getDeliveryFee();
            double total_price = order.getTotalPrice();
            double total_kcal = order.getTotalCal();
            
            /** 將參數回填至SQL指令當中 */
            pres = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pres.setInt(1, customer_id);
            pres.setInt(2, deliveryman_id);
            pres.setInt(3, order_status);
            pres.setDouble(4, delivery_fee);
            pres.setDouble(5, total_price);
            pres.setDouble(6, total_kcal);
            
            /** 執行新增之SQL指令並記錄影響之行數 */
            pres.executeUpdate();
            
            /** 紀錄真實執行的SQL指令，並印出 **/
            exexcute_sql = pres.toString();
            System.out.println(exexcute_sql);
            
            ResultSet rs = pres.getGeneratedKeys();

            if (rs.next()) {
                id = rs.getInt(1);
                ArrayList<Ordered_Details> opd = order.getOrderProduct();
                opa = odh.createByList(id, opd);
            }
        } catch (SQLException e) {
            /** 印出JDBC SQL指令錯誤 **/
            System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            /** 若錯誤則印出錯誤訊息 */
            e.printStackTrace();
        } finally {
            /** 關閉連線並釋放所有資料庫相關之資源 **/
            DBMgr.close(pres, conn);
        }

        /** 將SQL指令、花費時間與影響行數，封裝成JSONObject回傳 */
        JSONObject response = new JSONObject();
        response.put("order_id", id);
        response.put("order_product_id", opa);

        return response;
    }
    
    public JSONObject getAll() {
        Order o = null;
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
            String sql = "SELECT * FROM `missa`.`order`";
            
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
                int order_id = rs.getInt("order_id");
                int customer_id = rs.getInt("customer_id");
                int deliveryman_id = rs.getInt("deliveryman_id");
                int order_status = rs.getInt("order_status");
                int delivery_fee = rs.getInt("delivery_fee");
                int total_price = rs.getInt("total_price");
                int total_kcal = rs.getInt("total_kcal");
                
                /** 將每一筆商品資料產生一名新order物件 */
                o = new Order(order_id, customer_id, deliveryman_id, order_status, delivery_fee, total_price, total_kcal);
                /** 取出該項商品之資料並封裝至 JSONsonArray 內 */
                jsa.put(o.getOrderAllInfo());
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
    
    public JSONObject getById(String order_id) {
        JSONObject data = new JSONObject();
        Order o = null;
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
            String sql = "SELECT * FROM `missa`.`order` WHERE `order`.`order_id` = ?";
            
            /** 將參數回填至SQL指令當中，若無則不用只需要執行 prepareStatement */
            pres = conn.prepareStatement(sql);
            pres.setString(1, order_id);
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
                int order_id1 = rs.getInt("order_id");
                int customer_id = rs.getInt("customer_id");
                int deliveryman_id = rs.getInt("deliveryman_id");
                int order_status = rs.getInt("order_status");
                int delivery_fee = rs.getInt("delivery_fee");
                int total_price = rs.getInt("total_price");
                int total_kcal = rs.getInt("total_kcal");
                
                /** 將每一筆商品資料產生一名新Order物件 */
                o = new Order(order_id1, customer_id, deliveryman_id, order_status, delivery_fee, total_price, total_kcal);
                /** 取出該項商品之資料並封裝至 JSONsonArray 內 */
                data = o.getOrderAllInfo();
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
        response.put("data", data);

        return response;
    }
}