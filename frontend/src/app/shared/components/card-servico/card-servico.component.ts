import { CommonModule } from "@angular/common";
import { Component, EventEmitter, Input, Output } from "@angular/core";

@Component({
  selector: "app-card-servico",
  standalone: true,
  imports: [CommonModule],
  templateUrl: "./card-servico.component.html",
  styleUrl: "./card-servico.component.scss",
})
export class CardServicoComponent {
  @Input() imageUrl = "";
  @Input() title = "";
  @Input() type = "";
  @Input() rating = 0;
  @Input() reviewsCount = 0;
  @Input() locationStr = "";
  @Input() distanceStr = "";
  @Input() openHoursStr = "";
  @Input() description = "";
  @Input() isFavorite = false;

  @Output() detailsClick = new EventEmitter<void>();
  @Output() favoriteClick = new EventEmitter<void>();
}
