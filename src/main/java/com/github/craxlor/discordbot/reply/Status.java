package com.github.craxlor.discordbot.reply;

public enum Status {
    SUCCESS(true),
    FAIL(false),
    ERROR(false);

    public final boolean success;

    private Status(boolean success) {
        this.success = success;
    }
}
