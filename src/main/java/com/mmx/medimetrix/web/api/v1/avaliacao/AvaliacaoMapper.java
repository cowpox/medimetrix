package com.mmx.medimetrix.web.api.v1.avaliacao;

import com.mmx.medimetrix.application.avaliacao.commands.AvaliacaoCreate;
import com.mmx.medimetrix.domain.core.Avaliacao;
import com.mmx.medimetrix.web.api.v1.avaliacao.dto.AvaliacaoCreateDTO;
import com.mmx.medimetrix.web.api.v1.avaliacao.dto.AvaliacaoResponseDTO;
import com.mmx.medimetrix.web.api.v1.avaliacao.dto.AvaliacaoUpdateDTO;
import org.springframework.stereotype.Component;

@Component
public class AvaliacaoMapper {

    public AvaliacaoCreate toCommand(AvaliacaoCreateDTO dto) {
        AvaliacaoCreate cmd = new AvaliacaoCreate();
        cmd.setTitulo(dto.getTitulo());
        cmd.setDataInicioAplic(dto.getDataInicioAplic());
        cmd.setDataFimAplic(dto.getDataFimAplic());
        cmd.setkMinimo(dto.getkMinimo());
        cmd.setStatus(dto.getStatus());
        cmd.setAtivo(dto.getAtivo());
        return cmd;
    }

    public AvaliacaoCreate toCommand(AvaliacaoUpdateDTO dto, Avaliacao existente) {
        AvaliacaoCreate cmd = new AvaliacaoCreate();
        cmd.setTitulo(dto.getTitulo() != null ? dto.getTitulo() : existente.getTitulo());
        cmd.setDataInicioAplic(dto.getDataInicioAplic() != null ? dto.getDataInicioAplic() : existente.getDataInicioAplic());
        cmd.setDataFimAplic(dto.getDataFimAplic() != null ? dto.getDataFimAplic() : existente.getDataFimAplic());
        cmd.setkMinimo(dto.getkMinimo() != null ? dto.getkMinimo() : existente.getkMinimo());
        cmd.setStatus(dto.getStatus() != null ? dto.getStatus() : existente.getStatus());
        cmd.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : existente.getAtivo());
        return cmd;
    }

    public AvaliacaoResponseDTO toDTO(Avaliacao a) {
        AvaliacaoResponseDTO dto = new AvaliacaoResponseDTO();
        dto.setIdAvaliacao(a.getIdAvaliacao());
        dto.setTitulo(a.getTitulo());
        dto.setDataInicioAplic(a.getDataInicioAplic());
        dto.setDataFimAplic(a.getDataFimAplic());
        dto.setkMinimo(a.getkMinimo());
        dto.setStatus(a.getStatus());
        dto.setAtivo(a.getAtivo());
        dto.setVersao(a.getVersao());
        dto.setDataCriacao(a.getDataCriacao());
        dto.setDataUltimaEdicao(a.getDataUltimaEdicao());
        return dto;
    }
}
