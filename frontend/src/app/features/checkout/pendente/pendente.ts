import { Component, inject, OnInit, signal } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { CommonModule } from "@angular/common";

@Component({
  selector: "app-pendente",
  imports: [CommonModule],
  templateUrl: "./pendente.html",
  styleUrl: "./pendente.scss",
})
export class Pendente implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  paymentId = signal<string | null>(null);
  paymentType = signal<"boleto" | "pix" | "outro">("outro");
  externalResourceUrl = signal<string | null>(null);

  ngOnInit() {
    const paymentIdParam = this.route.snapshot.queryParamMap.get("payment_id");
    const typeParam = this.route.snapshot.queryParamMap.get("payment_type");
    const resourceUrlParam = this.route.snapshot.queryParamMap.get(
      "external_resource_url",
    );

    if (paymentIdParam) this.paymentId.set(paymentIdParam);
    if (resourceUrlParam) this.externalResourceUrl.set(resourceUrlParam);
    if (typeParam === "bolbradesco" || typeParam === "pec") {
      this.paymentType.set("boleto");
    } else if (typeParam === "pix") {
      this.paymentType.set("pix");
    }
  }

  copiarCodigo() {
    const url = this.externalResourceUrl();
    if (url) {
      navigator.clipboard.writeText(url);
    }
  }

  voltarParaInicio() {
    this.router.navigate(["/home"]);
  }
}
