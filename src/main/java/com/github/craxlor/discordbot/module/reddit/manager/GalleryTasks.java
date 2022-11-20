package com.github.craxlor.discordbot.module.reddit.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.github.craxlor.discordbot.manager.GuildManager;
import com.github.craxlor.discordbot.manager.json.GuildConfig;
import com.github.craxlor.discordbot.module.reddit.util.GalleryTask;

import net.dv8tion.jda.api.entities.Guild;

public class GalleryTasks extends ArrayList<GalleryTask> {

    private Guild guild;
    private static int HOUR = 3600000;
    // private static int SECOND10 = 10000;
    private static Logger logger;
    private Timer timer;

    public GalleryTasks(Guild guild) {
        this.guild = guild;
        timer = new Timer();

        MDC.put("filename", "reddit");
        logger = LoggerFactory.getLogger("sift");
    }

    public void reloadTasks() {
        GuildConfig config = GuildManager.getGuildManager(guild).getGuildConfig();
        JSONArray galleries = config.getRedditGalleries();
        if (galleries == null)
            return;
        clear();
        for (Object o : galleries) {
            JSONObject gallery = (JSONObject) o;
            GalleryTask galleryTask = new GalleryTask((String) gallery.get("name"),
                    guild.getTextChannelById((Long) gallery.get("channel-id")));
            add(galleryTask);
            logger.info("scheduled galleryTask" +
                    "\nGuild: " + guild.getName() +
                    "\nGallery: " + gallery.get("name"));
            timer.schedule(galleryTask, nextFullHour(), HOUR);
            // timer.schedule(galleryTask, nextFullMinute(), SECOND10);
        }
    }

    @Override
    public void clear() {
        super.clear();
        timer.cancel();
        for (GalleryTask galleryTask : this) {
            galleryTask.cancel();
        }
        logger.info("canceled timer and therefore all scheduled tasks.\ninstantiating new timer");
        timer = new Timer();
    }

    /**
     * Next full hour.
     *
     * @return Date today's next full hour
     */
    protected Date nextFullHour() {
        final Calendar calendar = Calendar.getInstance();
        final int hour = calendar.get(Calendar.HOUR_OF_DAY) + 1;
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * Next full minute.
     *
     * @return the date
     */
    protected Date nextFullMinute() {
        final Calendar calendar = Calendar.getInstance();
        final int minute = calendar.get(Calendar.MINUTE) + 1;
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}
