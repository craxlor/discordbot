package com.github.craxlor.discordbot.command.slash;

import java.util.List;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.Properties;
import com.github.craxlor.discordbot.database.Database;
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
        // check if member is developer
        if (member.getIdLong() == Properties.DEV_ID) {
            return true;
        }
        // check if member is an admin
        Database database = Database.getInstance();
        Long adminId = database.getDiscordServer(guild.getIdLong()).getAdmin_id();
        List<Role> memberRoles = member.getRoles();
        for (Role role : memberRoles) {
            if (adminId == role.getIdLong()) {
                return true;
            }
        }
        return false;
    }

}
