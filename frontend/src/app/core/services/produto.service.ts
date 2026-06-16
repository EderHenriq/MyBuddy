import { Injectable, inject } from "@angular/core";
import { Observable, of, throwError } from "rxjs";
import { catchError, map } from "rxjs/operators";
import { ApiService } from "./api.service";

export interface ProdutoRequest {
  nome: string;
  descricao: string;
  preco: number;
  estoque: number;
  subCategoriaId: number;
  imagens: string[];
}

@Injectable({
  providedIn: "root",
})
export class ProdutoService {
  private api = inject(ApiService);
  private localProdutosKey = "mybuddy_produtos_local";

  constructor() {
    this.inicializarProdutosLocais();
  }

  buscarComFiltros(filtros: any = {}): Observable<any[]> {
    let path = "produtos";
    const params: string[] = [];
    if (filtros.busca) params.push(`busca=${encodeURIComponent(filtros.busca)}`);
    if (filtros.categoriaId) params.push(`categoriaId=${filtros.categoriaId}`);
    if (filtros.subCategoriaId) params.push(`subCategoriaId=${filtros.subCategoriaId}`);
    if (filtros.petshopId) params.push(`petshopId=${filtros.petshopId}`);
    if (filtros.precoMin) params.push(`precoMin=${filtros.precoMin}`);
    if (filtros.precoMax) params.push(`precoMax=${filtros.precoMax}`);

    if (params.length > 0) {
      path += "?" + params.join("&");
    }

    return this.api.get<any>(path).pipe(
      map((res) => {
        if (res && res.content) {
          return res.content;
        }
        return res;
      }),
      catchError((err) => {
        console.warn("[ProdutoService] Erro ao buscar produtos da API. Usando mock local.", err);
        return of(this.obterProdutosLocaisFiltrados(filtros));
      })
    );
  }

  buscarPorId(id: number): Observable<any> {
    return this.api.get<any>(`produtos/${id}`).pipe(
      catchError((err) => {
        console.warn(`[ProdutoService] Erro ao buscar produto #${id} da API. Usando mock local.`, err);
        const produto = this.obterProdutosLocais().find((p) => p.id === id);
        if (produto) return of(produto);
        return throwError(() => new Error("Produto não encontrado no mock local."));
      })
    );
  }

  criar(request: ProdutoRequest): Observable<any> {
    return this.api.post<any>("produtos", request).pipe(
      catchError((err) => {
        console.warn("[ProdutoService] Erro ao criar produto na API. Usando mock local.", err);
        const novoProduto = this.criarMockLocal(request);
        return of(novoProduto);
      })
    );
  }

  atualizar(id: number, request: ProdutoRequest): Observable<any> {
    return this.api.put<any>(`produtos/${id}`, request).pipe(
      catchError((err) => {
        console.warn(`[ProdutoService] Erro ao atualizar produto #${id} na API. Usando mock local.`, err);
        const atualizado = this.atualizarMockLocal(id, request);
        return of(atualizado);
      })
    );
  }

  deletar(id: number): Observable<void> {
    return this.api.delete<void>(`produtos/${id}`).pipe(
      catchError((err) => {
        console.warn(`[ProdutoService] Erro ao deletar produto #${id} na API. Usando mock local.`, err);
        this.deletarMockLocal(id);
        return of(undefined);
      })
    );
  }

  buscarCategorias(): Observable<any[]> {
    return this.api.get<any[]>("categorias").pipe(
      catchError((err) => {
        console.warn("[ProdutoService] Erro ao buscar categorias da API. Usando mock local.", err);
        return of([
          { id: 1, nome: "Alimentação", subcategorias: [{ id: 1, nome: "Ração" }] },
          { id: 2, nome: "Acessórios", subcategorias: [{ id: 2, nome: "Coleiras" }] },
          { id: 3, nome: "Brinquedos", subcategorias: [{ id: 3, nome: "Bolas e Pelúcias" }] },
          { id: 4, nome: "Farmácia", subcategorias: [{ id: 4, nome: "Antipulgas" }] },
          { id: 5, nome: "Higiene", subcategorias: [{ id: 5, nome: "Tapetes" }] },
        ]);
      })
    );
  }

  avaliarProduto(produtoId: number, request: { nota: number; comentario: string }): Observable<any> {
    return this.api.post<any>(`produtos/${produtoId}/avaliacoes`, request).pipe(
      catchError((err) => {
        console.warn(`[ProdutoService] Falha ao enviar avaliacao para produto #${produtoId}. Usando mock local.`, err);
        const prods = this.obterProdutosLocais();
        const idx = prods.findIndex((p) => p.id === produtoId);
        if (idx !== -1) {
          if (!prods[idx].avaliacoes) {
            prods[idx].avaliacoes = [];
          }
          prods[idx].avaliacoes.push({
            autor: "Cliente Buddy",
            nota: request.nota,
            data: new Date().toLocaleDateString("pt-BR"),
            texto: request.comentario
          });
          const notas = prods[idx].avaliacoes.map((av: any) => av.nota);
          prods[idx].notaMedia = notas.reduce((a: number, b: number) => a + b, 0) / notas.length;
          prods[idx].totalAvaliacoes = prods[idx].avaliacoes.length;
          this.salvarProdutosLocais(prods);
          return of(prods[idx]);
        }
        return throwError(() => new Error("Produto não encontrado no mock local."));
      })
    );
  }

  // MOCK HELPERS
  private obterProdutosLocais(): any[] {
    const data = localStorage.getItem(this.localProdutosKey);
    return data ? JSON.parse(data) : [];
  }

  private salvarProdutosLocais(produtos: any[]): void {
    localStorage.setItem(this.localProdutosKey, JSON.stringify(produtos));
  }

  private obterProdutosLocaisFiltrados(filtros: any): any[] {
    let prods = this.obterProdutosLocais();

    if (filtros.busca) {
      const buscaLower = filtros.busca.toLowerCase();
      prods = prods.filter(
        (p) =>
          p.nome.toLowerCase().includes(buscaLower) ||
          (p.descricao && p.descricao.toLowerCase().includes(buscaLower))
      );
    }

    if (filtros.categoriaId) {
      prods = prods.filter((p) => p.categoriaId === Number(filtros.categoriaId));
    }

    if (filtros.subCategoriaId) {
      prods = prods.filter((p) => p.subCategoriaId === Number(filtros.subCategoriaId));
    }

    return prods;
  }

  private criarMockLocal(request: ProdutoRequest): any {
    const prods = this.obterProdutosLocais();
    const novoId = prods.length > 0 ? Math.max(...prods.map((p) => p.id)) + 1 : 1;

    const novo = {
      id: novoId,
      nome: request.nome,
      descricao: request.descricao,
      preco: request.preco,
      estoque: request.estoque,
      status: request.estoque > 0 ? "ATIVO" : "ESGOTADO",
      subCategoriaId: request.subCategoriaId,
      subCategoriaNome: request.subCategoriaId === 1 ? "Ração" : "Coleiras",
      categoriaId: request.subCategoriaId === 1 ? 1 : 2,
      categoriaNome: request.subCategoriaId === 1 ? "Alimentação" : "Acessórios",
      petshopId: 1,
      petshopNome: "PetLovers Shop",
      imagens: request.imagens && request.imagens.length > 0 
        ? request.imagens 
        : ["https://images.unsplash.com/photo-1589924691995-400dc9ecc119?auto=format&fit=crop&q=80&w=600"],
      notaMedia: 4.8,
    };

    prods.unshift(novo);
    this.salvarProdutosLocais(prods);
    return novo;
  }

  private atualizarMockLocal(id: number, request: ProdutoRequest): any {
    const prods = this.obterProdutosLocais();
    const idx = prods.findIndex((p) => p.id === id);
    if (idx !== -1) {
      prods[idx] = {
        ...prods[idx],
        nome: request.nome,
        descricao: request.descricao,
        preco: request.preco,
        estoque: request.estoque,
        status: request.estoque > 0 ? "ATIVO" : "ESGOTADO",
        subCategoriaId: request.subCategoriaId,
        imagens: request.imagens && request.imagens.length > 0 ? request.imagens : prods[idx].imagens,
      };
      this.salvarProdutosLocais(prods);
      return prods[idx];
    }
    throw new Error("Produto não encontrado");
  }

  private deletarMockLocal(id: number): void {
    let prods = this.obterProdutosLocais();
    prods = prods.filter((p) => p.id !== id);
    this.salvarProdutosLocais(prods);
  }

  private inicializarProdutosLocais(): void {
    const prods = this.obterProdutosLocais();
    if (prods.length === 0) {
      const mockInicial = [
        {
          id: 1,
          nome: "Ração Premier Formula Cães Adultos Frango",
          descricao: "Alimento completo de alta qualidade para cães adultos de porte médio e grande.",
          preco: 189.9,
          estoque: 24,
          status: "ATIVO",
          subCategoriaId: 1,
          subCategoriaNome: "Ração",
          categoriaId: 1,
          categoriaNome: "Alimentação",
          petshopId: 1,
          petshopNome: "Petz",
          imagens: ["https://images.unsplash.com/photo-1589924691995-400dc9ecc119?auto=format&fit=crop&q=80&w=600"],
          notaMedia: 4.8
        },
        {
          id: 2,
          nome: "Antipulgas Bravecto para Cães 10 a 20kg",
          descricao: "Comprimido mastigável que elimina pulgas e carrapatos de forma rápida e segura por até 12 semanas.",
          preco: 215.5,
          estoque: 15,
          status: "ATIVO",
          subCategoriaId: 2,
          subCategoriaNome: "Coleiras",
          categoriaId: 2,
          categoriaNome: "Acessórios",
          petshopId: 2,
          petshopNome: "Cobasi",
          imagens: ["https://images.unsplash.com/photo-1581888227599-779811939961?auto=format&fit=crop&q=80&w=600"],
          notaMedia: 4.9
        },
        {
          id: 3,
          nome: "Tapete Higiênico Super Seco 30 unidades",
          descricao: "Tapete de alta absorção com atrativo canino para educar o cão a fazer as necessidades no local certo.",
          preco: 49.9,
          estoque: 0,
          status: "ESGOTADO",
          subCategoriaId: 1,
          subCategoriaNome: "Ração",
          categoriaId: 1,
          categoriaNome: "Alimentação",
          petshopId: 1,
          petshopNome: "Petz",
          imagens: ["https://images.unsplash.com/photo-1576201836106-db1758fd1c97?auto=format&fit=crop&q=80&w=600"],
          notaMedia: 4.5
        },
        {
          id: 4,
          nome: "Bolinha de Tênis Chalesco para Cães",
          descricao: "Brinquedo de borracha super resistente para cães que adoram morder e correr atrás de bolinhas.",
          preco: 15.9,
          estoque: 80,
          status: "ATIVO",
          subCategoriaId: 2,
          subCategoriaNome: "Coleiras",
          categoriaId: 2,
          categoriaNome: "Acessórios",
          petshopId: 1,
          petshopNome: "PetLovers Shop",
          imagens: ["https://images.unsplash.com/photo-1537151608804-ea6f23b7b6c5?auto=format&fit=crop&q=80&w=600"],
          notaMedia: 4.7
        },
        {
          id: 5,
          nome: "Cama Pet Conforto Redonda G",
          descricao: "Caminha estofada super aconchegante com almofada lavável para o bem-estar do seu pet.",
          preco: 110.0,
          estoque: 5,
          status: "ATIVO",
          subCategoriaId: 2,
          subCategoriaNome: "Coleiras",
          categoriaId: 2,
          categoriaNome: "Acessórios",
          petshopId: 2,
          petshopNome: "Cobasi",
          imagens: ["https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&q=80&w=600"],
          notaMedia: 4.6
        }
      ];
      this.salvarProdutosLocais(mockInicial);
    }
  }
}
