package com.MagicTheGathering.email;

import com.MagicTheGathering.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class UserEmailTemplatesTest {

    @Mock
    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class SubjectTests {

        @Test
        void should_Return_CorrectUserCreatedSubject() {
            String subject = UserEmailTemplates.getUserCreatedSubject();

            assertNotNull(subject);
            assertEquals("Welcome to Cards&Decks! Your Account is Ready", subject);
        }

        @Test
        void subject_Should_NotBeEmpty() {
            String subject = UserEmailTemplates.getUserCreatedSubject();

            assertFalse(subject.isEmpty());
            assertTrue(subject.length() > 0);
        }
    }

    @Nested
    class PlainTextEmailTests {

        @Test
        void should_GeneratePlainTextEmailWithUsername() {
            when(mockUser.getUsername()).thenReturn("TestUser");

            String plainTextEmail = UserEmailTemplates.getUserWelcomeEmailPlainText(mockUser);

            assertNotNull(plainTextEmail);
            assertTrue(plainTextEmail.contains("TestUser"));
            assertTrue(plainTextEmail.contains("Hello TestUser!"));
        }

        @Test
        void plainTextEmail_Should_ContainWelcomeMessage() {
            String plainTextEmail = UserEmailTemplates.getUserWelcomeEmailPlainText(mockUser);

            assertTrue(plainTextEmail.contains("Welcome to Cards&Decks"));
            assertTrue(plainTextEmail.contains("thrilled to have you join"));
        }

        @Test
        void plainTextEmail_Should_ContainFeatures() {
            String plainTextEmail = UserEmailTemplates.getUserWelcomeEmailPlainText(mockUser);

            assertTrue(plainTextEmail.contains("manage your cards"));
            assertTrue(plainTextEmail.contains("create your decks"));
            assertTrue(plainTextEmail.contains("explore another users decks"));
        }

        @Test
        void plainTextEmail_Should_ContainSignature() {
            String plainTextEmail = UserEmailTemplates.getUserWelcomeEmailPlainText(mockUser);

            assertTrue(plainTextEmail.contains("Best regards"));
            assertTrue(plainTextEmail.contains("The Cards&Decks Team"));
        }

        @Test
        void should_Handle_DifferentUsernames() {
            when(mockUser.getUsername()).thenReturn("AnotherUser");

            String plainTextEmail = UserEmailTemplates.getUserWelcomeEmailPlainText(mockUser);

            assertTrue(plainTextEmail.contains("AnotherUser"));
            assertTrue(plainTextEmail.contains("Hello AnotherUser!"));
        }
    }

    @Nested
    class HtmlEmailTests {

        @Test
        void should_GenerateHtmlEmailWithUsername() {
            when(mockUser.getUsername()).thenReturn("TestUser");

            String htmlEmail = UserEmailTemplates.getUserWelcomeEmailHtml(mockUser);

            assertNotNull(htmlEmail);
            assertTrue(htmlEmail.contains("TestUser"));
            assertTrue(htmlEmail.contains("<strong>TestUser</strong>"));
        }

        @Test
        void htmlEmail_Should_ContainProperHtmlStructure() {
            String htmlEmail = UserEmailTemplates.getUserWelcomeEmailHtml(mockUser);

            assertTrue(htmlEmail.contains("<!DOCTYPE html>"));
            assertTrue(htmlEmail.contains("<html>"));
            assertTrue(htmlEmail.contains("<head>"));
            assertTrue(htmlEmail.contains("<body>"));
            assertTrue(htmlEmail.contains("</html>"));
        }

        @Test
        void htmlEmail_Should_ContainCssStyles() {
            String htmlEmail = UserEmailTemplates.getUserWelcomeEmailHtml(mockUser);

            assertTrue(htmlEmail.contains("<style>"));
            assertTrue(htmlEmail.contains("font-family: Arial"));
            assertTrue(htmlEmail.contains("background: #DBD6D2"));
            assertTrue(htmlEmail.contains("linear-gradient"));
        }

        @Test
        void htmlEmail_Should_ContainWelcomeContent() {
            String htmlEmail = UserEmailTemplates.getUserWelcomeEmailHtml(mockUser);

            assertTrue(htmlEmail.contains("Welcome to MagicDecks"));
            assertTrue(htmlEmail.contains("Magic: The Gathering"));
            assertTrue(htmlEmail.contains("adventure in the world"));
        }

        @Test
        void htmlEmail_Should_ContainFeatureList() {
            String htmlEmail = UserEmailTemplates.getUserWelcomeEmailHtml(mockUser);

            assertTrue(htmlEmail.contains("<ul>"));
            assertTrue(htmlEmail.contains("<li>"));
            assertTrue(htmlEmail.contains("Explore cards"));
            assertTrue(htmlEmail.contains("Build and manage"));
            assertTrue(htmlEmail.contains("Share your creations"));
        }

        @Test
        void htmlEmail_Should_ContainCallToActionButton() {
            String htmlEmail = UserEmailTemplates.getUserWelcomeEmailHtml(mockUser);

            assertTrue(htmlEmail.contains("class=\"button\""));
            assertTrue(htmlEmail.contains("href=\"http://localhost:8080/swagger-ui/index.html#/\""));
            assertTrue(htmlEmail.contains("Start Now"));
        }

        @Test
        void htmlEmail_Should_ContainEmojis() {
            String htmlEmail = UserEmailTemplates.getUserWelcomeEmailHtml(mockUser);

            assertTrue(htmlEmail.contains("⚔️"));
            assertTrue(htmlEmail.contains("👋"));
            assertTrue(htmlEmail.contains("✨"));
            assertTrue(htmlEmail.contains("🧙‍♂️"));
        }

        @Test
        void should_HandleSpecialCharactersInUsername() {
            when(mockUser.getUsername()).thenReturn("User@123");

            String htmlEmail = UserEmailTemplates.getUserWelcomeEmailHtml(mockUser);

            assertTrue(htmlEmail.contains("User@123"));
        }
    }
}
