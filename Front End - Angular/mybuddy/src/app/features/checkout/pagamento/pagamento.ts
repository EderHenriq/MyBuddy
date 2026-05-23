import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { PaymentService, PaymentRequest } from '../../../core/services/PaymentService';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-pagamento',
  imports: [CommonModule],
  templateUrl: './pagamento.html',
  styleUrl: './pagamento.scss',
})

export class Pagamento implements OnInit {

  private paymentService = inject(PaymentService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  petId = signal<number | null>(null);
  petNome = signal<string>('');
  amount = signal<number>(50);
  loading = signal<boolean>(false);
  error = signal<string | null>(null);

  ngOnInit(): void {
    const petIdParam = this.route.snapshot.queryParamMap.get('petId');
    const petNomeParam = this.route.snapshot.queryParamMap.get('petNome');
    const amountParam = this.route.snapshot.queryParamMap.get('amount');

    if (petIdParam) this.petId.set(Number(petIdParam));
    if (petNomeParam) this.petNome.set(petNomeParam);
    if (amountParam) this.amount.set(Number(amountParam));
  }

  confirmarPagamento(): void {
    this.loading.set(true);
    this.error.set(null);

    const request: PaymentRequest = {
      petId: this.petId() ?? undefined,
      amount: this.amount(),
      description: this.petNome() ? `Adoção - ${this.petNome()}` : 'Doação MyBuddy',
    };

    this.paymentService.createPayment(request).subscribe({
      next: (response) => {
        window.location.href = response.initPoint;
      },
      error: (err) => {
        this.error.set('Ocorreu um erro ao processar o pagamento.');
        this.loading.set(false);
        console.error('Payment error:', err);
      },
    });
  }

  voltar(): void {
    this.router.navigate(['/pets']);
  }
}