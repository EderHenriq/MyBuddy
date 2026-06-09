import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { Footer } from '@shared/components/footer/footer';
import { CardProdutoComponent } from '@shared/components/card-produto/card-produto.component';
import { CardLojaComponent } from '@shared/components/card-loja/card-loja.component';
import { CategoryCarouselComponent, CategoriaVisual } from '@shared/components/category-carousel/category-carousel.component';
import { CartDrawerComponent } from '@shared/components/cart-drawer/cart-drawer.component';
import { CartService } from '@core/services/cart.service';
import { HeroSectionComponent } from '@shared/components/hero-section/hero-section.component';

interface Loja {
  id: number;
  urlLogo: string;
  nome: string;
  avaliacao: number;
  tempoEntrega: string;
  taxaEntrega?: number;
}

interface Produto {
  id: number;
  urlImagem: string;
  titulo: string;
  preco: number;
  precoAntigo?: number;
  nomeLoja: string;
  badgeDesconto?: string;
  favorito?: boolean;
}

@Component({
  selector: 'app-marketplace',
  standalone: true,
  imports: [
    CommonModule,
    Footer,
    CardProdutoComponent,
    CardLojaComponent,
    CategoryCarouselComponent,
    CartDrawerComponent,
    HeroSectionComponent,
  ],
  templateUrl: './marketplace.html',
  styleUrl: './marketplace.scss',
})
export class Marketplace {
  carrinhoService = inject(CartService);
  enderecoAtual = 'Rua das Flores, 123 - Centro';

  categorias: CategoriaVisual[] = [
    { id: 1, nome: 'Rações', urlImagem: 'https://images.unsplash.com/photo-1589924691995-400dc9ecc119?auto=format&fit=crop&q=80&w=150' },
    { id: 2, nome: 'Petiscos', urlImagem: 'https://images.unsplash.com/photo-1537151608804-ea6f23b7b6c5?auto=format&fit=crop&q=80&w=150' },
    { id: 3, nome: 'Brinquedos', urlImagem: 'https://images.unsplash.com/photo-1576201836106-db1758fd1c97?auto=format&fit=crop&q=80&w=150' },
    { id: 4, nome: 'Farmácia', urlImagem: 'https://images.unsplash.com/photo-1581888227599-779811939961?auto=format&fit=crop&q=80&w=150' },
    { id: 5, nome: 'Higiene', urlImagem: 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&q=80&w=150' },
    { id: 6, nome: 'Camas', urlImagem: 'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&q=80&w=150' },
  ];

  lojas: Loja[] = [
    {
      id: 1,
      urlLogo: 'https://images.unsplash.com/photo-1599305445671-ac291c95aaa9?auto=format&fit=crop&q=80&w=150',
      nome: 'Petz',
      avaliacao: 4.8,
      tempoEntrega: '30-45 min',
      taxaEntrega: 5.9,
    },
    {
      id: 2,
      urlLogo: 'https://images.unsplash.com/photo-1560707854-fb9a10efa532?auto=format&fit=crop&q=80&w=150',
      nome: 'Cobasi',
      avaliacao: 4.9,
      tempoEntrega: '20-30 min',
      taxaEntrega: 0,
    },
    {
      id: 3,
      urlLogo: 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&q=80&w=150',
      nome: 'Petlove',
      avaliacao: 4.7,
      tempoEntrega: '15-25 min',
      taxaEntrega: 7.5,
    },
    {
      id: 4,
      urlLogo: 'https://images.unsplash.com/photo-1541364983171-a8ba01e95cfc?auto=format&fit=crop&q=80&w=150',
      nome: 'Bicho Chic',
      avaliacao: 4.6,
      tempoEntrega: '40-55 min',
      taxaEntrega: 4.0,
    },
    {
      id: 5,
      urlLogo: 'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&q=80&w=150',
      nome: 'Casa do Criador',
      avaliacao: 4.5,
      tempoEntrega: '25-40 min',
      taxaEntrega: 6.9,
    },
  ];

  marcasDestaque = [
    { id: 1, nome: 'Royal Canin', urlLogo: 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?w=100&q=80' },
    { id: 2, nome: 'Premier', urlLogo: 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?w=100&q=80' },
    { id: 3, nome: 'Zee.Dog', urlLogo: 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?w=100&q=80' },
    { id: 4, nome: 'Bravecto', urlLogo: 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?w=100&q=80' },
    { id: 5, nome: 'Golden', urlLogo: 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?w=100&q=80' },
    { id: 6, nome: 'Pedigree', urlLogo: 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?w=100&q=80' },
  ];

  produtosOferta: Produto[] = [
    {
      id: 1,
      urlImagem: 'https://images.unsplash.com/photo-1589924691995-400dc9ecc119?auto=format&fit=crop&q=80&w=300',
      titulo: 'Ração Premier Formula Cães Adultos Frango',
      preco: 189.9,
      precoAntigo: 229.9,
      nomeLoja: 'Petlove',
      badgeDesconto: '17% OFF',
      favorito: false,
    },
    {
      id: 2,
      urlImagem: 'https://images.unsplash.com/photo-1581888227599-779811939961?auto=format&fit=crop&q=80&w=300',
      titulo: 'Antipulgas Bravecto para Cães 10 a 20kg',
      preco: 215.5,
      nomeLoja: 'Cobasi',
      badgeDesconto: 'Frete Grátis',
      favorito: true,
    },
    {
      id: 3,
      urlImagem: 'https://images.unsplash.com/photo-1576201836106-db1758fd1c97?auto=format&fit=crop&q=80&w=300',
      titulo: 'Tapete Higiênico Super Seco 30 unidades',
      preco: 49.9,
      precoAntigo: 65.9,
      nomeLoja: 'Petz',
      badgeDesconto: '24% OFF',
      favorito: false,
    },
    {
      id: 4,
      urlImagem: 'https://images.unsplash.com/photo-1537151608804-ea6f23b7b6c5?auto=format&fit=crop&q=80&w=300',
      titulo: 'Bolinha de Tênis Chalesco para Cães',
      preco: 15.9,
      nomeLoja: 'Bicho Chic',
      favorito: false,
    },
  ];

  maisVendidos: Produto[] = [
    {
      id: 5,
      urlImagem: 'https://images.unsplash.com/photo-1623387641177-3141525a4d95?auto=format&fit=crop&q=80&w=300',
      titulo: 'Areia Higiênica Pipicat Floral 4kg',
      preco: 22.9,
      nomeLoja: 'Cobasi',
      favorito: false,
    },
    {
      id: 6,
      urlImagem: 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&q=80&w=300',
      titulo: 'Ração Golden Gatos Adultos Frango 10kg',
      preco: 139.9,
      nomeLoja: 'Petlove',
      favorito: true,
    },
    {
      id: 7,
      urlImagem: 'https://images.unsplash.com/photo-1524661135-423995f22d0b?auto=format&fit=crop&q=80&w=300',
      titulo: 'Coleira Antipulgas Seresto Cães Até 8kg',
      preco: 249.9,
      nomeLoja: 'Petz',
      favorito: false,
    },
    {
      id: 8,
      urlImagem: 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&q=80&w=300',
      titulo: 'Arranhador de Papelão Rampa Gatos',
      preco: 35.0,
      precoAntigo: 45.0,
      nomeLoja: 'Casa do Criador',
      badgeDesconto: '22% OFF',
      favorito: false,
    },
  ];

  todosProdutos: Produto[] = [
    ...this.produtosOferta,
    ...this.maisVendidos,
    {
      id: 9,
      titulo: 'Ração Golden Special Cães Adultos',
      preco: 139.9,
      precoAntigo: 159.9,
      nomeLoja: 'Cobasi',
      urlImagem: 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&q=80&w=400',
      badgeDesconto: '12% OFF',
      favorito: false,
    },
    {
      id: 10,
      titulo: 'Tapete Higiênico Super Premium',
      preco: 45.9,
      nomeLoja: 'Petz',
      urlImagem: 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&q=80&w=400',
      favorito: true,
    },
    {
      id: 11,
      titulo: 'Brinquedo Mordedor Osso Borracha',
      preco: 22.9,
      nomeLoja: 'Cobasi',
      urlImagem: 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&q=80&w=400',
      favorito: false,
    },
    {
      id: 12,
      titulo: 'Ração Royal Canin Gatos Castrados',
      preco: 219.9,
      precoAntigo: 249.9,
      nomeLoja: 'Petz',
      urlImagem: 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&q=80&w=400',
      badgeDesconto: 'Frete Grátis',
      favorito: true,
    },
    {
      id: 13,
      titulo: 'Areia Higiênica Viva Verde!',
      preco: 49.9,
      nomeLoja: 'Pet Love',
      urlImagem: 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&q=80&w=400',
      favorito: false,
    },
    {
      id: 14,
      titulo: 'Cama Pet Conforto Redonda G',
      preco: 110.0,
      nomeLoja: 'Cobasi',
      urlImagem: 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&q=80&w=400',
      favorito: false,
    },
    {
      id: 15,
      titulo: 'Petisco Dreamies Sabor Salmão',
      preco: 6.5,
      nomeLoja: 'Petz',
      urlImagem: 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&q=80&w=400',
      favorito: false,
    },
    {
      id: 16,
      titulo: 'Shampoo Neutro Pelos Claros',
      preco: 34.9,
      nomeLoja: 'Boutique Animal',
      urlImagem: 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&q=80&w=400',
      favorito: false,
    },
  ];

  adicionarAoCarrinho(produto: Produto, quantidade = 1) {
    this.carrinhoService.adicionarAoCarrinho({
      id: produto.id,
      nome: produto.titulo,
      preco: produto.preco,
      urlImagem: produto.urlImagem,
      lojaNome: produto.nomeLoja,
    });
  }

  verProduto(produto: Produto) {
    console.log(`Ver detalhes do produto ${produto.titulo}`);
  }

  abrirCategoria(categoria: CategoriaVisual) {
    console.log(`Categoria selecionada: ${categoria.nome}`);
  }

  alternarFavorito(produto: Produto) {
    produto.favorito = !produto.favorito;
  }

  abrirLoja(loja: Loja) {
    console.log('Abrir loja:', loja.nome);
  }

  rolarCarrossel(trackElement: HTMLElement, direcao: 'left' | 'right') {
    const qtdeRolagem = 300;
    if (direcao === 'left') {
      trackElement.scrollBy({ left: -qtdeRolagem, behavior: 'smooth' });
    } else {
      trackElement.scrollBy({ left: qtdeRolagem, behavior: 'smooth' });
    }
  }
}
