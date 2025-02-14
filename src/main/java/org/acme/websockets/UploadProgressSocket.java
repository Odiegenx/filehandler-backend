package org.acme.websockets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.websocket.server.PathParam;
import org.acme.DTO.UploadProgressDTO;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/upload-progress/{uploadId}")
@ApplicationScoped
public class UploadProgressSocket {

    @Inject
    ObjectMapper objectMapper;

    private Map<String,Session> sessions = new ConcurrentHashMap<>();



    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        sessions.put(userId, session);
    }

    @OnClose
    public void onClose(Session session, @PathParam("userId") String userId) {
        sessions.remove(userId);
    }

    public void sendProgress(String uploadId, UploadProgressDTO progress) throws Exception {
        Session session = sessions.get(uploadId);
        if (session != null && session.isOpen()) {
            try {
                String json = objectMapper.writeValueAsString(progress);
                session.getAsyncRemote().sendObject(json, result -> {
                    if (result.getException() != null) {
                        System.out.println("Unable to send progress" + result.getException());
                    }
                });
            } catch (Exception e) {
                throw new Exception("Unable to send progress", e);
            }
        }
    }

}
