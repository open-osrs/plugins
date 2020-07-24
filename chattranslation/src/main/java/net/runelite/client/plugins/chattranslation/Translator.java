package net.runelite.client.plugins.chattranslation;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MessageNode;
import net.runelite.api.ScriptID;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.events.ChatboxInput;
import net.runelite.http.api.RuneLiteAPI;
import okhttp3.Request;
import okhttp3.Response;

@RequiredArgsConstructor(
	access = AccessLevel.PRIVATE,
	onConstructor_ = @Inject
)
@Slf4j
final class Translator
{
	private static final String BASE_URL = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=";
	private static final String SOURCE = "auto";
	private static final String CENT_URL = "&tl=";
	private static final String LAST_URL = "&dt=t&q=";

	private final Client client;
	private final ChatMessageManager chatMessageManager;

	@Getter(AccessLevel.PACKAGE)
	private boolean sending = false;
	private String incomingUrlBase;
	private String outgoingUrlBase;

	void setInLang(Languages lang)
	{
		incomingUrlBase = BASE_URL + SOURCE + CENT_URL + lang.toShortString() + LAST_URL;
	}

	void setOutLang(Languages lang)
	{
		outgoingUrlBase = BASE_URL + SOURCE + CENT_URL + lang.toShortString() + LAST_URL;
	}

	void translateIncoming(final ChatMessage chatMessage)
	{
		final String url = incomingUrlBase + URLEncoder.encode(chatMessage.getMessage(), StandardCharsets.UTF_8);
		translate(url).subscribe(
			str -> updateMessage(chatMessage, str),
			err -> log.warn("Error translating incoming message: {}", err.getMessage()),
			client::refreshChat
		);
	}

	void translateOutgoing(ChatboxInput message)
	{
		if (sending)
		{
			return;
		}
		sending = true;

		final String mes = message.getValue();
		final String url = outgoingUrlBase + URLEncoder.encode(mes.startsWith("/") ? mes.substring(1) : mes, StandardCharsets.UTF_8);

		translate(url).subscribe(
			str -> sendMessage(str, message.getChatType()),
			err -> {
				log.warn("Error translating outgoing message: {}", err.getMessage());
				sending = false;
			},
			() -> sending = false
		);
	}

	private Observable<String> translate(String url)
	{
		final Request request = new Request.Builder()
			.header("User-Agent", "Mozilla/5.0")
			.url(url)
			.build();

		return Observable.fromCallable(() -> {
			try (final Response response = RuneLiteAPI.CLIENT.newCall(request).execute())
			{
				return parseResult(Objects.requireNonNull(response.body()).string());
			}
		}).subscribeOn(Schedulers.io())
			.observeOn(Schedulers.single());// Observe on client thread
	}

	private String parseResult(String inputJson)
	{
		String result;
		JsonArray jsonArray = JsonParser.parseString(inputJson).getAsJsonArray();
		JsonArray jsonArray2 = jsonArray.get(0).getAsJsonArray();
		JsonArray jsonArray3 = jsonArray2.get(0).getAsJsonArray();
		result = jsonArray3.get(0).toString();

		return result.substring(1, result.length() - 1);
	}

	private void updateMessage(ChatMessage cm, String str)
	{
		final MessageNode mn = cm.getMessageNode();
		mn.setRuneLiteFormatMessage(str);
		chatMessageManager.update(mn);
	}

	private void sendMessage(String str, int chatType)
	{
		client.runScript(ScriptID.CHATBOX_INPUT,
			chatType == 2 ? '/' + str : str,
			chatType
		);
	}
}
