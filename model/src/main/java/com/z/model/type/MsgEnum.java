package com.z.model.type;

public enum MsgEnum {
    ins;
    public enum MsgType {
        USR(1),GAME(2)
        ;
        private int key;

        MsgType(int key) {
            this.key = key;
        }
    }
}
