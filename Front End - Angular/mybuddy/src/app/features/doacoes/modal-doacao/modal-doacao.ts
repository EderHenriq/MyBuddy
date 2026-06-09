import { Component, EventEmitter, Input, Output, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DonationService, CampanhaDoacao } from '../../../core/services/donation.service';

@Component({
  selector: 'app-modal-doacao',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './modal-doacao.html',
  styleUrl: './modal-doacao.scss',
})
export class ModalDoacao {
  private donationService = inject(DonationService);

  @Input() campanha: CampanhaDoacao | null = null;
  @Output() close = new EventEmitter<void>();

  valoresFixos = [10, 25, 50, 100];
  valorSelecionado = signal<number>(25);
  valorPersonalizado = signal<string>('');

  formaPagamento = signal<'pix' | 'cartao' | 'boleto'>('pix');
  tipoDoacao = signal<'unica' | 'recorrente'>('unica');
  frequenciaRecorrente = signal<'weekly' | 'monthly'>('monthly');

  loading = signal<boolean>(false);
  error = signal<string | null>(null);
  sucesso = signal<boolean>(false);

  selecionarValor(valor: number) {
    this.valorSelecionado.set(valor);
    this.valorPersonalizado.set('');
  }

  getValorFinal(): number {
    const custom = parseFloat(this.valorPersonalizado());
    if (!isNaN(custom) && custom > 0) return custom;
    return this.valorSelecionado();
  }

  isValorCustom(): boolean {
    const v = parseFloat(this.valorPersonalizado());
    return !isNaN(v) && v > 0;
  }

  confirmarDoacao() {
    this.loading.set(true);
    this.error.set(null);

    const valor = this.getValorFinal();
    if (valor <= 0) {
      this.error.set('Por favor, informe um valor válido para doação.');
      this.loading.set(false);
      return;
    }

    const desc = this.campanha
      ? `Campanha - ${this.campanha.titulo}`
      : 'Doação Geral - MyBuddy';

    if (this.tipoDoacao() === 'unica') {
      this.donationService
        .createSingleDonation(
          valor,
          desc,
          this.campanha?.petId,
          this.campanha?.id,
          this.campanha?.organizacaoId
        )
        .subscribe({
          next: (response) => {
            if (response?.initPoint) {
              window.location.href = response.initPoint;
            } else {
              this.sucesso.set(true);
              this.loading.set(false);
            }
          },
          error: () => {
            this.error.set('Erro ao processar doação. Tente novamente.');
            this.loading.set(false);
          },
        });
    } else {
      this.donationService
        .createRecurringDonation(
          valor,
          this.frequenciaRecorrente(),
          this.campanha?.organizacaoId
        )
        .subscribe({
          next: (response) => {
            if (response?.initPoint) {
              window.location.href = response.initPoint;
            } else {
              this.sucesso.set(true);
              this.loading.set(false);
            }
          },
          error: () => {
            this.error.set('Erro ao criar assinatura recorrente. Tente novamente.');
            this.loading.set(false);
          },
        });
    }
  }

  fechar() {
    this.close.emit();
  }
}
