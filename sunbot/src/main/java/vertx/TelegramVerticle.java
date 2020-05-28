package vertx;

import java.util.ArrayList;

import org.schors.vertx.telegram.bot.LongPollingReceiver;
import org.schors.vertx.telegram.bot.TelegramBot;
import org.schors.vertx.telegram.bot.TelegramOptions;
import org.schors.vertx.telegram.bot.api.methods.SendMessage;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class TelegramVerticle extends AbstractVerticle{
	
	private static TelegramBot bot;
	private static ArrayList<String> chatid;
	
	@Override
	public void start(Promise<Void> future) {
		chatid = new ArrayList<String>();
		TelegramOptions telegramOptions = new TelegramOptions()
				.setBotName("SunbotBOT")
				.setBotToken("797617261:AAGvcVcNnJQDmvtdUa3iNJBGMna0hpzCQ_Q");
		bot = TelegramBot.create(vertx, telegramOptions)
				.receiver(new LongPollingReceiver().onUpdate(handler -> {
					if (handler.getMessage().getText().toLowerCase().equals("/avisa")) {
						bot.sendMessage(new SendMessage()
								.setText("Entendido " + handler.getMessage().getFrom().getFirstName() + ". Te avisaré cuando la humedad sea baja.")
								.setChatId(handler.getMessage().getChatId()));
						chatid.add(handler.getMessage().getChatId());
					} else if(handler.getMessage().getText().toLowerCase().equals("/noavisa")) {
						if(chatid.contains(handler.getMessage().getChatId())) {
							bot.sendMessage(new SendMessage()
									.setText("Entendido " + handler.getMessage().getFrom().getFirstName() + ". No recibirás más avisos.")
									.setChatId(handler.getMessage().getChatId()));
							chatid.remove(handler.getMessage().getChatId());
						}
					} else if (handler.getMessage().getText().toLowerCase().contentEquals("/test")) {	
						bot.sendMessage(new SendMessage()
								.setText("//\\ AVISO: funsiona")
								.setChatId(handler.getMessage().getChatId()));
					}
				}));
		
		bot.start();
	}
	
	public static void sendMessage(String h) {
		for(String s: chatid) {
			bot.sendMessage(new SendMessage().setText("Aviso: La humedad está a niveles bajos. ["+h+"]").setChatId(s));
		}
	}	
}
