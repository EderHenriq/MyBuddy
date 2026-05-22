import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { ApiService } from './api.service';

export interface PaymentRequest {
  petId?: number;
  amount: number;
  description: string;
}

export interface PaymentResponse {
  id: number;
  mpPreferenceId: string;
  mpPaymentId: string;
  usuarioId: number;
  petId: number | null;
  amount: number;
  status: 'PENDING' | 'COMPLETED' | 'CANCELLED';
  InitPoint: string;
  createdAt: string;
  updatedAt: string;
}

@Injectable({
  providedIn: 'root',
})
export class PaymentService {
  private api = inject(ApiService);

  createPayment(request: PaymentRequest): Observable<PaymentResponse> {
    return this.api.post<PaymentResponse>('/payments', request);
  }

  getPayment(id: number): Observable<PaymentResponse> {
    return this.api.get<PaymentResponse>(`/payments/${id}`);
  }
}
