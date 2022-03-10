package ncu.im3069.demo.app;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

import org.json.*;

public class Order {

    /** orderId，訂單編號 */
    private int orderId;
    
    /** customerId，訂單編號 */
    private int customerId;
    
    /** deliverymanId，負責訂單的外送員編號 */
    private int deliverymanId;
    
    /** orderStatus，訂單狀態 */
    private int orderStatus;
    
    /** deliverFee，外送費 */
    private int deliveryFee;
    
    /** totalPrice，訂單總金額 */
    private int totalPrice;
    
    /** totalCal，訂單總金額 */
    private int totalCal;

    /** list，訂單列表 */
    private ArrayList<Ordered_Details> list = new ArrayList<Ordered_Details>();
    

    /** oph，OrderItemHelper 之物件與 Order 相關之資料庫方法（Sigleton） */
    private Ordered_DetailsHelper odh = Ordered_DetailsHelper.getHelper();
    
    

    /**
     * 實例化（Instantiates）一個新的（new）Order 物件<br>
     * 採用多載（overload）方法進行，此建構子用於建立訂單資料時，產生一個新的訂單
     *
     * @param customerId 會員編號
     * @param deliverymanId 會員電子信箱
     * @param orderStatus 訂單狀態
     * @param deliveryFee 外送費
     * @param totalPrice 總金額
     * @param totalCal 總卡路里
     */
    public Order( int customerId, int deliverymanId, int orderStatus, int deliveryFee, int totalPrice, int totalCal) {
        this.customerId = customerId;
        this.deliverymanId = deliverymanId;
        this.orderStatus = orderStatus;
        this.deliveryFee = deliveryFee;
        this.totalPrice = totalPrice;
        this.totalCal = totalCal;
    }//end constructor

    /**
     * 實例化（Instantiates）一個新的（new）Order 物件<br>
     * 採用多載（overload）方法進行，此建構子用於修改訂單資料時，新改資料庫已存在的訂單
     * 這邊的修訂資料只有修訂訂購者的資料，不適修改訂單的商品內容
     *
     * @param orderId 訂單編號
     * @param customerId 會員編號
     * @param deliverymanId 會員電子信箱
     * @param orderStatus 訂單狀態
     * @param deliveryFee 外送費
     * @param totalPrice 總金額
     * @param totalCal 總卡路里

     */
 
    public Order(int orderId, int customerId, int deliverymanId, int orderStatus, int deliveryFee, int totalPrice, int totalCal) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.deliverymanId = deliverymanId;
        this.orderStatus = orderStatus;
        this.deliveryFee = deliveryFee;
        this.totalPrice = totalPrice;
        this.totalCal = totalCal;
        getOrderProductFromDB(); //把訂單裡所有的product
    }

    /**
     * 新增一個訂單產品及其數量
     */
    public void addOrderMeals(Meals ml, int quantity) {
        this.list.add(new Ordered_Details(ml, quantity));
    }

    /**
     * 新增一個訂單產品
     */
    public void addOrdered_Details(Ordered_Details od) {
        this.list.add(od);
    }

    /**
     * 設定訂單編號
     */
    public void setId(int orderId) {
        this.orderId = orderId;
    }

    /**
     * 取得訂單編號
     *
     * @return int 回傳訂單編號
     */
    public int getOrderId() {
        return this.orderId;
    }
    
    /**
     * 取得顧客編號
     *
     * @return int 回傳顧客編號
     */
    public int getCustomerId() {
        return this.customerId;
    }
    
    /**
     * 取得外送員編號
     *
     * @return int 回傳外送員編號
     */
    public int getDeliverymanId() {
        return this.deliverymanId;
    }
    
    /**
     * 取得訂單狀態
     *
     * @return int 回傳訂單狀態
     */
    public int getOrderStatus() {
        return this.orderStatus;
    }
    
    /**
     * 取得外送費
     *
     * @return int 回傳外送費
     */
    public int getDeliveryFee() {
        return this.deliveryFee;
    }
    

  
    /**
     * 取得訂單總金額
     *
     * @return double 回傳訂單總金額
     */
    public int getTotalPrice() {
        return this.totalPrice;
    }
    
    /**
     * 取得訂單總熱量
     *
     * @return double 回傳訂單總熱量
     */
    public int getTotalCal() {
        return this.totalCal;
    }


    /**
     * 取得該名會員所有資料
     *
     * @return the data 取得該名會員之所有資料並封裝於JSONObject物件內
     */
    public ArrayList<Ordered_Details> getOrderProduct() {
        return this.list;
    }

    /**
     * 從 DB 中取得訂單產品
     */
    private void getOrderProductFromDB() {
        ArrayList<Ordered_Details> data = odh.getOrder_DetailsByOrderId(this.orderId);
        this.list = data;
    }

    /**
     * 取得訂單基本資料
     *
     * @return JSONObject 取得訂單基本資料
     */
    public JSONObject getOrderData() {
        JSONObject jso = new JSONObject();
        jso.put("order_id", getOrderId());
        jso.put("customer_id", getCustomerId()); //要記得回去改html的json
        jso.put("deliveryman_id", getDeliverymanId());
        jso.put("order_status", getOrderStatus());
        jso.put("delivery_fee", getDeliveryFee());
        jso.put("total_price", getTotalPrice());
        jso.put("total_kcal", getTotalCal());

        return jso;
    }

    /**
     * 取得訂單產品資料
     *
     * @return JSONArray 取得訂單產品資料
     */
    public JSONArray getOrdered_DetailsData() {
        JSONArray result = new JSONArray();

        for(int i=0 ; i < this.list.size() ; i++) {
            result.put(this.list.get(i).getData());
        }

        return result;
    }

    /**
     * 取得訂單所有資訊
     *
     * @return JSONObject 取得訂單所有資訊
     */
    public JSONObject getOrderAllInfo() {
        JSONObject jso = new JSONObject();
        jso.put("order_info", getOrderData());
        jso.put("meals_info", getOrdered_DetailsData());

        return jso;
    }

    /**
     * 設定訂單產品編號
     */
    public void setOrderDetailsId(JSONArray data) {
        for(int i=0 ; i < this.list.size() ; i++) {
            this.list.get(i).setOrder_details_id((int) data.getLong(i));
        }
    }

}