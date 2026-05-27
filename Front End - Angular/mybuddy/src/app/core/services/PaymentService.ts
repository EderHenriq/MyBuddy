import { Injectable, inject } from "@angular/core";
import { Observable } from "rxjs";
import { ApiService } from "./api.service";

export interface PaymentRequest {
    petId?: number;
    amount: number;
    description?: string;
}

export interface PaymentResponse {
    id: number;
    mpPreferenceId: string;
    mpPaymentId: string | null;
    usuarioId: number;
    petId: number | null;
    amount: number;
    status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'CANCELLED' | 'REFUNDED';
    initPoint: string;
    createdAt: string;
    updatedAt: string;
}

@Injectable({
  providedIn: 'root',
})
export class PaymentService {
    private api = inject(ApiService);

    createPayment(request: PaymentRequest): Observable<PaymentResponse> {
        return this.api.post<PaymentResponse>('payments/create', request);
    }

    getPayment(id: number): Observable<PaymentResponse> {
        return this.api.get<PaymentResponse>(`payments/${id}`);
    }
}
