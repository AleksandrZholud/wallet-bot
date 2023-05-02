package telegrambot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import telegrambot.config.telegram.banner.MazhoBotBanner;

@SpringBootApplication
public class TelegramBotApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(TelegramBotApplication.class);
        app.setBanner(new MazhoBotBanner());
        app.run(args);
    }
}