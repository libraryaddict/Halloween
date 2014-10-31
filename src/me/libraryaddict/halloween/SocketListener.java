package me.libraryaddict.halloween;

import org.bukkit.Bukkit;

import me.libraryaddict.HubConnector.HubApi;
import me.libraryaddict.Sockets.Client.Events.ChatMessageEvent;
import me.libraryaddict.Sockets.Client.Events.Listener;
import me.libraryaddict.Sockets.Client.Events.ServerConnectEvent;
import me.libraryaddict.Sockets.Client.ServerConnector.EventHandler;

public class SocketListener implements Listener {

    @EventHandler
    public void onConnect(ServerConnectEvent event) {
        HubApi.setPlayersAndTime(Bukkit.getOnlinePlayers().size(), 0);
    }

    @EventHandler
    public void onMessage(ChatMessageEvent event) {
        if (event.getMessage().equals("resend")) {
            HubApi.setPlayersAndTime(Bukkit.getOnlinePlayers().size(), 0);
        }
    }
}
