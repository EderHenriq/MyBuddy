import { Component } from '@angular/core';
import { HeaderLandingPage } from '@shared/components/header-landing-page/header-landing-page';

@Component({
  selector: 'app-landing-page',
  imports: [HeaderLandingPage],
  templateUrl: './landing-page.html',
  styleUrl: './landing-page.scss',
})
export class LandingPage {}
