import { Component, input } from "@angular/core";

export type CardVariant = "landing" | "quick-access" | "product";

@Component({
  selector: "app-card-categoria",
  standalone: true,
  imports: [],
  templateUrl: "./card-categoria.component.html",
  styleUrl: "./card-categoria.component.scss",
})
export class CardCategoriaComponent {
  title = input.required<string>();
  subtitle = input<string>("");
  imageUrl = input.required<string>();
  variant = input<CardVariant>("landing");
}
