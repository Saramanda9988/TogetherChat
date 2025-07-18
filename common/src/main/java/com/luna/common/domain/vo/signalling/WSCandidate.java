package com.luna.common.domain.vo.signalling;

import com.luna.common.domain.vo.WSBaseSignalling;
import lombok.Data;

@Data
public class WSCandidate extends WSBaseSignalling {
    private Candidate candidate;

    @Data
    private static class Candidate {
        private String candidate;
        private String sdpMid;
        private String sdpMLineIndex;
        private String usernameFragment;
    }
}
