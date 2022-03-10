package ncu.im3069.demo.controller;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;
import org.json.*;

import ncu.im3069.demo.app.Order;
import ncu.im3069.demo.app.Meals;
import ncu.im3069.demo.app.MealsHelper;
import ncu.im3069.demo.app.OrderHelper;
import ncu.im3069.tools.JsonReader;

import javax.servlet.annotation.WebServlet;

@WebServlet("/api/order.do")
public class OrderController extends HttpServlet {

    /** The Constant serialVersionUID. */
 private static final long serialVersionUID = 1L;

    /** ph，ProductHelper 之物件與 Product 相關之資料庫方法（Sigleton） */
    private MealsHelper mh =  MealsHelper.getHelper();

    /** oh，OrderHelper 之物件與 order 相關之資料庫方法（Sigleton） */
 private OrderHelper oh =  OrderHelper.getHelper();

    public OrderController() {
        super();
    }

    /**
     * 處理 Http Method 請求 GET 方法（新增資料）
     *
     * @param request Servlet 請求之 HttpServletRequest 之 Request 物件（前端到後端）
     * @param response Servlet 回傳之 HttpServletResponse 之 Response 物件（後端到前端）
     * @throws ServletException the servlet exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
 protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /** 透過 JsonReader 類別將 Request 之 JSON 格式資料解析並取回 */
        JsonReader jsr = new JsonReader(request);

        /** 取出經解析到 JsonReader 之 Request 參數 */
        String order_id = jsr.getParameter("order_id");
        System.out.println("order_id = " + order_id);

        /** 新建一個 JSONObject 用於將回傳之資料進行封裝 */
        JSONObject resp = new JSONObject();

        /** 判斷該字串是否存在，若存在代表要取回個別訂單之資料，否則代表要取回全部資料庫內訂單之資料 */
        if (!order_id.isEmpty()) {
        	System.out.println("1");
          /** 透過 orderHelper 物件的 getByID() 方法自資料庫取回該筆訂單之資料，回傳之資料為 JSONObject 物件 */
          JSONObject query = oh.getById(order_id);
          
          resp.put("status", "200");
          resp.put("message", "單筆訂單資料取得成功");
          resp.put("response", query);
        }
        else {
          System.out.println("2");
          /** 透過 orderHelper 物件之 getAll() 方法取回所有訂單之資料，回傳之資料為 JSONObject 物件 */
          JSONObject query = oh.getAll();
          resp.put("status", "200");
          resp.put("message", "所有訂單資料取得成功");
          resp.put("response", query);
        }

        /** 透過 JsonReader 物件回傳到前端（以 JSONObject 方式） */
        jsr.response(resp, response);
 }

    /**
     * 處理 Http Method 請求 POST 方法（新增資料）
     *
     * @param request Servlet 請求之 HttpServletRequest 之 Request 物件（前端到後端）
     * @param response Servlet 回傳之 HttpServletResponse 之 Response 物件（後端到前端）
     * @throws ServletException the servlet exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
 protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	 	System.out.println("3");
     /** 透過 JsonReader 類別將 Request 之 JSON 格式資料解析並取回 */
        JsonReader jsr = new JsonReader(request);
        JSONObject jso = jsr.getObject();

        /** 取出經解析到 JSONObject 之 Request 參數 */
        int customer_id = jso.getInt("customer_id");
        int deliveryman_id = jso.getInt("deliveryman_id");
        int order_status = jso.getInt("order_status");
        int delivery_fee = jso.getInt("delivery_fee");
        int total_price = jso.getInt("total_price");
        int total_cal = jso.getInt("total_kcal");
        JSONArray item = jso.getJSONArray("item");
        JSONArray quantity = jso.getJSONArray("quantity");

        /** 建立一個新的訂單物件 */
        Order od = new Order(customer_id, deliveryman_id, order_status, delivery_fee, total_price, total_cal);

        /** 將每一筆訂單細項取得出來 */
        for(int i=0 ; i < item.length() ; i++) {
            String meals_id = item.getString(i);
            int amount = quantity.getInt(i);

            /** 透過 ProductHelper 物件之 getById()，取得產品的資料並加進訂單物件裡 */
            Meals ml = mh.getById(meals_id);
            od.addOrderMeals(ml, amount);
        }

        /** 透過 orderHelper 物件的 create() 方法新建一筆訂單至資料庫 */
        JSONObject result = oh.create(od);

        /** 設定回傳回來的訂單編號與訂單細項編號 */
        od.setId((int) result.getLong("order_id"));
        od.setOrderDetailsId(result.getJSONArray("order_details_id"));

        /** 新建一個 JSONObject 用於將回傳之資料進行封裝 */
        JSONObject resp = new JSONObject();
        resp.put("status", "200");
        resp.put("message", "訂單新增成功！");
        resp.put("response", od.getOrderAllInfo());

        /** 透過 JsonReader 物件回傳到前端（以 JSONObject 方式） */
        jsr.response(resp, response);
 }

}