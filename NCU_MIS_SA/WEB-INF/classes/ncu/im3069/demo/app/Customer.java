package ncu.im3069.demo.app;

import org.json.*;

public class Customer {
    
    private int customer_id;
    private String name;
    private String email;
    private String password;
    private String address;
    private String phone;
    
    /** ch，CustomerHelper之物件與Customer相關之資料庫方法（Sigleton） */
    private CustomerHelper ch =  CustomerHelper.getHelper();
    
    /**doPost()會用到
     * 實例化（Instantiates）一個新的（new）Customer物件<br>
     * 採用多載（overload）方法進行，此建構子用於建立會員資料時，產生一名新的會員
     */
    public Customer(String name, String email, String password, String address, String phone) {
        this.name = name;
    	this.email = email;
        this.password = password;
        this.address = address;
        this.phone = phone;
        update();
    }
    
    /**doGet()會用到
     * 實例化（Instantiates）一個新的（new）Customer物件<br>
     * 採用多載（overload）方法進行，此建構子用於"查詢"會員資料時，將每一筆資料新增為一個會員物件
     */
    public Customer(int customer_id, String name, String email, String password, String address, String phone) {
        this.customer_id = customer_id;
        this.name = name;
    	this.email = email;
        this.password = password;
        this.address = address;
        this.phone = phone;
    }
    
	/**doPut()會用到
     * 實例化（Instantiates）一個新的（new）Customer物件
     * 採用多載（overload）方法進行，此建構子用於"更新"會員資料時，產生一名會員
     */
	public Customer(int customer_id, String password, String address, String phone) {
		this.customer_id = customer_id;
        this.password = password;
        this.address = address;
        this.phone = phone;
	}

    public int getID() {
        return this.customer_id;
    }
    
    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }
    
    public String getAddress() {
        return this.address;
    }
    
    public String getPhone() {
        return this.phone;
    }

    /**
     * 更新會員資料
     *
     * @return the JSON object 回傳SQL更新之結果與相關封裝之資料
     */
    public JSONObject update() {
        /** 新建一個JSONObject用以儲存更新後之資料 */
        JSONObject data = new JSONObject();
        /** 檢查該名會員是否已經在資料庫 */
        if(this.customer_id != 0) {
            data = ch.update(this);
        }
        
        return data;
    }
    
    /**
     * 取得該名會員所有資料
     *
     * @return the data 取得該名會員之所有資料並封裝於JSONObject物件內
     */
    public JSONObject getData() {
        /** 透過JSONObject將該名會員所需之資料全部進行封裝*/ 
        JSONObject jso = new JSONObject();
        jso.put("customer_id", getID());
        jso.put("name", getName());
        jso.put("email", getEmail());
        jso.put("password", getPassword());
        jso.put("address", getAddress());
        jso.put("phone", getPhone());
        
        return jso;
    }
}