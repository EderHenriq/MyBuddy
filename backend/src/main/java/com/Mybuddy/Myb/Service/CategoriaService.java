package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.DTO.CategoriaRequestDTO;
import com.Mybuddy.Myb.DTO.CategoriaResponseDTO;
import com.Mybuddy.Myb.DTO.SubCategoriaRequestDTO;
import com.Mybuddy.Myb.DTO.SubCategoriaResponseDTO;
import com.Mybuddy.Myb.Exception.ResourceNotFoundException;
import com.Mybuddy.Myb.Model.Categoria;
import com.Mybuddy.Myb.Model.SubCategoria;
import com.Mybuddy.Myb.Repository.jpa.CategoriaRepository;
import com.Mybuddy.Myb.Repository.jpa.SubCategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final SubCategoriaRepository subCategoriaRepository;

    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarTodas() {
        return categoriaRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoriaResponseDTO buscarPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com o ID: " + id));
        return toResponseDTO(categoria);
    }

    /**
     * Cria uma nova categoria de produtos, garantindo que o nome seja único.
     *
     * @param request dados da categoria a ser criada
     * @return categoria criada
     */
    @Transactional
    public CategoriaResponseDTO criar(CategoriaRequestDTO request) {
        if (categoriaRepository.existsByNomeIgnoreCase(request.getNome())) {
            throw new IllegalArgumentException("Já existe uma categoria com o nome: " + request.getNome());
        }
        Categoria categoria = Categoria.builder()
                .nome(request.getNome())
                .build();
        return toResponseDTO(categoriaRepository.save(categoria));
    }

    @Transactional
    public SubCategoriaResponseDTO criarSubcategoria(SubCategoriaRequestDTO request) {
        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria pai não encontrada com o ID: " + request.getCategoriaId()));

        if (subCategoriaRepository.existsByNomeAndCategoriaId(request.getNome(), request.getCategoriaId())) {
            throw new IllegalArgumentException("Já existe uma subcategoria com este nome para esta categoria.");
        }

        SubCategoria subCategoria = SubCategoria.builder()
                .nome(request.getNome())
                .categoria(categoria)
                .build();

        SubCategoria salvo = subCategoriaRepository.save(subCategoria);
        return toSubResponseDTO(salvo);
    }

    @Transactional
    public void deletar(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoria não encontrada com o ID: " + id);
        }
        categoriaRepository.deleteById(id);
    }

    @Transactional
    public void deletarSubcategoria(Long subcategoriaId) {
        if (!subCategoriaRepository.existsById(subcategoriaId)) {
            throw new ResourceNotFoundException("Subcategoria não encontrada com o ID: " + subcategoriaId);
        }
        subCategoriaRepository.deleteById(subcategoriaId);
    }

    private CategoriaResponseDTO toResponseDTO(Categoria categoria) {
        List<SubCategoriaResponseDTO> subs = categoria.getSubcategorias().stream()
                .map(this::toSubResponseDTO)
                .collect(Collectors.toList());
        return new CategoriaResponseDTO(categoria.getId(), categoria.getNome(), subs);
    }

    private SubCategoriaResponseDTO toSubResponseDTO(SubCategoria sub) {
        return new SubCategoriaResponseDTO(sub.getId(), sub.getNome(), sub.getCategoria().getId());
    }
}
