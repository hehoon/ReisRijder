package reisrijder.reisrijder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootApplication
public class ReisRijderApplication {

	public static final String OCP_APIM_SUBSCRIPTION_KEY = "ENTER-YOUR-API-KEY-HERE";

	public static void main(String[] args) {
		Locale.setDefault(Locale.ENGLISH);

		Pattern pattern = Pattern.compile("[^0-9a-f]");
		Matcher matcher = pattern.matcher(OCP_APIM_SUBSCRIPTION_KEY);
		if (matcher.find()) {
			System.err.println("OCP_APIM_SUBSCRIPTION_KEY is invalid!");
			System.out.println("Please enter an API KEY in ReisRijderApplication!");
			return;
		}

		SpringApplication.run(ReisRijderApplication.class, args);
	}
}