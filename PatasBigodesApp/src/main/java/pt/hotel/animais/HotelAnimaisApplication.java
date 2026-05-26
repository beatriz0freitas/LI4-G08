package pt.hotel.animais;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HotelAnimaisApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelAnimaisApplication.class, args);
    }
}
