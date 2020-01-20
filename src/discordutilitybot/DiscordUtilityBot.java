
package discordutilitybot;

import javax.security.auth.login.LoginException;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

/**
 *
 * @author Brandon Rorie
 */
public class DiscordUtilityBot {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws LoginException, InterruptedException {

        JDA jda = new JDABuilder(AccountType.BOT).setToken("PUT TOKEN HERE").buildBlocking();
        jda.addEventListener(new Poll());
        jda.addEventListener(new MathOperations());
        jda.addEventListener(new HelpListener());

    }
    
}
