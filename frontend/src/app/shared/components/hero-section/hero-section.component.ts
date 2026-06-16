import { Component, inject, input, computed } from '@angular/core';
import { DomSanitizer, SafeStyle } from '@angular/platform-browser';

@Component({
  selector: 'app-hero-section',
  standalone: true,
  templateUrl: './hero-section.component.html',
  styleUrl: './hero-section.component.scss',
})
export class HeroSectionComponent {
  private sanitizer = inject(DomSanitizer);

  backgroundImageUrl = input('/assets/imagem/Hero-Section.jpg');
  kicker = input('');
  title = input('Conectar, cuidar e');
  highlightedText = input('comemorar');
  subtitle = input('Tudo isso em um só lugar');

  backgroundImageSafe = computed((): SafeStyle => this.sanitizer.bypassSecurityTrustStyle(`url(${this.backgroundImageUrl()})`));
}
