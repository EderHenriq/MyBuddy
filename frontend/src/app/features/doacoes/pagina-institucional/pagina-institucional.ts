import { Component, OnInit, inject, signal } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import {
  DonationService,
  CampanhaDoacao,
  DonationStats,
  OngParceira,
} from "../../../core/services/donation.service";
import { HeroSectionComponent } from "../../../shared/components/hero-section/hero-section.component";
import { Footer } from "../../../shared/components/footer/footer";
import { ModalDoacao } from "../modal-doacao/modal-doacao";

@Component({
  selector: "app-pagina-institucional",
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    HeroSectionComponent,
    Footer,
    ModalDoacao,
  ],
  templateUrl: "./pagina-institucional.html",
  styleUrl: "./pagina-institucional.scss",
})
export class PaginaInstitucional implements OnInit {
  private donationService = inject(DonationService);

  stats = signal<DonationStats>({
    petsSalvos: 0,
    ongsParceiras: 0,
    totalArrecadado: 0,
    doadoresAtivos: 0,
  });
  campanhas = signal<CampanhaDoacao[]>([]);
  ongs = signal<OngParceira[]>([]);

  categorias = [
    "Todos",
    "Pets em tratamento",
    "Ração e alimentação",
    "Cirurgias",
    "Abrigo / ONG",
    "Urgente",
  ];
  categoriaSelecionada = signal<string>("Todos");
  searchText = "";
  frequenciaRecorrente = signal<"mensal" | "semanal" | "unica">("mensal");

  showModal = signal<boolean>(false);
  selectedCampanha = signal<CampanhaDoacao | null>(null);

  carregandoCampanhas = signal<boolean>(true);
  carregandoStats = signal<boolean>(true);

  ngOnInit(): void {
    this.carregarStats();
    this.carregarCampanhas();
    this.carregarOngs();
  }

  carregarStats() {
    this.carregandoStats.set(true);
    this.donationService.getStats().subscribe((data) => {
      this.stats.set(data);
      this.carregandoStats.set(false);
    });
  }

  carregarCampanhas() {
    this.carregandoCampanhas.set(true);
    this.donationService
      .getCampaigns(this.categoriaSelecionada())
      .subscribe((data) => {
        this.campanhas.set(data);
        this.carregandoCampanhas.set(false);
      });
  }

  carregarOngs() {
    this.donationService.getOngsParceiras().subscribe((data) => {
      this.ongs.set(data);
    });
  }

  selecionarCategoria(cat: string) {
    this.categoriaSelecionada.set(cat);
    this.carregarCampanhas();
  }

  abrirModalDoacao(campanha?: CampanhaDoacao) {
    this.selectedCampanha.set(campanha || null);
    this.showModal.set(true);
    document.body.style.overflow = "hidden";
  }

  fecharModal() {
    this.showModal.set(false);
    this.selectedCampanha.set(null);
    document.body.style.overflow = "";
  }

  getPorcentagemArrecadada(c: CampanhaDoacao): number {
    if (c.meta <= 0) return 0;
    const pct = (c.arrecadado / c.meta) * 100;
    return pct > 100 ? 100 : pct;
  }

  isMetaAtingida(c: CampanhaDoacao): boolean {
    return c.arrecadado >= c.meta;
  }

  getCampanhasFiltradas(): CampanhaDoacao[] {
    if (!this.searchText.trim()) return this.campanhas();
    const q = this.searchText.toLowerCase();
    return this.campanhas().filter(
      (c) =>
        c.titulo.toLowerCase().includes(q) ||
        c.descricao.toLowerCase().includes(q) ||
        c.localizacao?.toLowerCase().includes(q),
    );
  }

  scrollToCampanhas() {
    const el = document.querySelector(".filter-section");
    el?.scrollIntoView({ behavior: "smooth" });
  }

  formatarValor(valor: number): string {
    if (valor >= 1000) return `R$${(valor / 1000).toFixed(0)}k`;
    return `R$${valor.toFixed(0)}`;
  }
}
