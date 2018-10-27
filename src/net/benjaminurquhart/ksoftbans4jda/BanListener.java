package net.benjaminurquhart.ksoftbans4jda;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import net.explodingbush.ksoftapi.KSoftAPI;
import net.explodingbush.ksoftapi.entities.Ban;
import net.explodingbush.ksoftapi.utils.Checks;

public class BanListener extends ListenerAdapter{
	
	private final Logger LOG = LoggerFactory.getLogger(BanListener.class);
	private List<GlobalBanHandler> handlers;
	private KSoftAPI ksoft = null;
	private boolean ban;
	
	public BanListener(){
		this.handlers = new ArrayList<>();
	}
	
	public BanListener(String ksoftToken, boolean ban){
		this.setAPIToken(ksoftToken);
		this.handlers = new ArrayList<>();
		this.ban = ban;
	}
	public BanListener(String ksoftToken, boolean ban, GlobalBanHandler... handlers){
		this.setAPIToken(ksoftToken);
		this.addHandlers(handlers);
		this.ban = ban;
	}
	public BanListener(String ksoftToken, boolean ban, List<GlobalBanHandler> handlers){
		this.setAPIToken(ksoftToken);
		this.addHandlers(handlers);
		this.ban = ban;
	}
	
	public BanListener setAPIToken(String token){
		Checks.notNull(token, "token");
		this.ksoft = new KSoftAPI(token);
		return this;
	}
	
	public BanListener ban(boolean ban){
		this.ban = ban;
		return this;
	}
	
	public BanListener addHandlers(List<GlobalBanHandler> handlers){
		if(this.handlers == null){
			this.handlers = new ArrayList<GlobalBanHandler>();
		}
		Checks.notNull(handlers, "handlers");
		this.handlers.addAll(handlers);
		return this;
	}
	
	public BanListener addHandlers(GlobalBanHandler... handlers){
		Checks.notNull(handlers, "handlers");
		return addHandlers(Arrays.asList(handlers));
	}
	
	public List<GlobalBanHandler> getHandlers(){
		return Collections.unmodifiableList(handlers);
	}
	
	private void handleEvent(Ban info, GuildMemberJoinEvent event){
		for(GlobalBanHandler handler : this.handlers){
			if(handler == null){
				LOG.warn("A null handler was found! Ignoring.");
				continue;
			}
			try{
				handler.onGlobalBanEvent(new GlobalBanEvent(event.getJDA(), info, ksoft, event.getGuild(), event.getUser(), event.getResponseNumber()));
			}
			catch(Exception e){
				LOG.error("A global ban handler threw an uncaught exception!");
				e.printStackTrace();
			}
		}
	}
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event){
		Ban info = null;
		Guild guild = event.getGuild();
		Member self = guild.getSelfMember();
		try{
			info = ksoft.getBan().setUserId(event.getUser().getId()).execute();
		}
		catch(Exception e){
			LOG.error(String.format("Failed to get ban status of user %s#%s (%s)!", event.getUser().getName(), event.getUser().getDiscriminator(), event.getUser().getId()));
			e.printStackTrace();
			return;
		}
		if(info.isBanned() && info.isBanActive()){
			LOG.info(String.format("Globally banned user %s joined the guild %s (%s)", info.getEffectiveName(), guild.getName(), guild.getId()));
			try{
				PrivateChannel channel = event.getUser().openPrivateChannel().complete();
				User mod = event.getJDA().retrieveUserById(info.getModId()).complete();
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(Color.RED);
				eb.setTitle("Your global ban info");
				eb.setFooter("Banned", mod.getAvatarUrl());
				eb.setTimestamp(info.getTimestamp());
				eb.setImage(event.getUser().getAvatarUrl());
				eb.addField("Banned by:", mod.getName() + "#" + mod.getDiscriminator(), true);
				eb.addField("Reason:", info.getReason(), false);
				eb.addField("Proof:", info.getProof(), true);
				eb.addField("Is appealable:", (info.isAppealable() ? "Yes" : "No"), true);
				channel.sendMessage(eb.build()).queue();
			}
			catch(Exception e){
				LOG.warn("This user has their DMs disabled! Unable to send them their global ban info.");
			}
			if(ban && self.hasPermission(Permission.BAN_MEMBERS) && self.canInteract(event.getMember())){
				guild.getController().ban(event.getMember(), 7).reason("Banned on KSoft (global ban)").queue();
				LOG.info("Banned user " + info.getEffectiveName());
				this.handleEvent(info, event);
			}
		}
	}
	public String toString(){
		return String.format("Ban: %s\nHandlers registered: %d", (ban ? "yes" : "no"), handlers.size());
	}
}
