package br.com.valueprojects.mock_spring.service;

import br.com.valueprojects.mock_spring.model.Participante;

public interface Notification {
    void send(Participante participante);
}
