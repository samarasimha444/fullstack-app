package com.example.backend.Service;
import com.example.backend.Entity.Message;
import com.example.backend.Repository.MessageRepository;
import org.springframework.stereotype.Service;
import java.util.Map;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    //chat history between two users

public List<Message> getChatHistory(String user1, String user2) {

        return messageRepository
                .findBySenderAndReceiverOrSenderAndReceiverOrderByTimestampAsc(
                        user1, user2,
                        user2, user1
                );
    }


    //recent chats for a user

     public List<Message> getRecentChats(String currentUser) {

        List<Message> messages =
                messageRepository.findBySenderOrReceiverOrderByTimestampDesc(
                        currentUser,
                        currentUser
                );

        Map<String, Message> latestByUser = new LinkedHashMap<>();

        for (Message m : messages) {

            String otherUser =
                    m.getSender().equals(currentUser)
                            ? m.getReceiver()
                            : m.getSender();

            // Because messages are sorted DESC,
            // first occurrence is the latest conversation
            if (!latestByUser.containsKey(otherUser)) {
                latestByUser.put(otherUser, m);
            }
        }

        return new ArrayList<>(latestByUser.values());
    }

}