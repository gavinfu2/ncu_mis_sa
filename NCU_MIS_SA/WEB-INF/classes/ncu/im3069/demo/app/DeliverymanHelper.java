package ncu.im3069.demo.app;

import java.sql.*;
import org.json.*;

import ncu.im3069.demo.util.DBMgr;

public class DeliverymanHelper {
    
    /**
     * 實例化（Instantiates）一個新的（new）DeliverymanHelper物件<br>
     * 採用Singleton不需要透過new
     */
    private DeliverymanHelper() {
        
    }
    
    /** 靜態變數，儲存DeliverymanHelper物件 */
    private static DeliverymanHelper dh;
    
    /** 儲存JDBC資料庫連線 */
    private Connection conn = null;
    
    /** 儲存JDBC預準備之SQL指令 */
    private PreparedStatement pres = null;
    
    /**
     * 靜態方法<br>
     * 實作Singleton（單例模式），僅允許建立一個DeliverymanHelper物件
     *
     * @return the helper 回傳DeliverymanHelper物件
     */
    public static DeliverymanHelper getHelper() {
        /** Singleton檢查是否已經有DeliverymanHelper物件，若無則new一個，若有則直接回傳 */
        if(dh == null) dh = new DeliverymanHelper();
        
        return dh;
    }
    
    /**
     * 透過會員編號（ID）刪除會員
     *
     * @param id 會員編號
     * @return the JSONObject 回傳SQL執行結果
     */
    public JSONObject deleteByID(int deliveryman_id) {
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
            String sql = "DELETE FROM `missa`.`deliveryman` WHERE `deliveryman_id` = ? LIMIT 1";
            
            /** 將參數回填至SQL指令當中 */
            pres = conn.prepareStatement(sql);
            pres.setInt(1, deliveryman_id);
            /** 執行刪除之SQL指令並記錄影響之行數 */
            row = pres.executeUpdate();

            /** 紀錄真實執行的SQL指令，並印出 **/
            exexcute_sql = pres.toString();
            System.out.println(exexcute_sql);
            
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
        
        /** 將SQL指令、花費時間與影響行數，封裝成JSONObject回傳 */
        JSONObject response = new JSONObject();
        response.put("sql", exexcute_sql);
        response.put("row", row);
        response.put("time", duration);

        return response;
    }
    
    /**
     * 取回所有會員資料
     *
     * @return the JSONObject 回傳SQL執行結果與自資料庫取回之所有資料
     */
    public JSONObject getAll() {
        /** 新建一個 DeliverymanHelper 物件之 d 變數，用於紀錄每一位查詢回之會員資料 */
    	Deliveryman d = null;
        /** 用於儲存所有檢索回之會員，以JSONArray方式儲存 */
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
            String sql = "SELECT * FROM `missa`.`deliveryman`";
            
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
                int deliveryman_id = rs.getInt("deliveryman_id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String password = rs.getString("password");
                String id_number = rs.getString("id_number");
                String phone = rs.getString("phone");
                
                /** 將每一筆會員資料產生一名新Deliveryman物件 */
                d = new Deliveryman(deliveryman_id, name, phone, id_number,email, password);
                /** 取出該名會員之資料並封裝至 JSONsonArray 內 */
                jsa.put(d.getData());
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
    
    /**
     * 透過會員編號（ID）取得會員資料
     *
     * @param id 會員編號
     * @return the JSON object 回傳SQL執行結果與該會員編號之會員資料
     */
    public JSONObject getByID(String deliveryman_id) {
        /** 新建一個 Deliveryman 物件之 d 變數，用於紀錄每一位查詢回之會員資料 */
    	Deliveryman d = null;
        /** 用於儲存所有檢索回之會員，以JSONArray方式儲存 */
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
            String sql = "SELECT * FROM `missa`.`deliveryman` WHERE `deliveryman_id` = ? LIMIT 1";
            
            /** 將參數回填至SQL指令當中 */
            pres = conn.prepareStatement(sql);
            pres.setString(1, deliveryman_id);
            /** 執行查詢之SQL指令並記錄其回傳之資料 */
            rs = pres.executeQuery();

            /** 紀錄真實執行的SQL指令，並印出 **/
            exexcute_sql = pres.toString();
            System.out.println(exexcute_sql);
            
            /** 透過 while 迴圈移動pointer，取得每一筆回傳資料 */
            /** 正確來說資料庫只會有一筆該會員編號之資料，因此其實可以不用使用 while 迴圈 */
            while(rs.next()) {
                /** 每執行一次迴圈表示有一筆資料 */
                row += 1;
                
                /** 將 ResultSet 之資料取出 */
                int deliveryman_id1 = rs.getInt("deliveryman_id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String password = rs.getString("password");
                String id_number = rs.getString("id_number");
                String phone = rs.getString("phone");
                
                /** 將每一筆會員資料產生一名新deliveryman物件 */
                d = new Deliveryman(deliveryman_id1, name, phone, id_number, email, password);
                /** 取出該名會員之資料並封裝至 JSONsonArray 內 */
                jsa.put(d.getData());
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
    public JSONObject getByEmail(String email) {
        /** 新建一個 Deliveryman 物件之 d 變數，用於紀錄每一位查詢回之會員資料 */
    	Deliveryman d = null;
        /** 用於儲存所有檢索回之會員，以JSONArray方式儲存 */
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
            String sql = "SELECT * FROM `missa`.`deliveryman` WHERE `email` = ? LIMIT 1";
            
            /** 將參數回填至SQL指令當中 */
            pres = conn.prepareStatement(sql);
            pres.setString(1, email);
            /** 執行查詢之SQL指令並記錄其回傳之資料 */
            rs = pres.executeQuery();

            /** 紀錄真實執行的SQL指令，並印出 **/
            exexcute_sql = pres.toString();
            System.out.println(exexcute_sql);
            
            /** 透過 while 迴圈移動pointer，取得每一筆回傳資料 */
            /** 正確來說資料庫只會有一筆該會員編號之資料，因此其實可以不用使用 while 迴圈 */
            while(rs.next()) {
                /** 每執行一次迴圈表示有一筆資料 */
                row += 1;
                
                /** 將 ResultSet 之資料取出 */
                int deliveryman_id = rs.getInt("deliveryman_id");
                String name = rs.getString("name");
                String email1 = rs.getString("email");
                String password = rs.getString("password");
                String id_number = rs.getString("id_number");
                String phone = rs.getString("phone");
                
                /** 將每一筆會員資料產生一名新deliveryman物件 */
                d = new Deliveryman(deliveryman_id, name, phone, id_number, email1, password);
                /** 取出該名會員之資料並封裝至 JSONsonArray 內 */
                jsa.put(d.getData());
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
    /**
     * 檢查該名會員之電子郵件信箱是否重複註冊
     *
     * @param c 一名會員之Customer物件
     * @return boolean 若重複註冊回傳False，若該信箱不存在則回傳True
     */
    public boolean checkDuplicate(Deliveryman d){
        /** 紀錄SQL總行數，若為「-1」代表資料庫檢索尚未完成 */
        int row = -1;
        /** 儲存JDBC檢索資料庫後回傳之結果，以 pointer 方式移動到下一筆資料 */
        ResultSet rs = null;
        
        try {
            /** 取得資料庫之連線 */
            conn = DBMgr.getConnection();
            /** SQL指令 */
            String sql = "SELECT count(*) FROM `missa`.`deliveryman` WHERE `email` = ?";
            
            /** 取得所需之參數 */
            String email = d.getEmail();
            
            /** 將參數回填至SQL指令當中 */
            pres = conn.prepareStatement(sql);
            pres.setString(1, email);
            /** 執行查詢之SQL指令並記錄其回傳之資料 */
            rs = pres.executeQuery();

            /** 讓指標移往最後一列，取得目前有幾行在資料庫內 */
            rs.next();
            row = rs.getInt("count(*)");
            System.out.print(row);

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
        
        /** 
         * 判斷是否已經有一筆該電子郵件信箱之資料
         * 若無一筆則回傳False，否則回傳True 
         */
        return (row == 0) ? false : true;
    }
    
    /**
     * 建立該名會員至資料庫
     * @return the JSON object 回傳SQL指令執行之結果
     */
    public JSONObject create(Deliveryman d) {
        /** 記錄實際執行之SQL指令 */
        String exexcute_sql = "";
        /** 紀錄程式開始執行時間 */
        long start_time = System.nanoTime();
        /** 紀錄SQL總行數 */
        int row = 0;
        
        try {
            /** 取得資料庫之連線 */
            conn = DBMgr.getConnection();
            /** SQL指令 */
            String sql = "INSERT INTO `missa`.`deliveryman`(`name`, `phone`, `id_number`, `email`, `password`)"
                    + " VALUES(?, ?, ?, ?, ?)";
            
            /** 取得所需之參數 */
            String name = d.getName();
            String phone = d.getPhone();
            String id_number = d.getId_number();
            String email = d.getEmail();
            String password = d.getPassword();
            
            
            
            /** 將參數回填至SQL指令當中 */
            pres = conn.prepareStatement(sql);
            pres.setString(1, name);
            pres.setString(2, phone);
            pres.setString(3, id_number);
            pres.setString(4, email);
            pres.setString(5, password);
    
            /** 執行新增之SQL指令並記錄影響之行數 */
            row = pres.executeUpdate();
            
            /** 紀錄真實執行的SQL指令，並印出 **/
            exexcute_sql = pres.toString();
            System.out.println(exexcute_sql);

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

        /** 紀錄程式結束執行時間 */
        long end_time = System.nanoTime();
        /** 紀錄程式執行時間 */
        long duration = (end_time - start_time);

        /** 將SQL指令、花費時間與影響行數，封裝成JSONObject回傳 */
        JSONObject response = new JSONObject();
        response.put("sql", exexcute_sql);
        response.put("time", duration);
        response.put("row", row);

        return response;
    }
    
    /**
     * 更新一名會員之會員資料
     *
     * @param d 一名會員之Deliveryman物件
     * @return the JSONObject 回傳SQL指令執行結果與執行之資料
     */
    public JSONObject update(Deliveryman d) {
        /** 紀錄回傳之資料 */
        JSONArray jsa = new JSONArray();
        /** 記錄實際執行之SQL指令 */
        String exexcute_sql = "";
        /** 紀錄程式開始執行時間 */
        long start_time = System.nanoTime();
        /** 紀錄SQL總行數 */
        int row = 0;
        
        try {
            /** 取得資料庫之連線 */
            conn = DBMgr.getConnection();
            /** SQL指令 */
            String sql = "Update `missa`.`deliveryman` SET `password` = ? , `phone` = ? WHERE `email` = ?";
            /** 取得所需之參數 */

            String password = d.getPassword();
            String phone = d.getPhone();
            
            /** 將參數回填至SQL指令當中 */
            pres = conn.prepareStatement(sql);
            pres.setString(1, password);
            pres.setString(2, phone);
            /** 執行更新之SQL指令並記錄影響之行數 */
            row = pres.executeUpdate();

            /** 紀錄真實執行的SQL指令，並印出 **/
            exexcute_sql = pres.toString();
            System.out.println(exexcute_sql);

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
        
        /** 紀錄程式結束執行時間 */
        long end_time = System.nanoTime();
        /** 紀錄程式執行時間 */
        long duration = (end_time - start_time);
        
        /** 將SQL指令、花費時間與影響行數，封裝成JSONObject回傳 */
        JSONObject response = new JSONObject();
        response.put("sql", exexcute_sql);
        response.put("row", row);
        response.put("time", duration);
        response.put("data", jsa);

        return response;
    }

}
