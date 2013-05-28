package smac.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

/**
 * SmacEmlUtils is a class which groups static methods pertaining to "EML" file operations used throughout the SMAC
 * daemon.
 *
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 0.1
 */
public class SmacEmlUtils {

    /**
     * Load an EML file and parse it as a MimeMessage object
     *
     * @param file EML file containing SEPAmail message
     * @return MimeMessage object containing the parsed EML file
     * @throws FileNotFoundException
     * @throws MessagingException
     */
    public static MimeMessage loadEml(File file) throws FileNotFoundException, MessagingException {

        // System properties
        Properties properties = System.getProperties();

        // Set property values
        properties.put("mail.transport.protocol", "smtp");

        // Mail session instance
        Session mailSession = Session.getDefaultInstance(properties, null);

        // Create input stream
        InputStream source = new FileInputStream(file);

        // Load EML file as a MIME message
        return new MimeMessage(mailSession, source);
    }
}
