package com.github.craxlor.discordbot.command.slash;

import java.util.List;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.Properties;
import com.github.craxlor.discordbot.manager.GuildManager;
import com.github.craxlor.discordbot.manager.json.GuildConfig;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;

public abstract class SCAdmin extends SlashCommand {

    public SCAdmin() {
        commandData.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR));
    }

    @Override
    public boolean memberHasPermission(@Nonnull Member member, @Nonnull Guild guild) {
        GuildConfig config = (GuildConfig) GuildManager.getGuildManager(guild).getGuildConfig();
        // check if member is developer
        if (member.getIdLong() == Long.parseLong(Properties.get("DEVELOPER_ID"))) {
            return true;
        }
        // check if member is an admin
        Role admin = config.getAdminRole();
        List<Role> memberRoles = member.getRoles();
        for (Role role : memberRoles) {
            if (admin.getIdLong() == role.getIdLong()) {
                return true;
            }
        }
        return false;
    }

}
