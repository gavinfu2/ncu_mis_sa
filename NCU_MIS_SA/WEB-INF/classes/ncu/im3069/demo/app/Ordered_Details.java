package ncu.im3069.demo.app;

import java.time.LocalDateTime;

import org.json.JSONObject;

import ncu.im3069.demo.util.Arith;

public class Ordered_Details {
	private int order_details_id;
	private int order_id;
	private int meals_id;
	private int unit_price;
	private int subtotal_price;
	private int count;
	private int unit_kcal;
	private int subtotal_kcal;
	
	private Meals meals;
	private MealsHelper mh =  MealsHelper.getHelper();
	
	//建立訂單細項
	public Ordered_Details(Meals meals, int count) {
        this.meals = meals;
        this.meals_id = meals.getMeals_id();
        
        this.unit_price = this.meals.getPrice();
        this.unit_kcal = this.meals.getKcal();
        this.count = count;
        
        this.subtotal_price = count * unit_price;;
        this.subtotal_kcal = count * unit_kcal;
	}

	public Ordered_Details(int order_id, Meals meals, int count) {
		this.order_id = order_id;
        this.meals = meals;
        this.meals_id = meals.getMeals_id();
        
        this.unit_price = this.meals.getPrice();
        this.unit_kcal = this.meals.getKcal();
        this.count = count;
        
        this.subtotal_price = count * unit_price;;
        this.subtotal_kcal = count * unit_kcal;
	}
	
	//修改訂單細項
    public Ordered_Details(int order_id, int meals_id, int count) {
        this.order_id = order_id;
        this.meals_id = meals_id;
        this.count = count;
        getMealsFromDB(meals_id);
        
        this.unit_price = this.meals.getPrice();
        this.unit_kcal = this.meals.getKcal();
        
        this.subtotal_price = count * unit_price;
        this.subtotal_kcal = count * unit_kcal;
    }
    
	public Ordered_Details(int order_details_id, int order_id, int meals_id, int unit_price, int subtotal_price, int count, int unit_kcal, int subtotal_kcal) {
		this.order_details_id = order_details_id;
		this.order_id = order_id;
        this.meals_id = meals_id;
        this.unit_price = unit_price;
        this.subtotal_price = subtotal_price;
        this.count = count;
        this.unit_kcal = unit_kcal;
        this.subtotal_kcal = subtotal_kcal;
        
        getMealsFromDB(meals_id);
	}
    
    public void setOrder_details_id(int order_details_id) {
        this.order_details_id = order_details_id;
    }
	
    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }
    
    public void setMeals_id(int meals_id) {
        this.meals_id = meals_id;
        getMealsFromDB(meals_id);
  
        this.unit_price = this.meals.getPrice();
        this.unit_kcal = this.meals.getKcal();
        this.subtotal_price = count * unit_price;
        this.subtotal_kcal = count * unit_kcal;
    }
    
    public void setCount(int count) {
        this.count = count;
        
        this.unit_price = this.meals.getPrice();
        this.unit_kcal = this.meals.getKcal();
        this.subtotal_price = count * unit_price;
        this.subtotal_kcal = count * unit_kcal;
    }
    
    //從 DB 中取得餐點
    private void getMealsFromDB(int meals_id) {
        String id = String.valueOf(meals_id);
        this.meals = mh.getById(id);
    }
    
    public Meals getMeals() {
        return this.meals;
    }
    
    public int getOrder_details_id() {
    	return this.order_details_id;
    }
    
    public int getOrder_id() {
        return this.order_id;
    }
    
    public int getMeals_id() {
        return this.meals_id;
    }
    
    public int getUnit_price() {
        return this.unit_price;
    }
    
    public int getSubtotal_price() {
        return this.subtotal_price;
    }
    
    public int getCount() {
        return this.count;
    }
    
    public int getUnit_kcal() {
        return this.unit_kcal;
    }
    
    public int getSubtotal_kcal() {
        return this.unit_kcal;
    }
    
	//JSONObject 回傳訂單明細資訊
	public JSONObject getData() {
        //透過JSONObject將該項訂單明細所需之資料全部進行封裝
        JSONObject jso = new JSONObject();
        jso.put("order_id", getOrder_id());
        jso.put("meals_id", getMeals_id());
        jso.put("unit_price", getUnit_price());
        jso.put("subtotal_price", getSubtotal_price());
        jso.put("count", getCount());
        jso.put("unit_kcal", getUnit_kcal());
        jso.put("subtotal_kcal", getSubtotal_kcal());
        
        return jso;
    }
}
