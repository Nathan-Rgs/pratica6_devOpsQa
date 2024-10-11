package infra;

import br.com.valueprojects.mock_spring.model.Participante;
import br.com.valueprojects.mock_spring.service.Notification;

public class Sms implements Notification {

    @Override
    public void send(Participante participante) {
        // Simula o envio de SMS ao participante
        System.out.println("Enviando SMS para o participante: " + participante.getNome() + " informando a sua vitória.");
    }
}
