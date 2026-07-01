package com.Mybuddy.Myb.DTO;

import com.Mybuddy.Myb.Model.InteresseAdocao;

public final class InteresseAdocaoMapper {

    private InteresseAdocaoMapper() {}

    /**
     * Converte a entidade {@link InteresseAdocao} em um DTO de resposta, mascarando o CPF
     * do adotante para não expor o dado sensível completo (LGPD).
     *
     * @param interesse entidade de interesse em adoção
     * @return DTO de resposta com o CPF mascarado
     */
    public static InteresseResponse toResponse(InteresseAdocao interesse) {
        UsuarioResponse usuarioResponse = interesse.getUsuario() != null
                ? new UsuarioResponse(interesse.getUsuario().getId(), interesse.getUsuario().getNome())
                : null;

        PetResumoResponse petResponse = interesse.getPet() != null
                ? new PetResumoResponse(interesse.getPet().getId(), interesse.getPet().getNome())
                : null;

        return new InteresseResponse(
                interesse.getId(),
                usuarioResponse,
                petResponse,
                interesse.getStatus(),
                interesse.getMensagem(),
                interesse.getCriadoEm(),
                interesse.getAtualizadoEm(),
                mascararCpf(interesse.getCpfAdotante()),
                interesse.getIdadeAdotante(),
                interesse.getMotivoAdocao(),
                interesse.getTipoResidencia(),
                interesse.getPossuiTelasProtecao(),
                interesse.getOutrosAnimais(),
                interesse.getTempoSozinhoHoras(),
                interesse.getTodosCientes(),
                interesse.getEspacoAdequado(),
                interesse.getConsentimentoLgpd()
        );
    }

    /**
     * Mascara o CPF do adotante, exibindo apenas os dígitos centrais e finais.
     *
     * @param cpf CPF completo (com ou sem formatação)
     * @return CPF mascarado no formato {@code ***.***.XXX-XX}, ou {@code null} se vazio
     */
    private static String mascararCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) return null;
        String digits = cpf.replaceAll("[^\\d]", "");
        if (digits.length() != 11) return "***.***.***-**";
        return "***.***.%s-%s".formatted(digits.substring(6, 9), digits.substring(9));
    }
}