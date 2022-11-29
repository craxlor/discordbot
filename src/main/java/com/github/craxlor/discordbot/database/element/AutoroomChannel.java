package com.github.craxlor.discordbot.database.element;

public class AutoroomChannel {
    private long channel_id, trigger_id;

    public AutoroomChannel(long channel_id, long trigger_id) {
        this.channel_id = channel_id;
        this.trigger_id = trigger_id;
    }

    // SETTER
    public void setChannel_id(long channel_id) {
        this.channel_id = channel_id;
    }

    public void setTrigger_id(long trigger_id) {
        this.trigger_id = trigger_id;
    }

    // GETTER
    public long getChannel_id() {
        return channel_id;
    }

    public long getTrigger_id() {
        return trigger_id;
    }

}
