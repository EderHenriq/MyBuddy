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

  //Seção de estados de controle
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

  //Seção de opções de perfis
  readonly profileOptions: { value: TipoPerfil; label: string; helper: string; icon: string }[] = [
    { value: 'ADOTANTE', label: 'Adotante', helper: 'Quero adotar e cuidar de um pet.', icon: 'pets' },
    { value: 'ONG', label: 'ONG', helper: 'Represento uma instituição.', icon: 'volunteer_activism' },
    { value: 'PETSHOP', label: 'Petshop', helper: 'Ofereço produtos ou serviços.', icon: 'storefront' },
  ];

  //Seção de formulário de cadastro
  registerForm: FormGroup = this.fb.group(
    {
      nome: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(200)]],
      email: ['', [Validators.required, Validators.email]],
      telefone: ['', [Validators.required, Validators.pattern(/^\(?\d{2}\)?[\s-]?\d{4,5}-?\d{4}$/)]],
      password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(40)]],
      confirmPassword: ['', [Validators.required]],
      terms: [false, [Validators.requiredTrue]],
      organizacaoNomeFantasia: [''],
      organizacaoCnpj: [''],
      organizacaoEmailContato: [''],
      organizacaoEndereco: [''],
      organizacaoTelefoneContato: [''],
      organizacaoDescricao: [''],
    },
    { validators: this.passwordMatchValidator },
  );

  constructor() {
    this.updateProfileValidators();
  }

  //Construção da navegação multi-step do formulário
  nextStep(): void {
    if (this.currentStep() < this.lastStep()) {
      this.currentStep.update(s => s + 1);
    }
  }

  prevSteo(): void {
    if (this.currentStep() > 1) {
      this.currentStep.update(s => s - 1);
    }
  }

  isCurrentStepValid(): boolean {
    const step = this.currentStep();
    const form = this.registerForm;

    if (step === 1) return !!this.selectedPerfil();

    if (step === 2) {
      return form.get('nome')!.valid && form.get('email')!.valid && form.get('telefone')!.valid;
    }

    if (step === 3 && this.isBusinessProfile()) {
      return (
        form.get('organizacaoNomeFantasia')!.valid &&
        form.get('organizacaoCnpj')!.valid &&
        form.get('organizacaoEmailContato')!.valid &&
        form.get('organizacaoEndereco')!.valid
      );
    }

    return true;
  }

  //Seção de perfis de usuários
  selectProfile(profile: TipoPerfil): void {
    this.selectProfile.set(profile);
    this.errorMessage.set(null);
    this.successMessage.set(null);
    this.updateProfileValidators();
  }

  isBusinessProfile(): boolean {
    return this.selectedProfile() === 'ONG' || this.selectProfile() === 'PETSHOP';
  }

  organizationLabel(): string {
    return this.selectedProfile() === 'ONG' ? 'Nome fantasia da ONG' : 'Nome fantasia do Petshop';
  }

  cnpjLabel(): string {
    return this.selectedPerfil() === 'ONG' ? 'CNPJ da ONG' : 'CNPJ do Petshop';
  }

  emailContatoLabel(): string {
    return this.selectedPerfil() === 'ONG' ? 'E-mail de contato da ONG' : 'E-mail de contato do Petshop';
  }

  enderecoLabel(): string {
    return this.selectedPerfil() === 'ONG' ? 'Endereço da ONG' : 'Endereço do Petshop';
  }

  descricaoPlaceholder(): string {
    return this.selectedPerfil() === 'ONG'
      ? 'Conte-nos um pouco sobre o propósito e a história de sua ONG...'
      : 'Conte-nos um pouco sobre seus produtos, serviços e especialidades...';
  }
}
