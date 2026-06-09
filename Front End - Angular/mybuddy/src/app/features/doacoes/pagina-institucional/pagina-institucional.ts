import { Component } from '@angular/core';

import { HeroSectionComponent } from '@shared/components/hero-section/hero-section.component';
import { Footer } from '@shared/components/footer/footer';

@Component({
  selector: 'app-pagina-institucional',
  imports: [HeroSectionComponent, Footer],
  templateUrl: './pagina-institucional.html',
  styleUrl: './pagina-institucional.scss',
})
export class PaginaInstitucional {}
