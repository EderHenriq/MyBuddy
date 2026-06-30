import { Component, inject, OnInit, OnDestroy, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { interval, Subscription } from 'rxjs';
import { switchMap, takeWhile } from 'rxjs/operators';

@Component({
  selector: 'app-confirmacao',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './confirmacao.html',
  styleUrl: './confirmacao.scss',
})
export class Confirmacao implements OnInit, OnDestroy {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private http = inject(HttpClient);

  status = signal<'APPROVED' | 'PENDING' | 'REJECTED' | 'loading'>('loading');
  paymentId = signal<string | null>(null);

  // Parâmetros do Marketplace
  isMarketplace = signal<boolean>(false);
  pedidoId = signal<number | null>(null);
  totalPedido = signal<number>(0);

  private pollingSub?: Subscription;

  ngOnInit() {
    const paymentIdParam = this.route.snapshot.queryParamMap.get('payment_id');
    const statusParam = this.route.snapshot.queryParamMap.get('status');
    const preferenceIdParam = this.route.snapshot.queryParamMap.get('preference_id');

    // Parâmetros do checkout do marketplace
    const pedidoIdParam = this.route.snapshot.queryParamMap.get('pedidoId');
    const totalParam = this.route.snapshot.queryParamMap.get('total');

    if (pedidoIdParam) {
      this.pedidoId.set(Number(pedidoIdParam));
      this.totalPedido.set(Number(totalParam || 0));
      this.isMarketplace.set(true);
      this.status.set('APPROVED');
      return;
    }

    if (paymentIdParam) this.paymentId.set(paymentIdParam);

    if (statusParam === 'approved' && preferenceIdParam) {
      this.status.set('APPROVED');
    } else if (statusParam === 'failure') {
      this.status.set('REJECTED');
    } else if (statusParam === 'pending' && preferenceIdParam) {
      this.status.set('PENDING');
      this.startPolling(preferenceIdParam);
    } else {
      this.status.set('PENDING');
    }
  }

  private startPolling(PreferenceId: string) {
    this.pollingSub = interval(5000)
      .pipe(
        switchMap(() => this.http.get<any>(`/api/payments/preference/${PreferenceId}`)),
        takeWhile(response => response.status === 'PENDING', true),
      )
      .subscribe({
        next: response => {
          if (response.status === 'APPROVED') {
            this.status.set('APPROVED');
          } else if (response.status === 'REJECTED' || response.status === 'CANCELLED') {
            this.status.set('REJECTED');
          }
        },
        error: () => {},
      });
  }

  ngOnDestroy() {
    this.pollingSub?.unsubscribe();
  }

  voltarParaInicio() {
    this.router.navigate(['/home']);
  }

  irParaPedidos() {
    this.router.navigate(['/meus-pedidos']);
  }

  irParaProdutos() {
    this.router.navigate(['/produtos']);
  }
}
