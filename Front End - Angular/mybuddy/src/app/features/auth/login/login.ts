import { Component, inject, signal, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '@core/services/auth.service';
import { switchMap } from 'rxjs';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login implements OnInit {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  loginForm!: FormGroup;

  //Gerenciamento dos estados do Login
  errorMessage = signal<string | null>(null);
  isLoading = signal<boolean>(false);
  showPassword = signal<boolean>(false);

  emailSuggestions = signal<string[]>([]);

  private readonly defaultDomains = ['gmail.com', 'hotmail.com', 'outlook.com', 'yahoo.com', 'icloud.com'];

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
    });

    this.listenEmailChanges();
  }

  //Sugestões de e-mail
  private listenEmailChanges(): void {
    this.loginForm.get('email')?.valueChanges.subscribe((value: string) => {
      if (!value) {
        this.emailSuggestions.set([]);
        return;
      }

      if (value.includes('@')) {
        const [username, domainPart] = value.split('@');

        const filtered = this.defaultDomains.filter(domain => domain.startsWith(domainPart.toLowerCase())).map(domain => `${username}@${domain}`);

        this.emailSuggestions.set(filtered);
      } else {
        const suggestions = this.defaultDomains.map(domain => `${value}@${domain}`);
        this.emailSuggestions.set(suggestions);
      }
    });
  }

  togglePasswordVisibility(): void {
    this.showPassword.update(v => !v);
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);

    const { email, password } = this.loginForm.value;

    this.authService
      .loginComCredenciais(email, password)
      .pipe(switchMap(() => this.authService.obterPerfil()))
      .subscribe({
        next: () => {
          this.isLoading.set(false);
          this.router.navigate(['/home']);
        },
        error: err => {
          this.isLoading.set(false);

          if (err.status === 401 || err.status === 403) {
            this.errorMessage.set('E-mail ou senha incorretos. Tente novamente.');
          } else {
            this.errorMessage.set('Não foi possível conectar ao servidor. Tente novamente mais tarde.');
          }
        },
      });
  }
}
