package br.com.valueprojects.mock_spring.service;

import br.com.valueprojects.mock_spring.model.Participante;

public interface SmsService {
    void send(Participante participante);
}
