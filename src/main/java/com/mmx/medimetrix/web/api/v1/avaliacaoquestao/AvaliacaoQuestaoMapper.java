package com.mmx.medimetrix.web.api.v1.avaliacaoquestao;

import com.mmx.medimetrix.application.avaliacaoquestao.commands.AvaliacaoQuestaoCreate;
import com.mmx.medimetrix.application.avaliacaoquestao.commands.AvaliacaoQuestaoUpdate;
import com.mmx.medimetrix.domain.core.AvaliacaoQuestao;
import com.mmx.medimetrix.web.api.v1.avaliacaoquestao.dto.AvaliacaoQuestaoCreateDTO;
import com.mmx.medimetrix.web.api.v1.avaliacaoquestao.dto.AvaliacaoQuestaoResponseDTO;
import com.mmx.medimetrix.web.api.v1.avaliacaoquestao.dto.AvaliacaoQuestaoUpdateDTO;
import org.springframework.stereotype.Component;

@Component
public class AvaliacaoQuestaoMapper {

    public AvaliacaoQuestaoCreate toCommand(AvaliacaoQuestaoCreateDTO dto) {
        AvaliacaoQuestaoCreate cmd = new AvaliacaoQuestaoCreate();
        cmd.setIdQuestao(dto.getIdQuestao());
        cmd.setOrdem(dto.getOrdem());
        cmd.setObrigatoriaNaAval(dto.getObrigatoriaNaAval());
        cmd.setAtivaNaAval(dto.getAtivaNaAval());
        cmd.setPesoNaAval(dto.getPesoNaAval());
        cmd.setVisivelParaGestorLocal(dto.getVisivelParaGestorLocal());
        cmd.setObservacaoAdmin(dto.getObservacaoAdmin());
        return cmd;
    }

    public AvaliacaoQuestaoUpdate toCommand(AvaliacaoQuestaoUpdateDTO dto) {
        AvaliacaoQuestaoUpdate cmd = new AvaliacaoQuestaoUpdate();
        cmd.setOrdem(dto.getOrdem());
        cmd.setObrigatoriaNaAval(dto.getObrigatoriaNaAval());
        cmd.setAtivaNaAval(dto.getAtivaNaAval());
        cmd.setPesoNaAval(dto.getPesoNaAval());
        cmd.setVisivelParaGestorLocal(dto.getVisivelParaGestorLocal());
        cmd.setObservacaoAdmin(dto.getObservacaoAdmin());
        return cmd;
    }

    public AvaliacaoQuestaoResponseDTO toDTO(AvaliacaoQuestao aq) {
        AvaliacaoQuestaoResponseDTO dto = new AvaliacaoQuestaoResponseDTO();
        dto.setIdAvaliacao(aq.getIdAvaliacao());
        dto.setIdQuestao(aq.getIdQuestao());
        dto.setOrdem(aq.getOrdem());
        dto.setObrigatoriaNaAval(aq.getObrigatoriaNaAval());
        dto.setAtivaNaAval(aq.getAtivaNaAval());
        dto.setPesoNaAval(aq.getPesoNaAval());
        dto.setVisivelParaGestorLocal(aq.getVisivelParaGestorLocal());
        dto.setObservacaoAdmin(aq.getObservacaoAdmin());
        dto.setDataCriacao(aq.getDataCriacao());
        dto.setDataUltimaEdicao(aq.getDataUltimaEdicao());
        return dto;
    }
}
