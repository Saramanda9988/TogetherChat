package com.luna.common.domain.vo.signalling;

import com.luna.common.domain.vo.WSBaseSignalling;
import lombok.Data;

@Data
public class WSOffer extends WSBaseSignalling {
    private Description description;

    @Data
    public static class Description {
        public static final String type = "offer";
        private String sdp;
    }
}
