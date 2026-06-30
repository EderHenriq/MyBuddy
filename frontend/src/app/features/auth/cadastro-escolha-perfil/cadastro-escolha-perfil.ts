import { Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, FormGroup, Validators } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { AuthService } from '@core/services/auth.service';

type TipoStep = 'perfil' | 'subtipo-org' | 'dados-pessoais' | 'dados-org' | 'senha';
type TipoPerfil = 'ADOTANTE' | 'ORGANIZACAO';
type TipoOrg = 'ONG' | 'PETSHOP' | 'VETERINARIO';

interface CadastroPayload {
  nome: string;
  email: string;
  telefone: string;
  password: string;
  roles: (TipoPerfil | TipoOrg)[];
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
  selectedOrgSubtype = signal<TipoOrg>('ONG');
  currentStep = signal<TipoStep>('perfil');
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);
  isLoading = signal<boolean>(false);
  showPassword = signal<boolean>(false);
  showConfirmPassword = signal<boolean>(false);

  //Steps dinamicos por tipo de perfil
  totalSteps = computed<TipoStep[]>(() => {
    if (this.isBusinessProfile()) {
      return ['perfil', 'subtipo-org', 'dados-pessoais', 'dados-org', 'senha'];
    }
    return ['perfil', 'dados-pessoais', 'senha'];
  });

  lastStep = computed<TipoStep>(() => 'senha');

  stepSubtitle = computed<string>(() => {
    const step = this.currentStep();

    if (step === 'perfil') return 'Escolha como você vai usar o MyBuddy.';
    if (step === 'subtipo-org') return 'Que tipo de organização você representa?';
    if (step === 'dados-pessoais') return 'Preencha seus dados pessoais.';
    if (step === 'dados-org') return 'Dados da sua organização.';

    return 'Crie sua senha de acesso.';
  });

  //Seção de opções de perfis
  readonly profileOptions: {
    value: TipoPerfil;
    label: string;
    helper: string;
    icon: string;
  }[] = [
    {
      value: 'ADOTANTE',
      label: 'Adotante',
      helper: 'Quero adotar e cuidar de um pet.',
      icon: 'fa-solid fa-dog',
    },
    {
      value: 'ORGANIZACAO',
      label: 'Organização',
      helper: 'Represento uma ONG, Veterinário ou um Petshop.',
      icon: 'fa-solid fa-hand-holding-heart',
    },
  ];

  readonly orgSubtypeOptions: {
    value: TipoOrg;
    label: string;
    helper: string;
    icon: string;
  }[] = [
    {
      value: 'ONG',
      label: 'Ong',
      helper: 'Instituição sem fins lucrativos',
      icon: 'fa-solid fa-hand-holding-heart',
    },
    {
      value: 'PETSHOP',
      label: 'Petshop',
      helper: 'Comércio de produtos e serviços para pets',
      icon: 'fa-solid fa-shop',
    },
    {
      value: 'VETERINARIO',
      label: 'Veterinário',
      helper: 'Centro médico para animais domésticos',
      icon: 'fa-solid fa-heart-pulse',
    },
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
    const step = this.currentStep();

    if (step === 'perfil') {
      this.currentStep.set(this.isBusinessProfile() ? 'subtipo-org' : 'dados-pessoais');
      return;
    }

    if (step === 'subtipo-org') {
      this.currentStep.set('dados-pessoais');
      return;
    }
    if (step === 'dados-pessoais') {
      this.currentStep.set(this.isBusinessProfile() ? 'dados-org' : 'senha');
      return;
    }

    if (step === 'dados-org') {
      this.currentStep.set('senha');
      return;
    }
  }

  prevStep(): void {
    const step = this.currentStep();

    if (step === 'subtipo-org') {
      this.currentStep.set('perfil');
      return;
    }
    if (step === 'dados-pessoais') {
      this.currentStep.set(this.isBusinessProfile() ? 'subtipo-org' : 'perfil');
      return;
    }
    if (step === 'dados-org') {
      this.currentStep.set('dados-pessoais');
      return;
    }
    if (step === 'senha') {
      this.currentStep.set(this.isBusinessProfile() ? 'dados-org' : 'dados-pessoais');
      return;
    }
  }

  isCurrentStepValid(): boolean {
    const step = this.currentStep();
    const form = this.registerForm;

    if (step === 'perfil') return !!this.selectedPerfil();
    if (step === 'subtipo-org') return !!this.selectedOrgSubtype();
    if (step === 'dados-pessoais') {
      return form.get('nome')!.valid && form.get('email')!.valid && form.get('telefone')!.valid;
    }

    if (step === 'dados-org') {
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
    this.selectedPerfil.set(profile);
    this.errorMessage.set(null);
    this.successMessage.set(null);
    this.updateProfileValidators();
  }

  selectOrgSubtype(subtype: TipoOrg): void {
    this.selectedOrgSubtype.set(subtype);
  }

  isBusinessProfile(): boolean {
    return this.selectedPerfil() === 'ORGANIZACAO';
  }

  organizationLabel(): string {
    const sub = this.selectedOrgSubtype();
    if (sub === 'ONG') return 'Nome fantasia da ONG';
    if (sub === 'VETERINARIO') return 'Nome do consultório veterinário';
    return 'Nome fantasia do Petshop';
  }

  cnpjLabel(): string {
    const sub = this.selectedOrgSubtype();
    if (sub === 'ONG') return 'CNPJ da ONG';
    if (sub === 'VETERINARIO') return 'CNPJ do consultório veterinário';
    return 'CNPJ do Petshop';
  }

  emailContatoLabel(): string {
    const sub = this.selectedOrgSubtype();
    if (sub === 'ONG') return 'E-mail de contato da ONG';
    if (sub === 'VETERINARIO') return 'E-mail de contato do consultório veterinário';
    return 'E-mail de contato do Petshop';
  }

  enderecoLabel(): string {
    const sub = this.selectedOrgSubtype();
    if (sub === 'ONG') return 'Endereço da ONG';
    if (sub === 'VETERINARIO') return 'Endereço do consultório veterinário';
    return 'Endereço do Petshop';
  }

  descricaoPlaceholder(): string {
    const sub = this.selectedOrgSubtype();
    if (sub === 'ONG') return 'Conte-nos um pouco sobre o propósito e a história de sua ONG...';
    if (sub === 'VETERINARIO') return 'Conte-nos um pouco sobre os serviços e especialidades da sua clínica veterinária...';
    return 'Conte-nos um pouco sobre seus produtos, serviços e especialidades...';
  }

  //Seção de toggles de senhas
  togglePassword(): void {
    this.showPassword.update(v => !v);
  }

  toggleConfirmPassword(): void {
    this.showConfirmPassword.update(v => !v);
  }

  //Seção de máscaras
  onTelefoneInput(event: Event, fieldName = 'telefone'): void {
    const target = event.target as HTMLInputElement;
    let input = target.value.replace(/\D/g, '');
    if (input.length > 11) input = input.substring(0, 11);

    if (input.length > 6) {
      input = `(${input.substring(0, 2)}) ${input.substring(2, 7)}-${input.substring(7)}`;
    } else if (input.length > 2) {
      input = `(${input.substring(0, 2)}) ${input.substring(2)}`;
    } else if (input.length > 0) {
      input = `(${input}`;
    }

    this.registerForm.get(fieldName)?.setValue(input, { emitEvent: false });
  }

  onCnpjInput(event: Event): void {
    const target = event.target as HTMLInputElement;
    let input = target.value.replace(/\D/g, '');
    if (input.length > 14) input = input.substring(0, 14);

    if (input.length > 12) {
      input = `${input.substring(0, 2)}.${input.substring(2, 5)}.${input.substring(5, 8)}/${input.substring(8, 12)}-${input.substring(12)}`;
    } else if (input.length > 8) {
      input = `${input.substring(0, 2)}.${input.substring(2, 5)}.${input.substring(5, 8)}/${input.substring(8)}`;
    } else if (input.length > 5) {
      input = `${input.substring(0, 2)}.${input.substring(2, 5)}.${input.substring(5)}`;
    } else if (input.length > 2) {
      input = `${input.substring(0, 2)}.${input.substring(2)}`;
    }

    this.registerForm.get('organizacaoCnpj')?.setValue(input, { emitEvent: false });
  }

  //Seção de envio
  onSubmit(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);

    const values = this.registerForm.value;

    const roles: (TipoPerfil | TipoOrg)[] = this.isBusinessProfile() ? [this.selectedOrgSubtype()] : ['ADOTANTE'];

    const payload: CadastroPayload = {
      nome: values.nome,
      email: values.email,
      telefone: values.telefone,
      password: values.password,
      roles,
    };

    if (this.isBusinessProfile()) {
      payload.organizacaoCnpj = values.organizacaoCnpj;
      payload.organizacaoNomeFantasia = values.organizacaoNomeFantasia;
      payload.organizacaoEmailContato = values.organizacaoEmailContato;
      payload.organizacaoEndereco = values.organizacaoEndereco;
      payload.organizacaoTelefoneContato = values.organizacaoTelefoneContato;
      payload.organizacaoDescricao = values.organizacaoDescricao;
    }

    this.authService.registrar(payload).subscribe({
      next: () => {
        this.isLoading.set(false);
        this.successMessage.set('Cadastro realizado com sucesso! Redirecionando para login...');
        setTimeout(() => this.router.navigate(['/auth/login']), 2000);
      },
      error: (err: { error?: { message?: string } }) => {
        this.isLoading.set(false);
        this.errorMessage.set(err.error?.message || 'Ocorreu um erro durante o cadastro. Por favor, verifique os dados e tente novamente.');
      },
    });
  }

  //Seção de validadores privados
  isStepCompleted(step: TipoStep): boolean {
    const order: TipoStep[] = ['perfil', 'subtipo-org', 'dados-pessoais', 'dados-org', 'senha'];
    return order.indexOf(step) < order.indexOf(this.currentStep());
  }

  private updateProfileValidators(): void {
    const businessValidators = {
      organizacaoNomeFantasia: [Validators.required, Validators.minLength(3)],
      organizacaoCnpj: [Validators.required, Validators.pattern(/^\d{2}\.\d{3}\.\d{3}\/\d{4}-\d{2}$/)],
      organizacaoEmailContato: [Validators.required, Validators.email],
      organizacaoEndereco: [Validators.required],
    };

    Object.entries(businessValidators).forEach(([field, validators]) => {
      const control = this.registerForm.get(field);
      if (!control) return;

      if (this.isBusinessProfile()) {
        control.setValidators(validators);
      } else {
        control.clearValidators();
        control.setValue('', { emitEvent: false });
      }

      control.updateValueAndValidity({ emitEvent: false });
    });

    if (!this.isBusinessProfile()) {
      this.registerForm.patchValue({ organizacaoTelefoneContato: '', organizacaoDescricao: '' }, { emitEvent: false });
    }
  }

  private passwordMatchValidator(group: FormGroup) {
    const password = group.get('password')?.value;
    const confirmPassword = group.get('confirmPassword')?.value;

    return password === confirmPassword ? null : { passwordMismatch: true };
  }
}
