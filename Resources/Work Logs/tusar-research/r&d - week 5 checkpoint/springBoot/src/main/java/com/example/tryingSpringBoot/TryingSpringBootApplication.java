/**
 * @author Tusar Paudel
 */

package com.example.tryingSpringBoot;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.content.Media;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@SpringBootApplication
@RestController
public class TryingSpringBootApplication {

	private ChatClient chatClient;
	private static final String STEP = "stepfun/step-3.5-flash:free";
	private static final String NVIDIA_NEMOTRON_3 = "nvidia/nemotron-3-super-120b-a12b:free";
	private static final String NVIDIA_NEMOTRON_NANO = "nvidia/nemotron-nano-12b-v2-vl:free";

	public TryingSpringBootApplication(ChatClient.Builder builder) {
		this.chatClient = builder.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(TryingSpringBootApplication.class, args);
	}

	@PostMapping(value = "/chat-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String chat(@RequestParam String message, @RequestParam MultipartFile image) throws Exception {
		// returns LLM response based on the query given on the URL according to the system prompts listed
		Media imageMedia = new Media(
				MimeTypeUtils.parseMimeType(image.getContentType()),
				image.getResource()
		);
		UserMessage userMessage = UserMessage.builder()
				.text(message)
				.media(imageMedia)
				.build();
		return chatClient.prompt()
				.system("Reply with a single word") //adding system prompts for set outputs
				.messages(userMessage)
				.options(OpenAiChatOptions.builder()
						.model(NVIDIA_NEMOTRON_NANO) // model selection
						.build())
				.call()
				.content();
	}
}