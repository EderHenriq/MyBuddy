import { Component } from "@angular/core";
import { CommonModule } from "@angular/common";
import { HeaderLandingPage } from "@shared/components/header-landing-page/header-landing-page";
import { Footer } from "@shared/components/footer/footer";

interface FaqItem {
  question: string;
  answer: string;
  category: "Adoção" | "ONGs" | "Plataforma";
  expanded: boolean;
}

@Component({
  selector: "app-faq",
  standalone: true,
  imports: [CommonModule, HeaderLandingPage, Footer],
  templateUrl: "./faq.html",
  styleUrl: "./faq.scss",
})
export class Faq {
  activeCategory: "Adoção" | "ONGs" | "Plataforma" | "Todas" = "Todas";

  readonly categories = ["Todas", "Adoção", "ONGs", "Plataforma"];

  faqs: FaqItem[] = [
    {
      category: "Adoção",
      question: "Como funciona o processo de adoção?",
      answer:
        "Basta encontrar o pet desejado, clicar em adotar e preencher o formulário. A ONG responsável entrará em contato para agendar uma entrevista e finalizar o processo.",
      expanded: false,
    },
    {
      category: "Adoção",
      question: "Preciso pagar para adotar um pet?",
      answer:
        "Não. A adoção pelo MyBuddy é totalmente gratuita. No entanto, algumas ONGs podem sugerir uma contribuição voluntária para ajudar com os custos de vacinação e castração prévios.",
      expanded: false,
    },
    {
      category: "ONGs",
      question: "Como minha ONG pode se cadastrar?",
      answer:
        'Na página de cadastro, selecione o perfil "ONG" e preencha os dados da sua instituição, incluindo CNPJ. Nossa equipe fará uma rápida verificação antes de liberar o painel para cadastro de pets.',
      expanded: false,
    },
    {
      category: "ONGs",
      question: "Existe limite de pets que posso cadastrar?",
      answer:
        "Não. ONGs parceiras podem cadastrar quantos animais precisarem, sem nenhum custo ou limite. A plataforma foi feita para ajudar a dar visibilidade a todos os seus resgatados.",
      expanded: false,
    },
    {
      category: "Plataforma",
      question: "O MyBuddy é seguro?",
      answer:
        "Sim! Levamos a segurança a sério. Todas as ONGs passam por verificação manual e adotantes precisam criar um perfil com informações de contato válidas para poder interagir na plataforma.",
      expanded: false,
    },
    {
      category: "Plataforma",
      question: "Como posso excluir minha conta?",
      answer:
        "Você pode excluir sua conta a qualquer momento acessando o seu Perfil > Configurações > Excluir Conta. Seus dados e mensagens serão permanentemente apagados.",
      expanded: false,
    },
  ];

  get filteredFaqs() {
    if (this.activeCategory === "Todas") return this.faqs;
    return this.faqs.filter((faq) => faq.category === this.activeCategory);
  }

  setCategory(cat: any) {
    this.activeCategory = cat;
  }

  toggleFaq(faq: FaqItem) {
    faq.expanded = !faq.expanded;
  }
}
