import { CommonModule } from "@angular/common";
import { Component, inject, OnInit, OnDestroy } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { Router, RouterModule } from "@angular/router";
import { Footer } from "@shared/components/footer/footer";
import { CardProdutoComponent } from "@shared/components/card-produto/card-produto.component";
import { CardLojaComponent } from "@shared/components/card-loja/card-loja.component";
import {
  CategoryCarouselComponent,
  CategoriaVisual,
} from "@shared/components/category-carousel/category-carousel.component";
import { CartDrawerComponent } from "@shared/components/cart-drawer/cart-drawer.component";
import { CartService } from "@core/services/cart.service";
import { HeroSectionComponent } from "@shared/components/hero-section/hero-section.component";
import { ProdutoService } from "@core/services/produto.service";
import { BtnOutlineComponent } from "@shared/components/btn-outline/btn-outline.component";

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
  categoria: string;
  petshopId?: number;
}

@Component({
  selector: "app-marketplace",
  standalone: true,
  imports: [
    CommonModule,
    Footer,
    CardProdutoComponent,
    CardLojaComponent,
    CategoryCarouselComponent,
    CartDrawerComponent,
    HeroSectionComponent,
    BtnOutlineComponent,
    FormsModule,
    RouterModule,
  ],
  templateUrl: "./marketplace.html",
  styleUrl: "./marketplace.scss",
})
export class Marketplace implements OnInit, OnDestroy {
  carrinhoService = inject(CartService);
  private router = inject(Router);
  private produtoService = inject(ProdutoService);
  enderecoAtual = "Rua das Flores, 123 - Centro";

  searchQuery = "";
  selectedCategoryName = "";
  activeSort = "";
  isSearching = false;
  searchFocused = false;
  filteredProdutos: Produto[] = [];
  favoritosExibir: Produto[] = [];

  // Banner autoplay
  activeBannerIndex = 0;
  bannerDots = [0, 1, 2, 3];
  private bannerInterval: ReturnType<typeof setInterval> | null = null;

  // Toast de feedback
  toastVisivel = false;
  toastMensagem = "";
  private toastTimeout: ReturnType<typeof setTimeout> | null = null;

  // FAB bounce
  cartBounce = false;

  ngOnInit() {
    this.carregarProdutos();
    this.iniciarAutoplayBanner();
  }

  ngOnDestroy() {
    this.pararAutoplayBanner();
    if (this.toastTimeout) clearTimeout(this.toastTimeout);
  }

  // === Banner Autoplay ===
  iniciarAutoplayBanner() {
    this.bannerInterval = setInterval(() => {
      this.activeBannerIndex = (this.activeBannerIndex + 1) % this.bannerDots.length;
      const track = document.querySelector('.banner-track') as HTMLElement;
      if (track) {
        track.scrollTo({
          left: track.clientWidth * this.activeBannerIndex,
          behavior: 'smooth'
        });
      }
    }, 5000);
  }

  pararAutoplayBanner() {
    if (this.bannerInterval) {
      clearInterval(this.bannerInterval);
      this.bannerInterval = null;
    }
  }

  onBannerScroll(track: HTMLElement) {
    if (track.clientWidth > 0) {
      this.activeBannerIndex = Math.round(track.scrollLeft / track.clientWidth);
    }
  }

  irParaBanner(track: HTMLElement, index: number) {
    this.activeBannerIndex = index;
    track.scrollTo({
      left: track.clientWidth * index,
      behavior: 'smooth'
    });
    // Reiniciar autoplay ao navegar manualmente
    this.pararAutoplayBanner();
    this.iniciarAutoplayBanner();
  }

  // === Toast de Feedback ===
  mostrarToast(mensagem: string) {
    if (this.toastTimeout) clearTimeout(this.toastTimeout);
    this.toastMensagem = mensagem;
    this.toastVisivel = true;
    this.toastTimeout = setTimeout(() => {
      this.toastVisivel = false;
    }, 2500);
  }

  // === FAB Bounce ===
  dispararBounce() {
    this.cartBounce = true;
    setTimeout(() => {
      this.cartBounce = false;
    }, 400);
  }

  // === Carregar Produtos ===
  carregarProdutos() {
    this.produtoService.buscarComFiltros().subscribe({
      next: (dados) => {
        const mapeados = dados.map((p: any) => ({
          id: p.id,
          urlImagem: p.imagens && p.imagens.length > 0 ? p.imagens[0] : "https://images.unsplash.com/photo-1589924691995-400dc9ecc119?auto=format&fit=crop&q=80&w=300",
          titulo: p.nome,
          preco: p.preco,
          precoAntigo: p.preco * 1.2,
          nomeLoja: p.petshopNome || "PetLovers Shop",
          badgeDesconto: this.gerarBadge(p),
          favorito: false,
          categoria: p.categoriaNome || p.subCategoriaNome || "Geral",
          petshopId: p.petshopId || 1
        }));
        this.todosProdutos = mapeados;
        this.produtosOferta = mapeados.slice(0, 4);
        this.maisVendidos = mapeados.slice().reverse().slice(0, 4);
        this.carregarFavoritosVisual();
        this.filtrarProdutos();
      },
      error: (err) => console.error(err)
    });
  }

  // Gera badge semântica por tipo
  gerarBadge(p: any): string {
    if (p.estoque === 0) return 'Esgotado';
    if (p.preco < 50) return '10% OFF';
    if (p.preco >= 100) return 'Frete Grátis';
    return '';
  }

  obterFavoritosSalvos(): any[] {
    const data = localStorage.getItem("mybuddy_favoritos");
    return data ? JSON.parse(data) : [];
  }

  salvarFavoritos(favs: any[]): void {
    localStorage.setItem("mybuddy_favoritos", JSON.stringify(favs));
  }

  carregarFavoritosVisual(): void {
    const favs = this.obterFavoritosSalvos();
    this.todosProdutos.forEach(p => {
      p.favorito = favs.some(f => f.id === p.id);
    });
    this.filteredProdutos.forEach(p => {
      p.favorito = favs.some(f => f.id === p.id);
    });
    this.produtosOferta.forEach(p => {
      p.favorito = favs.some(f => f.id === p.id);
    });
    this.maisVendidos.forEach(p => {
      p.favorito = favs.some(f => f.id === p.id);
    });
    this.favoritosExibir = favs;
  }

  categorias: CategoriaVisual[] = [
    {
      id: 1,
      nome: "Rações",
      urlImagem:
        "https://images.unsplash.com/photo-1589924691995-400dc9ecc119?auto=format&fit=crop&q=80&w=150",
    },
    {
      id: 2,
      nome: "Petiscos",
      urlImagem:
        "https://images.unsplash.com/photo-1608454509000-19aa87be5696?auto=format&fit=crop&q=80&w=150",
    },
    {
      id: 3,
      nome: "Brinquedos",
      urlImagem:
        "https://images.unsplash.com/photo-1576201836106-db1758fd1c97?auto=format&fit=crop&q=80&w=150",
    },
    {
      id: 4,
      nome: "Farmácia",
      urlImagem:
        "https://images.unsplash.com/photo-1607619056574-7b8f304b3c93?auto=format&fit=crop&q=80&w=150",
    },
    {
      id: 5,
      nome: "Higiene",
      urlImagem:
        "https://images.unsplash.com/photo-1516715094727-ec48be335d79?auto=format&fit=crop&q=80&w=150",
    },
    {
      id: 6,
      nome: "Camas",
      urlImagem:
        "https://images.unsplash.com/photo-1591584563745-0ad51f7a53d6?auto=format&fit=crop&q=80&w=150",
    },
  ];

  lojas: Loja[] = [
    {
      id: 1,
      urlLogo:
        "https://images.unsplash.com/photo-1599305445671-ac291c95aaa9?auto=format&fit=crop&q=80&w=150",
      nome: "Petz",
      avaliacao: 4.8,
      tempoEntrega: "30-45 min",
      taxaEntrega: 5.9,
    },
    {
      id: 2,
      urlLogo:
        "https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?auto=format&fit=crop&q=80&w=150",
      nome: "Cobasi",
      avaliacao: 4.9,
      tempoEntrega: "20-30 min",
      taxaEntrega: 0,
    },
    {
      id: 3,
      urlLogo:
        "https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&q=80&w=150",
      nome: "Petlove",
      avaliacao: 4.7,
      tempoEntrega: "15-25 min",
      taxaEntrega: 7.5,
    },
    {
      id: 4,
      urlLogo:
        "https://images.unsplash.com/photo-1541364983171-a8ba01e95cfc?auto=format&fit=crop&q=80&w=150",
      nome: "Bicho Chic",
      avaliacao: 4.6,
      tempoEntrega: "40-55 min",
      taxaEntrega: 4.0,
    },
    {
      id: 5,
      urlLogo:
        "https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&q=80&w=150",
      nome: "Casa do Criador",
      avaliacao: 4.5,
      tempoEntrega: "25-40 min",
      taxaEntrega: 6.9,
    },
  ];

  marcasDestaque = [
    {
      id: 1,
      nome: "Royal Canin",
      urlLogo:
        "https://images.unsplash.com/photo-1628009368231-7bb7cfcb0def?w=100&q=80",
    },
    {
      id: 2,
      nome: "Premier",
      urlLogo:
        "https://images.unsplash.com/photo-1589924691995-400dc9ecc119?w=100&q=80",
    },
    {
      id: 3,
      nome: "Zee.Dog",
      urlLogo:
        "https://images.unsplash.com/photo-1576201836106-db1758fd1c97?w=100&q=80",
    },
    {
      id: 4,
      nome: "Bravecto",
      urlLogo:
        "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=100&q=80",
    },
    {
      id: 5,
      nome: "Golden",
      urlLogo:
        "https://images.unsplash.com/photo-1517849845537-4d257902454a?w=100&q=80",
    },
    {
      id: 6,
      nome: "Pedigree",
      urlLogo:
        "https://images.unsplash.com/photo-1596492784531-6e6eb5ea9993?w=100&q=80",
    },
  ];

  produtosOferta: Produto[] = [
    {
      id: 1,
      urlImagem:
        "https://images.unsplash.com/photo-1589924691995-400dc9ecc119?auto=format&fit=crop&q=80&w=300",
      titulo: "Ração Premier Formula Cães Adultos Frango",
      preco: 189.9,
      precoAntigo: 229.9,
      nomeLoja: "Petlove",
      badgeDesconto: "17% OFF",
      favorito: false,
      categoria: "Rações",
    },
    {
      id: 2,
      urlImagem:
        "https://images.unsplash.com/photo-1581888227599-779811939961?auto=format&fit=crop&q=80&w=300",
      titulo: "Antipulgas Bravecto para Cães 10 a 20kg",
      preco: 215.5,
      nomeLoja: "Cobasi",
      badgeDesconto: "Frete Grátis",
      favorito: true,
      categoria: "Farmácia",
    },
    {
      id: 3,
      urlImagem:
        "https://images.unsplash.com/photo-1576201836106-db1758fd1c97?auto=format&fit=crop&q=80&w=300",
      titulo: "Tapete Higiênico Super Seco 30 unidades",
      preco: 49.9,
      precoAntigo: 65.9,
      nomeLoja: "Petz",
      badgeDesconto: "24% OFF",
      favorito: false,
      categoria: "Higiene",
    },
    {
      id: 4,
      urlImagem:
        "https://images.unsplash.com/photo-1537151608804-ea6f23b7b6c5?auto=format&fit=crop&q=80&w=300",
      titulo: "Bolinha de Tênis Chalesco para Cães",
      preco: 15.9,
      nomeLoja: "Bicho Chic",
      favorito: false,
      categoria: "Brinquedos",
    },
  ];

  maisVendidos: Produto[] = [
    {
      id: 5,
      urlImagem:
        "https://images.unsplash.com/photo-1623387641177-3141525a4d95?auto=format&fit=crop&q=80&w=300",
      titulo: "Areia Higiênica Pipicat Floral 4kg",
      preco: 22.9,
      nomeLoja: "Cobasi",
      favorito: false,
      categoria: "Higiene",
    },
    {
      id: 6,
      urlImagem:
        "https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&q=80&w=300",
      titulo: "Ração Golden Gatos Adultos Frango 10kg",
      preco: 139.9,
      nomeLoja: "Petlove",
      favorito: true,
      categoria: "Rações",
    },
    {
      id: 7,
      urlImagem:
        "https://images.unsplash.com/photo-1524661135-423995f22d0b?auto=format&fit=crop&q=80&w=300",
      titulo: "Coleira Antipulgas Seresto Cães Até 8kg",
      preco: 249.9,
      nomeLoja: "Petz",
      favorito: false,
      categoria: "Farmácia",
    },
    {
      id: 8,
      urlImagem:
        "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&q=80&w=300",
      titulo: "Arranhador de Papelão Rampa Gatos",
      preco: 35.0,
      precoAntigo: 45.0,
      nomeLoja: "Casa do Criador",
      badgeDesconto: "22% OFF",
      favorito: false,
      categoria: "Brinquedos",
    },
  ];

  todosProdutos: Produto[] = [
    ...this.produtosOferta,
    ...this.maisVendidos,
    {
      id: 9,
      titulo: "Ração Golden Special Cães Adultos",
      preco: 139.9,
      precoAntigo: 159.9,
      nomeLoja: "Cobasi",
      urlImagem:
        "https://images.unsplash.com/photo-1601758228041-f3b2795255f1?auto=format&fit=crop&q=80&w=400",
      badgeDesconto: "12% OFF",
      favorito: false,
      categoria: "Rações",
    },
    {
      id: 10,
      titulo: "Tapete Higiênico Super Premium",
      preco: 45.9,
      nomeLoja: "Petz",
      urlImagem:
        "https://images.unsplash.com/photo-1587300003388-59208cc962cb?auto=format&fit=crop&q=80&w=400",
      favorito: true,
      categoria: "Higiene",
    },
    {
      id: 11,
      titulo: "Brinquedo Mordedor Osso Borracha",
      preco: 22.9,
      nomeLoja: "Cobasi",
      urlImagem:
        "https://images.unsplash.com/photo-1535930749574-1399327ce78f?auto=format&fit=crop&q=80&w=400",
      favorito: false,
      categoria: "Brinquedos",
    },
    {
      id: 12,
      titulo: "Ração Royal Canin Gatos Castrados",
      preco: 219.9,
      precoAntigo: 249.9,
      nomeLoja: "Petz",
      urlImagem:
        "https://images.unsplash.com/photo-1574158622682-e40e69881006?auto=format&fit=crop&q=80&w=400",
      badgeDesconto: "Frete Grátis",
      favorito: true,
      categoria: "Rações",
    },
    {
      id: 13,
      titulo: "Areia Higiênica Viva Verde!",
      preco: 49.9,
      nomeLoja: "Pet Love",
      urlImagem:
        "https://images.unsplash.com/photo-1526336024174-e58f5cdd8e13?auto=format&fit=crop&q=80&w=400",
      favorito: false,
      categoria: "Higiene",
    },
    {
      id: 14,
      titulo: "Cama Pet Conforto Redonda G",
      preco: 110.0,
      nomeLoja: "Cobasi",
      urlImagem:
        "https://images.unsplash.com/photo-1541781774459-bb2af2f05b55?auto=format&fit=crop&q=80&w=400",
      favorito: false,
      categoria: "Camas",
    },
    {
      id: 15,
      titulo: "Petisco Dreamies Sabor Salmão",
      preco: 6.5,
      nomeLoja: "Petz",
      urlImagem:
        "https://images.unsplash.com/photo-1573865526739-10659fec78a5?auto=format&fit=crop&q=80&w=400",
      favorito: false,
      categoria: "Petiscos",
    },
    {
      id: 16,
      titulo: "Shampoo Neutro Pelos Claros",
      preco: 34.9,
      nomeLoja: "Boutique Animal",
      urlImagem:
        "https://images.unsplash.com/photo-1583511655826-05700d52f4d9?auto=format&fit=crop&q=80&w=400",
      favorito: false,
      categoria: "Higiene",
    },
  ];

  filtrarProdutos() {
    let resultado = [...this.todosProdutos];

    // Filtro por Categoria
    if (this.selectedCategoryName) {
      resultado = resultado.filter(
        (p) => p.categoria === this.selectedCategoryName
      );
    }

    // Filtro por termo de busca
    if (this.searchQuery) {
      const termo = this.searchQuery.toLowerCase().trim();
      resultado = resultado.filter(
        (p) =>
          p.titulo.toLowerCase().includes(termo) ||
          p.nomeLoja.toLowerCase().includes(termo)
      );
    }

    // Ordenação
    if (this.activeSort === "menor_preco") {
      resultado.sort((a, b) => a.preco - b.preco);
    } else if (this.activeSort === "maior_preco") {
      resultado.sort((a, b) => b.preco - a.preco);
    } else if (this.activeSort === "avaliacao") {
      resultado.sort((a, b) => (b.preco % 5) - (a.preco % 5));
    }

    this.filteredProdutos = resultado;
    const novoEstadoBusca =
      !!this.searchQuery || !!this.selectedCategoryName || !!this.activeSort;

    // Scroll to top ao entrar no modo busca (clique em marca, loja, categoria, etc.)
    if (novoEstadoBusca && !this.isSearching) {
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }

    this.isSearching = novoEstadoBusca;
  }

  onSearchInput(event: Event) {
    const input = event.target as HTMLInputElement;
    this.searchQuery = input.value;
    this.filtrarProdutos();
  }

  limparFiltros() {
    this.searchQuery = "";
    this.selectedCategoryName = "";
    this.activeSort = "";
    this.isSearching = false;
    this.filteredProdutos = [];
  }

  setOrdenacao(opcao: string) {
    this.activeSort = opcao;
    this.filtrarProdutos();
  }

  adicionarAoCarrinho(produto: Produto, quantidade = 1) {
    this.carrinhoService.adicionarAoCarrinho({
      id: produto.id,
      nome: produto.titulo,
      preco: produto.preco,
      urlImagem: produto.urlImagem,
      lojaNome: produto.nomeLoja,
      petshopId: produto.petshopId || 1,
    });
    this.mostrarToast(`${produto.titulo.substring(0, 30)}... adicionado ao carrinho`);
    this.dispararBounce();
  }

  verProduto(produto: Produto) {
    this.router.navigate(["/produtos", produto.id]);
  }

  abrirCategoria(categoria: CategoriaVisual) {
    if (this.selectedCategoryName === categoria.nome) {
      this.selectedCategoryName = "";
    } else {
      this.selectedCategoryName = categoria.nome;
    }
    this.filtrarProdutos();
  }

  alternarFavorito(produto: Produto) {
    produto.favorito = !produto.favorito;
    let favs = this.obterFavoritosSalvos();
    if (produto.favorito) {
      if (!favs.find((f: any) => f.id === produto.id)) {
        favs.push(produto);
      }
    } else {
      favs = favs.filter((f: any) => f.id !== produto.id);
    }
    this.salvarFavoritos(favs);
    this.carregarFavoritosVisual();
  }

  abrirLoja(loja: Loja) {
    this.searchQuery = loja.nome;
    this.filtrarProdutos();
  }

  rolarCarrossel(trackElement: HTMLElement, direcao: "left" | "right") {
    const qtdeRolagem = 300;
    if (trackElement) {
      if (direcao === "left") {
        trackElement.scrollBy({ left: -qtdeRolagem, behavior: "smooth" });
      } else {
        trackElement.scrollBy({ left: qtdeRolagem, behavior: "smooth" });
      }
    }
  }
}
