package net.serveron.hane;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.serveron.hane.util.Msg;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_MESSAGES;
import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_VOICE_STATES;

public class JDAManager extends ListenerAdapter {
    private JDA jda;
    private final String channelId;

    public JDAManager(String token, String channelId) throws LoginException {
        initDiscordBot(token);
        this.channelId = channelId;
    }
    public void shutdown(){
        deInitDiscordBot();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e){
        String id = e.getChannel().getId();
        if(id.equals(channelId)){
            if(!e.getAuthor().isBot()){
                String message = e.getMessage().getContentRaw();
            }
        }
    }

    public void sendMessage(String message){
        Objects.requireNonNull(jda.getTextChannelById(channelId)).sendMessage(message).queue();
    }

    private void initDiscordBot(String token) throws LoginException {
        if(token.isEmpty()){
            Msg.sendToConsole("Discord bot is disabled due to the empty token", Msg.MessageType.WARNING);
        } else {
            Msg.sendToConsole("Initialize JDA");
            if(jda==null){
                jda = JDABuilder.createLight(token, GUILD_MESSAGES, GUILD_VOICE_STATES)
                        .addEventListeners(this)
                        .build();
            } else {
                Msg.sendToConsole("JDA is already running.", Msg.MessageType.WARNING);
            }
        }
    }

    private void deInitDiscordBot(){
        // Clear JDA listeners
        if (jda != null) {
            jda.getEventManager().getRegisteredListeners().forEach(listener -> jda.getEventManager().unregister(listener));
        }
        // try to shut down jda gracefully
        if (jda != null) {
            CompletableFuture<Void> shutdownTask = new CompletableFuture<>();
            jda.addEventListener(new ListenerAdapter() {
                @Override
                public void onShutdown(@NotNull ShutdownEvent event) {
                    shutdownTask.complete(null);
                }
            });
            jda.shutdownNow();
            jda = null;
            try {
                shutdownTask.get(5, TimeUnit.SECONDS);
            } catch (TimeoutException | ExecutionException | InterruptedException e) {
                Msg.sendToConsole("JDA took too long to shut down, skipping", Msg.MessageType.ERROR);
            }
        }
    }
}
