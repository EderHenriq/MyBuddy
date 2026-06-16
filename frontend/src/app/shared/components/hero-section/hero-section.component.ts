import { Component, Input } from "@angular/core";
import { CommonModule } from "@angular/common";

@Component({
  selector: "app-hero-section",
  standalone: true,
  imports: [CommonModule],
  templateUrl: "./hero-section.component.html",
  styleUrl: "./hero-section.component.scss",
})
export class HeroSectionComponent {
  @Input() backgroundImageUrl =
    "https://images.unsplash.com/photo-1573865526739-10659fec78a5?auto=format&fit=crop&q=80&w=1800";
  @Input() kicker = "";
  @Input() title = "Conectar, cuidar e";
  @Input() highlightedText = "comemorar.";
  @Input() subtitle = "Tudo isso em um só lugar";
}
