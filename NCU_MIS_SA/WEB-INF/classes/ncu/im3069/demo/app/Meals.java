package ncu.im3069.demo.app;

import org.json.*;
import java.time.LocalDateTime;

public class Meals {
	private int meals_id;
	private int restaurant_id;
	private String name;
	private int price;
	private String image;
	private String description;
	private int kcal;
	private LocalDateTime updatetime;
	
	//新增餐點
	public Meals(int restaurant_id, String name, int price, String image, String description, int kcal) {
		this.restaurant_id = restaurant_id;
		this.name = name;
		this.price = price;
		this.image = image;
		this.description = description;
		this.kcal = kcal;
	}
	
	//修改餐點
	public Meals(int meals_id, int restaurant_id, String name, int price, String image, String description, int kcal) {
		this.meals_id = meals_id;
		this.restaurant_id = restaurant_id;
		this.name = name;
		this.price = price;
		this.image = image;
		this.description = description;
		this.kcal = kcal;
	}
	
	public int getMeals_id() {
		return this.meals_id;
	}
	
	public int getRestaurant_id() {
		return this.restaurant_id;
	}

	public String getName() {
		return this.name;
	}

	public int getPrice() {
		return this.price;
	}

	public String getImage() {
		return this.image;
	}

	public String getDescription() {
		return this.description;
	}
	
	public int getKcal() {
		return this.kcal;
	}
	
	public LocalDateTime getUpdatetime() {
		return this.updatetime;
	}

	//JSONObject 回傳餐點資訊
	public JSONObject getData() {
        //透過JSONObject將該項餐點所需之資料全部進行封裝
        JSONObject jso = new JSONObject();
        jso.put("meals_id", getMeals_id());
        jso.put("restaurant_id", getRestaurant_id());
        jso.put("name", getName());
        jso.put("price", getPrice());
        jso.put("image", getImage());
        jso.put("description", getDescription());
        jso.put("kcal", getKcal());
        jso.put("updatetime", getUpdatetime());
        
        return jso;
    }
}
