import { CommonModule } from "@angular/common";
import { Component } from "@angular/core";
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from "@angular/forms";
import { RouterModule, Router } from "@angular/router";
import { Footer } from "@shared/components/footer/footer";
import { UploadService } from "@core/services/upload.service";

@Component({
  selector: "app-cadastrar-pet",
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, Footer],
  templateUrl: "./cadastrar-pet.html",
  styleUrl: "./cadastrar-pet.scss",
})
export class CadastrarPet {
  petForm: FormGroup;
  uploadedImages: string[] = [];
  selectedFiles: File[] = [];
  isSaving = false;
  uploadError: string | null = null;
  isDragging = false;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private uploadService: UploadService,
  ) {
    this.petForm = this.fb.group({
      nome: ["", Validators.required],
      especie: ["", Validators.required],
      raca: ["", Validators.required],
      idade: ["", [Validators.required, Validators.min(0)]],
      sexo: ["", Validators.required],
      porte: ["", Validators.required],
      cor: ["", Validators.required],
      pelagem: [""],
      cidade: ["", Validators.required],
      estado: ["", Validators.required],
      vacinado: [false],
      castrado: [false],
      microchipado: [false],
    });
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = true;
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = false;
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = false;
    this.processFiles(event.dataTransfer?.files);
  }

  onFileChange(event: any): void {
    this.processFiles(event.target.files);

    event.target.value = "";
  }

  private processFiles(files: FileList | null | undefined): void {
    if (!files || files.length === 0) return;
    this.uploadError = null;

    const maxFileSize = 5 * 1024 * 1024;

    for (let i = 0; i < files.length; i++) {
      const file = files[i];

      if (!file.type.startsWith("image/")) {
        this.uploadError = "Apenas arquivos de imagem são permitidos.";
        return;
      }

      if (file.size > maxFileSize) {
        this.uploadError = `A imagem ${file.name} excede o limite de 5MB.`;
        return;
      }

      if (this.selectedFiles.length >= 3) {
        this.uploadError = "Você só pode adicionar até 3 imagens.";
        return;
      }

      this.selectedFiles.push(file);
      const reader = new FileReader();
      reader.onload = (e) => this.uploadedImages.push(reader.result as string);
      reader.readAsDataURL(file);
    }
  }

  removeImage(index: number): void {
    this.uploadedImages.splice(index, 1);
    this.selectedFiles.splice(index, 1);
    this.uploadError = null;
  }

  onSubmit(): void {
    if (this.petForm.valid) {
      if (this.selectedFiles.length === 0) {
        this.uploadError = "Adicione ao menos uma foto do pet.";
        return;
      }

      console.log("Dados do Pet (Formulário):", this.petForm.value);
      this.isSaving = true;
      this.uploadError = null;

      this.uploadService.uploadImages(this.selectedFiles).subscribe({
        next: (urls) => {
          console.log("Imagens mockadas salvas com sucesso! URLs:", urls);

          this.isSaving = false;
          this.router.navigate(["/ong/pets"]);
        },
        error: (err) => {
          console.error("Erro no upload das imagens", err);
          this.uploadError = "Erro ao processar imagens. Tente novamente.";
          this.isSaving = false;
        },
      });
    } else {
      Object.keys(this.petForm.controls).forEach((key) => {
        const control = this.petForm.get(key);
        control?.markAsTouched();
      });
    }
  }

  get f() {
    return this.petForm.controls;
  }
}
