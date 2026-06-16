import { CommonModule } from "@angular/common";
import { Component, inject, OnInit, signal } from "@angular/core";
import {
  PaymentService,
  PaymentRequest,
} from "../../../core/services/PaymentService";
import { MercadoPagoService } from "../../../core/services/mercadopago.service";
import { ActivatedRoute, Router } from "@angular/router";

@Component({
  selector: "app-pagamento",
  imports: [CommonModule],
  templateUrl: "./pagamento.html",
  styleUrl: "./pagamento.scss",
})
export class Pagamento implements OnInit {
  private paymentService = inject(PaymentService);
  private mpService = inject(MercadoPagoService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  petId = signal<number | null>(null);
  petNome = signal<string>("");
  amount = signal<number>(50);
  loading = signal<boolean>(false);
  error = signal<string | null>(null);
  preferenceId = signal<string | null>(null);
  showBrick = signal<boolean>(false);
  brickLoading = signal<boolean>(false);

  ngOnInit(): void {
    const petIdParam = this.route.snapshot.queryParamMap.get("petId");
    const petNomeParam = this.route.snapshot.queryParamMap.get("petNome");
    const amountParam = this.route.snapshot.queryParamMap.get("amount");

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
      description: this.petNome()
        ? `Adoção - ${this.petNome()}`
        : "Doação MyBuddy",
    };

    this.paymentService.createPayment(request).subscribe({
      next: (response) => {
        window.location.href = response.initPoint;
      },
      error: (err) => {
        this.error.set("Ocorreu um erro ao processar o pagamento.");
        this.loading.set(false);
        console.error("Payment error:", err);
      },
    });
  }

  async abrirPaymentBrick(): Promise<void> {
    if (this.preferenceId()) {
      this.showBrick.set(true);
      await this.renderBrick(this.preferenceId()!);
      return;
    }

    this.brickLoading.set(true);
    this.error.set(null);

    const request: PaymentRequest = {
      petId: this.petId() ?? undefined,
      amount: this.amount(),
      description: this.petNome()
        ? `Adoção - ${this.petNome()}`
        : "Doação MyBuddy",
    };

    this.paymentService.createPayment(request).subscribe({
      next: async (response) => {
        this.preferenceId.set(response.mpPreferenceId);
        this.showBrick.set(true);
        await this.renderBrick(response.mpPreferenceId);
        this.brickLoading.set(false);
      },
      error: (err) => {
        this.error.set("Erro ao inicializar o formulário de pagamento.");
        this.brickLoading.set(false);
        console.error(err);
      },
    });
  }

  private async renderBrick(preferenceId: string): Promise<void> {
    await this.mpService.initialize();
    const mp = this.mpService.getInstance();
    if (!mp) return;

    const bricksBuilder = mp.bricks();
    await bricksBuilder.create("wallet", "wallet-brick", {
      initialization: { preferenceId },
      customization: {
        texts: { valueProp: "smart_option" },
      },
    });
  }

  voltar(): void {
    this.router.navigate(["/pets"]);
  }
}
