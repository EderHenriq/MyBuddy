import { Component, Input, Output, EventEmitter } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";

@Component({
  selector: "app-search-bar",
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: "./search-bar.component.html",
  styleUrl: "./search-bar.component.scss",
})
export class SearchBarComponent {
  @Input() placeholder = "O que você procura?";
  @Output() searchTriggered = new EventEmitter<string>();

  searchTerm = "";

  onSearch() {
    this.searchTriggered.emit(this.searchTerm);
  }
}
