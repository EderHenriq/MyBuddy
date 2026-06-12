import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ModalComponent } from '../modal/modal.component';

@Component({
  selector: 'app-card-pet',
  standalone: true,
  imports: [CommonModule, ModalComponent],
  templateUrl: './card-pet.component.html',
  styleUrl: './card-pet.component.scss',
})
export class CardPetComponent {
  // Input unificado (Landing Page / rotas novas)
  @Input() pet?: any;

  // Inputs individuais em Inglês (Developer)
  @Input() imageUrl = '';
  @Input() name = '';
  @Input() age: string | number = '';
  @Input() breed = '';
  @Input() sex = '';
  @Input() vaccinated: string | boolean = 'Sim';
  @Input() description = '';
  @Input() badgeText = '';
  @Input() badgeType = 'default';
  @Input() isFavorite = false;
  @Input() showTopHeart = false;
  @Input() showFavoriteButton = false;
  @Input() ongMode = false;

  // Inputs individuais em Português (para Perfil/outras páginas antigas)
  @Input() urlImagem = '';
  @Input() nome = '';
  @Input() idade: string | number = '';
  @Input() raca = '';
  @Input() sexo = '';
  @Input() vacinado: string | boolean = 'Sim';
  @Input() textoBadge = '';
  @Input() tipoBadge = 'default';
  @Input() favorito = false;
  @Input() mostrarCoracaoTop = false;

  // Novos inputs do backend para listagens dinâmicas
  @Input() castrado: boolean | string = false;
  @Input() porte = '';
  @Input() cor = '';
  @Input() pelagem = '';

  // Outputs (compatibilidade completa de eventos)
  @Output() infoClick = new EventEmitter<void>();
  @Output() favoriteClick = new EventEmitter<any>();
  @Output() cliqueFavorito = new EventEmitter<any>();
  @Output() adoptClick = new EventEmitter<void>();
  @Output() editClick = new EventEmitter<any>();
  @Output() deleteClick = new EventEmitter<any>();
  @Output() openModalRequest = new EventEmitter<void>();

  isModalVisible = false;

  // Getters para unificar as propriedades (priorizando o objeto pet, depois inputs em Inglês, depois em Português)
  get cardImageUrl(): string {
    return this.pet?.imageUrl || this.pet?.urlImagem || this.imageUrl || this.urlImagem || '';
  }

  get cardName(): string {
    return this.pet?.name || this.pet?.nome || this.name || this.nome || '';
  }

  get cardAge(): string {
    return this.pet?.age || this.pet?.idade || this.age || this.idade || '';
  }

  get cardBreed(): string {
    return this.pet?.breed || this.pet?.raca || this.breed || this.raca || '';
  }

  get cardSex(): string {
    return this.pet?.gender || this.pet?.sex || this.pet?.sexo || this.sex || this.sexo || '';
  }

  get cardVaccinated(): string {
    if (this.pet?.isVaccinated !== undefined) {
      return this.pet.isVaccinated ? 'Sim' : 'Não';
    }
    if (this.pet?.vacinado !== undefined) {
      return this.pet.vacinado === 'Sim' || this.pet.vacinado === true ? 'Sim' : 'Não';
    }
    const val = this.vaccinated || this.vacinado || 'Sim';
    return val === 'Sim' || val === true ? 'Sim' : 'Não';
  }

  get cardBadgeText(): string {
    return this.pet?.badgeText || this.pet?.textoBadge || this.badgeText || this.textoBadge || '';
  }

  get cardBadgeType(): string {
    return this.pet?.badgeType || this.pet?.tipoBadge || this.badgeType || this.tipoBadge || 'default';
  }

  get cardIsFavorite(): boolean {
    return this.pet?.isFavorite || this.pet?.favorito || this.isFavorite || this.favorito || false;
  }

  get cardShowTopHeart(): boolean {
    return this.pet?.showTopHeart || this.pet?.mostrarCoracaoTop || this.showTopHeart || this.mostrarCoracaoTop || false;
  }

  get cardDescription(): string {
    const customDesc = this.pet?.description || this.description;
    if (customDesc) return customDesc;
    return `Olá! Eu sou ${this.cardName ? 'o ' + this.cardName : 'um pet'} e estou ansioso para encontrar uma família que me dê muito amor e carinho. Já estou pronto para ser seu novo melhor amigo!`;
  }

  get defaultDescription(): string {
    return `Olá! Eu sou ${this.cardName ? 'o ' + this.cardName : 'um pet'} e estou ansioso para encontrar um novo lar.`;
  }

  openModal(): void {
    this.openModalRequest.emit();
    this.isModalVisible = true;
    this.infoClick.emit();
  }

  handleAdopt(): void {
    this.isModalVisible = false;
    this.adoptClick.emit();
  }

  async sharePet() {
    const shareData = {
      title: `Adote o ${this.cardName} - MyBuddy`,
      text: `Conheça o ${this.cardName}! Ele tem ${this.cardAge} e é da raça ${this.cardBreed}.`,
      url: window.location.href,
    };

    if (navigator.share) {
      try {
        await navigator.share(shareData);
      } catch (err) {
        console.error('Erro ao compartilhar:', err);
      }
    } else {
      navigator.clipboard.writeText(window.location.href);
      alert('Link copiado para a área de transferência!');
    }
  }
}
