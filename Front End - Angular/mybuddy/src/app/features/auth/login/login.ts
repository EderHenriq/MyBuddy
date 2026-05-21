import { Component, inject, signal } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '@core/services/auth.service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  loginForm: FormGroup;
  errorMessage = signal<string | null>(null);
  isLoading = signal<boolean>(false);
  showPassword = signal<boolean>(false);

  constructor() {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
    });
  }

  togglePasswordVisibility() {
    this.showPassword.update(v => !v);
  }

  onSubmit() {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);

    const { email, password } = this.loginForm.value;

    this.authService.loginWithCredentials(email, password).subscribe({
      next: (res) => {
        if (res.isMock) {
          this.isLoading.set(false);
          this.router.navigate(['/home']);
        } else {
          // Obter os detalhes do perfil real do backend
          this.authService.getProfile().subscribe({
            next: () => {
              this.isLoading.set(false);
              this.router.navigate(['/home']);
            },
            error: () => {
              this.isLoading.set(false);
              this.errorMessage.set('Erro ao carregar dados do perfil. Tente novamente.');
            },
          });
        }
      },
      error: () => {
        this.isLoading.set(false);
        this.errorMessage.set('E-mail ou senha incorretos. Tente novamente.');
      },
    });
  }
}
