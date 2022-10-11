package com.github.craxlor.discordbot.module.autoroom.command.slash;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.command.slash.SCAdmin;
import com.github.craxlor.discordbot.manager.GuildManager;
import com.github.craxlor.discordbot.manager.json.GuildConfig;
import com.github.craxlor.discordbot.reply.Reply;
import com.github.craxlor.discordbot.reply.Status;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class Setup extends SCAdmin {

	private static final String CREATE_NAME = "create";
	private static final String CREATE_DESCRIPTION = "autoroom";
	private static final String CREATE_OPT_NAME_NAME = "name";
	private static final String CREATE_OPT_NAME_DESCRIPTION = "autoroom";
	private static final String CREATE_OPT_TRIGGER_NAME = "trigger";
	private static final String CREATE_OPT_TRIGGER_DESCRIPTION = "autoroom";
	private static final String CREATE_OPT_CATEGORY_NAME = "category";
	private static final String CREATE_OPT_CATEGORY_DESCRIPTION = "autoroom";
	private static final String CREATE_OPT_PARENT_NAME = "parent";
	private static final String CREATE_OPT_PARENT_DESCRIPTION = "autoroom";
	public static final String CHOICE_TRIGGER = "trigger";
	public static final String CHOICE_CATEGORY = "category";
	private static final String REMOVE_NAME = "remove";
	private static final String REMOVE_DESCRIPTION = "autoroom";
	private static final String REMOVE_OPT_NAME = "channel";
	private static final String REMOVE_OPT_DESCRIPTION = "autoroom";

	public Setup() {
		SubcommandData create = new SubcommandData(CREATE_NAME, CREATE_DESCRIPTION);
		// SubcommandData edit = new SubcommandData(STATICS.DVC_SUB_EDIT[0],
		// STATICS.DVC_SUB_EDIT[1]);
		SubcommandData remove = new SubcommandData(REMOVE_NAME, REMOVE_DESCRIPTION);
		// create options
		OptionData createOptionName = new OptionData(OptionType.STRING, CREATE_OPT_NAME_NAME,
				CREATE_OPT_NAME_DESCRIPTION, true);
		OptionData createOptionTrigger = new OptionData(OptionType.CHANNEL, CREATE_OPT_TRIGGER_NAME,
				CREATE_OPT_TRIGGER_DESCRIPTION, true);
		OptionData createOptionCategory = new OptionData(OptionType.CHANNEL, CREATE_OPT_CATEGORY_NAME,
				CREATE_OPT_CATEGORY_DESCRIPTION, true);
		OptionData createOptionParent = new OptionData(OptionType.STRING, CREATE_OPT_PARENT_NAME,
				CREATE_OPT_PARENT_DESCRIPTION, true);
		createOptionParent.addChoice(CHOICE_TRIGGER, CHOICE_TRIGGER);
		createOptionParent.addChoice(CHOICE_CATEGORY, CHOICE_CATEGORY);
		create.addOptions(createOptionName, createOptionTrigger, createOptionCategory, createOptionParent);
		// remove option
		OptionData removeOption = new OptionData(OptionType.CHANNEL, REMOVE_OPT_NAME, REMOVE_OPT_DESCRIPTION,
				true);
		remove.addOptions(removeOption);
		commandData.addSubcommands(create, /* edit, */ remove);
	}

	@Override
	@Nonnull
	public String getName() {
		return "autoroom";
	}

	@Override
	@Nonnull
	public String getDescription() {
		return "autoroom";
	}

	@Override
	@SuppressWarnings("null")
	public Reply execute(SlashCommandInteractionEvent event) throws Exception {
		String subcommandName = event.getSubcommandName();
		String statusDetail = "";
		Guild guild = event.getGuild();
		GuildManager guildManager = GuildManager.getGuildManager(guild);
		GuildConfig config = guildManager.getGuildConfig();
		switch (subcommandName) {
			case CREATE_NAME -> {
				String name = event.getOption(CREATE_OPT_NAME_NAME).getAsString();
				VoiceChannel trigger = event.getOption(CREATE_OPT_TRIGGER_NAME)
						.getAsChannel().asVoiceChannel();
				Category category = event.getOption(CREATE_OPT_CATEGORY_NAME)
						.getAsChannel().asCategory();
				String parent = event.getOption(CREATE_OPT_PARENT_NAME).getAsString();
				config.addAutoroomTrigger(name, trigger.getIdLong(), category.getIdLong(), parent);
				statusDetail = "set " + trigger.getAsMention()
						+ " as a autoroomTrigger.\nThe autorooms will be named: " + name;
			}
			case REMOVE_NAME -> {
				// remove trigger channel
				VoiceChannel trigger = event.getOption(REMOVE_OPT_NAME)
						.getAsChannel()
						.asVoiceChannel();
				if (config.removeAutoroomTrigger(trigger.getIdLong())) {
					trigger.delete().queue();
					statusDetail = "removed " + trigger.getAsMention();
				} else {
					statusDetail = "couldn't identify " + trigger.getAsMention() + " as a autoroomTrigger";
				}
			}
		}
		return new Reply(event.deferReply(), false).onCommand(event, Status.SUCCESS, statusDetail);
	}

	@Override
	public boolean isGuildOnly() {
		return true;
	}

}
