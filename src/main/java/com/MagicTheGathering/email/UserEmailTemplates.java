package com.MagicTheGathering.email;


import com.MagicTheGathering.user.User;

public class UserEmailTemplates {
    public static String getUserCreatedSubject(){
        return "Welcome to Cards&Decks! Your Account is Ready";
    }

    public static String getUserWelcomeEmailPlainText(User user){
        return String.format("Hello %s! 👋\n\n" +
                        "Welcome to Cards&Decks! We're thrilled to have you join our community. 🎉\n\n" +
                        "At Cards&Decks, you can manage your cards and create your decks. You can also explore another users decks.\n\n" +
                        "Log in now and start to explore this Magic world😉!\n\n" +
                        "Best regards,\n" +
                        "The Cards&Decks Team 🚀",
                user.getUsername());
    }

    public static String getUserWelcomeEmailHtml(User user) {
        return String.format(
                "<!DOCTYPE html>" +
                        "<html>" +
                        "<head>" +
                        "    <meta charset=\"UTF-8\">" +
                        "    <style>" +
                        "        body { font-family: Arial, sans-serif; line-height: 1.6; color: #000; background: #DBD6D2; }" +
                        "        .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                        "        .header { background: linear-gradient(135deg, #6F2830 0%%, #000 100%%); color: white; padding: 20px; border-radius: 10px; text-align: center; }" +
                        "        .content { background: white; padding: 20px; border-radius: 10px; margin: 20px 0; box-shadow: 0 2px 6px rgba(0,0,0,0.2); }" +
                        "        .content p { color: #000; }" +
                        "        .content ul li { color: #000; }" +
                        "        .footer { text-align: center; color: #333; font-size: 14px; margin-top: 20px; }" +
                        "        .emoji { font-size: 1.2em; }" +
                        "        .button {" +
                        "            display: inline-block;" +
                        "            background: linear-gradient(135deg, #6F2830 0%%, #000 100%%);" +
                        "            color: white !important;" +
                        "            padding: 10px 20px;" +
                        "            text-decoration: none;" +
                        "            border-radius: 5px;" +
                        "            margin-top: 15px;" +
                        "            border: none;" +
                        "            cursor: pointer;" +
                        "            font-weight: bold;" +
                        "        }" +
                        "    </style>" +
                        "</head>" +
                        "<body>" +
                        "    <div class=\"container\">" +
                        "        <div class=\"header\">" +
                        "            <h1><span class=\"emoji\">⚔️</span> Welcome to MagicDecks!</h1>" +
                        "        </div>" +
                        "        <div class=\"content\">" +
                        "            <p>Hello <strong>%s</strong>! <span class=\"emoji\">👋</span></p>" +
                        "            <p>Your adventure in the world of <strong>Magic: The Gathering</strong> begins here.</p>" +
                        "            <p>With <strong>MagicDecks</strong> you can:</p>" +
                        "            <ul>" +
                        "                <li>Explore cards and discover new strategies</li>" +
                        "                <li>Build and manage your own decks</li>" +
                        "                <li>Share your creations with the community</li>" +
                        "            </ul>" +
                        "            <p>Are you ready to create your next legendary deck?</p>" +
                        "            <p style=\"text-align: center;\">" +
                        "                <a href=\"http://localhost:8080/swagger-ui/index.html#/\" class=\"button\">Start Now</a>" +
                        "            </p>" +
                        "            <p>May the cards be ever in your favor! <span class=\"emoji\">✨</span></p>" +
                        "        </div>" +
                        "        <div class=\"footer\">" +
                        "            <p>Best regards,<br>The Cards&Decks Team <span class=\"emoji\">🧙‍♂️</span></p>" +
                        "        </div>" +
                        "    </div>" +
                        "</body>" +
                        "</html>",
                user.getUsername());
    }
}
