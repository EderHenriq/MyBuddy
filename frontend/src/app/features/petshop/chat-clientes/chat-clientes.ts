import { Component, OnInit, inject } from "@angular/core";
import { CommonModule } from "@angular/common";
import { PetshopService } from "../../../core/services/petshop.service";
import { ChatPetshop } from "../../../core/models/petshop.model";
import { DebounceDirective } from "../../../shared/directives/debounce.directive";
import { PaginatorComponent } from "../../../shared/components/paginator/paginator.component";

@Component({
  selector: "app-chat-clientes",
  standalone: true,
  imports: [CommonModule, DebounceDirective, PaginatorComponent],
  templateUrl: "./chat-clientes.html",
  styleUrl: "./chat-clientes.scss",
})
export class ChatClientes implements OnInit {
  chats: ChatPetshop[] = [];
  private petshopService = inject(PetshopService);

  currentPage = 1;
  totalPages = 1;

  ngOnInit() {
    this.petshopService.buscarChats().subscribe((data) => {
      this.chats = data;
      this.totalPages = Math.ceil(data.length / 10) || 1;
    });
  }

  get chatsPaginados() {
    const startIndex = (this.currentPage - 1) * 10;
    return this.chats.slice(startIndex, startIndex + 10);
  }

  onSearch(term: string) {
    console.log(`[Chat Petshop] Pesquisando por: ${term}`);
  }

  onPageChange(page: number) {
    console.log(`[Chat Petshop] Mudando para página: ${page}`);
    this.currentPage = page;
  }
}
