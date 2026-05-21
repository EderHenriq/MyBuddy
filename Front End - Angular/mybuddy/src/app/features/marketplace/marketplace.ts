import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Footer } from '@shared/components/footer/footer';
import { CardProdutoComponent } from '@shared/components/card-produto/card-produto.component';
import { CardLojaComponent } from '@shared/components/card-loja/card-loja.component';
import { CategoryCarouselComponent, CategoriaVisual } from '@shared/components/category-carousel/category-carousel.component';

interface Loja {
  id: number;
  logoUrl: string;
  name: string;
  rating: number;
  deliveryTime: string;
  deliveryFee?: number;
}

interface Produto {
  id: number;
  imageUrl: string;
  title: string;
  price: number;
  oldPrice?: number;
  storeName: string;
  discountBadge?: string;
  isFavorite?: boolean;
}

@Component({
  selector: 'app-marketplace',
  standalone: true,
  imports: [CommonModule, Footer, CardProdutoComponent, CardLojaComponent, CategoryCarouselComponent],
  templateUrl: './marketplace.html',
  styleUrl: './marketplace.scss',
})
export class Marketplace {
  enderecoAtual = 'Rua das Flores, 123 - Centro';

  categorias: CategoriaVisual[] = [
    { id: 1, name: 'Rações', imageUrl: 'https://images.unsplash.com/photo-1589924691995-400dc9ecc119?auto=format&fit=crop&q=80&w=150' },
    { id: 2, name: 'Petiscos', imageUrl: 'https://images.unsplash.com/photo-1537151608804-ea6f23b7b6c5?auto=format&fit=crop&q=80&w=150' },
    { id: 3, name: 'Brinquedos', imageUrl: 'https://images.unsplash.com/photo-1576201836106-db1758fd1c97?auto=format&fit=crop&q=80&w=150' },
    { id: 4, name: 'Farmácia', imageUrl: 'https://images.unsplash.com/photo-1581888227599-779811939961?auto=format&fit=crop&q=80&w=150' },
    { id: 5, name: 'Higiene', imageUrl: 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&q=80&w=150' },
    { id: 6, name: 'Camas', imageUrl: 'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&q=80&w=150' },
  ];

  lojas: Loja[] = [
    { id: 1, logoUrl: 'https://images.unsplash.com/photo-1599305445671-ac291c95aaa9?auto=format&fit=crop&q=80&w=150', name: 'Petz', rating: 4.8, deliveryTime: '30-45 min', deliveryFee: 5.90 },
    { id: 2, logoUrl: 'https://images.unsplash.com/photo-1560707854-fb9a10efa532?auto=format&fit=crop&q=80&w=150', name: 'Cobasi', rating: 4.9, deliveryTime: '20-30 min', deliveryFee: 0 },
    { id: 3, logoUrl: 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&q=80&w=150', name: 'Petlove', rating: 4.7, deliveryTime: '15-25 min', deliveryFee: 7.50 },
    { id: 4, logoUrl: 'https://images.unsplash.com/photo-1541364983171-a8ba01e95cfc?auto=format&fit=crop&q=80&w=150', name: 'Bicho Chic', rating: 4.6, deliveryTime: '40-55 min', deliveryFee: 4.00 },
    { id: 5, logoUrl: 'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&q=80&w=150', name: 'Casa do Criador', rating: 4.5, deliveryTime: '25-40 min', deliveryFee: 6.90 },
  ];

  marcasDestaque = [
    { id: 1, name: 'Royal Canin', logoUrl: 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?w=100&q=80' },
    { id: 2, name: 'Premier', logoUrl: 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?w=100&q=80' },
    { id: 3, name: 'Zee.Dog', logoUrl: 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?w=100&q=80' },
    { id: 4, name: 'Bravecto', logoUrl: 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?w=100&q=80' },
    { id: 5, name: 'Golden', logoUrl: 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?w=100&q=80' },
    { id: 6, name: 'Pedigree', logoUrl: 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?w=100&q=80' },
  ];

  produtosOferta: Produto[] = [
    {
      id: 1,
      imageUrl: 'https://images.unsplash.com/photo-1589924691995-400dc9ecc119?auto=format&fit=crop&q=80&w=300',
      title: 'Ração Premier Formula Cães Adultos Frango',
      price: 189.90,
      oldPrice: 229.90,
      storeName: 'Petlove',
      discountBadge: '17% OFF',
      isFavorite: false
    },
    {
      id: 2,
      imageUrl: 'https://images.unsplash.com/photo-1581888227599-779811939961?auto=format&fit=crop&q=80&w=300',
      title: 'Antipulgas Bravecto para Cães 10 a 20kg',
      price: 215.50,
      storeName: 'Cobasi',
      discountBadge: 'Frete Grátis',
      isFavorite: true
    },
    {
      id: 3,
      imageUrl: 'https://images.unsplash.com/photo-1576201836106-db1758fd1c97?auto=format&fit=crop&q=80&w=300',
      title: 'Tapete Higiênico Super Seco 30 unidades',
      price: 49.90,
      oldPrice: 65.90,
      storeName: 'Petz',
      discountBadge: '24% OFF',
      isFavorite: false
    },
    {
      id: 4,
      imageUrl: 'https://images.unsplash.com/photo-1537151608804-ea6f23b7b6c5?auto=format&fit=crop&q=80&w=300',
      title: 'Bolinha de Tênis Chalesco para Cães',
      price: 15.90,
      storeName: 'Bicho Chic',
      isFavorite: false
    },
  ];

  maisVendidos: Produto[] = [
    {
      id: 5,
      imageUrl: 'https://images.unsplash.com/photo-1623387641177-3141525a4d95?auto=format&fit=crop&q=80&w=300',
      title: 'Areia Higiênica Pipicat Floral 4kg',
      price: 22.90,
      storeName: 'Cobasi',
      isFavorite: false
    },
    {
      id: 6,
      imageUrl: 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&q=80&w=300',
      title: 'Ração Golden Gatos Adultos Frango 10kg',
      price: 139.90,
      storeName: 'Petlove',
      isFavorite: true
    },
    {
      id: 7,
      imageUrl: 'https://images.unsplash.com/photo-1524661135-423995f22d0b?auto=format&fit=crop&q=80&w=300',
      title: 'Coleira Antipulgas Seresto Cães Até 8kg',
      price: 249.90,
      storeName: 'Petz',
      isFavorite: false
    },
    {
      id: 8,
      imageUrl: 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&q=80&w=300',
      title: 'Arranhador de Papelão Rampa Gatos',
      price: 35.00,
      oldPrice: 45.00,
      storeName: 'Casa do Criador',
      discountBadge: '22% OFF',
      isFavorite: false
    },
  ];

  todosProdutos: Produto[] = [
    { id: 9, title: 'Ração Golden Special Cães Adultos', price: 139.9, oldPrice: 159.9, storeName: 'Cobasi', imageUrl: 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&q=80&w=400', discountBadge: '12% OFF', isFavorite: false },
    { id: 10, title: 'Tapete Higiênico Super Premium', price: 45.9, storeName: 'Petz', imageUrl: 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&q=80&w=400', isFavorite: true },
    { id: 11, title: 'Brinquedo Mordedor Osso Borracha', price: 22.9, storeName: 'Cobasi', imageUrl: 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&q=80&w=400', isFavorite: false },
    { id: 12, title: 'Ração Royal Canin Gatos Castrados', price: 219.9, oldPrice: 249.9, storeName: 'Petz', imageUrl: 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&q=80&w=400', discountBadge: 'Frete Grátis', isFavorite: true },
    { id: 13, title: 'Areia Higiênica Viva Verde!', price: 49.9, storeName: 'Pet Love', imageUrl: 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&q=80&w=400', isFavorite: false },
    { id: 14, title: 'Cama Pet Conforto Redonda G', price: 110.0, storeName: 'Cobasi', imageUrl: 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&q=80&w=400', isFavorite: false },
    { id: 15, title: 'Petisco Dreamies Sabor Salmão', price: 6.5, storeName: 'Petz', imageUrl: 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&q=80&w=400', isFavorite: false },
    { id: 16, title: 'Shampoo Neutro Pelos Claros', price: 34.9, storeName: 'Boutique Animal', imageUrl: 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&q=80&w=400', isFavorite: false },
  ];

  adicionarAoCarrinho(produto: Produto, quantidade: number = 1) {
    console.log(`Adicionou ${quantidade} do produto ${produto.title}`);
  }

  verProduto(produto: Produto) {
    console.log(`Ver detalhes do produto ${produto.title}`);
  }

  abrirCategoria(categoria: CategoriaVisual) {
    console.log(`Categoria selecionada: ${categoria.name}`);
  }

  toggleFavorite(produto: Produto) {
    produto.isFavorite = !produto.isFavorite;
  }

  abrirLoja(loja: Loja) {
    console.log('Abrir loja:', loja.name);
  }

  scrollTrack(trackElement: HTMLElement, direction: 'left' | 'right') {
    const scrollAmount = 300;
    if (direction === 'left') {
      trackElement.scrollBy({ left: -scrollAmount, behavior: 'smooth' });
    } else {
      trackElement.scrollBy({ left: scrollAmount, behavior: 'smooth' });
    }
  }
}
