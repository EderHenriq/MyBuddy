import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { Footer } from '@shared/components/footer/footer';

@Component({
  selector: 'app-cadastrar-pet',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, Footer],
  templateUrl: './cadastrar-pet.html',
  styleUrl: './cadastrar-pet.scss',
})
export class CadastrarPet {
  petForm: FormGroup;
  uploadedImages: string[] = [];

  constructor(private fb: FormBuilder, private router: Router) {
    this.petForm = this.fb.group({
      nome: ['', Validators.required],
      especie: ['', Validators.required],
      raca: ['', Validators.required],
      idade: ['', [Validators.required, Validators.min(0)]],
      sexo: ['', Validators.required],
      porte: ['', Validators.required],
      cor: ['', Validators.required],
      pelagem: [''],
      cidade: ['', Validators.required],
      estado: ['', Validators.required],
      vacinado: [false],
      castrado: [false],
      microchipado: [false]
    });
  }

  // Simulação de upload
  onFileChange(event: any): void {
    if (event.target.files && event.target.files[0]) {
      const file = event.target.files[0];
      const reader = new FileReader();
      reader.onload = e => this.uploadedImages.push(reader.result as string);
      reader.readAsDataURL(file);
    }
  }

  removeImage(index: number): void {
    this.uploadedImages.splice(index, 1);
  }

  onSubmit(): void {
    if (this.petForm.valid) {
      console.log('Dados do Pet:', this.petForm.value);
      console.log('Imagens:', this.uploadedImages);
      // Aqui faria o post para o back-end e depois redirecionaria
      this.router.navigate(['/ong/pets']);
    } else {
      Object.keys(this.petForm.controls).forEach(key => {
        const control = this.petForm.get(key);
        control?.markAsTouched();
      });
    }
  }

  get f() {
    return this.petForm.controls;
  }
}
