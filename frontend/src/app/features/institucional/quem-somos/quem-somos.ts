import { Component } from "@angular/core";
import { CommonModule } from "@angular/common";
import { HeaderLandingPage } from "@shared/components/header-landing-page/header-landing-page";
import { Footer } from "@shared/components/footer/footer";

@Component({
  selector: "app-quem-somos",
  standalone: true,
  imports: [CommonModule, HeaderLandingPage, Footer],
  templateUrl: "./quem-somos.html",
  styleUrl: "./quem-somos.scss",
})
export class QuemSomos {}
