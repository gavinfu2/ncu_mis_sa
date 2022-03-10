package ncu.im3069.demo.controller;

import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.json.*;
import ncu.im3069.demo.app.Deliveryman;
import ncu.im3069.demo.app.DeliverymanHelper;
import ncu.im3069.tools.JsonReader;

@WebServlet("/api/deliveryman.do")

public class DeliverymanController extends HttpServlet {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** dh，DeliverymanHelper之物件與Deliveryman相關之資料庫方法（Sigleton） */
    private DeliverymanHelper dh =  DeliverymanHelper.getHelper();
    
    /**
     * 處理Http Method請求POST方法（新增資料）
     *
     * @param request Servlet請求之HttpServletRequest之Request物件（前端到後端）
     * @param response Servlet回傳之HttpServletResponse之Response物件（後端到前端）
     * @throws ServletException the servlet exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        /** 透過JsonReader類別將Request之JSON格式資料解析並取回 */
        JsonReader jsr = new JsonReader(request);
        JSONObject jso = jsr.getObject();
        
        /** 取出經解析到JSONObject之Request參數 */
        String name = jso.getString("name");
        String phone = jso.getString("phone");
        String id_number = jso.getString("id_number");
        String email = jso.getString("email");
        String password = jso.getString("password");
        
        /** 建立一個新的外送員物件 */
        Deliveryman d = new Deliveryman(name, phone, id_number,email, password);
        
        /** 後端檢查是否有欄位為空值，若有則回傳錯誤訊息 */
        if(email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            /** 以字串組出JSON格式之資料 */
            String resp = "{\"status\": \'400\', \"message\": \'欄位不能有空值\', \'response\': \'\'}";
            /** 透過JsonReader物件回傳到前端（以字串方式） */
            jsr.response(resp, response);
        }
        /** 透過DeliverymanHelper物件的checkDuplicate()檢查該外送員電子郵件信箱是否有重複 */
        else if (!dh.checkDuplicate(d)) {
            /** 透過DeliverymanHelper物件的create()方法新建一個外送員至資料庫 */
            JSONObject data = dh.create(d);
            
            /** 新建一個JSONObject用於將回傳之資料進行封裝 */
            JSONObject resp = new JSONObject();
            resp.put("status", "200");
            resp.put("message", "成功! 註冊外送員資料...");
            resp.put("response", data);
            
            /** 透過JsonReader物件回傳到前端（以JSONObject方式） */
            jsr.response(resp, response);
        }
        else {
            /** 以字串組出JSON格式之資料 */
            String resp = "{\"status\": \'400\', \"message\": \'新增帳號失敗，此E-Mail帳號重複！\', \'response\': \'\'}";
            /** 透過JsonReader物件回傳到前端（以字串方式） */
            jsr.response(resp, response);
        }
    }

    /**
     * 處理Http Method請求GET方法（取得資料）
     *
     * @param request Servlet請求之HttpServletRequest之Request物件（前端到後端）
     * @param response Servlet回傳之HttpServletResponse之Response物件（後端到前端）
     * @throws ServletException the servlet exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            /** 透過JsonReader類別將Request之JSON格式資料解析並取回 */
            JsonReader jsr = new JsonReader(request);
            /** 若直接透過前端AJAX之data以key=value之字串方式進行傳遞參數，可以直接由此方法取回資料 */
            String deliveryman_id = jsr.getParameter("deliveryman_id");
            String email = jsr.getParameter("email");
            
            /** 判斷該字串是否存在，若存在代表要取回個別外送員之資料，否則代表要取回全部資料庫內外送員之資料 */
            if (deliveryman_id.isEmpty() && email.isEmpty()) {
                /** 透過DeliverymanHelper物件之getAll()方法取回所有外送員之資料，回傳之資料為JSONObject物件 */
                JSONObject query = dh.getAll();
                
                /** 新建一個JSONObject用於將回傳之資料進行封裝 */
                JSONObject resp = new JSONObject();
                resp.put("status", "200");
                resp.put("message", "所有外送員資料取得成功");
                resp.put("response", query);
        
                /** 透過JsonReader物件回傳到前端（以JSONObject方式） */
                jsr.response(resp, response);
            }
            else if(deliveryman_id.isEmpty() && !(email.isEmpty())) {
                JSONObject query = dh.getByEmail(email);
                
                /** 新建一個JSONObject用於將回傳之資料進行封裝 */
                JSONObject resp = new JSONObject();
                resp.put("status", "200");
                resp.put("message", "外送員登入資料取得成功");
                resp.put("response", query);
        
                /** 透過JsonReader物件回傳到前端（以JSONObject方式） */
                jsr.response(resp, response);
            }
            else {
                /** 透過DeliverymanHelper物件的getByID()方法自資料庫取回該名外送員之資料，回傳之資料為JSONObject物件 */
                JSONObject query = dh.getByID(deliveryman_id);
                
                /** 新建一個JSONObject用於將回傳之資料進行封裝 */
                JSONObject resp = new JSONObject();
                resp.put("status", "200");
                resp.put("message", "外送員資料取得成功");
                resp.put("response", query);
        
                /** 透過JsonReader物件回傳到前端（以JSONObject方式） */
                jsr.response(resp, response);
            }
        }

    /**
     * 處理Http Method請求DELETE方法（刪除）
     *
     * @param request Servlet請求之HttpServletRequest之Request物件（前端到後端）
     * @param response Servlet回傳之HttpServletResponse之Response物件（後端到前端）
     * @throws ServletException the servlet exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void doDelete(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        /** 透過JsonReader類別將Request之JSON格式資料解析並取回 */
        JsonReader jsr = new JsonReader(request);
        JSONObject jso = jsr.getObject();
        
        /** 取出經解析到JSONObject之Request參數 */
        int deliveryman_id = jso.getInt("deliveryman_id");
        
        /** 透過DeliverymanHelper物件的deleteByID()方法至資料庫刪除該名外送員，回傳之資料為JSONObject物件 */
        JSONObject query = dh.deleteByID(deliveryman_id);
        
        /** 新建一個JSONObject用於將回傳之資料進行封裝 */
        JSONObject resp = new JSONObject();
        resp.put("status", "200");
        resp.put("message", "外送員移除成功！");
        resp.put("response", query);

        /** 透過JsonReader物件回傳到前端（以JSONObject方式） */
        jsr.response(resp, response);
    }

    /**
     * 處理Http Method請求PUT方法（更新）
     *
     * @param request Servlet請求之HttpServletRequest之Request物件（前端到後端）
     * @param response Servlet回傳之HttpServletResponse之Response物件（後端到前端）
     * @throws ServletException the servlet exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void doPut(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        /** 透過JsonReader類別將Request之JSON格式資料解析並取回 */
        JsonReader jsr = new JsonReader(request);
        JSONObject jso = jsr.getObject();
        
        /** 取出經解析到JSONObject之Request參數 */
        int deliveryman_id = jso.getInt("deliveryman_id");
        String password = jso.getString("password");
        String phone = jso.getString("phone");

        /** 透過傳入之參數，新建一個以這些參數之外送員Deliveryman物件 */
        Deliveryman d = new Deliveryman(deliveryman_id, password, phone);
        
        /** 透過Deliveryman物件的update()方法至資料庫更新該名外送員資料，回傳之資料為JSONObject物件 */
        JSONObject data = d.update();
        
        /** 新建一個JSONObject用於將回傳之資料進行封裝 */
        JSONObject resp = new JSONObject();
        resp.put("status", "200");
        resp.put("message", "成功! 更新外送員資料...");
        resp.put("response", data);
        
        /** 透過JsonReader物件回傳到前端（以JSONObject方式） */
        jsr.response(resp, response);
    }
}