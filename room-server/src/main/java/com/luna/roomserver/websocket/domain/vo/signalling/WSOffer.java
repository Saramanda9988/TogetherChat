package com.luna.roomserver.websocket.domain.vo.signalling;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class WSOffer extends WSBaseSignalling{
    private Description description;

    @Data
    public static class Description {
        public static final String type = "offer";
        private String sdp;
    }
}
