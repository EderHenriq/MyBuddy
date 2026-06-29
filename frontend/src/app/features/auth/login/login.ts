import { Component, inject, signal, OnInit, DestroyRef } from "@angular/core";
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from "@angular/forms";
import { Router, RouterLink } from "@angular/router";
import { AuthService } from "@core/services/auth.service";
import {
  AutoCompleteCompleteEvent,
  AutoCompleteModule,
} from "primeng/autocomplete";
import { switchMap } from "rxjs";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";

@Component({
  selector: "app-login",
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, AutoCompleteModule],
  templateUrl: "./login.html",
  styleUrl: "./login.scss",
})
export class Login implements OnInit {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);
  private destroyRef = inject(DestroyRef);

  loginForm!: FormGroup;

  //Gerenciamento dos estados do Login
  errorMessage = signal<string | null>(null);
  isLoading = signal<boolean>(false);
  showPassword = signal<boolean>(false);
  emailSuggestions = signal<string[]>([]);

  private readonly defaultDomains = [
    "gmail.com",
    "hotmail.com",
    "outlook.com",
    "yahoo.com",
    "icloud.com",
  ];

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      email: ["", [Validators.required, Validators.email]],
      password: ["", [Validators.required, Validators.minLength(8)]],
    });
  }

  //Sugestões de e-mail
  onEmailInput(event: AutoCompleteCompleteEvent): void {
    const value = event.query;

    if (!value) {
      this.emailSuggestions.set([]);
      return;
    }

    if (value.includes("@")) {
      const [username, domainPart] = value.split("@");
      this.emailSuggestions.set(
        this.defaultDomains
          .filter((d) => d.startsWith(domainPart.toLowerCase()))
          .map((d) => `${username}@${d}`),
      );
    } else {
      this.emailSuggestions.set(
        this.defaultDomains.map((d) => `${value}@${d}`),
      );
    }
  }

  togglePasswordVisibility(): void {
    this.showPassword.update((v) => !v);
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
      .pipe(
        switchMap(() => this.authService.obterPerfil()),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe({
        next: () => {
          this.isLoading.set(false);
          this.router.navigate(["/home"]);
        },
        error: (err) => {
          this.isLoading.set(false);

          if (err.status === 401 || err.status === 403) {
            this.errorMessage.set(
              "E-mail ou senha incorretos. Tente novamente.",
            );
          } else {
            this.errorMessage.set(
              "Não foi possível conectar ao servidor. Tente novamente mais tarde.",
            );
          }
        },
      });
  }
}
