package com.Mybuddy.Myb.DTO;

import com.Mybuddy.Myb.Model.InteresseAdocao;

public final class InteresseAdocaoMapper {

    private InteresseAdocaoMapper() {}

    public static InteresseResponse toResponse(InteresseAdocao i) {
        return new InteresseResponse(
                i.getId(),
                new UsuarioResponse(
                        i.getUsuario().getId(),
                        i.getUsuario().getNome()
                ),
                new PetResumoResponse(
                        i.getPet().getId(),
                        i.getPet().getNome()
                ),
                i.getStatus(),
                i.getMensagem(),
                i.getCriadoEm(),
                i.getAtualizadoEm()
        );
    }
}