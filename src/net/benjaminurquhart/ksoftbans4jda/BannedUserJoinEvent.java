package net.benjaminurquhart.ksoftbans4jda;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.explodingbush.ksoftapi.KSoftAPI;
import net.explodingbush.ksoftapi.entities.Ban;

final class BannedUserJoinEvent {
	
	private JDA jda;
	private Ban banInfo;
	private User user;
	private Guild guild;
	private KSoftAPI api;
	private long responseNum;
	
	protected BannedUserJoinEvent(JDA jda, Ban banInfo, KSoftAPI api, Guild guild, User user, long responseNum){
		this.jda = jda;
		this.banInfo = banInfo;
		this.api = api;
		this.user = user;
		this.guild = guild;
		this.responseNum = responseNum;
	}
	public Ban getBanInfo(){
		return banInfo;
	}
	public JDA getJDA(){
		return jda;
	}
	public KSoftAPI getKSoftAPI(){
		return api;
	}
	public Guild getGuild(){
		return guild;
	}
	public User getUser(){
		return user;
	}
	public long getResponseNumber(){
		return responseNum;
	}
}
