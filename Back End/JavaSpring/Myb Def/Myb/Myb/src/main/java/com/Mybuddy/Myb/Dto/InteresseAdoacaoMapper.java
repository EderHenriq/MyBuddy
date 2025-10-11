package com.Mybuddy.Myb.Dto;

import com.Mybuddy.Myb.Model.InteresseAdoacao;

public final class InteresseAdoacaoMapper {
    private InteresseAdoacaoMapper() {}

    public static InteresseResponse toResponse(InteresseAdoacao i) {
        return new InteresseResponse(
                i.getId(),
                i.getUsuario().getId(),
                i.getPet().getId(),
                i.getStatus(),
                i.getMensagem(),
                i.getCriadoEm(),
                i.getAtuaziladoEm()
        );
    }
}

