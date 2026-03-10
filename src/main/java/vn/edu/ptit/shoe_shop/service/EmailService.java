package vn.edu.ptit.shoe_shop.service;

import java.util.Map;

public interface EmailService {
    void sendEmailSync(String to, String subject, String content, boolean isMultipart, boolean isHtml);
    void sendEmailFromTemplateSync(String to, String subject, String templateName, Map<String, Object> variables);
}
