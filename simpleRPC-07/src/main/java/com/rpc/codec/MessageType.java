package com.rpc.codec;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum MessageType {
    REQUEST(0),RESPONSE(1);

    private int code;

    public int getCode() {
        return code;
    }
}
