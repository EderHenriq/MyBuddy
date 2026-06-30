import { Injectable, inject } from '@angular/core';
import { Observable, of, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { ApiService } from './api.service';

export interface ProdutoRequest {
  nome: string;
  descricao: string;
  preco: number;
  estoque: number;
  subCategoriaId: number;
  imagens: string[];
  marca?: string;
  origem?: string;
  porteRaca?: string;
  peso?: string;
  idade?: string;
}

@Injectable({
  providedIn: 'root',
})
export class ProdutoService {
  private api = inject(ApiService);
  private localProdutosKey = 'mybuddy_produtos_local';

  constructor() {
    this.inicializarProdutosLocais();
  }

  buscarComFiltros(filtros: any = {}): Observable<any[]> {
    let path = 'produtos';
    const params: string[] = [];
    if (filtros.busca) params.push(`busca=${encodeURIComponent(filtros.busca)}`);
    if (filtros.categoriaId) params.push(`categoriaId=${filtros.categoriaId}`);
    if (filtros.subCategoriaId) params.push(`subCategoriaId=${filtros.subCategoriaId}`);
    if (filtros.petshopId) params.push(`petshopId=${filtros.petshopId}`);
    if (filtros.precoMin) params.push(`precoMin=${filtros.precoMin}`);
    if (filtros.precoMax) params.push(`precoMax=${filtros.precoMax}`);

    if (params.length > 0) {
      path += '?' + params.join('&');
    }

    return this.api.get<any>(path).pipe(
      map(res => {
        if (res && res.content) {
          return res.content;
        }
        return res;
      }),
      catchError(err => {
        console.warn('[ProdutoService] Erro ao buscar produtos da API. Usando mock local.', err);
        return of(this.obterProdutosLocaisFiltrados(filtros));
      }),
    );
  }

  buscarPorId(id: number): Observable<any> {
    return this.api.get<any>(`produtos/${id}`).pipe(
      catchError(err => {
        console.warn(`[ProdutoService] Erro ao buscar produto #${id} da API. Usando mock local.`, err);
        const produto = this.obterProdutosLocais().find(p => p.id === id);
        if (produto) return of(produto);
        return throwError(() => new Error('Produto não encontrado no mock local.'));
      }),
    );
  }

  criar(request: ProdutoRequest): Observable<any> {
    return this.api.post<any>('produtos', request).pipe(
      catchError(err => {
        console.warn('[ProdutoService] Erro ao criar produto na API. Usando mock local.', err);
        const novoProduto = this.criarMockLocal(request);
        return of(novoProduto);
      }),
    );
  }

  atualizar(id: number, request: ProdutoRequest): Observable<any> {
    return this.api.put<any>(`produtos/${id}`, request).pipe(
      catchError(err => {
        console.warn(`[ProdutoService] Erro ao atualizar produto #${id} na API. Usando mock local.`, err);
        const atualizado = this.atualizarMockLocal(id, request);
        return of(atualizado);
      }),
    );
  }

  deletar(id: number): Observable<void> {
    return this.api.delete<void>(`produtos/${id}`).pipe(
      catchError(err => {
        console.warn(`[ProdutoService] Erro ao deletar produto #${id} na API. Usando mock local.`, err);
        this.deletarMockLocal(id);
        return of(undefined);
      }),
    );
  }

  buscarCategorias(): Observable<any[]> {
    return this.api.get<any[]>('categorias').pipe(
      catchError(err => {
        console.warn('[ProdutoService] Erro ao buscar categorias da API. Usando mock local.', err);
        return of([
          { id: 1, nome: 'Alimentação', subcategorias: [{ id: 1, nome: 'Ração' }] },
          { id: 2, nome: 'Acessórios', subcategorias: [{ id: 2, nome: 'Coleiras' }] },
          { id: 3, nome: 'Brinquedos', subcategorias: [{ id: 3, nome: 'Bolas e Pelúcias' }] },
          { id: 4, nome: 'Farmácia', subcategorias: [{ id: 4, nome: 'Antipulgas' }] },
          { id: 5, nome: 'Higiene', subcategorias: [{ id: 5, nome: 'Tapetes' }] },
        ]);
      }),
    );
  }

  avaliarProduto(produtoId: number, request: { nota: number; comentario: string }): Observable<any> {
    return this.api.post<any>(`produtos/${produtoId}/avaliacoes`, request).pipe(
      catchError(err => {
        console.warn(`[ProdutoService] Falha ao enviar avaliacao para produto #${produtoId}. Usando mock local.`, err);
        const prods = this.obterProdutosLocais();
        const idx = prods.findIndex(p => p.id === produtoId);
        if (idx !== -1) {
          if (!prods[idx].avaliacoes) {
            prods[idx].avaliacoes = [];
          }
          prods[idx].avaliacoes.push({
            autor: 'Cliente Buddy',
            nota: request.nota,
            data: new Date().toLocaleDateString('pt-BR'),
            texto: request.comentario,
          });
          const notas = prods[idx].avaliacoes.map((av: any) => av.nota);
          prods[idx].notaMedia = notas.reduce((a: number, b: number) => a + b, 0) / notas.length;
          prods[idx].totalAvaliacoes = prods[idx].avaliacoes.length;
          this.salvarProdutosLocais(prods);
          return of(prods[idx]);
        }
        return throwError(() => new Error('Produto não encontrado no mock local.'));
      }),
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
      const termosBusca = this.normalizarTexto(filtros.busca)
        .split(/\s+/)
        .filter(termo => termo.length > 1);

      prods = prods.filter(p => {
        const textoProduto = this.normalizarTexto(
          [p.nome, p.descricao, p.categoriaNome, p.subCategoriaNome, p.petshopNome, p.marca, p.status].filter(Boolean).join(' '),
        );

        return termosBusca.every(termo => textoProduto.includes(termo));
      });
    }

    if (filtros.categoriaId) {
      prods = prods.filter(p => p.categoriaId === Number(filtros.categoriaId));
    }

    if (filtros.subCategoriaId) {
      prods = prods.filter(p => p.subCategoriaId === Number(filtros.subCategoriaId));
    }

    if (filtros.petshopId) {
      prods = prods.filter(p => p.petshopId === Number(filtros.petshopId));
    }

    if (filtros.precoMin) {
      prods = prods.filter(p => p.preco >= Number(filtros.precoMin));
    }

    if (filtros.precoMax) {
      prods = prods.filter(p => p.preco <= Number(filtros.precoMax));
    }

    return prods;
  }

  private normalizarTexto(valor: string | number | undefined | null): string {
    return String(valor ?? '')
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .toLowerCase()
      .trim();
  }

  private criarMockLocal(request: ProdutoRequest): any {
    const prods = this.obterProdutosLocais();
    const novoId = prods.length > 0 ? Math.max(...prods.map(p => p.id)) + 1 : 1;

    const novo = {
      id: novoId,
      nome: request.nome,
      descricao: request.descricao,
      preco: request.preco,
      estoque: request.estoque,
      status: request.estoque > 0 ? 'ATIVO' : 'ESGOTADO',
      subCategoriaId: request.subCategoriaId,
      subCategoriaNome: this.obterNomeCategoriaLocal(request.subCategoriaId),
      categoriaId: request.subCategoriaId,
      categoriaNome: this.obterNomeCategoriaLocal(request.subCategoriaId),
      petshopId: 1,
      petshopNome: 'PetLovers Shop',
      imagens: request.imagens && request.imagens.length > 0 ? request.imagens : ['/assets/placeholders/pets/purebred-dog-being-cute-studio.jpg'],
      notaMedia: 4.8,
      marca: request.marca || '',
      origem: request.origem || '',
      porteRaca: request.porteRaca || '',
      peso: request.peso || '',
      idade: request.idade || '',
    };

    prods.unshift(novo);
    this.salvarProdutosLocais(prods);
    return novo;
  }

  private obterNomeCategoriaLocal(id: number): string {
    const categorias: Record<number, string> = {
      1: 'Rações',
      2: 'Petiscos',
      3: 'Brinquedos',
      4: 'Farmácia',
      5: 'Higiene',
      6: 'Camas',
    };

    return categorias[id] || 'Geral';
  }

  private atualizarMockLocal(id: number, request: ProdutoRequest): any {
    const prods = this.obterProdutosLocais();
    const idx = prods.findIndex(p => p.id === id);
    if (idx !== -1) {
      prods[idx] = {
        ...prods[idx],
        nome: request.nome,
        descricao: request.descricao,
        preco: request.preco,
        estoque: request.estoque,
        status: request.estoque > 0 ? 'ATIVO' : 'ESGOTADO',
        subCategoriaId: request.subCategoriaId,
        imagens: request.imagens && request.imagens.length > 0 ? request.imagens : prods[idx].imagens,
        marca: request.marca,
        origem: request.origem,
        porteRaca: request.porteRaca,
        peso: request.peso,
        idade: request.idade,
      };
      this.salvarProdutosLocais(prods);
      return prods[idx];
    }
    throw new Error('Produto não encontrado');
  }

  private deletarMockLocal(id: number): void {
    let prods = this.obterProdutosLocais();
    prods = prods.filter(p => p.id !== id);
    this.salvarProdutosLocais(prods);
  }

  private inicializarProdutosLocais(): void {
    const prods = this.obterProdutosLocais();
    const contemUnsplash = prods.some((p: any) => p.imagens && p.imagens.some((img: string) => img.includes('unsplash.com')));
    const faltaMarca = prods.length > 0 && !prods[0].hasOwnProperty('marca');
    const faltaPrecoAntigo = prods.length > 0 && !prods.some((p: any) => p.precoAntigo);
    if (prods.length < 16 || contemUnsplash || faltaMarca || faltaPrecoAntigo) {
      const mockInicial = [
        {
          id: 1,
          nome: 'Ração Premier Formula Cães Adultos Frango',
          descricao: 'Alimento completo de alta qualidade para cães adultos de porte médio e grande.',
          preco: 189.9,
          precoAntigo: 229.9,
          estoque: 24,
          status: 'ATIVO',
          subCategoriaId: 1,
          subCategoriaNome: 'Rações',
          categoriaId: 1,
          categoriaNome: 'Rações',
          petshopId: 1,
          petshopNome: 'Petz',
          imagens: ['/assets/placeholders/pets/purebred-dog-being-cute-studio.jpg'],
          notaMedia: 4.8,
          marca: 'Premier',
        },
        {
          id: 2,
          nome: 'Antipulgas Bravecto para Cães 10 a 20kg',
          descricao: 'Comprimido mastigável que elimina pulgas e carrapatos de forma rápida e segura por até 12 semanas.',
          preco: 215.5,
          estoque: 15,
          status: 'ATIVO',
          subCategoriaId: 4,
          subCategoriaNome: 'Farmácia',
          categoriaId: 4,
          categoriaNome: 'Farmácia',
          petshopId: 2,
          petshopNome: 'Cobasi',
          imagens: ['/assets/placeholders/pets/Border Collie 01.webp'],
          notaMedia: 4.9,
          marca: 'Bravecto',
        },
        {
          id: 3,
          nome: 'Tapete Higiênico Super Seco 30 unidades',
          descricao: 'Tapete de alta absorção com atrativo canino para educar o cão a fazer as necessidades no local certo.',
          preco: 49.9,
          precoAntigo: 65.9,
          estoque: 0,
          status: 'ESGOTADO',
          subCategoriaId: 5,
          subCategoriaNome: 'Higiene',
          categoriaId: 5,
          categoriaNome: 'Higiene',
          petshopId: 1,
          petshopNome: 'Petz',
          imagens: ['/assets/placeholders/pets/adocao-coelho.jpg'],
          notaMedia: 4.5,
          marca: 'Petz',
        },
        {
          id: 4,
          nome: 'Bolinha de Tênis Chalesco para Cães',
          descricao: 'Brinquedo de borracha super resistente para cães que adoram morder e correr atrás de bolinhas.',
          preco: 15.9,
          estoque: 80,
          status: 'ATIVO',
          subCategoriaId: 3,
          subCategoriaNome: 'Brinquedos',
          categoriaId: 3,
          categoriaNome: 'Brinquedos',
          petshopId: 4,
          petshopNome: 'Bicho Chic',
          imagens: ['/assets/placeholders/pets/Paçoca.jpg'],
          notaMedia: 4.7,
          marca: 'Chalesco',
        },
        {
          id: 5,
          nome: 'Areia Higiênica Pipicat Floral 4kg',
          descricao: 'Areia sanitária perfumada para controle superior de odores e torrões firmes.',
          preco: 22.9,
          estoque: 35,
          status: 'ATIVO',
          subCategoriaId: 5,
          subCategoriaNome: 'Higiene',
          categoriaId: 5,
          categoriaNome: 'Higiene',
          petshopId: 2,
          petshopNome: 'Cobasi',
          imagens: ['/assets/placeholders/pets/gato-laranja-e1748043537291.webp'],
          notaMedia: 4.6,
          marca: 'Pipicat',
        },
        {
          id: 6,
          nome: 'Ração Golden Gatos Adultos Frango 10kg',
          descricao: 'Formulação balanceada para atender as necessidades nutricionais de gatos adultos.',
          preco: 139.9,
          estoque: 12,
          status: 'ATIVO',
          subCategoriaId: 1,
          subCategoriaNome: 'Rações',
          categoriaId: 1,
          categoriaNome: 'Rações',
          petshopId: 3,
          petshopNome: 'Petlove',
          imagens: ['/assets/placeholders/pets/kitty-with-monochrome-wall-her.jpg'],
          notaMedia: 4.8,
          marca: 'Golden',
        },
        {
          id: 7,
          nome: 'Coleira Antipulgas Seresto Cães Até 8kg',
          descricao: 'Proteção contínua contra pulgas e carrapatos por até 8 meses.',
          preco: 249.9,
          estoque: 8,
          status: 'ATIVO',
          subCategoriaId: 4,
          subCategoriaNome: 'Farmácia',
          categoriaId: 4,
          categoriaNome: 'Farmácia',
          petshopId: 1,
          petshopNome: 'Petz',
          imagens: ['/assets/placeholders/pets/Kira.jpg'],
          notaMedia: 4.7,
          marca: 'Seresto',
        },
        {
          id: 8,
          nome: 'Arranhador de Papelão Rampa Gatos',
          descricao: 'Diversão e cuidado com as unhas do seu felino em um design prático e resistente.',
          preco: 35.0,
          precoAntigo: 45.0,
          estoque: 20,
          status: 'ATIVO',
          subCategoriaId: 3,
          subCategoriaNome: 'Brinquedos',
          categoriaId: 3,
          categoriaNome: 'Brinquedos',
          petshopId: 5,
          petshopNome: 'Casa do Criador',
          imagens: ['/assets/placeholders/pets/gato-laranja-e1748043537291.webp'],
          notaMedia: 4.6,
          marca: 'Zee.Dog',
        },
        {
          id: 9,
          nome: 'Ração Golden Special Cães Adultos',
          descricao: 'Formulada com ingredientes de excelente digestibilidade e sabor inigualável.',
          preco: 139.9,
          precoAntigo: 159.9,
          estoque: 40,
          status: 'ATIVO',
          subCategoriaId: 1,
          subCategoriaNome: 'Rações',
          categoriaId: 1,
          categoriaNome: 'Rações',
          petshopId: 2,
          petshopNome: 'Cobasi',
          imagens: ['/assets/placeholders/pets/purebred-dog-being-cute-studio.jpg'],
          notaMedia: 4.5,
          marca: 'Golden',
        },
        {
          id: 10,
          nome: 'Tapete Higiênico Super Premium',
          descricao: 'Alta absorção e controle de odores para manter sua casa sempre limpa.',
          preco: 45.9,
          estoque: 18,
          status: 'ATIVO',
          subCategoriaId: 5,
          subCategoriaNome: 'Higiene',
          categoriaId: 5,
          categoriaNome: 'Higiene',
          petshopId: 1,
          petshopNome: 'Petz',
          imagens: ['/assets/placeholders/pets/Border Collie 01.webp'],
          notaMedia: 4.8,
          marca: 'Petz',
        },
        {
          id: 11,
          nome: 'Brinquedo Mordedor Osso Borracha',
          descricao: 'Mordedor resistente com texturas que auxiliam na higiene bucal do cão.',
          preco: 22.9,
          estoque: 50,
          status: 'ATIVO',
          subCategoriaId: 3,
          subCategoriaNome: 'Brinquedos',
          categoriaId: 3,
          categoriaNome: 'Brinquedos',
          petshopId: 2,
          petshopNome: 'Cobasi',
          imagens: ['/assets/placeholders/pets/Paçoca.jpg'],
          notaMedia: 4.4,
          marca: 'Zee.Dog',
        },
        {
          id: 12,
          nome: 'Ração Royal Canin Gatos Castrados',
          descricao: 'Alimento completo para gatos adultos castrados dos 1 aos 7 anos de idade.',
          preco: 219.9,
          precoAntigo: 249.9,
          estoque: 14,
          status: 'ATIVO',
          subCategoriaId: 1,
          subCategoriaNome: 'Rações',
          categoriaId: 1,
          categoriaNome: 'Rações',
          petshopId: 1,
          petshopNome: 'Petz',
          imagens: ['/assets/placeholders/pets/kitty-with-monochrome-wall-her.jpg'],
          notaMedia: 4.9,
          marca: 'Royal Canin',
        },
        {
          id: 13,
          nome: 'Areia Higiênica Viva Verde!',
          descricao: 'Areia biodegradável de milho e mandioca com eliminação total do odor de amônia.',
          preco: 49.9,
          estoque: 30,
          status: 'ATIVO',
          subCategoriaId: 5,
          subCategoriaNome: 'Higiene',
          categoriaId: 5,
          categoriaNome: 'Higiene',
          petshopId: 3,
          petshopNome: 'Petlove',
          imagens: ['/assets/placeholders/pets/gato-laranja-e1748043537291.webp'],
          notaMedia: 4.8,
          marca: 'Viva Verde!',
        },
        {
          id: 14,
          nome: 'Cama Pet Conforto Redonda G',
          descricao: 'Oferece o máximo de conforto para o descanso saudável do seu pet.',
          preco: 110.0,
          estoque: 10,
          status: 'ATIVO',
          subCategoriaId: 6,
          subCategoriaNome: 'Camas',
          categoriaId: 6,
          categoriaNome: 'Camas',
          petshopId: 2,
          petshopNome: 'Cobasi',
          imagens: ['/assets/placeholders/pets/Armindo.png'],
          notaMedia: 4.7,
          marca: 'Zee.Dog',
        },
        {
          id: 15,
          nome: 'Petisco Dreamies Sabor Salmão',
          descricao: 'Petiscos crocantes por fora e macios por dentro para fazer a alegria do seu gato.',
          preco: 6.5,
          estoque: 100,
          status: 'ATIVO',
          subCategoriaId: 2,
          subCategoriaNome: 'Petiscos',
          categoriaId: 2,
          categoriaNome: 'Petiscos',
          petshopId: 1,
          petshopNome: 'Petz',
          imagens: ['/assets/placeholders/pets/gato-laranja-e1748043537291.webp'],
          notaMedia: 4.8,
          marca: 'Dreamies',
        },
        {
          id: 16,
          nome: 'Shampoo Neutro Pelos Claros',
          descricao: 'Shampoo formulado especialmente para cães e gatos de pelagem clara, realçando o brilho natural.',
          preco: 34.9,
          estoque: 16,
          status: 'ATIVO',
          subCategoriaId: 5,
          subCategoriaNome: 'Higiene',
          categoriaId: 5,
          categoriaNome: 'Higiene',
          petshopId: 4,
          petshopNome: 'Bicho Chic',
          imagens: ['/assets/placeholders/pets/Border Collie 01.webp'],
          notaMedia: 4.5,
          marca: 'Bicho Chic',
        },
      ];
      this.salvarProdutosLocais(mockInicial);
    }
  }
}
