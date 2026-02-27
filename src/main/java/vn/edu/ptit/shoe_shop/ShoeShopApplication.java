package vn.edu.ptit.shoe_shop;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // <-- Kích hoạt tự động lên lịch
@EnableCaching
public class ShoeShopApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShoeShopApplication.class, args);
	}

}
