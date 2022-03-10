package ncu.im3069.demo.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import ncu.im3069.demo.app.MealsHelper;
import ncu.im3069.tools.JsonReader;

/**
 * Servlet implementation class MealsController
 */
@WebServlet("/api/meals.do")
public class MealsController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private MealsHelper mh =  MealsHelper.getHelper();
	
    public MealsController() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/** 透過JsonReader類別將Request之JSON格式資料解析並取回 */
        JsonReader jsr = new JsonReader(request);
        /** 若直接透過前端AJAX之data以key=value之字串方式進行傳遞參數，可以直接由此方法取回資料 */
        String id_list = jsr.getParameter("id_list");

        JSONObject resp = new JSONObject();
        /** 判斷該字串是否存在，若存在代表要取回購物車內產品之資料，否則代表要取回全部資料庫內產品之資料 */
        if (!id_list.isEmpty()) {
          JSONObject query = mh.getByIdList(id_list);
          resp.put("status", "200");
          resp.put("message", "所有購物車之餐點資料取得成功");
          resp.put("response", query);
        }
        else {
          JSONObject query = mh.getAll();

          resp.put("status", "200");
          resp.put("message", "所有餐點資料取得成功");
          resp.put("response", query);
        }

        jsr.response(resp, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
