package com.luna.roomserver.websocket.domain.vo.signalling;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class WSCandidate extends WSBaseSignalling{
    private Candidate candidate;

    @Data
    private static class Candidate {
        private String candidate;
        private String sdpMid;
        private String sdpMLineIndex;
        private String usernameFragment;
    }
}
