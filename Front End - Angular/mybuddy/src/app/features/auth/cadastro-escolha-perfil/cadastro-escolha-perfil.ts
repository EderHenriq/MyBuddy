import { Component, inject, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '@core/services/auth.service';

type ProfileType = 'ADOTANTE' | 'ONG' | 'PETSHOP';

interface CadastroPayload {
  nome: string;
  email: string;
  telefone: string;
  password?: string;
  roles: ProfileType[];
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

  selectedProfile = signal<ProfileType>('ADOTANTE');
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);
  isLoading = signal<boolean>(false);

  readonly profileOptions: { value: ProfileType; label: string; helper: string }[] = [
    { value: 'ADOTANTE', label: 'Adotante', helper: 'Quero adotar e cuidar de um pet.' },
    { value: 'ONG', label: 'ONG', helper: 'Represento uma instituicao.' },
    { value: 'PETSHOP', label: 'Petshop', helper: 'Ofereco produtos ou servicos.' },
  ];

  registerForm: FormGroup = this.fb.group(
    {
      nome: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      email: ['', [Validators.required, Validators.email]],
      telefone: ['', [Validators.required, Validators.pattern(/^\(?\d{2}\)?[\s-]?\d{4,5}-?\d{4}$/)]],
      password: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(40)]],
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

  selectProfile(profile: ProfileType): void {
    this.selectedProfile.set(profile);
    this.errorMessage.set(null);
    this.successMessage.set(null);
    this.updateProfileValidators();
  }

  isBusinessProfile(): boolean {
    return this.selectedProfile() === 'ONG' || this.selectedProfile() === 'PETSHOP';
  }

  profileTitle(): string {
    const profile = this.selectedProfile();
    if (profile === 'ONG') return 'Cadastro de ONG';
    if (profile === 'PETSHOP') return 'Cadastro de Petshop';
    return 'Cadastro de Adotante';
  }

  organizationLabel(): string {
    return this.selectedProfile() === 'ONG' ? 'Nome fantasia da ONG' : 'Nome fantasia do Petshop';
  }

  cnpjLabel(): string {
    return this.selectedProfile() === 'ONG' ? 'CNPJ da ONG' : 'CNPJ do Petshop';
  }

  emailContatoLabel(): string {
    return this.selectedProfile() === 'ONG' ? 'E-mail de contato da ONG' : 'E-mail de contato do Petshop';
  }

  enderecoLabel(): string {
    return this.selectedProfile() === 'ONG' ? 'Endereco da ONG' : 'Endereco do Petshop';
  }

  descricaoPlaceholder(): string {
    return this.selectedProfile() === 'ONG'
      ? 'Conte um pouco sobre o proposito e a historia da sua ONG...'
      : 'Conte um pouco sobre seus produtos, servicos e especialidades...';
  }

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

  onSubmit(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);

    const values = this.registerForm.value;
    const profile = this.selectedProfile();

    const payload: CadastroPayload = {
      nome: values.nome,
      email: values.email,
      telefone: values.telefone,
      password: values.password,
      roles: [profile],
    };

    if (this.isBusinessProfile()) {
      payload.organizacaoCnpj = values.organizacaoCnpj;
      payload.organizacaoNomeFantasia = values.organizacaoNomeFantasia;
      payload.organizacaoEmailContato = values.organizacaoEmailContato;
      payload.organizacaoEndereco = values.organizacaoEndereco;
      payload.organizacaoTelefoneContato = values.organizacaoTelefoneContato;
      payload.organizacaoDescricao = values.organizacaoDescricao;
    }

    this.authService.register(payload).subscribe({
      next: () => {
        this.isLoading.set(false);
        this.successMessage.set('Cadastro realizado com sucesso! Redirecionando para o login...');
        setTimeout(() => {
          this.router.navigate(['/auth/login']);
        }, 2000);
      },
      error: err => {
        this.isLoading.set(false);
        this.errorMessage.set(err.error?.message || 'Erro ao realizar cadastro. Verifique os dados e tente novamente.');
      },
    });
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
      this.registerForm.patchValue(
        {
          organizacaoTelefoneContato: '',
          organizacaoDescricao: '',
        },
        { emitEvent: false },
      );
    }
  }

  private passwordMatchValidator(group: FormGroup) {
    const password = group.get('password')?.value;
    const confirmPassword = group.get('confirmPassword')?.value;
    return password === confirmPassword ? null : { passwordMismatch: true };
  }
}
