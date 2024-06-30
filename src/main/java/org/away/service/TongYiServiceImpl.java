package org.away.service;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.StreamingChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

@Service
public class TongYiServiceImpl implements AbstractTongyiService {

    private final ChatClient chatClient;

    private final StreamingChatClient streamingChatClient;

    @Autowired
    public TongYiServiceImpl(ChatClient chatClient, StreamingChatClient streamingChatClient) {
        this.chatClient = chatClient;
        this.streamingChatClient = streamingChatClient;
    }

    @Override
    public String completion(String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return chatClient.call(prompt).getResult().getOutput().getContent();
    }

    @Override
    public Map<String, String> streamCompletion(String messsage) {
        StringBuilder stringBuilder = new StringBuilder();
        streamingChatClient.stream(new Prompt(messsage))
                .flatMap(chatResponse -> Flux.fromIterable(chatResponse.getResults()))
                .map(content -> content.getOutput().getContent())
                .doOnNext(stringBuilder::append)
                .last()
                .map(lastContent -> {
                    HashMap<Object, Object> res = new HashMap<>(1);
                    res.put(messsage, stringBuilder.toString());
                    return res;
                })
                .block();
//        log.info("streamCompletion: {}", stringBuilder.toString());
        Map<String, String> res1 = new HashMap<>(1);
        res1.put(messsage, stringBuilder.toString());
        return res1;
    }
}
