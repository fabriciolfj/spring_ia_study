package com.github.iatest.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.anthropic.AnthropicSkillsResponseHelper;
import org.springframework.ai.anthropic.api.AnthropicApi;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatClient chatClient;
    private final AnthropicApi anthropicApi;

    public void requestDocument() throws IOException, URISyntaxException {

        var response = chatClient.prompt()
                .user("texto basico com 1 linha ")
                .options(AnthropicChatOptions.builder()
                        .model("claude-sonnet-4-5")
                        .skill(AnthropicApi.AnthropicSkill.PDF)
                        .maxTokens(300)
                        .build())
                .call()
                .chatResponse();

        final List<String> fileIds = AnthropicSkillsResponseHelper.extractFileIds(response);

        for (String fileId : fileIds) {
            AnthropicApi.FileMetadata metadata = anthropicApi.getFileMetadata(fileId);
            byte[] content = anthropicApi.downloadFile(fileId);

            Path resourcesPath = Paths.get(
                    Objects.requireNonNull(getClass().getClassLoader().getResource("")).toURI()
            );

            Files.write(resourcesPath.resolve(metadata.filename()), content);
        }
    }
}
