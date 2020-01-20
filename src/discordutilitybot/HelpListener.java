
package discordutilitybot;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 *
 * @author Brandon Rorie
 */
public class HelpListener extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent event) {

        Message message = event.getMessage();
        String messageContent = message.getContentRaw();
        MessageChannel channel = event.getChannel();

        if (messageContent.toLowerCase().startsWith("!help")) {

            String msg = messageContent.toLowerCase();

            EmbedBuilder info = new EmbedBuilder();
            info.setTitle("Command List");
            info.setDescription("Math command: performs basic math operations and trig\n"
                    + "!math 'math operation' (no quotes)\n\nPoll/vote command: creates a poll and voting system\n!poll \"question\" \"answer 1\" \"answer 2\" \"answer 3\" \"up to 9 questions\"\n\n"
            );

            info.setColor(0xf45642);
            info.setFooter("Created by Kyuubi", event.getMember().getUser().getAvatarUrl());

            Message m = channel.sendMessage(info.build()).complete();
            info.clear();
        }
    }
}
