package com.github.iatest.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.anthropic.AnthropicSkillsResponseHelper;
import org.springframework.ai.anthropic.api.AnthropicApi;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatClient chatClient;
    private final AnthropicApi anthropicApi;

    public void requestDocument() throws IOException {

        var response = chatClient.prompt()
                .user("reate a Word document with our product roadmap")
                .options(AnthropicChatOptions.builder()
                        .model("claude-sonnet-4-5")
                        .skill(AnthropicApi.AnthropicSkill.XLSX)
                        .maxTokens(4096)
                        .build())
                .call()
                .chatResponse();

        final List<String> fileIds = AnthropicSkillsResponseHelper.extractFileIds(response);

        for (String fileId : fileIds) {
            AnthropicApi.FileMetadata metadata = anthropicApi.getFileMetadata(fileId);
            byte[] content = anthropicApi.downloadFile(fileId);

            Files.wriete(Path.of(metadata.filename()), content);
        }
    }
}
