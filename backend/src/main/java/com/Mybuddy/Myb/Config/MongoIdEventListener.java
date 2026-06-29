package com.Mybuddy.Myb.Config;

import com.Mybuddy.Myb.Model.Identifiable;
import com.Mybuddy.Myb.Service.SequenceGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * Ouvinte de ciclo de vida do MongoDB.
 * Intercepta os documentos antes de serem convertidos e persistidos, atribuindo um ID numérico incremental
 * caso o campo 'id' (do tipo Long) esteja nulo.
 */
@Component
@RequiredArgsConstructor
public class MongoIdEventListener extends AbstractMongoEventListener<Object> {

    private final SequenceGeneratorService sequenceGenerator;

    @Override
    public void onBeforeConvert(@NonNull BeforeConvertEvent<Object> event) {
        Object source = event.getSource();
        if (source != null) {
            if (source instanceof Identifiable identifiable) {
                if (identifiable.getId() == null) {
                    long nextId = sequenceGenerator.generateSequence(source.getClass().getSimpleName() + "_sequence");
                    identifiable.setId(nextId);
                }
            } else {
                // Fallback usando reflexão para outras entidades legadas ou externas
                try {
                    Field idField = getField(source.getClass(), "id");
                    if (idField != null) {
                        idField.setAccessible(true);
                        Object idValue = idField.get(source);
                        if (idValue == null && idField.getType().equals(Long.class)) {
                            long nextId = sequenceGenerator.generateSequence(source.getClass().getSimpleName() + "_sequence");
                            idField.set(source, nextId);
                        }
                    }
                } catch (Exception e) {
                    // Silencia exceções de reflexão para evitar interrupções,
                    // caso o campo 'id' não possa ser manipulado.
                }
            }
        }
    }

    private Field getField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null && !superClass.equals(Object.class)) {
                return getField(superClass, fieldName);
            }
        }
        return null;
    }
}
