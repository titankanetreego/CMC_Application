package com.example.cmc_application.comm;

import com.example.cmc_application.S;
import com.example.cmc_application.util.Util;

public class ReceivedMessage implements IMessage {

    private String command;
    private String message;
    private String currentTime;

    public ReceivedMessage(String command) {
        this.command = command;
        this.currentTime = Util.getNowString(Util.FORMAT_FULL_DATETIME);
        this.message = Util.getNowString(Util.FORMAT_FULL_DATETIME) + "    收到命令：" + command;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getCurrentTime() {
        return currentTime;
    }

    @Override
    public String getCommand() {
        return command;
    }

    @Override
    public String getAction() {
        return S.ACTION_RECEIVED;
    }

}
