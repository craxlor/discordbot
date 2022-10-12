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
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class Setup extends SCAdmin {

	private static final String CREATE_NAME = "create";
	private static final String CREATE_DESCRIPTION = "autoroom";
	private static final String OPT_NAME_NAME = "name";
	private static final String OPT_NAME_DESCRIPTION = "autoroom";
	private static final String OPT_TRIGGER_NAME = "trigger";
	private static final String OPT_TRIGGER_DESCRIPTION = "autoroom";
	private static final String OPT_CATEGORY_NAME = "category";
	private static final String OPT_CATEGORY_DESCRIPTION = "autoroom";
	private static final String OPT_PARENT_NAME = "parent";
	private static final String OPT_PARENT_DESCRIPTION = "autoroom";
	public static final String CHOICE_TRIGGER = "trigger";
	public static final String CHOICE_CATEGORY = "category";
	private static final String REMOVE_NAME = "remove";
	private static final String REMOVE_DESCRIPTION = "autoroom";
	private static final String REMOVE_OPT_NAME = "channel";
	private static final String REMOVE_OPT_DESCRIPTION = "autoroom";
	private static final String EDIT_NAME = "edit";
	private static final String EDIT_DESCRIPTION = "desc";

	public Setup() {
		SubcommandData create = new SubcommandData(CREATE_NAME, CREATE_DESCRIPTION);
		SubcommandData edit = new SubcommandData(EDIT_NAME, EDIT_DESCRIPTION);
		SubcommandData remove = new SubcommandData(REMOVE_NAME, REMOVE_DESCRIPTION);
		// create options
		OptionData createOptionName = new OptionData(OptionType.STRING, OPT_NAME_NAME,
				OPT_NAME_DESCRIPTION, true);
		OptionData createOptionTrigger = new OptionData(OptionType.CHANNEL, OPT_TRIGGER_NAME,
				OPT_TRIGGER_DESCRIPTION, true);
		OptionData createOptionCategory = new OptionData(OptionType.CHANNEL, OPT_CATEGORY_NAME,
				OPT_CATEGORY_DESCRIPTION, true);
		OptionData createOptionParent = new OptionData(OptionType.STRING, OPT_PARENT_NAME,
				OPT_PARENT_DESCRIPTION, true);
		createOptionParent.addChoice(CHOICE_TRIGGER, CHOICE_TRIGGER);
		createOptionParent.addChoice(CHOICE_CATEGORY, CHOICE_CATEGORY);
		create.addOptions(createOptionName, createOptionTrigger, createOptionCategory, createOptionParent);
		// remove option
		OptionData removeOption = new OptionData(OptionType.CHANNEL, REMOVE_OPT_NAME, REMOVE_OPT_DESCRIPTION,
				true);
		remove.addOptions(removeOption);
		// edit options
		OptionData editOptionChannel = new OptionData(OptionType.CHANNEL, OPT_TRIGGER_NAME, OPT_TRIGGER_DESCRIPTION,
				true);
		OptionData editOptionName = new OptionData(OptionType.STRING, OPT_NAME_NAME, OPT_NAME_DESCRIPTION);
		OptionData editOptionCategory = new OptionData(OptionType.CHANNEL, OPT_CATEGORY_NAME, OPT_CATEGORY_DESCRIPTION);
		OptionData editOptionParent = new OptionData(OptionType.STRING, OPT_PARENT_NAME, OPT_PARENT_DESCRIPTION);
		editOptionParent.addChoice(CHOICE_TRIGGER, CHOICE_TRIGGER);
		editOptionParent.addChoice(CHOICE_CATEGORY, CHOICE_CATEGORY);
		edit.addOptions(editOptionChannel, editOptionName, editOptionCategory, editOptionParent);
		commandData.addSubcommands(create, edit, remove);
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
				String name = event.getOption(OPT_NAME_NAME).getAsString();
				VoiceChannel trigger = event.getOption(OPT_TRIGGER_NAME)
						.getAsChannel().asVoiceChannel();
				Category category = event.getOption(OPT_CATEGORY_NAME)
						.getAsChannel().asCategory();
				String parent = event.getOption(OPT_PARENT_NAME).getAsString();
				config.addAutoroomTrigger(name, trigger.getIdLong(), category.getIdLong(), parent);
				statusDetail = "set " + trigger.getAsMention()
						+ " as a autoroomTrigger.\nThe autorooms will be named: " + name;
			}
			case EDIT_NAME -> {
				VoiceChannel trigger = event.getOption(OPT_TRIGGER_NAME).getAsChannel().asVoiceChannel();
				if (config.isAutoroomTrigger(trigger.getIdLong()) == false)
					return new Reply(event.deferReply(), false).onCommand(event, Status.FAIL,
							"the selceted channel is not an autoroom trigger");
				OptionMapping option = event.getOption(OPT_NAME_NAME);
				String name = null, parent = null;
				long categoryID = -1;
				if (option != null)
					name = option.getAsString();
				if ((option = event.getOption(OPT_CATEGORY_NAME)) != null)
					categoryID = option.getAsLong();
				if ((option = event.getOption(OPT_PARENT_NAME)) != null)
					parent = option.getAsString();
				config.editAutoroomtrigger(trigger.getIdLong(), name, categoryID, parent);
				/**
				 * todo
				 * include null checks because the options are optional and not mandatory
				 * only apply chanhges to edited setting
				 * check parent option
				 * check category option
				 * check name option
				 */

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
