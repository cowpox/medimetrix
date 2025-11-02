package com.mmx.medimetrix.web.api.v1.resposta;

import com.mmx.medimetrix.application.resposta.commands.RespostaCreate;
import com.mmx.medimetrix.application.resposta.commands.RespostaUpdateValores;
import com.mmx.medimetrix.domain.core.Resposta;
import com.mmx.medimetrix.web.api.v1.resposta.dto.RespostaCreateDTO;
import com.mmx.medimetrix.web.api.v1.resposta.dto.RespostaResponseDTO;
import com.mmx.medimetrix.web.api.v1.resposta.dto.RespostaUpdateValoresDTO;
import org.springframework.stereotype.Component;

@Component
public class RespostaMapper {

    public RespostaCreate toCommand(RespostaCreateDTO dto) {
        RespostaCreate cmd = new RespostaCreate();
        cmd.setIdParticipacao(dto.getIdParticipacao());
        cmd.setIdAvaliacao(dto.getIdAvaliacao());
        cmd.setIdQuestao(dto.getIdQuestao());
        cmd.setValorNumerico(dto.getValorNumerico());
        cmd.setValorBinario(dto.getValorBinario());
        cmd.setTexto(dto.getTexto());
        return cmd;
    }

    public RespostaUpdateValores toCommand(RespostaUpdateValoresDTO dto) {
        RespostaUpdateValores cmd = new RespostaUpdateValores();
        cmd.setValorNumerico(dto.getValorNumerico());
        cmd.setValorBinario(dto.getValorBinario());
        cmd.setTexto(dto.getTexto());
        return cmd;
    }

    public RespostaResponseDTO toDTO(Resposta r) {
        RespostaResponseDTO dto = new RespostaResponseDTO();
        dto.setIdResposta(r.getIdResposta());
        dto.setIdParticipacao(r.getIdParticipacao());
        dto.setIdAvaliacao(r.getIdAvaliacao());
        dto.setIdQuestao(r.getIdQuestao());
        dto.setValorNumerico(r.getValorNumerico());
        dto.setValorBinario(r.getValorBinario());
        dto.setTexto(r.getTexto());
        dto.setDataCriacao(r.getDataCriacao());
        return dto;
    }
}
