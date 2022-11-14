package org.example.Servlets;

import org.example.Account.UserCookies;
import org.example.Account.UserProfile;
import org.example.Account.UserService;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Date;

public class MainServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        UserProfile user;
        try {
            user = UserService.USER_SERVICE.getUserByCookies(req.getCookies());
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (user == null) {
            resp.sendRedirect("./auth");
        } else {
            String currentPath = req.getParameter("path");
            if (currentPath == null) {
                currentPath = System.getProperty("os.name").toLowerCase().startsWith("win") ? "D:/Учеба/java-users" : "/home/maxim/projects";
                currentPath += "/" + user.getLogin();
                File file = new File(currentPath);
                if (!file.exists()) {
                    file.mkdir();
                }
            }
            File file = new File(currentPath);
            if (file.isFile()) {

                String filePath = currentPath;
                File downloadFile = new File(filePath);
                FileInputStream inStream = new FileInputStream(downloadFile);

                // obtains ServletContext
                ServletContext context = getServletContext();

                // gets MIME type of the file
                String mimeType = context.getMimeType(filePath);
                if (mimeType == null) {
                    // set to binary type if MIME mapping not found
                    mimeType = "application/octet-stream";
                }

                // modifies response
                resp.setContentType(mimeType);
                resp.setContentLength((int) downloadFile.length());

                // forces download
                String headerKey = "Content-Disposition";
                String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
                resp.setHeader(headerKey, headerValue);

                // obtains response's output stream
                OutputStream outStream = resp.getOutputStream();

                byte[] buffer = new byte[4096];
                int bytesRead = -1;

                while ((bytesRead = inStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }

                inStream.close();
                outStream.close();

            }
            showFiles(req, new File(currentPath).listFiles(), currentPath);
            req.setAttribute("currentPath", currentPath);
            req.getRequestDispatcher("FileManager.jsp").forward(req, resp);
        }
    }

    private void showFiles(HttpServletRequest req, File[] files, String currentPath) {
        String currentDate = new Date().toString();
        StringBuilder attrFolders = new StringBuilder();
        StringBuilder attrFiles = new StringBuilder();
        for (File file : files) {
            if (file.isDirectory()) {
                attrFolders.append("<li><a href=\"?path=").append(currentPath).append("/").append(file.getName())
                        .append("\">")
                        .append(file.getName())
                        .append("</a></li>");
            } else {
                attrFiles.append("<li><a href=\"?path=").append(currentPath).append("/").append(file.getName())
                        .append("\">")
                        .append(file.getName())
                        .append("</a></li>");
            }
        }
        req.setAttribute("currentTime", currentDate);
        req.setAttribute("folders", attrFolders);
        req.setAttribute("files", attrFiles);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getParameter("exitBtn") != null) {
            try {
                UserService.USER_SERVICE.removeUserBySession(UserCookies.getValue(req.getCookies(), "JSESSIONID"));
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            UserCookies.addCookie(resp, "JSESSIONID", null);
            resp.sendRedirect("./");
        }
    }
}
