package com.example.gbsports.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class ZaloPayConfig {
	@Value("${zalopay.app_id}")
	public String APP_ID;

	@Value("${zalopay.key1}")
	public String KEY1;

	@Value("${zalopay.key2}")
	public String KEY2;

	@Value("${zalopay.create_order_url}")
	public String CREATE_ORDER_URL;

	@Value("${zalopay.get_status_pay_url}")
	public String GET_STATUS_PAY_URL;

	@Value("${zalopay.redirect_url}")
	public String REDIRECT_URL;

	private String endpoint = "https://sandbox.zalopay.com.vn";

//	APP_ID=15555
//	PAYMENT_ID=CASHIN
//			KEY1=2uFWRt7qFGdUNjwBQb4DtEia3QOHJmoO
//			PRIVATE_KEY="MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAuhr9fssauZOaef4HCEJ4OAJQ6g8y\
//	O8de5KwB1LM/fIlRZGsnD0VO+YBGrdttnKsieErQPujmyV7Tnw19yLVGGwIDAQABAkEAgpPzbyZU\
//	rwbIqXW6O2pf7XR6j29wII9nnmytsC7AicCd2uGAd+yHKEOQGEHBN+rm/IZ8F5WWT2OpnOTY3DZT\
//	gQIhAPyzS24ahh0ogYuDy4VXsiLscEAngrAvZA5qpWWPFV9BAiEAvIkWcJzM9kaJ2YjoNcGvO3yd\
//	DsepNeC79dfIA7tL6lsCIELaRChayARKxQrd0SfzrWLj3kZ6rW5i+zt9J0iY8/SBAiEAsw53c2hX\
//			+KWxkhpGf5d9dz+4YisZ94OCv8+5tGGTjfUCIEs9pi1DsVZBi0HNibXIpVBO4KERvHBJ92bAbPep\
//	lIb2"
}
