import { Component, Input, Output, EventEmitter } from "@angular/core";
import { CommonModule } from "@angular/common";

@Component({
  selector: "app-chip-filtro",
  standalone: true,
  imports: [CommonModule],
  templateUrl: "./chip-filtro.component.html",
  styleUrl: "./chip-filtro.component.scss",
})
export class ChipFiltroComponent {
  @Input() label = "";
  @Input() iconClass = "";
  @Input() active = false;

  @Output() chipClick = new EventEmitter<void>();
}
