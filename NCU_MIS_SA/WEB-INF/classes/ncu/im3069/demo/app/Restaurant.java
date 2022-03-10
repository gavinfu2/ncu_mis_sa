package ncu.im3069.demo.app;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import org.json.*;

public class Restaurant {
	private int restaurant_id=1;
	private String name="Spice Mystry";
	private String phone="034948811";
	private String address="8188 Brecksville Rd, Brecksville, OH";
	private String email = "SpiceMystry@gmail.com";
	private String password = "a1234";
	
	public String getEmail() {
		return email;
	}
	public String getPassword() {
		return password;
	}
	
}
