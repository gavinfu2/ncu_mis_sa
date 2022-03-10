package ncu.im3069.demo.app;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import ncu.im3069.demo.util.DBMgr;

public class Ordered_DetailsHelper {
    private Ordered_DetailsHelper() {
        
    }
    
    private static Ordered_DetailsHelper odh;
    private Connection conn = null;
    private PreparedStatement pres = null;
    
    /**
     * 靜態方法<br>
     * 實作Singleton（單例模式），僅允許建立一個MemberHelper物件
     *
     * @return the helper 回傳MemberHelper物件
     */
    public static Ordered_DetailsHelper getHelper() {
        /** Singleton檢查是否已經有MemberHelper物件，若無則new一個，若有則直接回傳 */
        if(odh == null) odh = new Ordered_DetailsHelper();
        
        return odh;
    }
    
    public JSONArray createByList(int order_id, List<Ordered_Details> ordered_details) {
        JSONArray jsa = new JSONArray();
        /** 記錄實際執行之SQL指令 */
        String exexcute_sql = "";
        
        for(int i=0 ; i < ordered_details.size() ; i++) {
        	Ordered_Details od = ordered_details.get(i);
            
            /** 取得所需之參數 */
        	int order_details_id = od.getOrder_details_id();
        	int meals_id = od.getMeals().getMeals_id();
        	int unit_price = od.getUnit_price();
        	int subtotal_price = od.getSubtotal_price();
        	int count = od.getCount();
        	int unit_kcal = od.getUnit_kcal();
        	int subtotal_kcal = od.getSubtotal_kcal();
            
            try {
                /** 取得資料庫之連線 */
                conn = DBMgr.getConnection();
                /** SQL指令 */
                String sql = "INSERT INTO `missa`.`ordered_details`(`order_details_id` ,`order_id`, `meals_id`, `unit_price`, `subtotal_price`, `count`, `unit_kcal`, `subtotal_kcal`)"
                        + " VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
                
                /** 將參數回填至SQL指令當中 */
                pres = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                pres.setInt(1, order_details_id);
                pres.setInt(2, order_id);
                pres.setInt(3, meals_id);
                pres.setInt(4, unit_price);
                pres.setInt(5, subtotal_price);
                pres.setInt(6, count);
                pres.setInt(7, unit_kcal);
                pres.setInt(8, subtotal_kcal);
                
                /** 執行新增之SQL指令並記錄影響之行數 */
                pres.executeUpdate();
                
                /** 紀錄真實執行的SQL指令，並印出 **/
                exexcute_sql = pres.toString();
                System.out.println(exexcute_sql);
                
                ResultSet rs = pres.getGeneratedKeys();

                if (rs.next()) {
                    long id = rs.getLong(1);
                    jsa.put(id);
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
        }
        
        return jsa;
    }
    
    public ArrayList<Ordered_Details> getOrder_DetailsByOrderId(int order_id) {
        ArrayList<Ordered_Details> result = new ArrayList<Ordered_Details>();
        /** 記錄實際執行之SQL指令 */
        String exexcute_sql = "";
        ResultSet rs = null;
        Ordered_Details od;
        
        try {
            /** 取得資料庫之連線 */
            conn = DBMgr.getConnection();
            /** SQL指令 */
            String sql = "SELECT * FROM `missa`.`ordered_details` WHERE `ordered_details`.`order_id` = ?";
            
            /** 將參數回填至SQL指令當中 */
            pres = conn.prepareStatement(sql);
            pres.setInt(1, order_id);
            
            /** 執行新增之SQL指令並記錄影響之行數 */
            rs = pres.executeQuery();
            
            /** 紀錄真實執行的SQL指令，並印出 **/
            exexcute_sql = pres.toString();
            System.out.println(exexcute_sql);
            
            while(rs.next()) {
                /** 每執行一次迴圈表示有一筆資料 */
                
                /** 將 ResultSet 之資料取出 */
            	int order_details_id = rs.getInt("order_details_id");
            	int meals_id = rs.getInt("meals_id");
            	int unit_price = rs.getInt("unit_price");
            	int subtotal_price = rs.getInt("subtotal_price");
            	int count = rs.getInt("count");
            	int unit_kcal = rs.getInt("unit_kcal");
            	int subtotal_kcal = rs.getInt("subtotal_kcal");
                
                /** 將每一筆會員資料產生一名新Ordered_Details物件 */
                od = new Ordered_Details(order_details_id, order_id, meals_id, unit_price, subtotal_price, count, unit_kcal, subtotal_kcal);
                /** 取出該名會員之資料並封裝至 JSONsonArray 內 */
                result.add(od);
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
        
        return result;
    }
}