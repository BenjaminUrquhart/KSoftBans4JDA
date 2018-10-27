package net.benjaminurquhart.ksoftbans4jda;

public interface GlobalBanHandler {

	void onGlobalBanEvent(GlobalBanEvent event);
	void onBannedUserJoin(BannedUserJoinEvent event);
}
