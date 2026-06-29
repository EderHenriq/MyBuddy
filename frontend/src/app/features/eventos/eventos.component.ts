import { CommonModule } from "@angular/common";
import { Component } from "@angular/core";
import { CardEventoComponent } from "@shared/components/card-evento/card-evento.component";
import { Footer } from "@shared/components/footer/footer";
import { HeroSectionComponent } from "@shared/components/hero-section/hero-section.component";

interface Evento {
  id: number;
  imageUrl: string;
  badgeText: string;
  title: string;
  dateStr: string;
  timeStr: string;
  locationStr: string;
  organizerStr: string;
  description: string;
  isFavorite: boolean;
}

@Component({
  selector: "app-eventos",
  standalone: true,
  imports: [CommonModule, CardEventoComponent, Footer, HeroSectionComponent],
  templateUrl: "./eventos.component.html",
  styleUrl: "./eventos.component.scss",
})
export class EventosComponent {
  eventos: Evento[] = [
    {
      id: 1,
      imageUrl:
        "https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&q=80&w=800",
      badgeText: "PRÓXIMO",
      title: "Feira de Adoção 'Amor de Pet'",
      dateStr: "Sábado, 25 de Maio",
      timeStr: "10:00 - 17:00",
      locationStr: "Parque do Ibirapuera, SP",
      organizerStr: "ONG Patas Unidas",
      description:
        "Vários cachorros e gatos prontos para um novo lar. Vacinação no local.",
      isFavorite: false,
    },
    {
      id: 2,
      imageUrl:
        "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&q=80&w=800",
      badgeText: "",
      title: "Gatoteca & Adoção de Felinos",
      dateStr: "Domingo, 26 de Maio",
      timeStr: "11:00 - 16:00",
      locationStr: "Centro de Eventos, Curitiba",
      organizerStr: "ONG Felinos Felizes",
      description:
        "Venha conhecer felinos especiais procurando um lar amoroso.",
      isFavorite: true,
    },
    {
      id: 3,
      imageUrl:
        "https://images.unsplash.com/photo-1537151608804-ea6f23b7b6c5?auto=format&fit=crop&q=80&w=800",
      badgeText: "",
      title: "Cãominhada e Feira de Adoção",
      dateStr: "Próximo Fim de Semana",
      timeStr: "09:00 - 18:00",
      locationStr: "Lagoa do Taquaral, Campinas",
      organizerStr: "Coletivo Animal",
      description:
        "Caminhe com seu buddy e adote um novo amigo na nossa feira.",
      isFavorite: false,
    },
  ];

  historias = [
    {
      imagem:
        "https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?auto=format&fit=crop&q=80&w=150",
      texto:
        '"Venha e veja a doçura familiar de uma zona onde somos acolhidos"',
    },
    {
      imagem:
        "https://images.unsplash.com/photo-1516366478644-84566f1e149f?auto=format&fit=crop&q=80&w=150",
      texto: '"Venha ver a conversa em família que a união com um buddy traz"',
    },
    {
      imagem:
        "https://images.unsplash.com/photo-1530281700549-e82e7bf110d6?auto=format&fit=crop&q=80&w=150",
      texto:
        '"Venha ser adotado e sinta como se fossem seus problemas resolvidos"',
    },
    {
      imagem:
        "https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&q=80&w=150",
      texto: '"Um pet devoto na família ajuda a viver sem encorajar medos"',
    },
  ];

  verDetalhes(evento: Evento) {
    console.log("Ver detalhes de", evento.title);
    alert("Ver detalhes do evento: " + evento.title);
  }

  toggleFavorite(evento: Evento) {
    evento.isFavorite = !evento.isFavorite;
  }
}
