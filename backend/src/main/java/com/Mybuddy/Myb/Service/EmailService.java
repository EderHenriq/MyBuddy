package com.Mybuddy.Myb.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    public void enviarEmail(String destinatario, String assunto, String corpo) {
        log.info("Simulando envio de e-mail | Assunto: {}", assunto);
    }
}
