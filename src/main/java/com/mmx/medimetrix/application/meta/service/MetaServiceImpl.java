package com.mmx.medimetrix.application.meta.service;

import com.mmx.medimetrix.application.meta.commands.MetaCreate;
import com.mmx.medimetrix.application.meta.commands.MetaUpdate;
import com.mmx.medimetrix.application.meta.port.out.MetaDao;
import com.mmx.medimetrix.application.meta.queries.MetaFiltro;
import com.mmx.medimetrix.domain.core.Meta;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class MetaServiceImpl implements MetaService {

    private final MetaDao dao;

    public MetaServiceImpl(MetaDao dao) { this.dao = dao; }

    @Override
    public Meta create(MetaCreate cmd) {
        if (cmd == null || cmd.idCriterio() == null || cmd.alvo() == null || !StringUtils.hasText(cmd.operador())) {
            throw new IllegalArgumentException("Critério, alvo e operador são obrigatórios.");
        }

        Meta m = new Meta();
        m.setIdCriterio(cmd.idCriterio());
        m.setIdUnidade(cmd.idUnidade());
        m.setIdEspecialidade(cmd.idEspecialidade());
        m.setAlvo(cmd.alvo());
        m.setOperador(cmd.operador());

        // 👇 Regra nova: se não vier vigência início, usa hoje
        if (cmd.vigenciaInicio() != null) {
            m.setVigenciaInicio(cmd.vigenciaInicio());
        } else {
            m.setVigenciaInicio(java.time.LocalDate.now());
        }

        // Vigência fim continua exatamente como o usuário mandar (pode ser null)
        m.setVigenciaFim(cmd.vigenciaFim());

        m.setPrioridade(cmd.prioridade());
        m.setJustificativa(cmd.justificativa());
        m.setAtivo(true);

        Long id = dao.insert(m);
        return dao.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Meta não encontrada após inserir."));
    }


    @Override
    public Meta update(Long id, MetaUpdate cmd) {
        Meta atual = dao.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Meta não encontrada."));

        // campos “imutáveis” ou raramente nulos: segue a lógica antiga
        if (cmd.idCriterio() != null)     atual.setIdCriterio(cmd.idCriterio());
        if (cmd.alvo() != null)           atual.setAlvo(cmd.alvo());
        if (cmd.operador() != null)       atual.setOperador(cmd.operador());
        if (cmd.vigenciaInicio() != null) atual.setVigenciaInicio(cmd.vigenciaInicio());
        if (cmd.vigenciaFim() != null)    atual.setVigenciaFim(cmd.vigenciaFim());
        if (cmd.ativo() != null)          atual.setAtivo(cmd.ativo());
        if (cmd.prioridade() != null)     atual.setPrioridade(cmd.prioridade());
        if (cmd.justificativa() != null)  atual.setJustificativa(cmd.justificativa());

        // Ponto de atenção:
        // unidade/especialidade podem precisar ser LIMPOS.
        // Então copiamos SEM o if != null (permitindo setar null).
        atual.setIdUnidade(cmd.idUnidade());
        atual.setIdEspecialidade(cmd.idEspecialidade());

        dao.update(atual);
        return dao.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Meta não encontrada após atualizar."));
    }


    @Override public Optional<Meta> findById(Long id) { return dao.findById(id); }

    @Override
    public List<Meta> list(MetaFiltro filtro) {
        int page = filtro.page() != null && filtro.page() > 0 ? filtro.page() : 0;
        int size = filtro.size() != null && filtro.size() > 0 ? filtro.size() : 20;
        int offset = page * size;

        // 1) se vier só idCriterio
        if (filtro.idCriterio() != null
                && filtro.idUnidade() == null
                && filtro.idEspecialidade() == null) {
            return dao.listByCriterio(filtro.idCriterio(), offset, size);
        }

        // 2) se vier qualquer combinação de escopo
        if (filtro.idCriterio() != null
                || filtro.idUnidade() != null
                || filtro.idEspecialidade() != null) {
            return dao.listByEscopo(
                    filtro.idCriterio(),
                    filtro.idUnidade(),
                    filtro.idEspecialidade(),
                    offset, size
            );
        }

        // 3) Sem escopo definido:

        // 3.1 Se vier 'naData' => metas ATIVAS e VIGENTES nessa data
        if (filtro.naData() != null) {
            return dao.listAtivasVigentesEm(filtro.naData(), offset, size);
        }

        // 3.2 Sem 'naData' => todas as metas (ativas + inativas),
        // o filtro de status será aplicado depois no controller
        return dao.listAll(offset, size);
    }


    @Override public void activate(Long id) { dao.reactivate(id); }
    @Override public void deactivate(Long id) { dao.deactivate(id); }
}
