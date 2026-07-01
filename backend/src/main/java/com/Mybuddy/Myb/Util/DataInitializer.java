package com.Mybuddy.Myb.Util;

import com.Mybuddy.Myb.Model.*;
import com.Mybuddy.Myb.Repository.jpa.*;
import com.Mybuddy.Myb.Repository.mongo.*;
import com.Mybuddy.Myb.Security.ERole;
import com.Mybuddy.Myb.Security.Role;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Seed de dados para ambiente de desenvolvimento.
 * NÃO é executado em produção (profile docker/prod).
 * Usuários criados aqui usam senha local "Senha123" — 
 * em produção a autenticação é exclusivamente via Keycloak.
 */

@Configuration
@Profile("dev")
@RequiredArgsConstructor
@SuppressWarnings("null")
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder encoder;
    private final OrganizacaoRepository organizacaoRepository;
    private final PetRepository petRepository;
    private final PetshopRepository petshopRepository;
    private final ProdutoRepository produtoRepository;
    private final EventoOngRepository eventoOngRepository;
    private final ChatRepository chatRepository;
    private final CategoriaRepository categoriaRepository;
    private final SubCategoriaRepository subCategoriaRepository;

    @PostConstruct
    public void initData() {
        log.info("Iniciando inicialização de dados...");

        Role adminRole = new Role(ERole.ROLE_ADMIN);
        Role ongRole = new Role(ERole.ROLE_ONG);
        Role adotanteRole = new Role(ERole.ROLE_ADOTANTE);
        Role petshopRole = new Role(ERole.ROLE_PETSHOP);

        // 2. Cria uma Organização de teste usando o Padrão BUILDER do Lombok
        Organizacao myBuddyOrg = organizacaoRepository.findByCnpj("11.222.333/0001-44")
                .orElseGet(() -> {
                    log.info("Criando Organização MyBuddy via Builder...");
                    return organizacaoRepository.save(Organizacao.builder()
                            .nomeFantasia("MyBuddy ONG Principal")
                            .emailContato("contato@mybuddy.com")
                            .cnpj("11.222.333/0001-44")
                            .telefoneContato("(11) 98765-4321")
                            .endereco("Rua das Flores, 123 - Centro, SP")
                            .descricao("Organização dedicada ao resgate e adoção de animais.")
                            .website("http://www.mybuddy.com")
                            .build());
                });

        // 3. Cria Usuários de teste
        // Nota: Se a classe Usuario também usar @AllArgsConstructor do Lombok,
        // certifique-se de que a ordem dos parâmetros está correta ou use o Builder nela também.

        if (usuarioRepository.findByEmail("admin@mybuddy.com").isEmpty()) {
            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(adminRole);

            Usuario adminUser = new Usuario(
                    "Administrador MyBuddy",
                    "admin@mybuddy.com",
                    "(11) 99999-9999",
                    encoder.encode("Senha123"),
                    null,
                    adminRoles
            );
            usuarioRepository.save(adminUser);
            log.info("Usuário Admin criado!");
        }

        if (usuarioRepository.findByEmail("ong@mybuddy.com").isEmpty()) {
            Set<Role> ongRoles = new HashSet<>();
            ongRoles.add(ongRole);

            Usuario ongUser = new Usuario(
                    "ONG Teste",
                    "ong@mybuddy.com",
                    "(11) 98888-8888",
                    encoder.encode("Senha123"),
                    myBuddyOrg,
                    ongRoles
            );
            usuarioRepository.save(ongUser);
            log.info("Usuário ONG criado!");
        }

        if (usuarioRepository.findByEmail("user@mybuddy.com").isEmpty()) {
            Set<Role> adotanteRoles = new HashSet<>();
            adotanteRoles.add(adotanteRole);

            Usuario adotanteUser = new Usuario(
                    "Adotante Teste",
                    "user@mybuddy.com",
                    "(11) 97777-7777",
                    encoder.encode("Senha123"),
                    null,
                    adotanteRoles
            );
            usuarioRepository.save(adotanteUser);
            log.info("Usuário Adotante criado!");
        }

        // 4. Criação de Petshop e Produtos
        Petshop petshop = petshopRepository.findByCnpj("22.333.444/0001-55")
                .orElseGet(() -> {
                    log.info("Criando Petshop...");
                    return petshopRepository.save(Petshop.builder()
                            .nomeFantasia("PetLovers Shop")
                            .emailContato("vendas@petlovers.com")
                            .cnpj("22.333.444/0001-55")
                            .telefoneContato("(11) 91234-5678")
                            .endereco("Av Paulista, 1000 - SP")
                            .website("http://www.petlovers.com")
                            .build());
                });

        if (usuarioRepository.findByEmail("petshop@mybuddy.com").isEmpty()) {
            Set<Role> roles = new HashSet<>();
            roles.add(petshopRole);

            Usuario petshopUser = new Usuario(
                    "Petshop Parceiro",
                    "petshop@mybuddy.com",
                    "(11) 96666-6666",
                    encoder.encode("Senha123"),
                    null,
                    roles
            );
            petshopUser.setPetshopId(petshop.getId());
            usuarioRepository.save(petshopUser);
            log.info("Usuário Petshop criado!");
        }

        // Inicialização de Categorias e SubCategorias
        Categoria catAlimentacao = categoriaRepository.findByNome("Alimentação")
                .orElseGet(() -> categoriaRepository.save(Categoria.builder().nome("Alimentação").build()));

        Categoria catAcessorios = categoriaRepository.findByNome("Acessórios")
                .orElseGet(() -> categoriaRepository.save(Categoria.builder().nome("Acessórios").build()));

        SubCategoria subRacao = subCategoriaRepository.findByNomeAndCategoriaId("Ração", catAlimentacao.getId())
                .orElseGet(() -> subCategoriaRepository.save(SubCategoria.builder().nome("Ração").categoria(catAlimentacao).build()));

        SubCategoria subColeira = subCategoriaRepository.findByNomeAndCategoriaId("Coleiras", catAcessorios.getId())
                .orElseGet(() -> subCategoriaRepository.save(SubCategoria.builder().nome("Coleiras").categoria(catAcessorios).build()));

        if (produtoRepository.count() == 0) {
            log.info("Criando Produtos reais...");
            Produto prod1 = new Produto();
            prod1.setNome("Ração Golden Premier 15kg");
            prod1.setSubCategoria(subRacao);
            prod1.setPreco(new BigDecimal("149.90"));
            prod1.setEstoque(15);
            prod1.setPetshop(petshop);
            prod1.setStatus(StatusProduto.ATIVO);
            FotoProduto f1 = new FotoProduto();
            f1.setUrl("https://images.unsplash.com/photo-1589924691995-400dc9ecc119?auto=format&fit=crop&q=80&w=600");
            prod1.addFoto(f1);
            produtoRepository.save(prod1);

            Produto prod2 = new Produto();
            prod2.setNome("Coleira Anti-pulgas");
            prod2.setSubCategoria(subColeira);
            prod2.setPreco(new BigDecimal("89.90"));
            prod2.setEstoque(0);
            prod2.setPetshop(petshop);
            prod2.setStatus(StatusProduto.ATIVO);
            FotoProduto f2 = new FotoProduto();
            f2.setUrl("https://images.unsplash.com/photo-1601633519842-83569502ab45?auto=format&fit=crop&q=80&w=600");
            prod2.addFoto(f2);
            produtoRepository.save(prod2);
        }

        // 5. Criação de Pets vinculados à ONG
        if (petRepository.count() < 3) {
            log.info("Garantindo Pets reais no banco de dados...");
            if (petRepository.findByNome("Zeus").isEmpty()) {
                Pet p1 = new Pet("Zeus", "SRD", 2, Especie.CAO, Porte.MEDIO, "Marrom", "Curta", "Macho", myBuddyOrg, true, true, true, "São Paulo", "SP");
                p1.setDescricao("Zeus é um cãozinho muito alegre, dócil e companheiro. Adora correr em espaços abertos e se dá muito bem com outros cachorros.");
                FotoPet f1 = new FotoPet();
                f1.setUrl("https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&q=80&w=600");
                f1.setPrincipal(true);
                p1.addFoto(f1);
                petRepository.save(p1);
                log.info("Pet Zeus criado com ID 1!");
            }
            if (petRepository.findByNome("Mia").isEmpty()) {
                Pet p2 = new Pet("Mia", "Persa", 1, Especie.GATO, Porte.PEQUENO, "Branco", "Longa", "Fêmea", myBuddyOrg, true, true, false, "São Paulo", "SP");
                p2.setDescricao("Mia é uma gatinha calma, carinhosa e muito dorminhoca. Perfeita para apartamentos. Gosta de ser escovada e receber carinho.");
                FotoPet f2 = new FotoPet();
                f2.setUrl("https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&q=80&w=600");
                f2.setPrincipal(true);
                p2.addFoto(f2);
                petRepository.save(p2);
                log.info("Pet Mia criado com ID 2!");
            }
            if (petRepository.findByNome("Thor").isEmpty()) {
                Pet p3 = new Pet("Thor", "Golden Retriever", 1, Especie.CAO, Porte.GRANDE, "Dourado", "Longa", "Macho", myBuddyOrg, true, true, true, "São Paulo", "SP");
                p3.setDescricao("Thor é um Golden Retriever brincalhão, inteligente e cheio de energia. Ideal para famílias ativas que adoram passear.");
                FotoPet f3 = new FotoPet();
                f3.setUrl("https://images.unsplash.com/photo-1552053831-71594a27632d?auto=format&fit=crop&q=80&w=600");
                f3.setPrincipal(true);
                p3.addFoto(f3);
                petRepository.save(p3);
                log.info("Pet Thor criado com ID 3!");
            }
        }

        // 6. Criação de Eventos de ONG
        if (eventoOngRepository.count() == 0) {
            log.info("Criando Eventos de ONG...");
            eventoOngRepository.save(new EventoOng(null, "Feirinha de Adoção Inverno", "Parque Ibirapuera", "25 Jul, 2026", "Agendado"));
            eventoOngRepository.save(new EventoOng(null, "Mega Adoção", "Shopping SP", "05 Mai, 2026", "Concluído"));
        }

        // 7. Criação de Chats
        if (chatRepository.count() == 0) {
            log.info("Criando Chats de Suporte...");
            chatRepository.save(new Chat(null, "Ana Souza", "A ração já foi enviada?", "14:32", "Não Lido"));
            chatRepository.save(new Chat(null, "Carlos Lima", "Obrigado pela adoção!", "Ontem", "Lido"));
        }

        log.info("Finalizada inicialização de dados.");
    }
}
