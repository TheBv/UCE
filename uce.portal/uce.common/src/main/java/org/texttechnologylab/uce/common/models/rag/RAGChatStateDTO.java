package org.texttechnologylab.uce.common.models.rag;

import lombok.*;
import org.joda.time.DateTime;
import org.texttechnologylab.uce.common.config.uceConfig.RAGModelConfig;
import org.texttechnologylab.uce.common.utils.SupportedLanguages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RAGChatStateDTO {
    private UUID chatId;
    private RAGModelConfig model;
    private DateTime started;
    private @Singular List<RAGChatMessage> messages;
    private SupportedLanguages language;

    public void setModel(RAGModelConfig model) {
        if (model != null) {
            // Dont leak API keys or internal URLs
            this.model = new RAGModelConfig();
            this.model.setModel(model.getModel());
            this.model.setDisplayName(model.getDisplayName());
            this.model.setStreaming(model.isStreaming());
        }
        else {
            this.model = null;
        }
    }

    public void setMessages(ArrayList<RAGChatMessage> messages) {
        this.messages = messages;
        if (this.messages != null) {
            // NOTE remove the documents for now, could be useful later though
            for (RAGChatMessage message : this.messages) {
                message.setContextDocuments(new ArrayList<>());
            }
        }
    }

    public static RAGChatStateDTO fromRAGChatState(RAGChatState ragChatState) {
        RAGChatStateDTO dto = new RAGChatStateDTO();
        dto.setChatId(ragChatState.getChatId());
        dto.setModel(ragChatState.getModel());
        dto.setStarted(ragChatState.getStarted());
        dto.setMessages(ragChatState.getMessages());
        dto.setLanguage(ragChatState.getLanguage());
        return dto;
    }

    public static HashMap<String, Object> toHashMap(RAGChatState dto) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("chatId", dto.getChatId().toString());
        map.put("model", dto.getModel() != null ? dto.getModel().getModel() : null);
        map.put("started", dto.getStarted().toString());
        map.put("language", dto.getLanguage() != null ? dto.getLanguage().name() : null);
        // Messages
        ArrayList<HashMap<String, Object>> messagesList = new ArrayList<>();
        if (dto.getMessages() != null) {
            for (RAGChatMessage message : dto.getMessages()) {
                HashMap<String, Object> messageMap = new HashMap<>();
                messageMap.put("role", message.getRole().name());
                messageMap.put("message", message.getMessage());
                messageMap.put("created", message.getCreated());
                messageMap.put("images", message.getImages());
                messageMap.put("contextDocuments", message.getContextDocuments());
                messageMap.put("done", message.isDone());
                messageMap.put("contextDocument_Ids", message.getContextDocument_Ids());
                // Context documents are omitted for privacy/security reasons
                messagesList.add(messageMap);
            }
        }
        map.put("messages", messagesList);
        return map;
    }
}
