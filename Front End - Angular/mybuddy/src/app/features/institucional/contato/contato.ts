import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HeaderLandingPage } from '@shared/components/header-landing-page/header-landing-page';
import { Footer } from '@shared/components/footer/footer';

@Component({
  selector: 'app-contato',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, HeaderLandingPage, Footer],
  templateUrl: './contato.html',
  styleUrl: './contato.scss',
})
export class Contato {
  private fb = inject(FormBuilder);

  contactForm = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]],
    subject: ['', Validators.required],
    message: ['', [Validators.required, Validators.minLength(10)]],
  });

  isSubmitting = signal(false);
  successMessage = signal(false);

  isFieldInvalid(field: string): boolean {
    const control = this.contactForm.get(field);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  onSubmit(): void {
    if (this.contactForm.invalid) {
      this.contactForm.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);

    setTimeout(() => {
      this.isSubmitting.set(false);
      this.successMessage.set(true);
      this.contactForm.reset();

      setTimeout(() => {
        this.successMessage.set(false);
      }, 5000);
    }, 1500);
  }
}
