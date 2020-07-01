package pt.up.hs.uaa.service;

import io.github.jhipster.config.JHipsterProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import pt.up.hs.uaa.domain.User;
import pt.up.hs.uaa.util.StreamUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * Service for sending emails.
 * <p>
 * We use the {@link Async} annotation to send emails asynchronously.
 */
@Service
public class MailService {

    private final Logger log = LoggerFactory.getLogger(MailService.class);

    private static final String USER = "user";
    private static final String BASE_URL = "baseUrl";
    private static final String LOGO = "logo";

    private static final String LOGO_FILENAME = "logo.png";
    private static final String LOGO_LOCATION = "/templates/assets/" + LOGO_FILENAME;
    private static final String LOGO_CONTENT_TYPE = "image/png";

    private final JHipsterProperties jHipsterProperties;

    private final JavaMailSender javaMailSender;

    private final MessageSource messageSource;

    private final SpringTemplateEngine templateEngine;

    public MailService(JHipsterProperties jHipsterProperties, JavaMailSender javaMailSender,
            MessageSource messageSource, SpringTemplateEngine templateEngine) {

        this.jHipsterProperties = jHipsterProperties;
        this.javaMailSender = javaMailSender;
        this.messageSource = messageSource;
        this.templateEngine = templateEngine;
    }

    @Async
    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml,
                          InlineResource[] inlines) {
        log.debug("Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
            isMultipart, isHtml, to, subject, content);

        // Prepare message using a Spring helper
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setFrom(jHipsterProperties.getMail().getFrom());
            message.setSubject(subject);
            message.setText(content, isHtml);
            if (isHtml && inlines != null) {
                for (InlineResource inline: inlines) {
                    final InputStreamSource source = new ByteArrayResource(inline.bytes);
                    message.addInline(inline.name, source, inline.contentType);
                }
            }
            javaMailSender.send(mimeMessage);
            log.debug("Sent email to User '{}'", to);
        }  catch (MailException | MessagingException e) {
            log.warn("Email could not be sent to user '{}'", to, e);
        }
    }

    @Async
    public void sendEmailFromTemplate(User user, String templateName, String titleKey) {
        sendEmailFromTemplate(user, templateName, titleKey, null);
    }

    @Async
    public void sendEmailFromTemplate(User user, String templateName, String titleKey, String[] titleArgs) {
        if (user.getEmail() == null) {
            log.debug("Email doesn't exist for user '{}'", user.getLogin());
            return;
        }
        Locale locale = user.getLangKey() == null ? Locale.getDefault() : Locale.forLanguageTag(user.getLangKey());
        Context context = new Context(locale);
        context.setVariable(USER, user);
        context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());
        String content = templateEngine.process(templateName, context);
        String subject = messageSource.getMessage(titleKey, titleArgs, locale);
        sendEmail(user.getEmail(), subject, content, false, true, null);
    }

    @Async
    public void sendEmailFromTemplateWithLogo(
        User user, String templateName, String titleKey, String[] titleArgs
    ) {
        if (user.getEmail() == null) {
            log.debug("Email doesn't exist for user '{}'", user.getLogin());
            return;
        }
        Locale locale = user.getLangKey() == null ? Locale.getDefault() : Locale.forLanguageTag(user.getLangKey());
        Context context = new Context(locale);
        context.setVariable(USER, user);
        context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());

        byte[] logoBytes = new byte[0];
        try {
            logoBytes = StreamUtils.getBytes(getClass().getResourceAsStream(LOGO_LOCATION));
        } catch (IOException e) {
            log.error("Could not inline logo in email", e);
        }
        InlineResource logo = new InlineResource(LOGO_FILENAME, logoBytes, LOGO_CONTENT_TYPE);

        context.setVariable(LOGO, LOGO_FILENAME);

        String content = templateEngine.process(templateName, context);
        String subject = messageSource.getMessage(titleKey, titleArgs, locale);

        sendEmail(user.getEmail(), subject, content, true, true, new InlineResource[] {
            logo
        });
    }

    @Async
    public void sendActivationEmail(User user) {
        log.debug("Sending activation email to '{}'", user.getEmail());
        sendEmailFromTemplateWithLogo(user, "mail/activationEmail", "email.activation.title",
            new String[] { user.getFirstName(), user.getLastName() });
    }

    @Async
    public void sendCreationEmail(User user) {
        log.debug("Sending creation email to '{}'", user.getEmail());
        sendEmailFromTemplateWithLogo(user, "mail/creationEmail", "email.creation.title",
            new String[] { user.getFirstName(), user.getLastName() });
    }

    @Async
    public void sendPasswordResetMail(User user) {
        log.debug("Sending password reset email to '{}'", user.getEmail());
        sendEmailFromTemplateWithLogo(user, "mail/passwordResetEmail", "email.reset.title", null);
    }

    class InlineResource {
        final String name;
        final byte[] bytes;
        final String contentType;

        InlineResource(String name, byte[] bytes, String contentType) {
            this.name = name;
            this.bytes = bytes;
            this.contentType = contentType;
        }
    }
}
