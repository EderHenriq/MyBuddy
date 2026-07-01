package com.Mybuddy.Myb.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    /**
     * Envia um e-mail para o destinatário informado.
     * Atualmente apenas simula o envio via log, sem integração real com um provedor de e-mail.
     *
     * @param destinatario endereço de e-mail do destinatário
     * @param assunto assunto do e-mail
     * @param corpo conteúdo do e-mail
     */
    public void enviarEmail(String destinatario, String assunto, String corpo) {
        log.info("Simulando envio de e-mail | Assunto: {}", assunto);
    }
}
