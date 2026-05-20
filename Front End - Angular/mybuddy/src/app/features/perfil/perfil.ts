import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CardPetComponent } from '@shared/components/card-pet/card-pet.component';
import { AuthService } from '@core/services/auth.service';

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [CommonModule, CardPetComponent],
  templateUrl: './perfil.html',
  styleUrl: './perfil.scss',
})
export class Perfil implements OnInit {
  private authService = inject(AuthService);

  user = {
    name: 'Carregando...',
    role: '',
    email: '',
    phone: '',
    address: 'São Paulo, SP',
    profilePic: 'https://images.unsplash.com/photo-1531123897727-8f129e1688ce?auto=format&fit=crop&q=80&w=300'
  };

  buddies = [
    {
      name: 'Paçoca',
      age: '5 anos',
      breed: 'Vira-lata',
      sex: 'Macho',
      vaccinated: 'Sim',
      imageUrl: 'https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&q=80&w=500',
      badgeText: 'Em processo de adoção',
      badgeType: 'adoption' as const,
      isFavorite: true,
      showTopHeart: false
    },
    {
      name: 'Jade',
      age: '1 ano',
      breed: 'Vira-lata',
      sex: 'Fêmea',
      vaccinated: 'Sim',
      imageUrl: 'https://images.unsplash.com/photo-1585110396000-c9ffd4e4b308?auto=format&fit=crop&q=80&w=500',
      badgeText: 'Adotado',
      badgeType: 'adopted' as const,
      isFavorite: true,
      showTopHeart: false
    }
  ];

  favorites = [
    {
      name: 'Nevasca',
      age: '3 anos',
      breed: 'Persa',
      sex: 'Fêmea',
      vaccinated: 'Sim',
      imageUrl: 'https://images.unsplash.com/photo-1618826411640-d6df44dd3f7a?auto=format&fit=crop&q=80&w=500',
      badgeText: '',
      badgeType: '' as const,
      isFavorite: true,
      showTopHeart: true
    },
    {
      name: 'Thor',
      age: '4 anos',
      breed: 'Border Collie',
      sex: 'Macho',
      vaccinated: 'Sim',
      imageUrl: 'https://images.unsplash.com/photo-1517849845537-4d257902454a?auto=format&fit=crop&q=80&w=500',
      badgeText: '',
      badgeType: '' as const,
      isFavorite: true,
      showTopHeart: true
    },
    {
      name: 'Francesca',
      age: '4 anos',
      breed: 'SRD',
      sex: 'Fêmea',
      vaccinated: 'Sim',
      imageUrl: 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&q=80&w=500',
      badgeText: '',
      badgeType: '' as const,
      isFavorite: true,
      showTopHeart: true
    },
    {
      name: 'Armindo',
      age: '2 anos',
      breed: 'Rex',
      sex: 'Macho',
      vaccinated: 'Sim',
      imageUrl: 'https://images.unsplash.com/photo-1585110396000-c9ffd4e4b308?auto=format&fit=crop&q=80&w=500',
      badgeText: '',
      badgeType: '' as const,
      isFavorite: true,
      showTopHeart: true
    }
  ];

  get buddiesTitle(): string {
    const roles = this.authService.getUserRoles();
    if (roles.includes('ROLE_ONG') || roles.includes('ROLE_PETSHOP')) {
      return 'Animais Disponíveis';
    }
    return 'Meus Buddies';
  }

  ngOnInit() {
    const profile = this.authService.currentUser();
    if (profile) {
      this.setupProfile(profile);
    } else {
      this.authService.getProfile().subscribe(data => {
        this.setupProfile(data);
      });
    }
  }

  private setupProfile(profile: any) {
    this.user.name = profile.nome || profile.nomeFantasia || 'Usuário MyBuddy';
    this.user.email = profile.email || '';
    this.user.phone = profile.telefone || profile.telefoneContato || '';
    
    const roles = this.authService.getUserRoles();
    
    if (roles.includes('ROLE_ONG')) {
      this.user.role = 'ONG';
      this.user.address = profile.organizacao?.endereco || 'Endereço não informado';
      this.user.profilePic = 'https://images.unsplash.com/photo-1542382257-80da9fb9f5c4?auto=format&fit=crop&q=80&w=300';
    } else if (roles.includes('ROLE_PETSHOP')) {
      this.user.role = 'Petshop';
      this.user.address = profile.petshop?.endereco || 'Endereço não informado';
      this.user.profilePic = 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&q=80&w=300';
    } else {
      this.user.role = 'Tutora';
      this.user.address = 'São Paulo, SP';
      this.user.profilePic = 'https://images.unsplash.com/photo-1531123897727-8f129e1688ce?auto=format&fit=crop&q=80&w=300';
    }
  }
}
