package com.luna.userserver.websocket.domain.vo.signalling;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class WSAnswer extends WSBaseSignalling{
    private WSOffer.Description description;

    @Data
    public static class Description {
        public static final String type = "answer";
        private String sdp;
    }
}
