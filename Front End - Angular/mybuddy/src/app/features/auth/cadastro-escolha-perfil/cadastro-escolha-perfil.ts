import { Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, FormGroup, Validators } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { AuthService } from '@core/services/auth.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

type TipoPerfil = 'ADOTANTE' | 'ONG' | 'PETSHOP';

interface CadastroPayload {
  nome: string;
  email: string;
  telefone: string;
  password: string;
  roles: TipoPerfil[];
  organizacaoCnpj?: string;
  organizacaoNomeFantasia?: string;
  organizacaoEmailContato?: string;
  organizacaoEndereco?: string;
  organizacaoTelefoneContato?: string;
  organizacaoDescricao?: string;
}

@Component({
  selector: 'app-cadastro-escolha-perfil',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './cadastro-escolha-perfil.html',
  styleUrl: './cadastro-escolha-perfil.scss',
})
export class CadastroEscolhaPerfil {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  //Estados de controle
  selectedPerfil = signal<TipoPerfil>('ADOTANTE');
  currentStep = signal<number>(1);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);
  isLoading = signal<boolean>(false);
  showPassword = signal<boolean>(false);
  showConfirmPassword = signal<boolean>(false);

  //Steps dinamicos por tipo de perfil
  totalSteps = computed<number[]>(() => {
    this.isBusinessProfile() ? [1, 2, 3, 4] : [1, 2, 3];
  });

  lastStep = computed<number>(() => {
    this.isBusinessProfile() ? 4 : 3;
  });

  stepSubtitle = computed<string>(() => {
    const step = this.currentStep();
    const isBusiness = this.isBusinessProfile();

    if (step === 1) return 'Escolha como você vai usar o MyBuddy.';
    if (step === 2) return 'Preencha seus dados pessoais.';
    if (step === 3 && isBusiness) return 'Dados da sua organização.';
    return 'Crie sua senha de acesso.';
  });

  //Opções de perfis
  readonly profileOptions: { value: TipoPerfil; label: string; helper: string; icon: string }[] = [
    { value: 'ADOTANTE', label: 'Adotante', helper: 'Quero adotar e cuidar de um pet.', icon: 'pets' },
    { value: 'ONG', label: 'ONG', helper: 'Represento uma instituição.', icon: 'volunteer_activism' },
    { value: 'PETSHOP', label: 'Petshop', helper: 'Ofereço produtos ou serviços.', icon: 'storefront' },
  ];
}
