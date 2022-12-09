package com.github.craxlor.discordbot.command.slash;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.Properties;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;

public abstract class SCDev extends SlashCommand {

    public SCDev() {
        commandData.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR));
    }

    @Override
    public boolean memberHasPermission(@Nonnull Member member,@Nonnull  Guild guild) {
        return member.getIdLong() == Properties.DEV_ID;
    }

}
