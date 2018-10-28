package net.benjaminurquhart.ksoftbans4jda;

public interface GlobalBanHandler {

	default void onGlobalBanEvent(GlobalBanEvent event){
		return;
	}
	default void onBannedUserJoin(BannedUserJoinEvent event){
		return;
	}
}
