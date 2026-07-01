package com.Mybuddy.Myb.DTO;

import com.Mybuddy.Myb.Model.InteresseAdocao;

public final class InteresseAdocaoMapper {

    private InteresseAdocaoMapper() {}

    public static InteresseResponse toResponse(InteresseAdocao i) {
        UsuarioResponse usuarioResponse = i.getUsuario() != null
                ? new UsuarioResponse(i.getUsuario().getId(), i.getUsuario().getNome())
                : null;

        PetResumoResponse petResponse = i.getPet() != null
                ? new PetResumoResponse(i.getPet().getId(), i.getPet().getNome())
                : null;

        return new InteresseResponse(
                i.getId(),
                usuarioResponse,
                petResponse,
                i.getStatus(),
                i.getMensagem(),
                i.getCriadoEm(),
                i.getAtualizadoEm(),
                mascararCpf(i.getCpfAdotante()),
                i.getIdadeAdotante(),
                i.getMotivoAdocao(),
                i.getTipoResidencia(),
                i.getPossuiTelasProtecao(),
                i.getOutrosAnimais(),
                i.getTempoSozinhoHoras(),
                i.getTodosCientes(),
                i.getEspacoAdequado(),
                i.getConsentimentoLgpd()
        );
    }

    private static String mascararCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) return null;
        String digits = cpf.replaceAll("[^\\d]", "");
        if (digits.length() != 11) return "***.***.***-**";
        return "***.***.%s-%s".formatted(digits.substring(6, 9), digits.substring(9));
    }
}