import { Injectable, inject } from "@angular/core";
import { Observable } from "rxjs";
import { ApiService } from "./api.service";

@Injectable({
  providedIn: "root",
})
export class UploadService {
  private api = inject(ApiService);

  constructor() {}

  /**
   * Upload a single image
   */
  uploadImage(file: File): Observable<string> {
    const formData = new FormData();
    formData.append("file", file);
    return this.api.post<string>("pets/upload-image", formData);
  }

  /**
   * Upload multiple images
   */
  uploadImages(files: File[]): Observable<string[]> {
    const formData = new FormData();
    files.forEach((f) => formData.append("files", f));
    return this.api.post<string[]>("pets/upload-images", formData);
  }
}
