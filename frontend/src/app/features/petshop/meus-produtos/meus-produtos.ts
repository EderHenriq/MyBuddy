import { Component, OnInit, inject } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { ProdutoService } from "../../../core/services/produto.service";
import { DebounceDirective } from "../../../shared/directives/debounce.directive";
import { PaginatorComponent } from "../../../shared/components/paginator/paginator.component";

@Component({
  selector: "app-meus-produtos",
  standalone: true,
  imports: [CommonModule, FormsModule, DebounceDirective, PaginatorComponent],
  templateUrl: "./meus-produtos.html",
  styleUrl: "./meus-produtos.scss",
})
export class MeusProdutos implements OnInit {
  produtos: any[] = [];
  categorias: any[] = [];
  private produtoService = inject(ProdutoService);

  currentPage = 1;
  totalPages = 1;
  searchTerm = "";

  // Controle de Modal
  isModalAberto = false;
  modoEdicao = false;
  produtoIdEdicao: number | null = null;
  imagemUrlInput = "";

  formProduto = {
    nome: "",
    descricao: "",
    preco: 0,
    estoque: 0,
    subCategoriaId: 1,
  };

  ngOnInit() {
    this.carregarProdutos();
    this.carregarCategorias();
  }

  carregarProdutos() {
    this.produtoService.buscarComFiltros({ busca: this.searchTerm }).subscribe((data) => {
      this.produtos = data;
      this.totalPages = Math.ceil(data.length / 10) || 1;
    });
  }

  carregarCategorias() {
    this.produtoService.buscarCategorias().subscribe((data) => {
      this.categorias = data;
    });
  }

  onSearch(term: string) {
    this.searchTerm = term;
    this.carregarProdutos();
  }

  onPageChange(page: number) {
    this.currentPage = page;
  }

  abrirModalNovo() {
    this.modoEdicao = false;
    this.produtoIdEdicao = null;
    this.imagemUrlInput = "";
    this.formProduto = {
      nome: "",
      descricao: "",
      preco: 0,
      estoque: 0,
      subCategoriaId: 1,
    };
    this.isModalAberto = true;
  }

  abrirModalEditar(item: any) {
    this.modoEdicao = true;
    this.produtoIdEdicao = item.id;
    this.imagemUrlInput = item.imagens && item.imagens.length > 0 ? item.imagens[0] : "";
    this.formProduto = {
      nome: item.nome,
      descricao: item.descricao || "",
      preco: item.preco,
      estoque: item.estoque,
      subCategoriaId: item.subCategoriaId || 1,
    };
    this.isModalAberto = true;
  }

  fecharModal() {
    this.isModalAberto = false;
  }

  salvarProduto() {
    const request = {
      nome: this.formProduto.nome,
      descricao: this.formProduto.descricao,
      preco: this.formProduto.preco,
      estoque: this.formProduto.estoque,
      subCategoriaId: Number(this.formProduto.subCategoriaId),
      imagens: this.imagemUrlInput ? [this.imagemUrlInput] : [],
    };

    if (this.modoEdicao && this.produtoIdEdicao !== null) {
      this.produtoService.atualizar(this.produtoIdEdicao, request).subscribe({
        next: () => {
          this.fecharModal();
          this.carregarProdutos();
        },
        error: (err) => console.error(err),
      });
    } else {
      this.produtoService.criar(request).subscribe({
        next: () => {
          this.fecharModal();
          this.carregarProdutos();
        },
        error: (err) => console.error(err),
      });
    }
  }

  deletarProduto(id: number) {
    if (confirm("Tem certeza que deseja excluir este produto do catálogo?")) {
      this.produtoService.deletar(id).subscribe({
        next: () => {
          this.carregarProdutos();
        },
        error: (err) => console.error(err),
      });
    }
  }
}
