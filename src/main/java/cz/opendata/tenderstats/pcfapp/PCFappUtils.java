package cz.opendata.tenderstats.pcfapp;

import cz.opendata.tenderstats.ComponentConfiguration;
import cz.opendata.tenderstats.UserContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.GregorianCalendar;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.LogManager;

public class PCFappUtils implements Serializable {

    private static final long serialVersionUID = -128129525758003394L;
    private final ComponentConfiguration config;

    public PCFappUtils(ComponentConfiguration config) {
        this.config = config;
    }

    public void retreiveDocument(HttpServletRequest request,
            HttpServletResponse response, UserContext uc) {

        Document document = ExtendedDocument.fetchByIdAndGraph(request.getParameter("token"), uc.getNamedGraph());
        try {
            if (document != null) {
                try {
                    response.setContentType(document.getContentType());
                    response.setHeader("Content-Disposition", "attachment; filename=" + document.getName());
                    IOUtils.copy(new FileInputStream(document.getFile()), response.getOutputStream());
                } catch (IOException ex) {
                    response.sendError(404, "File not found");
                }
            } else {
                response.sendError(404, "File not found");
            }
        } catch (IOException ex) {
            LogManager.getLogger("Documents").warn(ex, ex);
        }
    }

    public String getFileName(final Part part) {
        //final String partHeader = part.getHeader("content-disposition");	    
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(
                        content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

    public Document processFileUpload(Part part, String token, UserContext user) {
        final String fileName = getFileName(part);
        if (fileName.isEmpty()) {
            return null;
        }
        try (InputStream filecontent = part.getInputStream()) {
            File documentFile = new File(config.getPreference("documentsDir"), token);
            org.apache.commons.io.FileUtils.copyInputStreamToFile(filecontent, new File(config.getPreference("documentsDir"), token));
            return new Document(documentFile, fileName, part.getContentType(), user);
        } catch (IOException ex) {
            return null;
        }
    }

    public static String currentXMLTime() {

        DatatypeFactory df;
        try {
            df = DatatypeFactory.newInstance();
            XMLGregorianCalendar calendar = df.newXMLGregorianCalendar(new GregorianCalendar());
            return calendar.toXMLFormat();
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }

}
