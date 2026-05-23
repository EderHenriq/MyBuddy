import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { PaymentService } from '../../../core/services/PaymentService';

@Component({
  selector: 'app-confirmacao',
  imports: [CommonModule],
  templateUrl: './confirmacao.html',
  styleUrl: './confirmacao.scss',
})
export class Confirmacao implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private paymentService = inject(PaymentService);

  status = signal<'APPROVED' | 'PENDING' | 'REJECTED' | 'loading'>('loading');
  paymentId = signal<string | null>(null);
  amount = signal<number | null>(null);

  ngOnInit() {
    const preferenceId = this.route.snapshot.queryParamMap.get('preference_id');
    const paymentIdParam = this.route.snapshot.queryParamMap.get('payment_id');
    const statusParam = this.route.snapshot.queryParamMap.get('status');

    if (paymentIdParam) this.paymentId.set(paymentIdParam);

    if (statusParam === 'approved') {
      this.status.set('APPROVED');
    } else if (statusParam === 'pending') {
      this.status.set('PENDING');
    } else if (statusParam === 'failure') {
      this.status.set('REJECTED');
    } else {
      this.status.set('PENDING');
    }
  }

  voltarParaInicio() {
    this.router.navigate(['/home']);
  }

  adotarOutroPet() {
    this.router.navigate(['/marketplace']);
  }
}