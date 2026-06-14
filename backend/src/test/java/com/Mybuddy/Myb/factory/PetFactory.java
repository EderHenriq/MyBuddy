package com.Mybuddy.Myb.factory;

import com.Mybuddy.Myb.Model.*;

public class PetFactory {

    public static Pet criarPetPadrao(Organizacao organizacao) {
        return new Pet(
                "Rex",
                "Labrador",
                3,
                Especie.CAO,
                Porte.MEDIO,
                "Amarelo",
                "CURTA",
                "MACHO",
                organizacao,
                true,
                true,
                false,
                "Maringá",
                "PR"
        );
    }

    public static Pet criarPetGato(Organizacao organizacao) {
        return new Pet(
                "Mimi",
                "Siamês",
                2,
                Especie.GATO,
                Porte.PEQUENO,
                "Branco",
                "CURTA",
                "FEMEA",
                organizacao,
                false,
                true,
                true,
                "Maringá",
                "PR"
        );
    }
}