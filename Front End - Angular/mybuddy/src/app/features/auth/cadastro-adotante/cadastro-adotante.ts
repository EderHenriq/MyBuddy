import { Component, inject, signal } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.services';

@Component({
  selector: 'app-cadastro-adotante',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './cadastro-adotante.html',
  styleUrl: './cadastro-adotante.scss',
})
export class CadastroAdotante {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  registerForm: FormGroup;
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);
  isLoading = signal<boolean>(false);

  constructor() {
    this.registerForm = this.fb.group({
      nome: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      email: ['', [Validators.required, Validators.email]],
      telefone: ['', [Validators.required, Validators.pattern(/^\(?\d{2}\)?[\s-]?\d{4,5}-?\d{4}$/)]],
      password: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(40)]],
      confirmPassword: ['', [Validators.required]]
    }, { validators: this.passwordMatchValidator });
  }

  private passwordMatchValidator(group: FormGroup) {
    const password = group.get('password')?.value;
    const confirmPassword = group.get('confirmPassword')?.value;
    return password === confirmPassword ? null : { passwordMismatch: true };
  }

  // Máscara automática de telefone (XX) XXXXX-XXXX
  onTelefoneInput(event: any) {
    let input = event.target.value.replace(/\D/g, '');
    if (input.length > 11) input = input.substring(0, 11);
    
    if (input.length > 6) {
      input = `(${input.substring(0, 2)}) ${input.substring(2, 7)}-${input.substring(7)}`;
    } else if (input.length > 2) {
      input = `(${input.substring(0, 2)}) ${input.substring(2)}`;
    } else if (input.length > 0) {
      input = `(${input}`;
    }
    
    this.registerForm.get('telefone')?.setValue(input, { emitEvent: false });
  }

  onSubmit() {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);

    const { nome, email, telefone, password } = this.registerForm.value;

    const payload = {
      nome,
      email,
      telefone,
      password,
      roles: ['ADOTANTE']
    };

    this.authService.register(payload).subscribe({
      next: () => {
        this.isLoading.set(false);
        this.successMessage.set('Cadastro realizado com sucesso! Redirecionando para o login...');
        setTimeout(() => {
          this.router.navigate(['/auth/login']);
        }, 2000);
      },
      error: (err) => {
        this.isLoading.set(false);
        this.errorMessage.set(err.error?.message || 'Erro ao realizar cadastro. Tente outro e-mail.');
      }
    });
  }
}
