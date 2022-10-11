package com.github.craxlor.discordbot.module.core.command.slash;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.command.slash.SCAdmin;
import com.github.craxlor.discordbot.manager.GuildManager;
import com.github.craxlor.discordbot.manager.json.GuildConfig;
import com.github.craxlor.discordbot.reply.Reply;
import com.github.craxlor.discordbot.reply.Status;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

//TODO DJ role has to be added with the music package, not in the core package
public class Role extends SCAdmin {

	private static final String ADMIN_NAME = "admin";
	private static final String ADMIN_DESCRIPTION = "roles";
	private static final String DJ_NAME = "dj";
	private static final String DJ_DESCRIPTION = "roles";
	private static final String OPT_NAME = "role";
	private static final String OPT_DESCRIPTION = "role desc";

	public Role() {
		SubcommandData admin = new SubcommandData(ADMIN_NAME, ADMIN_DESCRIPTION);
		SubcommandData dj = new SubcommandData(DJ_NAME, DJ_DESCRIPTION);
		OptionData roleOption = new OptionData(OptionType.ROLE, OPT_NAME, OPT_DESCRIPTION, true);
		admin.addOptions(roleOption);
		dj.addOptions(roleOption);
		commandData.addSubcommands(admin, dj);
	}

	@Override
	@Nonnull
	public String getName() {
		return "role";
	}

	@Override
	@Nonnull
	public String getDescription() {
		return "configurate the bot";
	}

	@Override
	@SuppressWarnings("null")
	public Reply execute(@Nonnull SlashCommandInteractionEvent event) {
		GuildConfig config = (GuildConfig) GuildManager.getGuildManager(event.getGuild()).getGuildConfig();
		String subcommandName = event.getSubcommandName();
		String msg = "";
		net.dv8tion.jda.api.entities.Role role = event.getOption(OPT_NAME).getAsRole();
		switch (subcommandName) {
			case ADMIN_NAME -> {
				config.setAdminRole(role.getIdLong());
				msg = role.getAsMention() + " has been set as admin";
			}
			case DJ_NAME -> {
				config.setDJRole(role.getIdLong());
				msg = role.getAsMention() + " has been set as dj";
			}
		}
		return new Reply(event.deferReply(), false).onCommand(event, Status.SUCCESS, msg);
	}

	@Override
	public boolean isGuildOnly() {
		return false;
	}

}
