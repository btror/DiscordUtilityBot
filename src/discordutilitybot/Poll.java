
package discordutilitybot;


import java.util.ArrayList;
import net.dv8tion.jda.core.EmbedBuilder;

import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * A class that allows the bot to create a poll/voting system for the user
 * Command: !poll "question" "answer 1" "answer 2" "answer 3" etc... Example:
 * !poll "who is the president?" "trump" "hitler" "AOC" "jeff"
 *
 *
 * @author Brandon Rorie
 */
public class Poll extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent event) {
        // if (event.getAuthor().isBot()) return; IF YOU WANT THE BOT TO NOT RESPOND TO OTHER BOTS

        Message message = event.getMessage();
        String messageContent = message.getContentRaw();
        MessageChannel channel = event.getChannel();

        if (messageContent.toLowerCase().startsWith("!poll")) {

            //message.delete().queue();
            String msg = messageContent.toLowerCase();

            boolean isEmptyAfterCommand = false;
            if (msg.length() < 6) {
                isEmptyAfterCommand = true;
            }
            
            boolean questionPresent = false;
            boolean nullQuestion = false;
            int c = 0;
            for (int i = 1; i < msg.length(); i++){
                if (msg.charAt(i) == '"'){
                    c++;
                    if (c == 2 && msg.charAt(i - 1) == '"'){
                        nullQuestion = true;
                    }
                }
            }
            if (c < 2){    
                questionPresent = false;
            } else {
                questionPresent = true;
            }

            boolean keepRunning = false;
            if (isEmptyAfterCommand == true || questionPresent == false || nullQuestion == true) {
                keepRunning = false;
            } else {
                keepRunning = true;
            }

            if (keepRunning == true) {
                message.delete().queue();
                String question = "";
                ArrayList<Integer> indexes = new ArrayList<>();
                //add the indexes of the quotation marks (should be even)
                for (int i = 0; i < msg.length(); i++) {
                    if (msg.charAt(i) == '"') {
                        indexes.add(i);
                    }
                }
                question = msg.substring(indexes.get(0) + 1, indexes.get(1));
                String[] answers = new String[(indexes.size() / 2) - 1];
                int j = 2;
                for (int i = 0; i < answers.length; i++) {
                    answers[i] = msg.substring(indexes.get(j) + 1, indexes.get(j + 1));
                    j += 2;
                }

                String[] nums = {":one:", ":two:", ":three:", ":four:", ":five:", ":six:", ":seven:", ":eight:", ":nine:"};
                //a cool command you can use to show that the bot is typing
                //event.getChannel().sendTyping().queue();

                String choiceOutput = "";
                for (int i = 0; i < answers.length; i++) {
                    choiceOutput += nums[i] + " " + answers[i] + "\n\n";
                }

                EmbedBuilder info = new EmbedBuilder();
                info.setTitle(question);
                info.setDescription(choiceOutput);

                info.setColor(0xf45642);
                info.setFooter("Created by Kyuubi", event.getMember().getUser().getAvatarUrl());

                //event.getChannel().sendMessage(info.build()).complete().addReaction("👌").queue();
                Message m = channel.sendMessage(info.build()).complete();
                //m.addReaction("👌").queue(); THIS RIGHT HERE
                String[] reactions = {"1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣"};
                for (int i = 0; i < answers.length; i++) {
                    m.addReaction(reactions[i]).queue();
                }

                info.clear();
            } else {
                channel.sendMessage("Error: syntax of the !poll command is incorrect. Use this format: !poll \"question\" \"first answer\" \"second answer\" \"up to nine possible answers..\"").queue();
            }
        }
    }
}

