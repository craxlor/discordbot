package com.github.craxlor.discordbot.command.module.core.command.slash;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.command.slash.SCAdmin;
import com.github.craxlor.discordbot.database.Database;
import com.github.craxlor.discordbot.database.element.DiscordServer;
import com.github.craxlor.discordbot.util.reply.Reply;
import com.github.craxlor.discordbot.util.reply.Status;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class Role extends SCAdmin {

	private static final String ADMIN_NAME = "admin";
	private static final String ADMIN_DESCRIPTION = "Sets the Admin role.";
	private static final String DJ_NAME = "dj";
	private static final String DJ_DESCRIPTION = "Sets the DJ role.";
	private static final String OPT_NAME = "role";
	private static final String OPT_DESCRIPTION = "Select the role matching your choice.";

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
		String subcommandName = event.getSubcommandName();
		String msg = "";
		Status status = Status.ERROR;
		net.dv8tion.jda.api.entities.Role role = event.getOption(OPT_NAME).getAsRole();
		Database database = Database.getInstance();
		DiscordServer discordServer = database.getDiscordServer(event.getGuild().getIdLong());
		switch (subcommandName) {
			case ADMIN_NAME -> {
				discordServer.setAdmin_id(role.getIdLong());
				msg = role.getAsMention() + " has been set as Admin role.";
				status = Status.SUCCESS;
			}
			case DJ_NAME -> {
				discordServer.setDj_id(role.getIdLong());
				msg = role.getAsMention() + " has been set as DJ role.";
				status = Status.SUCCESS;
			}
		}
		database.update(discordServer);
		

		return new Reply(event.deferReply(), false).onCommand(event, status, msg);
	}

	@Override
	public boolean isGuildOnly() {
		return false;
	}

}
