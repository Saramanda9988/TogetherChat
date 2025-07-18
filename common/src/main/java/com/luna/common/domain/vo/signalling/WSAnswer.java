package com.luna.common.domain.vo.signalling;

import com.luna.common.domain.vo.WSBaseSignalling;
import lombok.Data;

@Data
public class WSAnswer extends WSBaseSignalling {
    private WSOffer.Description description;

    @Data
    public static class Description {
        public static final String type = "answer";
        private String sdp;
    }
}
