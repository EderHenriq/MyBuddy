package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.Model.Chat;
import com.Mybuddy.Myb.Model.Pedido;
import com.Mybuddy.Myb.Model.Produto;
import com.Mybuddy.Myb.Repository.mongo.ChatRepository;
import com.Mybuddy.Myb.Repository.jpa.PedidoRepository;
import com.Mybuddy.Myb.Repository.jpa.ProdutoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/petshop")
public class PetshopController {

    private final ProdutoRepository produtoRepository;
    private final PedidoRepository pedidoRepository;
    private final ChatRepository chatRepository;

    public PetshopController(ProdutoRepository produtoRepository, 
                             PedidoRepository pedidoRepository, 
                             ChatRepository chatRepository) {
        this.produtoRepository = produtoRepository;
        this.pedidoRepository = pedidoRepository;
        this.chatRepository = chatRepository;
    }

    @GetMapping("/produtos")
    public ResponseEntity<List<Produto>> getProdutos() {
        return ResponseEntity.ok(produtoRepository.findAll());
    }

    @GetMapping("/pedidos")
    public ResponseEntity<List<Pedido>> getPedidos() {
        return ResponseEntity.ok(pedidoRepository.findAll());
    }

    @GetMapping("/chats")
    public ResponseEntity<List<Chat>> getChats() {
        return ResponseEntity.ok(chatRepository.findAll());
    }
}
