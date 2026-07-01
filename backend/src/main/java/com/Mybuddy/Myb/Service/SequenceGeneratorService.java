package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.Model.DatabaseSequence;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
@RequiredArgsConstructor
public class SequenceGeneratorService {

    private final MongoOperations mongoOperations;

    /**
     * Gera o próximo valor de uma sequência numérica armazenada no MongoDB, usada como
     * substituto do auto-incremento nativo do SQL para entidades persistidas no Mongo.
     *
     * @param seqName nome/identificador da sequência
     * @return próximo valor da sequência, iniciando em 1 caso ainda não exista
     */
    public long generateSequence(String seqName) {
        DatabaseSequence counter = mongoOperations.findAndModify(
                query(where("_id").is(seqName)),
                new Update().inc("seq", 1),
                options().returnNew(true).upsert(true),
                DatabaseSequence.class);
        return !Objects.isNull(counter) ? counter.getSeq() : 1;
    }
}
